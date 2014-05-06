/* --------------------------------------------------------------------
Copyright (C) 2009-2014 Swedish Meteorological and Hydrological Institute, SMHI,

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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.scansun.ScansunRule;
import eu.baltrad.beast.rules.util.IRuleUtilities;

/**
 * @author Anders Henja
 */
@Controller
public class ScansunRoutesController {
  /**
   * The router manager
   */
  private IRouterManager manager = null;

  /**
   * The adaptor manager
   */
  private IBltAdaptorManager adaptormanager = null;

  /**
   * The rule utilities
   */
  private IRuleUtilities utilities = null;
  
  /**
   * The logger
   */
  private static Logger logger = LogManager.getLogger(ScansunRoutesController.class);
  
  /**
   * Default constructor
   */
  public ScansunRoutesController() {
    
  }
  
  /**
   * @param manager the manager to set
   */
  @Autowired
  public void setManager(IRouterManager manager) {
    this.manager = manager;
  }

  /**
   * @param adaptormanager the adaptor manager to set
   */
  @Autowired
  public void setAdaptorManager(IBltAdaptorManager adaptormanager) {
    this.adaptormanager = adaptormanager;
  }
  
  /**
   * @param template the jdbc operations to set
   */
  @Autowired
  public void setRuleUtilities(IRuleUtilities utils) {
    this.utilities = utils;
  }
  
  @RequestMapping("/route_create_scansun.htm")
  public String createRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "sources", required = false) List<String> sources) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    String emessage = null;
    
    if (adaptors.size() == 0) {
      model.addAttribute("emessage",
          "No adaptors defined, please add one before creating a route.");
      return "redirect:adaptors.htm";
    }
    
    if (name == null && author == null && 
        active == null && description == null &&
        recipients == null && sources == null) {
      return viewCreateRoute(model, name, author, active, description,
          recipients, sources, null);
    }
    
    if (name == null || name.trim().equals("")) {
      emessage = "Name must be specified.";
    } else if (sources == null || sources.size() == 0) {
      emessage = "Must specify at least one source";
    }
    
    if (emessage == null) {
      try {
        boolean bactive = (active == null) ? false : active.booleanValue();
        ScansunRule rule = (ScansunRule)manager.createRule(ScansunRule.TYPE);
        rule.setSources(sources);
        RouteDefinition def = manager.create(name, author, bactive, description, recipients, rule);
        manager.storeDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        t.printStackTrace();
        emessage = "Failed to create definition: '" + t.getMessage() + "'";
      }
    }

    return viewCreateRoute(model, name, author, active, description,
        recipients, sources, emessage);
  }
  
  @RequestMapping("/route_show_scansun.htm")
  public String showRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "sources", required = false) List<String> sources,
      @RequestParam(value = "submitButton", required = false) String operation) {
    RouteDefinition def = manager.getDefinition(name);
    if (def == null) {
      return viewShowRoutes(model, "No route named \"" + name + "\"");
    }
    if (operation != null && operation.equals("Save")) {
      return modifyRoute(model, name, author, active, description, recipients, sources);
    } else if (operation != null && operation.equals("Delete")) {
      try {
        manager.deleteDefinition(name);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        return viewShowRoutes(model, "Failed to delete \"" + name + "\", have you verified that there are no reffering scheduled jobs");
      }
    } else {
      if (def.getRule() instanceof ScansunRule) {
        ScansunRule crule = (ScansunRule)def.getRule();
        return viewShowRoute(model, def.getName(), def.getAuthor(), def.isActive(), def.getDescription(),
            def.getRecipients(), crule.getSources(), null);
      } else {
        return viewShowRoutes(model, "Atempting to show a route definition that not is a scansun rule");
      }
    }
  }
  
  /**
   * Modifies an existing route
   * @param model the model
   * @param name the name of the route definition
   * @param author the author
   * @param active if route is active or not
   * @param description the description of the route
   * @param recipients the recipients
   * @param script the script
   * @return the redirection string
   */
  protected String modifyRoute(
      Model model, 
      String name, 
      String author,
      Boolean active, 
      String description,
      List<String> recipients,
      List<String> sources) {
    List<String> newrecipients = (recipients == null) ? new ArrayList<String>() : recipients;
    List<String> newsources = (sources == null) ? new ArrayList<String>() : sources;
    
    boolean isactive = (active != null) ? active.booleanValue() : false;
    String emessage = null;
    if (newsources.size() <= 0) {
      emessage = "You must specify at least one source.";
    }
    if (emessage == null) {
      try {
        ScansunRule rule = (ScansunRule)manager.createRule(ScansunRule.TYPE);
        rule.setSources(newsources);
        RouteDefinition def = manager.create(name, author, isactive, description, newrecipients, rule);
        manager.updateDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        t.printStackTrace();
        emessage = "Failed to update definition: '" + t.getMessage() + "'";
      }
    }
    
    return viewShowRoute(model, name, author, active, description,
        newrecipients,sources, emessage);
  }
  
  protected String viewShowRoute(
      Model model,
      String name,
      String author,
      Boolean active,
      String description,
      List<String> recipients,
      List<String> sources,
      String emessage) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    
    model.addAttribute("adaptors", adaptors);
    model.addAttribute("sourceids", utilities.getRadarSources());
    
    model.addAttribute("name", (name == null) ? "" : name);
    model.addAttribute("author", (author == null) ? "" : author);
    model.addAttribute("active", (active == null) ? new Boolean(true) : active);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("recipients",
        (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("sources",
        (sources == null) ? new ArrayList<String>() : sources);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "route_show_scansun";
  }
  /**
   * Shows the routes view
   * 
   * @param model
   *          the model
   * @param emessage
   *          the error message if any
   * @return "routes"
   */
  protected String viewShowRoutes(Model model, String emessage) {
    List<RouteDefinition> definitions = manager.getDefinitions();
    model.addAttribute("routes", definitions);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "routes";
  }
  
  protected String viewCreateRoute(Model model, String name, String author,
      Boolean active, String description, List<String> recipients, List<String> sources, String emessage) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    model.addAttribute("sourceids", utilities.getRadarSources());
    model.addAttribute("adaptors", adaptors);
    model.addAttribute("name", (name == null) ? "" : name);
    model.addAttribute("author", (author == null) ? "" : author);
    model.addAttribute("active", (active == null) ? new Boolean(true) : active);
    model.addAttribute("recipients",
        (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("sources",
        (sources == null) ? new ArrayList<String>() : sources);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "route_create_scansun";
  }
}
