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

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.RuleException;
import eu.baltrad.beast.rules.groovy.GroovyRule;

/**
 * Manages the groovy routes and routing rules.
 * 
 * @author Anders Henja
 */
@Controller
public class GroovyRoutesController {
  /**
   * The router manager
   */
  private IRouterManager manager = null;

  /**
   * The adaptor manager
   */
  private IBltAdaptorManager adaptormanager = null;

  /**
   * We need a logger here
   */
  private static Logger logger = LogManager.getLogger(AdaptorsController.class);

  /**
   * Default constructor
   */
  public GroovyRoutesController() {
  }

  /**
   * Sets the router manager instance
   * 
   * @param manager
   *          the manager
   */
  @Autowired
  public void setManager(IRouterManager manager) {
    this.manager = manager;
  }

  /**
   * Sets the adaptor manager
   * 
   * @param adaptormanager
   */
  @Autowired
  public void setAdaptorManager(IBltAdaptorManager adaptormanager) {
    this.adaptormanager = adaptormanager;
  }

  @RequestMapping("/groovyroute_create.htm")
  public String createRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "typdef", required = false) String typdef) {
    logger.debug("createRoute(Model)");
    String emessage = null;

    // If everything are null, then we probably came here from some menu.
    if (name == null && author == null && active == null && description == null &&
        recipients == null && typdef == null) {
      return viewCreateRoute(model, name, author, active, description,
          recipients, typdef, null);      
    }
    
    if (name == null || name.equals("")) {
      emessage = "Name must be specified.";
    } else if (typdef == null || typdef.trim().equals("")) {
      emessage = "Can not create a groovy rule when script is empty.";
    }
    
    if (emessage == null) {
      try {
        boolean bactive = (active == null) ? false : active.booleanValue();
        GroovyRule rule = createRule(typdef);
        RouteDefinition def = manager.create(name, author, bactive, description, recipients, rule);
        manager.storeDefinition(def);
        return "redirect:showroutes.htm";
      } catch (Throwable t) {
        emessage = "Failed to create definition: '" + t.getMessage() + "'";
        if (t.getCause() != null) {
          emessage += " caused by:\n" + t.getCause().toString();
        }
      }
    }
    return viewCreateRoute(model, name, author, active, description,
        recipients, typdef, emessage);
  }

  @RequestMapping("/groovyroute_show.htm")
  public String showRoute(
      Model model,
      @RequestParam(value = "name", required = true) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "typdef", required = false) String typdef,
      @RequestParam(value = "submitButton", required = false) String operation) {
    logger.debug("showRoute(Model)");
    RouteDefinition def = manager.getDefinition(name);
    if (def == null) {
      return viewShowRoutes(model, "No route named \"" + name + "\"");
    }
    if (operation != null && operation.equals("Modify")) {
      return modifyRoute(model, name, author, active, description, recipients, typdef);
    } else if (operation != null && operation.equals("Delete")) {
      try {
        manager.deleteDefinition(name);
        return "redirect:showroutes.htm";
      } catch (Throwable t) {
        return viewShowRoutes(model, "Failed to delete \"" + name + "\", have you verified that there are no reffering scheduled jobs");
      }
    } else {
      String d = "";
      String emsg = null;
      if (def.getRule() instanceof GroovyRule) {
        GroovyRule r = (GroovyRule)def.getRule();
        d = r.getScript();
        if (r.getState() != GroovyRule.OK) {
          emsg = "Failure in groovy script";
          if (r.getThrowable() != null) {
            emsg += ":" + r.getThrowable().getMessage();
            if (r.getThrowable().getCause() != null) {
              emsg += " caused by " + r.getThrowable().getCause().toString();
            }
          }
        }
      }
      return viewShowRoute(model, def.getName(), def.getAuthor(), def
          .isActive(), def.getDescription(), def.getRecipients(), d, emsg);
    }
  }

  /**
   * Shows the createroute view.
   * 
   * @param model
   *          the model
   * @param name
   *          the name
   * @param author
   *          the author
   * @param active
   *          if active or not
   * @param description
   *          the description
   * @param recipients
   *          the recipients
   * @param definition
   *          the definition
   * @param emessage
   *          an error message
   * @return "showroute"
   */
  protected String viewCreateRoute(Model model, String name, String author,
      Boolean active, String description, List<String> recipients,
      String definition, String emessage) {
    if (active == null)
      active = new Boolean(true);
    if (recipients == null)
      recipients = new ArrayList<String>();
    
    model.addAttribute("adaptors", adaptormanager.getAdaptorNames());
    RouteDefinition def = manager.create(name, author, active, description, recipients, null);
    model.addAttribute("route", def);
    model.addAttribute("emessage", emessage);
    model.addAttribute("typdef", definition);

    return "groovyroute_create";
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
    logger.debug("showRoutes(Model)");
    List<RouteDefinition> definitions = manager.getDefinitions();
    model.addAttribute("routes", definitions);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "showroutes";
  }

  /**
   * Shows the showroute view.
   * 
   * @param model
   *          the model
   * @param name
   *          the name
   * @param author
   *          the author
   * @param active
   *          if active or not
   * @param description
   *          the description
   * @param recipients
   *          the recipients
   * @param definition
   *          the definition
   * @param emessage
   *          an error message
   * @return "showroute"
   */
  protected String viewShowRoute(Model model, String name, String author,
      Boolean active, String description, List<String> recipients,
      String definition, String emessage) {
    if (active == null)
      active = new Boolean(false);
    if (recipients == null)
      recipients = new ArrayList<String>();

    model.addAttribute("adaptors", adaptormanager.getAdaptorNames());
    RouteDefinition def = manager.create(name, author, active, description, recipients, null);
    model.addAttribute("route", def);
    model.addAttribute("definition", definition);
    model.addAttribute("emessage", emessage);

    return "groovyroute_show";
  }
  
  /**
   * Creates a rule.
   * @param script the groovy script
   * @return a groovy rule
   * @throws RuleException if groovy rule not could be created from script
   */
  protected GroovyRule createRule(String script) {
    GroovyRule rule = (GroovyRule)manager.createRule(GroovyRule.TYPE);
    rule.setScript(script);
    return rule;
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
  protected String modifyRoute(Model model, String name, String author,
      Boolean active, String description, List<String> recipients, String script) {
    List<String> newrecipients = recipients == null ? new ArrayList<String>() : recipients;
    boolean isactive = (active != null) ? active.booleanValue() : false;
    String emessage = null;
    if (script == null || script.trim().equals("")) {
      emessage = "Definition missing.";
    }
    
    if (emessage == null) {
      try {
        logger.debug("Creating rule");
        GroovyRule rule = createRule(script);
        logger.debug("Rule created");
        RouteDefinition def = manager.create(name, author, isactive, description,
            newrecipients, rule);
        manager.updateDefinition(def);
        return "redirect:showroutes.htm";
      } catch (Throwable t) {
        emessage = "Failed to update definition: '" + t.getMessage() + "'";
        if (t.getCause() != null) {
          emessage += " caused by:\n" + t.getCause().toString();
        }
        logger.debug("Failure during rule modification", t);
      }
    }
    
    return viewShowRoute(model, name, author, active, description,
        newrecipients, script, emessage);
  }
}
