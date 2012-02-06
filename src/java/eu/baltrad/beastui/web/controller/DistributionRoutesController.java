/* --------------------------------------------------------------------
Copyright (C) 2009-2011 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the BaltradDex package.

The BaltradDex package is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The BaltradDex package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex package library.  If not, see <http://www.gnu.org/licenses/>.
------------------------------------------------------------------------*/
package eu.baltrad.beastui.web.controller;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.RuleException;
import eu.baltrad.beast.rules.dist.DistributionRule;

/**
 * Manages DistributionRule instances
 */
@Controller
public class DistributionRoutesController {
  private IRouterManager manager = null;
  private ObjectMapper jsonMapper = new ObjectMapper();
  private static Logger logger = LogManager.getLogger(DistributionRoutesController.class);

  protected enum Operation { Add, Modify, Delete, View };
 
  /**
   * Set the router manager instance
   */
  @Autowired
  public void setManager(IRouterManager manager) {
    this.manager = manager;
  }

  @RequestMapping("/distributionroute.htm")
  public String distributionRoute(
      Model model,
      @RequestParam(value="name", required=false) String name,
      @RequestParam(value="author", required=false) String author,
      @RequestParam(value="active", required=false) Boolean active,
      @RequestParam(value="description", required=false) String description,
      @RequestParam(value="destination", required=false) String destination,
      @RequestParam(value="namingTemplate", required=false) String namingTemplate,
      @RequestParam(value="filterJson", required=false) String filterJson,
      @RequestParam(value="submitButton", required=false) String opString) {
    
    Operation op = null;
    try {
      op = Operation.valueOf(opString);
    } catch (Exception e) {
      op = Operation.View;
    }

    RouteDefinition routeDef = null;
    if (op == Operation.View && name != null) {
      routeDef = manager.getDefinition(name);
      DistributionRule rule = (DistributionRule)routeDef.getRule();
      destination = rule.getDestination().toString();
      namingTemplate = rule.getMetadataNamingTemplate();
      try {
        filterJson = jsonMapper.writeValueAsString(rule.getFilter());
      } catch (IOException e) {
        logger.error("failed to create JSON string from filter", e);
      }
    } else {
      routeDef = createRoute(name, author, active, description);
    }

    if ((op == Operation.View && name == null) || op == Operation.Add) {
      model.addAttribute("create", true);
    } else {
      model.addAttribute("create", false);
    }

    switch (op) {
      case Add:
        return addRoute(model, routeDef, destination, namingTemplate, filterJson);
      case Modify:
        return modifyRoute(model, routeDef, destination, namingTemplate, filterJson);
      case Delete:
        return deleteRoute(model, name);
      default:
        return viewShowRoute(model, routeDef, destination, namingTemplate, filterJson, null);
    }
  }

  protected String addRoute(
      Model model,
      RouteDefinition routeDef,
      String destination,
      String namingTemplate,
      String filterJson) {
    String emessage = null;
    try {
      validateDefinition(routeDef);
      DistributionRule rule = createRule(destination, namingTemplate, filterJson);
      routeDef.setRule(rule);
      manager.storeDefinition(routeDef);
      return "redirect:showroutes.htm";
    } catch (Exception e) {
      logger.error("Failed to create definition", e);
      emessage = "Failed to create definition: " + e.getMessage();
    }
    return viewShowRoute(model, routeDef, destination, namingTemplate, filterJson, emessage);
  }

  protected String deleteRoute(Model model, String name) {
    RouteDefinition def = manager.getDefinition(name);
    if (def == null) {
      return viewShowRoutes(model, "No route named '" + name + "'");
    }
    try {
      manager.deleteDefinition(name);
      return "redirect:showroutes.htm";
    } catch (RuntimeException e) {
      logger.error("failed to delete route '" + name + "'", e);
      return viewShowRoutes(
        model,
        "Failed to delete route '" + name + "'. Check the log for details."
      );
    }
  }

  protected String modifyRoute(
      Model model,
      RouteDefinition routeDef,
      String destination,
      String namingTemplate,
      String filterJson) {
    String emessage = null;

    try {
      validateDefinition(routeDef);
      DistributionRule rule = createRule(destination, namingTemplate, filterJson);
      routeDef.setRule(rule);
      manager.updateDefinition(routeDef);
      return "redirect:showroutes.htm";
    } catch (Exception e) {
      logger.error("Failed to update definition", e);
      emessage = "Failed to update definition: " + e.getMessage();
    }
    return viewShowRoute(model, routeDef, destination, namingTemplate, filterJson, emessage);
  }
  
  /**
   * Show the distributionroute view
   */
  protected String viewShowRoute(
      Model model,
      RouteDefinition route,
      String destination,
      String namingTemplate,
      String filterJson,
      String emessage) {
    model.addAttribute("route", route);
    model.addAttribute("destination", destination);
    model.addAttribute("namingTemplate", namingTemplate);
    model.addAttribute("filterJson", filterJson);
    model.addAttribute("emessage", emessage);
    return "distributionroute";
  }

  /**
   * Shows the routes view
   * 
   * @param model the model
   * @param emessage the error message if any
   * @return "showroutes"
   */
  protected String viewShowRoutes(Model model, String emessage) {
    logger.debug("showRoutes(Model)");
    List<RouteDefinition> definitions = manager.getDefinitions();
    model.addAttribute("routes", definitions);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "showroutes";
  }
  
  /**
   * create a RouteDefinition instance.
   * @param name name of the route
   * @param author author of the route
   * @param active is the route active
   * @param description description of the route
   */
  protected RouteDefinition createRoute(
      String name,
      String author,
      Boolean active,
      String description) {
    if (active == null)
      active = new Boolean(false);
    return manager.create(name, author, active, description, null, null);
  }
  
  /**
   * create a DistributionRule instance.
   * @param destination the destination URI
   * @param filterJson JSON string of the filter
   * @throws RuleException if the rule can't be created
   * @return the created instance
   */
  protected DistributionRule createRule(
      String destination,
      String namingTemplate,
      String filterJson) {
    DistributionRule rule =
      (DistributionRule)manager.createRule(DistributionRule.TYPE);
    try {
      rule.setDestination(destination);
    } catch (IllegalArgumentException e) {
      throw new RuleException("invalid destination: " + e.getCause().getMessage());
    }
    if (namingTemplate != null && !namingTemplate.isEmpty()) {
      rule.setMetadataNamingTemplate(namingTemplate);
    } else {
      rule.setUuidNamer();
    }
    if (filterJson == null || filterJson.equals(""))
      throw new RuleException("filter must be specified");
    try {
      rule.setFilter(jsonMapper.readValue(filterJson, IFilter.class));
    } catch (IOException e) {
      throw new RuleException("could not create filter from " + filterJson, e);
    }
    return rule;
  }

  protected void validateDefinition(RouteDefinition route) {
    if (route.getName() == null || route.getName().equals("")) {
      throw new RuleException("name must be specified");
    }
  }
}
