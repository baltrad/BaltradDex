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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.qc.IAnomalyDetectorManager;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.util.IRuleUtilities;
import eu.baltrad.beast.rules.volume.VolumeRule;

/**
 * Manages the volume routes and routing rules. This controller manages
 * the possibility to specify what scans should be included in a volume.
 * @author Anders Henja
 * @date 2011-01-05
 */
@Controller
public class VolumeRoutesController {
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
   * The anomaly detector manager
   */
  private IAnomalyDetectorManager anomalymanager = null;
  
  /**
   * Default constructor
   */
  public VolumeRoutesController() {
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
   * @param utils the rule utilities
   */
  @Autowired
  public void setRuleUtilities(IRuleUtilities utils) {
    this.utilities = utils;
  }
  
  /**
   * @param anomalymanager the anomaly manager to set
   */
  @Autowired
  public void setAnomalyDetectorManager(IAnomalyDetectorManager anomalymanager) {
    this.anomalymanager = anomalymanager;
  }
  
  /**
   * Handles create route requests 
   * @param model the model
   * @param name the name of the route
   * @param author the author
   * @param active if route is active or not
   * @param description the description of this route
   * @param ascending if it is ascending or descending elevation angles
   * @param mine the minimum elevation angle
   * @param maxe the maximum elevation angle
   * @param recipients the recipients
   * @param interval the interval
   * @param timeout the timeout
   * @param sources the sources this rule should affect
   * @return a jsp page string or redirect
   */
  @RequestMapping("/route_create_volume.htm")
  public String createRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "ascending", required = false) Boolean ascending,
      @RequestParam(value = "mine", required = false) Double mine,
      @RequestParam(value = "maxe", required = false) Double maxe,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "interval", required = false) Integer interval,
      @RequestParam(value = "timeout", required = false) Integer timeout,
      @RequestParam(value = "sources", required = false) List<String> sources,
      @RequestParam(value = "detectors", required = false) List<String> detectors) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    String emessage = null;
    
    if (adaptors.size() == 0) {
      model.addAttribute("emessage",
          "No adaptors defined, please add one before creating a route.");
      return "redirect:adaptors.htm";
    }
    
    if (name == null && author == null && active == null && description == null &&
        recipients == null && interval == null && timeout == null &&
        sources == null && detectors == null) {
      return viewCreateRoute(model, name, author, active, description,
          ascending, mine, maxe, recipients, interval, timeout, sources, detectors, null);
    }
    
    if (name == null || name.trim().equals("")) {
      emessage = "Name must be specified.";
    } else if (sources == null || sources.size() <= 0) {
      emessage = "Must specify at least one source.";
    } else if (recipients == null || recipients.size() <= 0) {
      emessage = "You must specify at least one recipient";
    }
    
    if (emessage == null) {
      try {
        boolean bactive = (active == null) ? false : active.booleanValue();
        boolean bascending = (ascending == null) ? true : ascending.booleanValue();
        double dmine = (mine == null) ? -90.0 : mine.doubleValue();
        double dmaxe = (maxe == null) ? 90.0 : maxe.doubleValue();
        int iinterval = (interval == null) ? 15 : interval.intValue();
        int itimeout = (timeout == null) ? 15*60 : timeout.intValue();
        VolumeRule rule = createRule(bascending, dmine, dmaxe, iinterval, sources, detectors, itimeout);
        List<String> recip = (recipients == null) ? new ArrayList<String>() : recipients;
        RouteDefinition def = manager.create(name, author, bactive, description, recip, rule);
        manager.storeDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        t.printStackTrace();
        emessage = "Failed to create definition: '" + t.getMessage() + "'";
      }
    }
    
    return viewCreateRoute(model, name, author, active, description,
        ascending, mine, maxe, recipients, interval, timeout, sources, detectors, emessage);
  }

  /**
   * Supports modification of a routing rule
   * @param model the model
   * @param name the name of the route
   * @param author the author
   * @param active if route is active or not
   * @param description the description of this route
   * @param ascending if it is ascending or descending elevation angles
   * @param mine the minimum elevation angle
   * @param maxe the maximum elevation angle
   * @param recipients the recipients
   * @param interval the interval
   * @param timeout the timeout
   * @param sources the sources this rule should affect
   * @return a jsp page string or redirect
   */
  @RequestMapping("/route_show_volume.htm")
  public String showRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "ascending", required = false) Boolean ascending,
      @RequestParam(value = "mine", required = false) Double mine,
      @RequestParam(value = "maxe", required = false) Double maxe,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "interval", required = false) Integer interval,
      @RequestParam(value = "timeout", required = false) Integer timeout,
      @RequestParam(value = "sources", required = false) List<String> sources,
      @RequestParam(value = "detectors", required = false) List<String> detectors,
      @RequestParam(value = "submitButton", required = false) String operation) {
    RouteDefinition def = manager.getDefinition(name);
    if (def == null) {
      return viewShowRoutes(model, "No route named \"" + name + "\"");
    }
    if (operation != null && operation.equals("Save")) {
      return modifyRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources, detectors);
    } else if (operation != null && operation.equals("Delete")) {
      try {
        manager.deleteDefinition(name);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        return viewShowRoutes(model, "Failed to delete \"" + name + "\", have you verified that there are no reffering scheduled jobs");
      }
    } else {
      if (def.getRule() instanceof VolumeRule) {
        VolumeRule vrule = (VolumeRule)def.getRule();
        return viewShowRoute(model, def.getName(), def.getAuthor(), def.isActive(), def.getDescription(),
            vrule.isAscending(), vrule.getElevationMin(), vrule.getElevationMax(), def.getRecipients(), 
            vrule.getInterval(), vrule.getTimeout(), vrule.getSources(), vrule.getDetectors(), null);
      } else {
        return viewShowRoutes(model, "Atempting to show a route definition that not is a volume rule");
      }
    }
  }
  
  /**
   * Sets up the model for directing to the volumeroute_create page
   * @param model the model
   * @param name the name of the route
   * @param author the author
   * @param active if active or not
   * @param description the description
   * @param ascending if elevations should be triggered by ascending order or descending order
   * @param mine the min elevation
   * @param maxe the max elevation
   * @param recipients the recipients
   * @param interval the interval
   * @param timeout the timeout
   * @param sources the sources
   * @param emessage if a message should be shown
   * @return compositeroute_create
   */
  protected String viewCreateRoute(Model model,
      String name, 
      String author,
      Boolean active, 
      String description, 
      Boolean ascending, 
      Double mine, 
      Double maxe,
      List<String> recipients, 
      Integer interval, 
      Integer timeout, 
      List<String> sources,
      List<String> detectors,
      String emessage) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    model.addAttribute("sourceids", utilities.getRadarSources());
    model.addAttribute("intervals", getIntervals());
    model.addAttribute("adaptors", adaptors);
    model.addAttribute("anomaly_detectors", anomalymanager.list());    
    model.addAttribute("name", (name == null) ? "" : name);
    model.addAttribute("author", (author == null) ? "" : author);
    model.addAttribute("active", (active == null) ? new Boolean(true) : active);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("ascending", (ascending == null) ? new Boolean(true) : ascending);
    model.addAttribute("mine", (mine == null) ? new Double(-90.0) : mine);
    model.addAttribute("maxe", (maxe == null) ? new Double(90.0) : maxe);
    model.addAttribute("recipients",
        (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("interval", (interval == null) ? new Integer(15) : interval);
    model.addAttribute("timeout", (timeout == null) ? new Integer(15*60) : timeout);
    model.addAttribute("sources",
        (sources == null) ? new ArrayList<String>() : sources);
    model.addAttribute("detectors",
        (detectors == null) ? new ArrayList<String>() : detectors);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "route_create_volume";
  }

  /**
   * Sets up the model for directing to the volumeroute_show page
   * @param model the model
   * @param name the name of the route
   * @param author the author
   * @param active if active or not
   * @param description the description
   * @param ascending if elevations should be triggered by ascending order or descending order
   * @param mine the min elevation
   * @param maxe the max elevation
   * @param recipients the recipients
   * @param interval the interval
   * @param timeout the timeout
   * @param sources the sources
   * @param emessage if a message should be shown
   * @return compositeroute_create
   */  
  protected String viewShowRoute(
      Model model,
      String name,
      String author,
      Boolean active,
      String description,
      Boolean ascending, 
      Double mine, 
      Double maxe,
      List<String> recipients,
      Integer interval,
      Integer timeout,
      List<String> sources,
      List<String> detectors,
      String emessage) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    
    model.addAttribute("adaptors", adaptors);
    model.addAttribute("sourceids", utilities.getRadarSources());
    model.addAttribute("intervals", getIntervals());
    model.addAttribute("anomaly_detectors", anomalymanager.list());
    
    model.addAttribute("name", (name == null) ? "" : name);
    model.addAttribute("author", (author == null) ? "" : author);
    model.addAttribute("active", (active == null) ? new Boolean(true) : active);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("ascending", (ascending == null) ? new Boolean(true) : ascending);
    model.addAttribute("mine", (mine == null) ? new Double(-90.0) : mine);
    model.addAttribute("maxe", (maxe == null) ? new Double(90.0) : maxe);
    model.addAttribute("recipients",
        (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("interval", (interval == null) ? new Integer(15) : interval);
    model.addAttribute("timeout", (timeout == null) ? new Integer(15*60) : timeout);
    model.addAttribute("sources",
        (sources == null) ? new ArrayList<String>() : sources);
    model.addAttribute("detectors",
        (detectors == null) ? new ArrayList<String>() : detectors);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "route_show_volume";
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
   * Modifies an existing route
   * @param model the model
   * @param name the name of the route definition
   * @param author the author
   * @param active if route is active or not
   * @param description the description of the route
   * @param ascending if ascending order or not
   * @param mine min elevation angle
   * @param maxe max elevation angle
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
      Boolean ascending,
      Double mine,
      Double maxe,
      List<String> recipients,
      Integer interval,
      Integer timeout,
      List<String> sources,
      List<String> detectors) {
    List<String> newrecipients = (recipients == null) ? new ArrayList<String>() : recipients;
    List<String> newsources = (sources == null) ? new ArrayList<String>() : sources;
    List<String> newdetectors = (detectors == null) ? new ArrayList<String>() : detectors;
    boolean isactive = (active != null) ? active.booleanValue() : false;
    int iinterval = (interval != null) ? interval.intValue() : 15;
    int itimeout = (timeout != null) ? timeout.intValue() : 15*60;
    String emessage = null;
    boolean bascending = (ascending != null) ? ascending.booleanValue() : true;
    double dmine = (mine != null) ? mine.doubleValue() : -90.0;
    double dmaxe = (maxe != null) ? maxe.doubleValue() : 90.0;
    
    if (newsources.size() <= 0) {
      emessage = "You must specify at least one source.";
    } else if (newrecipients.size() <= 0) {
      emessage = "You must specify at least one recipient.";
    }
    
    if (emessage == null) {
      try {
        VolumeRule rule = createRule(bascending, dmine, dmaxe, iinterval, newsources, newdetectors, itimeout);
        RouteDefinition def = manager.create(name, author, isactive, description,
            newrecipients, rule);
        manager.updateDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        t.printStackTrace();
        emessage = "Failed to update definition: '" + t.getMessage() + "'";
      }
    }
    
    return viewShowRoute(model, name, author, active, description,
        ascending, mine, maxe, newrecipients, interval, timeout, sources, detectors, emessage);
  }

  
  /**
   * Returns the allowed intervals when generating volumes.
   * @return the allowed intervals
   */
  protected List<Integer> getIntervals() {
    List<Integer> result = new ArrayList<Integer>();
    int[] valid = {1,2,3,4,5,6,10,12,15,20,30,60};
    for (int x : valid) {
      result.add(x);
    }
    return result;
  }
  
  /**
   * Creates a volume rule.
   * @param areaid
   * @param interval
   * @param sources
   * @param timeout
   * @param byscan
   * @return
   */
  protected VolumeRule createRule(boolean ascending, double mine, double maxe, int interval, List<String> sources, List<String> detectors, int timeout) {
    VolumeRule rule = (VolumeRule)manager.createRule(VolumeRule.TYPE);
    rule.setAscending(ascending);
    rule.setElevationMin(mine);
    rule.setElevationMax(maxe);
    rule.setInterval(interval);
    rule.setSources(sources);
    rule.setDetectors(detectors);
    rule.setTimeout(timeout);
    return rule;
  }
}
