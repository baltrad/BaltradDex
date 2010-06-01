/* --------------------------------------------------------------------
Copyright (C) 2009-2010 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the beast-ui package.

The beast-ui package is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The beast-ui package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the beast-ui package library.  If not, see <http://www.gnu.org/licenses/>.
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
import eu.baltrad.beast.rules.IRule;
import eu.baltrad.beast.rules.IRuleFactory;

/**
 * Manages the routes and routing rules.
 * @author Anders Henja
 */
@Controller
public class RoutesController {
  /**
   * The router manager
   */
  private IRouterManager manager = null;
  
  /**
   * The rule factory
   */
  private IRuleFactory factory = null;
  
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
  public RoutesController() {
  }
  
  /**
   * Sets the router manager instance
   * @param manager the manager
   */
  @Autowired
  public void setManager(IRouterManager manager) {
    this.manager = manager;
  }
  
  /**
   * Sets the rule factory
   * @param factory the rule factory
   */
  @Autowired
  public void setRuleFactory(IRuleFactory factory) {
    this.factory = factory;
  }
  
  /**
   * Sets the adaptor manager
   * @param adaptormanager
   */
  @Autowired
  public void setAdaptorManager(IBltAdaptorManager adaptormanager) {
    this.adaptormanager = adaptormanager;
  }
  
  @RequestMapping("/routes.htm")
  public String showRoutes(Model model) {
    logger.debug("showRoutes(Model)");
    List<RouteDefinition> definitions = manager.getDefinitions();
    model.addAttribute("routes", definitions);
    return "routes";
  }
  
  @RequestMapping("/createroute.htm")
  public String createRoute(Model model,
      @RequestParam(value="name", required=false) String name,
      @RequestParam(value="author", required=false) String author,
      @RequestParam(value="active", required=false) Boolean active,
      @RequestParam(value="description", required=false) String description,
      @RequestParam(value="recipients", required=false) List<String> recipients,
      @RequestParam(value="type", required=false) String type,
      @RequestParam(value="typdef", required=false) String typdef) {
    logger.debug("createRoute(Model)");
    List<String> types = factory.getTypes();
    List<String> adaptors = adaptormanager.getAdaptorNames();
    if (adaptors.size() == 0) {
      model.addAttribute("emessage", "No adaptors defined, please add one before creating a route.");
      return "redirect:adaptors.htm";
    }
    if (types.size() == 0) {
      model.addAttribute("emessage", "No types defined, configuration error, please contact your administrator.");
      return "routes";
    }

    if (name != null || author != null || active != null ||
        description != null || recipients != null || type != null || typdef != null) {
      String emessage = null;
      if (name == null || name.equals("")) {
        emessage = "Name must be specified.";
      } else if (typdef == null || typdef.trim().equals("")) {
        emessage = "Definition must be specified.";
      }
      if (emessage != null) {
        return viewCreateRoute(model, name, author, active, description, recipients, type, typdef, emessage);
      } else {
        try {
          boolean bactive = (active == null)?false:active.booleanValue();
          IRule rule = factory.create(type, typdef);
          RouteDefinition def = manager.create(name, author, bactive, description, recipients, rule);
          manager.storeDefinition(def);
          return "redirect:routes.htm";
        } catch (Throwable t) {
          return viewCreateRoute(model, name, author, active, description, recipients, type, typdef, "Failed to create definition: '"+t.getMessage()+"'");
        }
      }
    }
    
    return viewCreateRoute(model, name, author, active, description, recipients, type, typdef, null);
  }

