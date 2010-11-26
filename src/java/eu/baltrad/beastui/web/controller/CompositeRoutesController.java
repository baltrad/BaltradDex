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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.composite.CompositingRule;
import eu.baltrad.beast.rules.util.IRuleUtilities;

/**
 * Manages the composite routes and routing rules. Both scan based composites
 * and volume based composites are possible to generate but it is not
 * possible to combine them both.
 * 
 * @author Anders Henja
 */
@Controller
public class CompositeRoutesController {
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
   * Default constructor
   */
  public CompositeRoutesController() {
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
   * @param template the jdbc operations to set
   */
  @Autowired
  public void setRuleUtilities(IRuleUtilities utils) {
    this.utilities = utils;
  }
  
  /**
   * Handles create route requests 
   * @param model the model
   * @param name the name of the route
   * @param author the author
   * @param active if route is active or not
   * @param description the description of this route
   * @param recipients the recipients
   * @param byscan if composite should affect scans or volumes
   * @param areaid the composite area to be generated
   * @param interval the interval
   * @param timeout the timeout
   * @param sources the sources this rule should affect
   * @return a jsp page string or redirect
   */
  @RequestMapping("/compositeroute_create.htm")
  public String createRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "byscan", required = false) Boolean byscan,
      @RequestParam(value = "areaid", required = false) String areaid,
      @RequestParam(value = "interval", required = false) Integer interval,
      @RequestParam(value = "timeout", required = false) Integer timeout,
      @RequestParam(value = "sources", required = false) List<String> sources) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    String emessage = null;
    
    if (adaptors.size() == 0) {
      model.addAttribute("emessage",
          "No adaptors defined, please add one before creating a route.");
      return "redirect:adaptors.htm";
    }
    
    if (name == null && author == null && active == null && description == null && byscan == null &&
        recipients == null && areaid == null && interval == null && timeout == null &&
        sources == null) {
      return viewCreateRoute(model, name, author, active, description,
          recipients, byscan, areaid, interval, timeout, sources, null);
    }
    
    if (name == null || name.trim().equals("")) {
      emessage = "Name must be specified.";
    } else if (areaid == null || areaid.trim().equals("")) {
      emessage = "Areaid must be specified.";
    } else if (sources == null || sources.size() <= 0) {
      emessage = "Must specify at least one source.";
    }
    
    if (emessage == null) {
      try {
        boolean bactive = (active == null) ? false : active.booleanValue();
        int iinterval = (interval == null) ? 15 : interval.intValue();
        int itimeout = (timeout == null) ? 15*60 : timeout.intValue();
        boolean bbyscan = (byscan == null) ? false : byscan.booleanValue();
        CompositingRule rule = createRule(areaid, iinterval, sources, itimeout, bbyscan);
        List<String> recip = (recipients == null) ? new ArrayList<String>() : recipients;
        RouteDefinition def = manager.create(name, author, bactive, description, recip, rule);
        manager.storeDefinition(def);
        return "redirect:showroutes.htm";
      } catch (Throwable t) {
        t.printStackTrace();
        emessage = "Failed to create definition: '" + t.getMessage() + "'";
      }
    }
    
    return viewCreateRoute(model, name, author, active, description,
        recipients, byscan, areaid, interval, timeout, sources, emessage);
  }
  
  /**
   * Supports modification of a routing rule
   * @param model the model
   * @param name the name of the route
   * @param author the author
   * @param active if route is active or not
   * @param description the description of this route
   * @param recipients the recipients
   * @param byscan if composite should affect scans or volumes
   * @param areaid the composite area to be generated
   * @param interval the interval
   * @param timeout the timeout
   * @param sources the sources this rule should affect
   * @return a jsp page string or redirect
   */
  @RequestMapping("/compositeroute_show.htm")
  public String showRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "byscan", required = false) Boolean byscan,
      @RequestParam(value = "areaid", required = false) String areaid,
      @RequestParam(value = "interval", required = false) Integer interval,
      @RequestParam(value = "timeout", required = false) Integer timeout,
      @RequestParam(value = "sources", required = false) List<String> sources,
      @RequestParam(value = "submitButton", required = false) String operation) {
    RouteDefinition def = manager.getDefinition(name);
    if (def == null) {
      return viewShowRoutes(model, "No route named \"" + name + "\"");
    }
    if (operation != null && operation.equals("Modify")) {
      return modifyRoute(model, name, author, active, description, byscan, recipients, areaid, interval, timeout, sources);
    } else if (operation != null && operation.equals("Delete")) {
      try {
        manager.deleteDefinition(name);
        return "redirect:showroutes.htm";
      } catch (Throwable t) {
        return viewShowRoutes(model, "Failed to delete \"" + name + "\"");
      }
    } else {
      if (def.getRule() instanceof CompositingRule) {
        CompositingRule crule = (CompositingRule)def.getRule();
        return viewShowRoute(model, def.getName(), def.getAuthor(), def.isActive(), def.getDescription(),
            def.getRecipients(), crule.isScanBased(), crule.getArea(), crule.getInterval(), crule.getTimeout(), crule.getSources(), null);
      } else {
        return viewShowRoutes(model, "Atempting to show a route definition that not is a compositing rule");
      }
    }
  }
  
  
  /**
   * Sets up the model for directing to the compositeroute_create page
   * @param model the model
   * @param name the name of the route
   * @param author the author
   * @param active if active or not
   * @param description the description
   * @param recipients the recipients
   * @param areaid the area for the composite
   * @param interval the interval
   * @param timeout the timeout
   * @param sources the sources
   * @param emessage if a message should be shown
   * @return compositeroute_create
   */
  protected String viewCreateRoute(Model model, String name, String author,
      Boolean active, String description, List<String> recipients, Boolean byscan,
      String areaid, Integer interval, Integer timeout, List<String> sources, String emessage) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    model.addAttribute("sourceids", utilities.getRadarSources());
    model.addAttribute("intervals", getIntervals());
    model.addAttribute("adaptors", adaptors);
    model.addAttribute("name", (name == null) ? "" : name);
    model.addAttribute("author", (author == null) ? "" : author);
    model.addAttribute("active", (active == null) ? new Boolean(true) : active);
    model.addAttribute("byscan", (byscan == null) ? new Boolean(false) : byscan);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("recipients",
        (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("areaid", (areaid == null) ? "" : areaid);
    model.addAttribute("interval", (interval == null) ? new Integer(15) : interval);
    model.addAttribute("timeout", (timeout == null) ? new Integer(15*60) : timeout);
    model.addAttribute("sources",
        (sources == null) ? new ArrayList<String>() : sources);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "compositeroute_create";
  }
  
  protected String viewShowRoute(
      Model model,
      String name,
      String author,
      Boolean active,
      String description,
      List<String> recipients,
      Boolean byscan,
      String areaid,
      Integer interval,
      Integer timeout,
      List<String> sources,
      String emessage) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    
    model.addAttribute("adaptors", adaptors);
    model.addAttribute("sourceids", utilities.getRadarSources());
    model.addAttribute("intervals", getIntervals());
    
    model.addAttribute("name", (name == null) ? "" : name);
    model.addAttribute("author", (author == null) ? "" : author);
    model.addAttribute("active", (active == null) ? new Boolean(true) : active);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("recipients",
        (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("byscan", (byscan == null) ? new Boolean(false) : byscan);
    model.addAttribute("areaid", (areaid == null) ? "" : areaid);
    model.addAttribute("interval", (interval == null) ? new Integer(15) : interval);
    model.addAttribute("timeout", (timeout == null) ? new Integer(15*60) : timeout);
    model.addAttribute("sources",
        (sources == null) ? new ArrayList<String>() : sources);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "compositeroute_show";
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
    return "showroutes";
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
      Boolean byscan,
      List<String> recipients,
      String area,
      Integer interval,
      Integer timeout,
      List<String> sources) {
    List<String> newrecipients = (recipients == null) ? new ArrayList<String>() : recipients;
    List<String> newsources = (sources == null) ? new ArrayList<String>() : sources;
    boolean isactive = (active != null) ? active.booleanValue() : false;
    int iinterval = (interval != null) ? interval.intValue() : 15;
    int itimeout = (timeout != null) ? timeout.intValue() : 15*60;
    String emessage = null;
    boolean bbyscan = (byscan != null) ? byscan.booleanValue() : false;
    if (area == null || area.trim().equals("")) {
      emessage = "You must specify an area.";
    }
    if (newsources.size() <= 0) {
      emessage = "You must specify at least one source.";
    }
    
    if (emessage == null) {
      try {
        CompositingRule rule = createRule(area, iinterval, newsources, itimeout, bbyscan);
        RouteDefinition def = manager.create(name, author, isactive, description,
            newrecipients, rule);
        manager.updateDefinition(def);
        return "redirect:showroutes.htm";
      } catch (Throwable t) {
        t.printStackTrace();
        emessage = "Failed to update definition: '" + t.getMessage() + "'";
      }
    }
    
    return viewShowRoute(model, name, author, active, description,
        newrecipients,byscan, area, interval, timeout, sources, null);
  }
  
  protected List<Integer> getIntervals() {
    List<Integer> result = new ArrayList<Integer>();
    int[] valid = {1,2,3,4,5,6,10,12,15,20,30,60};
    for (int x : valid) {
      result.add(x);
    }
    return result;
  }
  
  protected CompositingRule createRule(String areaid, int interval,
      List<String> sources, int timeout, boolean byscan) {
    CompositingRule rule = new CompositingRule();
    rule.setArea(areaid);
    rule.setInterval(interval);
    rule.setSources(sources);
    rule.setTimeout(timeout);
    rule.setScanBased(byscan);
    return rule;
  }
}
