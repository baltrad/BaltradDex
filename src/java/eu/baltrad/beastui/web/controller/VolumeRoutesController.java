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
import eu.baltrad.beast.qc.AnomalyDetector;
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
  
  private final static Logger logger = LogManager.getLogger(VolumeRoutesController.class);
  
  /**
   * The translator from string to json and vice versa
   */
  private ObjectMapper jsonMapper = new ObjectMapper();

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
      @RequestParam(value = "elangles", required = false) String elangles,
      @RequestParam(value = "adaptive_elangles", required = false) Boolean adaptive_elangles,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "interval", required = false) Integer interval,
      @RequestParam(value = "timeout", required = false) Integer timeout,
      @RequestParam(value = "nominal_timeout", required = false) Boolean nominal_timeout,
      @RequestParam(value = "sources", required = false) List<String> sources,
      @RequestParam(value = "detectors", required = false) List<String> detectors,
      @RequestParam(value = "quality_control_mode", required = false) Integer quality_control_mode,
      @RequestParam(value = "filterJson", required=false) String filterJson) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    String emessage = null;
    
    if (adaptors.size() == 0) {
      model.addAttribute("emessage",
          "No adaptors defined, please add one before creating a route.");
      return "redirect:adaptors.htm";
    }

    if (name == null && author == null && active == null && description == null &&
        ascending == null && mine == null && maxe == null && elangles == null && adaptive_elangles == null &&
        recipients == null && interval == null && timeout == null && nominal_timeout == null &&
        sources == null && detectors == null && quality_control_mode == null) {
      return viewCreateRoute(model, name, author, active, description,
          true, mine, maxe, elangles, adaptive_elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, filterJson, null);
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
        boolean bascending = (ascending == null) ? false : ascending.booleanValue();
        double dmine = (mine == null) ? -90.0 : mine.doubleValue();
        double dmaxe = (maxe == null) ? 90.0 : maxe.doubleValue();
        int iinterval = (interval == null) ? 15 : interval.intValue();
        int itimeout = (timeout == null) ? 15*60 : timeout.intValue();
        int iqc_mode = (quality_control_mode == null) ? 0 : quality_control_mode.intValue();
        boolean badaptive_elangles = (adaptive_elangles == null) ? false : adaptive_elangles.booleanValue();
        boolean bnominal_timeout = (nominal_timeout == null) ? false : nominal_timeout.booleanValue();
        VolumeRule rule = createRule(bascending, dmine, dmaxe, elangles, badaptive_elangles, iinterval, sources, detectors, iqc_mode, itimeout, bnominal_timeout, filterJson);
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
        ascending, mine, maxe, elangles, adaptive_elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, filterJson, emessage);
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
      @RequestParam(value = "elangles", required = false) String elangles,
      @RequestParam(value = "adaptive_elangles", required = false) Boolean adaptive_elangles,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "interval", required = false) Integer interval,
      @RequestParam(value = "timeout", required = false) Integer timeout,
      @RequestParam(value = "nominal_timeout", required = false) Boolean nominal_timeout,
      @RequestParam(value = "sources", required = false) List<String> sources,
      @RequestParam(value = "detectors", required = false) List<String> detectors,
      @RequestParam(value = "quality_control_mode", required = false) Integer quality_control_mode,
      @RequestParam(value = "filterJson", required = false) String filterJson,
      @RequestParam(value = "submitButton", required = false) String operation) {
    RouteDefinition def = manager.getDefinition(name);
    if (def == null) {
      return viewShowRoutes(model, "No route named \"" + name + "\"");
    }
    if (operation != null && operation.equals("Save")) {
      return modifyRoute(model, name, author, active, description, ascending, mine, maxe, elangles, adaptive_elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, filterJson);
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
        String filterstr = null;
        if (vrule.getFilter() != null) {
          try {
            filterstr = jsonMapper.writeValueAsString(vrule.getFilter());
          } catch (IOException e) {
            logger.error("failed to create JSON string from filter", e);
          }
        }
        
        return viewShowRoute(model, def.getName(), def.getAuthor(), def.isActive(), def.getDescription(),
            vrule.isAscending(), vrule.getElevationMin(), vrule.getElevationMax(), vrule.getElevationAngles(), vrule.isAdaptiveElevationAngles(), def.getRecipients(), 
            vrule.getInterval(), vrule.getTimeout(), vrule.isNominalTimeout(), vrule.getSources(), vrule.getDetectors(), vrule.getQualityControlMode(), filterstr, null);
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
      String elangles,
      Boolean adaptive_elangles,
      List<String> recipients, 
      Integer interval, 
      Integer timeout, 
      Boolean nominal_timeout,
      List<String> sources,
      List<String> detectors,
      Integer quality_control_mode,
      String jsonFilter,
      String emessage) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    model.addAttribute("sourceids", utilities.getRadarSources());
    model.addAttribute("intervals", getIntervals());
    model.addAttribute("adaptors", adaptors);
    model.addAttribute("anomaly_detectors", createOrderedDetectorList(anomalymanager.list(), detectors));
    model.addAttribute("name", (name == null) ? "" : name);
    model.addAttribute("author", (author == null) ? "" : author);
    model.addAttribute("active", (active == null) ? new Boolean(true) : active);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("ascending", (ascending == null) ? new Boolean(false) : ascending);
    model.addAttribute("mine", (mine == null) ? new Double(-90.0) : mine);
    model.addAttribute("maxe", (maxe == null) ? new Double(90.0) : maxe);
    model.addAttribute("elangles", (elangles == null) ? "" : elangles);
    model.addAttribute("adaptive_elangles", (adaptive_elangles==null) ? Boolean.valueOf(false) : adaptive_elangles);
    model.addAttribute("recipients",
        (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("interval", (interval == null) ? new Integer(15) : interval);
    model.addAttribute("timeout", (timeout == null) ? new Integer(15*60) : timeout);
    model.addAttribute("nominal_timeout", (nominal_timeout == null) ? new Boolean(false) : nominal_timeout);
    model.addAttribute("sources",
        (sources == null) ? new ArrayList<String>() : sources);
    model.addAttribute("detectors",
        (detectors == null) ? new ArrayList<String>() : detectors);
    model.addAttribute("quality_control_mode",
        (quality_control_mode == null) ? new Integer(0) : quality_control_mode);
    model.addAttribute("filterJson", jsonFilter);
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
      String elangles,
      Boolean adaptive_elangles,
      List<String> recipients,
      Integer interval,
      Integer timeout,
      Boolean nominal_timeout,
      List<String> sources,
      List<String> detectors,
      Integer quality_control_mode,
      String jsonFilter,
      String emessage) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    
    model.addAttribute("adaptors", adaptors);
    model.addAttribute("sourceids", utilities.getRadarSources());
    model.addAttribute("intervals", getIntervals());
    model.addAttribute("anomaly_detectors", createOrderedDetectorList(anomalymanager.list(), detectors));
    model.addAttribute("name", (name == null) ? "" : name);
    model.addAttribute("author", (author == null) ? "" : author);
    model.addAttribute("active", (active == null) ? new Boolean(true) : active);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("ascending", (ascending == null) ? new Boolean(false) : ascending);
    model.addAttribute("mine", (mine == null) ? new Double(-90.0) : mine);
    model.addAttribute("maxe", (maxe == null) ? new Double(90.0) : maxe);
    model.addAttribute("elangles", (elangles == null) ? "" : elangles);
    model.addAttribute("adaptive_elangles", (adaptive_elangles==null)?Boolean.valueOf(false):adaptive_elangles);
    model.addAttribute("recipients",
        (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("interval", (interval == null) ? new Integer(15) : interval);
    model.addAttribute("timeout", (timeout == null) ? new Integer(15*60) : timeout);
    model.addAttribute("nominal_timeout", (nominal_timeout == null) ? new Boolean(false) : nominal_timeout);
    model.addAttribute("sources",
        (sources == null) ? new ArrayList<String>() : sources);
    model.addAttribute("detectors",
        (detectors == null) ? new ArrayList<String>() : detectors);
    model.addAttribute("quality_control_mode",
        (quality_control_mode == null) ? new Integer(0) : quality_control_mode);
    if (jsonFilter != null && !jsonFilter.equals("")) {
      model.addAttribute("filterJson", jsonFilter);
    }
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
      String elangles,
      Boolean adaptive_elangles,
      List<String> recipients,
      Integer interval,
      Integer timeout,
      Boolean nominal_timeout,
      List<String> sources,
      List<String> detectors,
      Integer quality_control_mode,
      String jsonFilter) {
    List<String> newrecipients = (recipients == null) ? new ArrayList<String>() : recipients;
    List<String> newsources = (sources == null) ? new ArrayList<String>() : sources;
    List<String> newdetectors = (detectors == null) ? new ArrayList<String>() : detectors;
    boolean isactive = (active != null) ? active.booleanValue() : false;
    int iinterval = (interval != null) ? interval.intValue() : 15;
    int itimeout = (timeout != null) ? timeout.intValue() : 15*60;
    boolean bnominal_timeout = (nominal_timeout == null) ? false : nominal_timeout.booleanValue();
    int iqc_mode = (quality_control_mode != null) ? quality_control_mode.intValue() : 0;
    String emessage = null;
    boolean bascending = (ascending != null) ? ascending.booleanValue() : false;
    double dmine = (mine != null) ? mine.doubleValue() : -90.0;
    double dmaxe = (maxe != null) ? maxe.doubleValue() : 90.0;
    boolean badaptive_elangles = (adaptive_elangles == null) ? Boolean.valueOf(false) : adaptive_elangles;
    if (newsources.size() <= 0) {
      emessage = "You must specify at least one source.";
    } else if (newrecipients.size() <= 0) {
      emessage = "You must specify at least one recipient.";
    }

    if (emessage == null) {
      try {
        VolumeRule rule = createRule(bascending, dmine, dmaxe, elangles, badaptive_elangles, iinterval, newsources, newdetectors, iqc_mode, itimeout, bnominal_timeout, jsonFilter);
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
        ascending, mine, maxe, elangles, adaptive_elangles, newrecipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter, emessage);
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
   * Organizes so that the selected detectors comes in order in the list of anomaly detectors. We are doing it like this since
   * we don't want to have 2 different select lists where one contains the "not selected" and another containing the selected.
   * @param detectors all anomaly detectors
   * @param selectedDetectors the selected detectors
   * @return a reorganized list of detectors
   */
  protected List<AnomalyDetector> createOrderedDetectorList(List<AnomalyDetector> detectors, List<String> selectedDetectors) {
    List<AnomalyDetector> result = new ArrayList<AnomalyDetector>();
    for (AnomalyDetector x : detectors) {
      result.add(x);
    }
    if (selectedDetectors != null) {
      int sdlen = selectedDetectors.size();
      for (int i = sdlen - 1; i >= 0; i--) {
        String sdname = selectedDetectors.get(i);
        for (int j = 0; j < result.size(); j++) {
          AnomalyDetector ad = result.get(j);
          if (ad.getName().equals(sdname)) {
            result.remove(j);
            result.add(0, ad);
            break;
          }
        }
      }
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
  protected VolumeRule createRule(boolean ascending, double mine, double maxe, String elangles, boolean adaptive_elangles, int interval, List<String> sources, 
      List<String> detectors, int iqc_mode, int timeout, boolean nominal_timeout, String jsonFilter) {
    VolumeRule rule = (VolumeRule)manager.createRule(VolumeRule.TYPE);
    rule.setAscending(ascending);
    rule.setElevationMin(mine);
    rule.setElevationMax(maxe);
    rule.setElevationAngles(elangles);
    rule.setAdaptiveElevationAngles(adaptive_elangles);
    rule.setInterval(interval);
    rule.setSources(sources);
    rule.setDetectors(detectors);
    rule.setQualityControlMode(iqc_mode);
    rule.setTimeout(timeout);
    rule.setNominalTimeout(nominal_timeout);
    
    if (jsonFilter != null && !jsonFilter.equals("")) {
      try {
        rule.setFilter(jsonMapper.readValue(jsonFilter, IFilter.class));
      } catch (Exception e) {
        logger.error("Failed to translate json to filter", e);
      }
    }
    return rule;
  }
}