  @RequestMapping("/showroute.htm")
  public String showRoute(Model model,
      @RequestParam(value="name", required=true) String name,
      @RequestParam(value="author", required=false) String author,
      @RequestParam(value="active", required=false) Boolean active,
      @RequestParam(value="description", required=false) String description,
      @RequestParam(value="recipients", required=false) List<String> recipients,
      @RequestParam(value="type", required=false) String type,
      @RequestParam(value="typdef", required=false) String typdef,
      @RequestParam(value="submitButton", required=false) String operation) {
    logger.debug("showRoute(Model)");
    
    RouteDefinition def = manager.getDefinition(name);
    if (def != null) {
      if (operation != null && operation.equals("Modify")) {
        List<String> newrecipients = recipients == null?new ArrayList<String>():recipients;
        boolean isactive = (active != null)?active.booleanValue():false;
        if (typdef == null || typdef.trim().equals("")) {
          return viewShowRoute(model, name, author, active, description, newrecipients, type, typdef, "Definition missing.");
        } else {
          try {
            IRule rule = factory.create(type, typdef);
            def = manager.create(name, author, isactive, description, newrecipients, rule);
            manager.updateDefinition(def);
            return "redirect:routes.htm";
          } catch (Throwable t) {
            return viewShowRoute(model, name, author, active, description, newrecipients, type, typdef, "Failed to update definition: '"+t.getMessage()+"'");
          }
        }
      } else if (operation != null && operation.equals("Delete")) {
        try {
          manager.deleteDefinition(name);
          return "redirect:routes.htm";
        } catch (Throwable t) {
          return viewShowRoutes(model, "Failed to delete \"" + name + "\"");
        }
      } else {
        return viewShowRoute(model, def.getName(), def.getAuthor(), def.isActive(), def.getDescription(), def.getRecipients(), def.getRule().getType(), def.getRule().getDefinition(), null);
      }
    } else {
      return viewShowRoutes(model, "No route named \"" + name + "\"");
    }
  }
  
  /**
   * Shows the createroute view.
   * @param model the model
   * @param name the name
   * @param author the author
   * @param active if active or not
   * @param description the description
   * @param recipients the recipients
   * @param type the definition type
   * @param definition the definition
   * @param emessage an error message
   * @return "showroute"
   */
  protected String viewCreateRoute(Model model,
      String name,
      String author,
      Boolean active,
      String description,
      List<String> recipients,
      String type,
      String definition,
      String emessage) {
    List<String> types = factory.getTypes();
    List<String> adaptors = adaptormanager.getAdaptorNames();
    
    model.addAttribute("types", types);
    model.addAttribute("adaptors", adaptors);
    model.addAttribute("name", (name == null)?"":name);
    model.addAttribute("author", (author == null)?"":author);
    model.addAttribute("active", (active == null)?new Boolean(true):active);
    model.addAttribute("description", (description == null)?"":description);
    model.addAttribute("recipients", (recipients == null)?new ArrayList<String>():recipients);
    model.addAttribute("type", (type == null)?"":type);
    model.addAttribute("typdef", (definition == null)?"":definition);
    if (emessage != null) {
      model.addAttribute("emessage",emessage);
    }
    return "createroute";
  }

  
  /**
   * Shows the routes view
   * @param model the model
   * @param emessage the error message if any
   * @return "routes"
   */
  protected String viewShowRoutes(Model model,
      String emessage){
    logger.debug("showRoutes(Model)");
    List<RouteDefinition> definitions = manager.getDefinitions();
    model.addAttribute("routes", definitions);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "routes";   
  }
  
  /**
   * Shows the showroute view.
   * @param model the model
   * @param name the name
   * @param author the author
   * @param active if active or not
   * @param description the description
   * @param recipients the recipients
   * @param type the definition type
   * @param definition the definition
   * @param emessage an error message
   * @return "showroute"
   */
  protected String viewShowRoute(Model model,
      String name,
      String author,
      Boolean active,
      String description,
      List<String> recipients,
      String type,
      String definition,
      String emessage) {
    List<String> types = factory.getTypes();
    List<String> adaptors = adaptormanager.getAdaptorNames();

    model.addAttribute("types", types);
    model.addAttribute("adaptors", adaptors);
    model.addAttribute("name",name==null?"":name);
    model.addAttribute("author",author==null?"":author);
    model.addAttribute("active",(active==null?false:active.booleanValue()));
    model.addAttribute("description",description==null?"":description);
    model.addAttribute("recipients",recipients==null?new ArrayList<String>():recipients);
    model.addAttribute("type",type==null?"":type);
    model.addAttribute("definition",definition==null?"":definition);
    if (emessage != null) {
      model.addAttribute("emessage",emessage);
    }
    return "showroute";
  }
}
