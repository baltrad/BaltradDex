/* --------------------------------------------------------------------
Copyright (C) 2009-2012 Swedish Meteorological and Hydrological Institute, SMHI,

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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.pgf.IPgfClientHelper;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.RuleException;
import eu.baltrad.beast.rules.gmap.GoogleMapRule;

/**
 * Manages the google maps routes and routing rules.
 * @author Anders Henja
 */
@Controller
public class GoogleMapRoutesController {
  /**
   * The router manager
   */
  private IRouterManager manager = null;

  /**
   * The adaptor manager
   */
  private IBltAdaptorManager adaptormanager = null;

  /**
   * The pgf client helper
   */
  private IPgfClientHelper pgfClientHelper = null;

  /**
   * The translator from string to json and vice versa
   */
  private ObjectMapper jsonMapper = new ObjectMapper();

  
  /**
   * We need a logger here
   */
  private static Logger logger = LogManager.getLogger(GoogleMapRoutesController.class);

  /**
   * Default constructor
   */
  public GoogleMapRoutesController() {
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

  /**
   * @param pgfClientHelper the pgf client helpler
   */
  @Autowired
  public void setPgfClientHelper(IPgfClientHelper pgfClientHelper) {
    this.pgfClientHelper = pgfClientHelper;
  }
  
  /**
   * Invoked when creating a route.
   * @param model the model
   * @param name the name of the route to create
   * @param author the author
   * @param active if active or not
   * @param description the description
   * @param recipients at least one
   * @param area the area identifier
   * @param path the path
   * @return
   */
  @RequestMapping("/route_create_google_map.htm")
  public String createRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "area", required = false) String area,
      @RequestParam(value = "areapath", required = false) Boolean useAreaInPath,
      @RequestParam(value = "path", required = false) String path,
      @RequestParam(value = "filterJson", required=false) String filterJson) {
    logger.debug("createRoute(Model)");
    String emessage = null;

    // If everything are null, then we probably came here from some menu.
    if (name == null && author == null && active == null && description == null &&
        recipients == null && area == null && useAreaInPath == null && path == null) {
      return viewCreateRoute(model, name, author, active, description,
          recipients, area, Boolean.TRUE, path, filterJson, null);      
    }
    
    if (name == null || name.equals("")) {
      emessage = "Name must be specified.";
    } else if (area == null || area.trim().equals("")) {
      emessage = "Can not create a google map rule without an area identifier.";
    } else if (recipients == null || recipients.size() == 0) {
      emessage = "Must at least specify one recipient";
    }
    
    if (emessage == null) {
      try {
        boolean bactive = (active == null) ? false : active.booleanValue();
        boolean buseAreaInPath = (useAreaInPath == null) ? false : useAreaInPath.booleanValue();
        GoogleMapRule rule = createRule(area, buseAreaInPath, path, filterJson);
        RouteDefinition def = manager.create(name, author, bactive, description, recipients, rule);
        manager.storeDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        emessage = "Failed to create definition: '" + t.getMessage() + "'";
        if (t.getCause() != null) {
          emessage += " caused by:\n" + t.getCause().toString();
        }
      }
    }
    return viewCreateRoute(model, name, author, active, description,
        recipients, area, useAreaInPath, path, filterJson, emessage);
  }
  
  /**
   * Initiated when showing a route
   * @param model
   * @param name
   * @param author
   * @param active
   * @param description
   * @param recipients
   * @param area
   * @param path
   * @param operation
   * @return
   */
  @RequestMapping("/route_show_google_map.htm")
  public String showRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "area", required = false) String area,
      @RequestParam(value = "areapath", required = false) Boolean areapath,
      @RequestParam(value = "path", required = false) String path,
      @RequestParam(value = "filterJson", required = false) String filterJson,
      @RequestParam(value = "submitButton", required = false) String operation) {
    logger.debug("showRoute(Model)");

    RouteDefinition def = manager.getDefinition(name);
    if (def == null) {
      return viewShowRoutes(model, "No route named \"" + name + "\"");
    }
    
    if (operation != null && operation.equals("Save")) {
      return modifyRoute(model, name, author, active, description, recipients, area, areapath, path, filterJson);
    } else if (operation != null && operation.equals("Delete")) {
      try {
        manager.deleteDefinition(name);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        return viewShowRoutes(model, "Failed to delete \"" + name + "\", have you verified that there are no reffering scheduled jobs");
      }
    } else {
      if (def.getRule() instanceof GoogleMapRule) {
        GoogleMapRule vrule = (GoogleMapRule)def.getRule();
        String filterstr = null;
        if (vrule.getFilter() != null) {
          try {
            filterstr = jsonMapper.writeValueAsString(vrule.getFilter());
          } catch (IOException e) {
            logger.error("failed to create JSON string from filter", e);
          }
        }        
        return viewShowRoute(model, def.getName(), def.getAuthor(), def.isActive(), def.getDescription(),
            def.getRecipients(), vrule.getArea(), vrule.isUseAreaInPath(), vrule.getPath(), filterstr, null); 
      } else {
        return viewShowRoutes(model, "Atempting to show a route definition that not is a google map rule");
      }
    }
  }


  /**
   * Modifies the route if possible 
   * @param model the model
   * @param name the name of the route
   * @param author the author
   * @param active if active or not
   * @param description the description
   * @param recipients the recipients
   * @param area the area
   * @param path the path
   * @return html page directive
   */
  protected String modifyRoute(Model model, String name, String author,
      Boolean active, String description, List<String> recipients, String area,
      Boolean useAreaInPath, String path, String filterJson) {
    List<String> newrecipients = (recipients == null) ? new ArrayList<String>() : recipients;
    boolean isactive = (active != null) ? active.booleanValue() : false;
    boolean buseAreaInPath = (useAreaInPath != null) ? useAreaInPath.booleanValue() : false;
    String emessage = null;
    
    if (newrecipients.size() <= 0) {
      emessage = "You must specify at least one recipient.";
    } else if (area == null || area.trim().equals("")) {
      emessage = "Can not create a google map rule without an area identifier.";
    }
    
    if (emessage == null) {
      try {
        GoogleMapRule rule = createRule(area, buseAreaInPath, path, filterJson);
        RouteDefinition def = manager.create(name, author, isactive, description, newrecipients, rule);
        manager.updateDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        t.printStackTrace();
        emessage = "Failed to update definition: '" + t.getMessage() + "'";
      }
    }
    return viewShowRoute(model, name, author, active, description,
        newrecipients, area, useAreaInPath, path, filterJson, emessage);
  }
  
  /**
   * Creates a rule.
   * @param area the area id
   * @param path the path
   * @return a google map rule
   * @throws RuleException if the google map rule not could be created
   */
  protected GoogleMapRule createRule(String area, boolean useAreaInPath, String path, String jsonFilter) {
    GoogleMapRule rule = (GoogleMapRule)manager.createRule(GoogleMapRule.TYPE);
    rule.setArea(area);
    rule.setPath(path);
    rule.setUseAreaInPath(useAreaInPath);
    if (jsonFilter != null && !jsonFilter.equals("")) {
      try {
        rule.setFilter(jsonMapper.readValue(jsonFilter, IFilter.class));
      } catch (Exception e) {
        logger.error("Failed to translate json to filter", e);
      }
    }        
    return rule;
  }
  
  /**
   * Shows the routes view
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
  
  /**
   * Setups the google map create view
   * @param model the model
   * @param name the name of the rule
   * @param author the authoer
   * @param active if active or not
   * @param description description about the rule
   * @param recipients the recipients
   * @param area the area identifier
   * @param path the path
   * @param emessage
   * @return googlemaproute_create
   */
  protected String viewCreateRoute(Model model, String name, String author,
      Boolean active, String description, List<String> recipients,
      String area, Boolean useAreaInPath, String path, String jsonFilter, String emessage) {
    if (active == null)
      active = Boolean.TRUE;
    if (recipients == null)
      recipients = new ArrayList<String>();
    
    model.addAttribute("adaptors", adaptormanager.getAdaptorNames());
    model.addAttribute("name", (name == null)?"":name);
    model.addAttribute("author", (author == null)?"":author);
    model.addAttribute("active", (active == null)?Boolean.TRUE:active);
    model.addAttribute("recipients", (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("arealist", pgfClientHelper.getUniqueAreaIds());    
    model.addAttribute("area", (area == null)?"":area);
    model.addAttribute("areapath", (useAreaInPath == null)?Boolean.TRUE:useAreaInPath);
    model.addAttribute("path", (path == null)?"":path);
    model.addAttribute("filterJson", jsonFilter);

    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    
    return "route_create_google_map";
  }

  /**
   * The view for showing a gmap route rule
   * @param model the model
   * @param name the name of the rule
   * @param author the authoer
   * @param active if active or not
   * @param description description about the rule
   * @param recipients the recipients
   * @param area the area identifier
   * @param path the path
   * @param emessage
   * @return googlemaproute_show
   */
  protected String viewShowRoute(Model model, String name, String author,
      Boolean active, String description, List<String> recipients,
      String area, Boolean useAreaInPath, String path, String jsonFilter, String emessage) {
    
    if (active == null)
      active = Boolean.TRUE;
    if (recipients == null)
      recipients = new ArrayList<String>();
    
    model.addAttribute("adaptors", adaptormanager.getAdaptorNames());
    model.addAttribute("name", (name == null)?"":name);
    model.addAttribute("author", (author == null)?"":author);
    model.addAttribute("active", (active == null)?Boolean.TRUE:active);
    model.addAttribute("recipients", (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("arealist", pgfClientHelper.getUniqueAreaIds());    
    model.addAttribute("area", (area == null)?"":area);
    model.addAttribute("areapath", (useAreaInPath == null)?Boolean.TRUE:useAreaInPath);
    model.addAttribute("path", (path == null)?"":path);
    model.addAttribute("filterJson", jsonFilter);

    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    
    return "route_show_google_map";
  }
}
