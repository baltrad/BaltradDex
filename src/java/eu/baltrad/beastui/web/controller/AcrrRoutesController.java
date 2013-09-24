/* --------------------------------------------------------------------
Copyright (C) 2009-2013 Swedish Meteorological and Hydrological Institute, SMHI,

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
import eu.baltrad.beast.rules.acrr.AcrrRule;

/**
 * Manages the acrr routes and routing rules.
 * 
 * @author Anders Henja
 */
@Controller
public class AcrrRoutesController {
  /**
   * The router manager
   */
  private IRouterManager manager = null;

  /**
   * The adaptor manager
   */
  private IBltAdaptorManager adaptormanager = null;

  /**
   * The logger
   */
  private static Logger logger = LogManager.getLogger(AcrrRoutesController.class);
  
  /**
   * Default constructor
   */
  public AcrrRoutesController() {
    
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
  
  @RequestMapping("/route_create_acrr.htm")
  public String createRoute(
    Model model,
    @RequestParam(value = "name", required = false) String name,
    @RequestParam(value = "author", required = false) String author,
    @RequestParam(value = "active", required = false) Boolean active,
    @RequestParam(value = "description", required = false) String description,
    @RequestParam(value = "recipients", required = false) List<String> recipients,
    @RequestParam(value = "area", required = false) String area,
    @RequestParam(value = "object_type", required = false) String object_type,
    @RequestParam(value = "quantity", required = false) String quantity,
    @RequestParam(value = "hours", required = false) Integer hours,
    @RequestParam(value = "filesPerHour", required = false) Integer filesPerHour,
    @RequestParam(value = "acceptableLoss", required = false) Integer acceptableLoss,
    @RequestParam(value = "distanceField", required = false) String distanceField,
    @RequestParam(value = "zrA", required = false) Double zrA,
    @RequestParam(value = "zrB", required = false) Double zrB) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    String emessage = null;
    
    if (adaptors.size() == 0) {
      model.addAttribute("emessage",
          "No adaptors defined, please add one before creating a route.");
      return "redirect:adaptors.htm";
    }
    
    if (recipients != null) {
      for (String r : recipients) {
        logger.info("CHOOSEN RECIPIENT: " + r);
      }
    }
    
    if (name == null && author == null && active == null && description == null &&
        recipients == null && area == null && object_type == null && quantity == null &&
        hours == null && filesPerHour == null && acceptableLoss == null && distanceField == null &&
        zrA == null && zrB == null) {
      return viewCreateRoute(model, name, author, active, description,
          recipients, area, object_type, quantity, hours, filesPerHour, acceptableLoss,
          distanceField, zrA, zrB, null);
    }
    
    if (name == null || name.trim().equals("")) {
      emessage = "Name must be specified.";
    } else if (area == null || area.trim().equals("")) {
      emessage = "Area must be specified.";
    }
    
    if (emessage == null) {
      try {
        boolean bactive = (active == null) ? false : active.booleanValue();
        int ihours = (hours == null) ? 24 : hours.intValue();
        int ifilesPerHour = (filesPerHour == null) ? 4 : filesPerHour.intValue();
        int iacceptableLoss = (acceptableLoss == null) ? 0 : acceptableLoss.intValue();
        double dzra = (zrA == null) ? 200.0 : zrA.doubleValue();
        double dzrb = (zrB == null) ? 1.6 : zrB.doubleValue();
        AcrrRule rule = createRule(area, object_type, quantity, ihours, ifilesPerHour, iacceptableLoss, distanceField, dzra, dzrb);
        RouteDefinition def = manager.create(name, author, bactive, description, recipients, rule);
        manager.storeDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        t.printStackTrace();
        emessage = "Failed to create definition: '" + t.getMessage() + "'";
      }
    }

    return viewCreateRoute(model, name, author, active, description,
        recipients, area, object_type, quantity, hours, filesPerHour, acceptableLoss,
        distanceField, zrA, zrB, emessage);
  }
  
