/* --------------------------------------------------------------------
Copyright (C) 2009-2010 Swedish Meteorological and Hydrological Institute, SMHI,

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

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.RuleException;
import eu.baltrad.beast.rules.bdb.BdbTrimAgeRule;

/**
 * Manages BdbTrimAgeRule instances
 */
@Controller
public class BdbTrimAgeRoutesController {
  /**
   * The router manager
   */
  private IRouterManager manager = null;

  /**
   * We need a logger here
   */
  private static Logger logger = LogManager.getLogger(BdbTrimAgeRoutesController.class);
  
  /**
   * Set the router manager instance
   */
  @Autowired
  public void setManager(IRouterManager manager) {
    this.manager = manager;
  }

  @RequestMapping("/route_create_bdb_trim_age.htm")
  public String createRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "ageLimit", required = false) Integer ageLimit) {
    logger.debug("createRoute(Model)");
    String emessage = null;

    if (name == null && author == null && active == null &&
        description == null && ageLimit == null) {
      return viewCreateRoute(model, name, author, active, description,
          ageLimit, null);
    }

    if (name == null || name.equals("")) {
      emessage = "Name must be specified.";
    } else if (ageLimit == null || ageLimit <= 0) {
      emessage = "age limit must be specified and greater than 0.";
    }

    if (emessage == null) {
      try {
        boolean bactive = (active == null) ? false : active.booleanValue();
        BdbTrimAgeRule rule = createRule(ageLimit);
        RouteDefinition def = manager.create(name, author, bactive,
            description, new ArrayList<String>(), rule);
        manager.storeDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        emessage = "Failed to create definition: '" + t.getMessage() + "'";
      }
    }
    return viewCreateRoute(model, name, author, active, description,
        ageLimit, emessage);
  }
  
  protected String viewCreateRoute(Model model, String name, String author,
      Boolean active, String description, Integer ageLimit, String emessage) {

    model.addAttribute("name", (name == null) ? "" : name);
    model.addAttribute("author", (author == null) ? "" : author);
    model.addAttribute("active", (active == null) ? new Boolean(true) : active);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("ageLimit", (ageLimit == null) ? 0 : ageLimit);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "route_create_bdb_trim_age";
  }


  @RequestMapping("/route_show_bdb_trim_age.htm")
  public String showRoute(
      Model model,
      @RequestParam(value = "name", required = true) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "ageLimit", required = false) Integer ageLimit,
      @RequestParam(value = "submitButton", required = false) String operation) {
    logger.debug("showRoute(Model)");
    RouteDefinition def = manager.getDefinition(name);
    if (def == null) {
      return viewShowRoutes(model, "No route named \"" + name + "\"");
    }
    if (operation != null && operation.equals("Save")) {
      return modifyRoute(model, name, author, active, description, ageLimit);
    } else if (operation != null && operation.equals("Delete")) {
      try {
        manager.deleteDefinition(name);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        return viewShowRoutes(model, "Failed to delete \"" + name + "\", have you verified that there are no reffering scheduled jobs");
      }
    } else {
      if (def.getRule() instanceof BdbTrimAgeRule) {
        ageLimit = ((BdbTrimAgeRule) def.getRule()).getFileAgeLimit();
      }
      return viewShowRoute(model, def.getName(), def.getAuthor(),
          def.isActive(), def.getDescription(), ageLimit, null);
    }
  }

  protected String viewShowRoutes(Model model, String emessage) {
    logger.debug("showRoutes(Model)");
    List<RouteDefinition> definitions = manager.getDefinitions();
    model.addAttribute("routes", definitions);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "routes";
  }

  protected String viewShowRoute(Model model, String name, String author,
      Boolean active, String description, Integer ageLimit, String emessage) {

    model.addAttribute("name", name == null ? "" : name);
    model.addAttribute("author", author == null ? "" : author);
    model.addAttribute("active", (active == null ? false : active
        .booleanValue()));
    model.addAttribute("description", description == null ? "" : description);
    model.addAttribute("ageLimit", ageLimit == null ? 0 : ageLimit);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "route_show_bdb_trim_age";
  }

  /**
   * Creates a rule.
   * @param ageLimit the age limit on the rule
   * @return a BdbTrimAgeRule
   * @throws RuleException if the rule not could be created
   */
  protected BdbTrimAgeRule createRule(Integer ageLimit) {
    BdbTrimAgeRule rule = (BdbTrimAgeRule)manager.createRule(BdbTrimAgeRule.TYPE);
    rule.setFileAgeLimit(ageLimit);
    return rule;
  }

  /**
   * Modifies an existing route
   * @param model the model
   * @param name the name of the route definition
   * @param author the author
   * @param active if route is active or not
   * @param description the description of the route
   * @param script the script
   * @return the redirection string
   */
  protected String modifyRoute(Model model, String name, String author,
      Boolean active, String description, Integer ageLimit) {
    boolean isactive = (active != null) ? active.booleanValue() : false;
    String emessage = null;
    if (ageLimit == null  || ageLimit <= 0) {
      emessage = "age limit missing.";
    }
    
    if (emessage == null) {
      try {
        BdbTrimAgeRule rule = createRule(ageLimit);
        RouteDefinition def = manager.create(name, author, isactive,
            description, new ArrayList<String>(), rule);
        manager.updateDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        emessage = "Failed to update definition: '" + t.getMessage() + "'";
      }
    }
    
    return viewShowRoute(model, name, author, active, description,
        ageLimit, emessage);
  }
}