  /**
   * Handles presentation, deletion and modification of an ACRR route
   */
  @RequestMapping("/route_show_acrr.htm")
  public String showRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "area", required = false) String area,
      @RequestParam(value = "object_type", required = false) String object_type,
      @RequestParam(value = "quantity", required = false) String quantity,
      @RequestParam(value = "hours", required = false) Integer hours,
      @RequestParam(value = "filesPerHour", required = false) Integer filesPerHour,
      @RequestParam(value = "acceptableLoss", required = false) Integer acceptableLoss,
      @RequestParam(value = "distanceField", required = false) String distanceField,
      @RequestParam(value = "zrA", required = false) Double zrA,
      @RequestParam(value = "zrB", required = false) Double zrB,
      @RequestParam(value = "submitButton", required = false) String operation) {
    logger.debug("showRoute(Model)");

    RouteDefinition def = manager.getDefinition(name);
    if (def == null) {
      return viewShowRoutes(model, "No route named \"" + name + "\"");
    }
    
    if (operation != null && operation.equals("Save")) {
      //
      return modifyRoute(model, name, author, active, description, recipients, area, object_type, quantity, hours, filesPerHour, acceptableLoss, distanceField, zrA, zrB);
    } else if (operation != null && operation.equals("Delete")) {
      try {
        manager.deleteDefinition(name);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        return viewShowRoutes(model, "Failed to delete \"" + name + "\", have you verified that there are no reffering scheduled jobs");
      }
    } else {
      if (def.getRule() instanceof AcrrRule) {
        AcrrRule vrule = (AcrrRule)def.getRule();
        return viewShowRoute(model, def.getName(), def.getAuthor(), def.isActive(), def.getDescription(),
            def.getRecipients(), vrule.getArea(), vrule.getObjectType(), vrule.getQuantity(),
            vrule.getHours(), vrule.getFilesPerHour(), vrule.getAcceptableLoss(),
            vrule.getDistancefield(), vrule.getZrA(), vrule.getZrB(), null); 
      } else {
        return viewShowRoutes(model, "Atempting to show a route definition that not is an acrr rule");
      }
    }
  }
  
  /**
   * Executes the actual update (modification) of the ACRR rule.
   */
  protected String modifyRoute(
      Model model,
      String name,
      String author,
      Boolean active,
      String description,
      List<String> recipients,
      String area,
      String object_type,
      String quantity,
      Integer hours,
      Integer filesPerHour,
      Integer acceptableLoss,
      String distanceField,
      Double zrA,
      Double zrB) {
    List<String> newrecipients = (recipients == null) ? new ArrayList<String>() : recipients;
    boolean isactive = (active != null) ? active.booleanValue() : false;
    String emessage = null;
    
    if (area == null || area.trim().equals("")) {
      emessage = "Can not create an acrr rule without an area identifier.";
    } 
    if (emessage == null) {
      try {
        AcrrRule rule = createRule(area, object_type, quantity, hours, filesPerHour, acceptableLoss, distanceField, zrA, zrB);
        RouteDefinition def = manager.create(name, author, isactive, description, newrecipients, rule);
        manager.updateDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        t.printStackTrace();
        emessage = "Failed to update definition: '" + t.getMessage() + "'";
      }
    }
    return viewShowRoute(model, name, author, isactive, description,
        newrecipients, area, object_type, quantity,
        hours, filesPerHour, acceptableLoss,
        distanceField, zrA, zrB, emessage); 
  }
  
  /**
   * Sets the model and returns the jsp-page to show for the create acrr route page
   */
  protected String viewCreateRoute(Model model,
      String name, 
      String author, 
      Boolean active, 
      String description,
      List<String> recipients, 
      String area, 
      String object_type, 
      String quantity, 
      Integer hours, 
      Integer filesPerHour, 
      Integer acceptableLoss,
      String distanceField, 
      Double zrA, 
      Double zrB,
      String emessage) {
    return viewRoute(model, name, author, active, description,
        recipients, area, object_type, quantity, hours, filesPerHour,
        acceptableLoss, distanceField, zrA, zrB, emessage, "route_create_acrr");
  }

  /**
   * Sets the model and returns the jsp-page to show for the show acrr route page
   */
  protected String viewShowRoute(Model model,
      String name, 
      String author, 
      Boolean active, 
      String description,
      List<String> recipients, 
      String area, 
      String object_type, 
      String quantity, 
      Integer hours, 
      Integer filesPerHour, 
      Integer acceptableLoss,
      String distanceField, 
      Double zrA, 
      Double zrB,
      String emessage) {
    return viewRoute(model, name, author, active, description,
        recipients, area, object_type, quantity, hours, filesPerHour,
        acceptableLoss, distanceField, zrA, zrB, emessage, "route_show_acrr");
  }
  
  /**
   * Sets the model with provided attributes
   */
  protected String viewRoute(Model model, 
      String name, 
      String author, 
      Boolean active, 
      String description,
      List<String> recipients, 
      String area, 
      String object_type, 
      String quantity, 
      Integer hours, 
      Integer filesPerHour, 
      Integer acceptableLoss,
      String distanceField, 
      Double zrA, 
      Double zrB,
      String emessage,
      String jsppagename) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    List<String> objectTypes = new ArrayList<String>();
    objectTypes.add("IMAGE");
    objectTypes.add("COMP");
    
    model.addAttribute("adaptors", adaptors);
    model.addAttribute("object_types", objectTypes);
    model.addAttribute("name", (name == null) ? "" : name);
    model.addAttribute("author", (author == null) ? "" : author);
    model.addAttribute("active", (active == null) ? new Boolean(true) : active);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("recipients",
        (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("area", (area == null)? "" : area);
    model.addAttribute("object_type", (object_type == null)? "IMAGE" : object_type);
    model.addAttribute("quantity", (quantity == null) ? "DBZH" : quantity);
    model.addAttribute("hours", (hours == null) ? 24 : hours);
    model.addAttribute("filesPerHour", (filesPerHour == null) ? 4 : filesPerHour);
    model.addAttribute("acceptableLoss", (acceptableLoss == null) ? 0 : acceptableLoss);
    model.addAttribute("distanceField", (distanceField == null) ? "eu.baltrad.composite.quality.distance.radar" : distanceField);
    model.addAttribute("zrA", (zrA == null) ? 200.0 : zrA);
    model.addAttribute("zrB", (zrB == null) ? 1.6 : zrB);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    
    return jsppagename;
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
   * Creates an ACRR rule
   */
  protected AcrrRule createRule(
      String area, 
      String object_type, 
      String quantity, 
      int hours, 
      int filesPerHour, 
      int acceptableLoss, 
      String distanceField, 
      double zrA, 
      double zrB) {
    AcrrRule rule = (AcrrRule)manager.createRule(AcrrRule.TYPE);
    rule.setArea(area);
    rule.setObjectType(object_type);
    rule.setQuantity(quantity);
    rule.setHours(hours);
    rule.setFilesPerHour(filesPerHour);
    rule.setAcceptableLoss(acceptableLoss);
    rule.setDistancefield(distanceField);
    rule.setZrA(zrA);
    rule.setZrB(zrB);
    return rule;
  }
}
