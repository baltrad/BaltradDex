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
import eu.baltrad.beast.qc.AnomalyDetector;
import eu.baltrad.beast.qc.IAnomalyDetectorManager;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.composite.CompositingRule;
import eu.baltrad.beast.rules.site2d.Site2DRule;
import eu.baltrad.beast.rules.util.IRuleUtilities;

/**
 * Manages the site2d routes and routing rules. Both scan based site2d 
 * and volume based site2d products are possible to generate but it is not
 * possible to combine them both.
 * 
 * @author Anders Henja
 */
@Controller
public class Site2dRoutesController {
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
   * The pgf client helper
   */
  private IPgfClientHelper pgfClientHelper = null;
  
  /**
   * Logger
   */
  private static Logger logger = LogManager.getLogger(Site2dRoutesController.class);
  
  /**
   * The translator from string to json and vice versa
   */
  private ObjectMapper jsonMapper = new ObjectMapper();
  
  private enum SubmitOperation {
    NONE,
    SAVE,
    DELETE,
    DUPLICATE
  }
  
  /**
   * Default constructor
   */
  public Site2dRoutesController() {
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

  /**
   * @param anomalymanager the anomaly manager to set
   */
  @Autowired
  public void setAnomalyDetectorManager(IAnomalyDetectorManager anomalymanager) {
    this.anomalymanager = anomalymanager;
  }
  
  /**
   * @param pgfClientHelper the pgf client helpler
   */
  @Autowired
  public void setPgfClientHelper(IPgfClientHelper pgfClientHelper) {
    this.pgfClientHelper = pgfClientHelper;
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
   * @param applygra if gra correction should be applied or not
   * @param ZR_A the ZR A coefficient to use when converting between reflectivity and MM/H
   * @param ZR_b the ZR b coefficient to use when converting between reflectivity and MM/H
   * @param ignore_malfunc if how/malfunc should be used when determining scans to be included
   * @param ctfilter if ct-filtering should be performed or not
   * @param sources the sources this rule should affect
   * @param detectors the detectors that should be used for this rule
   * @return a jsp page string or redirect
   */
  @RequestMapping("/route_create_site2d.htm")
  public String createRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "byscan", required = false) Boolean byscan,
      @RequestParam(value = "method", required = false) String method,
      @RequestParam(value = "prodpar", required = false) String prodpar,
      @RequestParam(value = "areaid", required = false) String areaid,
      @RequestParam(value = "interval", required = false) Integer interval,
      @RequestParam(value = "applygra", required = false) Boolean applygra,
      @RequestParam(value = "ZR_A", required = false) Double ZR_A,
      @RequestParam(value = "ZR_b", required = false) Double ZR_b,
      @RequestParam(value = "ignore_malfunc", required = false) Boolean ignore_malfunc,
      @RequestParam(value = "ctfilter", required = false) Boolean ctfilter,
      @RequestParam(value = "pcsid", required = false) String pcsid,
      @RequestParam(value = "xscale", required = false) Double xscale,
      @RequestParam(value = "yscale", required = false) Double yscale,
      @RequestParam(value = "options", required = false) String options,      
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
    
    if (name == null && author == null && active == null && description == null && byscan == null &&
        method == null && prodpar == null && recipients == null && 
        areaid == null && interval == null && applygra == null && ZR_A == null && ZR_b == null && options == null &&
        ignore_malfunc == null && ctfilter == null && sources == null && detectors == null && quality_control_mode == null) {
      return viewCreateRoute(model, name, author, active, description,
          recipients, byscan, method, prodpar, areaid, 
          interval, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, pcsid, xscale, yscale, options, sources, detectors, quality_control_mode, filterJson, null);
    }
    
    logger.info("areaid=" + areaid + ", pcsid=" + pcsid);
    
    if (name == null || name.trim().equals("")) {
      emessage = "Name must be specified.";
    } else if ((areaid == null || areaid.trim().equals("")) && 
        (pcsid == null || pcsid.trim().equals(""))) {
      emessage = "Must specify either areaid or pcsid";
    } else if (sources == null || sources.size() <= 0) {
      emessage = "Must specify at least one source.";
    } else if (recipients == null || recipients.size() == 0) {
      emessage = "Must at least specify one recipient";
    }
    if (prodpar != null) {
      logger.info("prodpar != null");
      if (method != null && method.equals(CompositingRule.PMAX)) {
        logger.info("method == pmax, trying to split prodpar");
        String[] values = prodpar.split(",");
        if (values.length >= 1) {
          try {
            Double.parseDouble(values[0].trim());
            if (values.length > 1) {
              Double.parseDouble(values[1].trim());
            }
          } catch (NumberFormatException e) {
            emessage = "Product parameter must be <height>,<range> value for pmax.";
          }
        }
      } else {
        try { 
          Double.parseDouble(prodpar);
        } catch (NumberFormatException e) {
          e.printStackTrace();
          emessage = "Product parameter must be a floating point value for " + method;
        }
      }
    }
    
    if (emessage == null) {
      try {
        boolean bactive = (active == null) ? false : active.booleanValue();
        int iinterval = (interval == null) ? 15 : interval.intValue();
        boolean bbyscan = (byscan == null) ? false : byscan.booleanValue();
        boolean bapplygra = (applygra == null) ? false : applygra.booleanValue();
        double dZR_A = (ZR_A == null) ? 200.0 : ZR_A.doubleValue();
        double dZR_b = (ZR_b == null) ? 1.6 : ZR_b.doubleValue();
        boolean bignore_malfunc = (ignore_malfunc == null) ? false : ignore_malfunc.booleanValue();
        boolean bctfilter = (ctfilter == null) ? false : ctfilter.booleanValue();
        double dxscale = (xscale == null) ? 2000.0 : xscale.doubleValue();
        double dyscale = (xscale == null) ? 2000.0 : yscale.doubleValue();
        int iquality_control_mode = (quality_control_mode == null) ? 0 : quality_control_mode.intValue();
        Site2DRule rule = createRule(areaid, iinterval, sources, detectors, iquality_control_mode, bbyscan, method, prodpar, bapplygra, dZR_A, dZR_b, bignore_malfunc, bctfilter, pcsid, dxscale, dyscale, options, filterJson);
        List<String> recip = (recipients == null) ? new ArrayList<String>() : recipients;
        RouteDefinition def = manager.create(name, author, bactive, description, recip, rule);
        manager.storeDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        if (t.toString().contains("(" + name + ") already exists")) {
          emessage = "Failed to create definition. Rule with name '" + name + "' already exists. Enter another name.";
        } else {
          logger.warn("Failed to create Site2DRule.", t);
          emessage = "Failed to create definition: '" + t.getMessage() + "'";          
        }
      }
    }
    
    return viewCreateRoute(model, name, author, active, description,
        recipients, byscan, method, prodpar, areaid, interval, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, pcsid, xscale, yscale, options, sources, detectors, quality_control_mode, filterJson, emessage);
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
   * @param sources the sources this rule should affect
   * @return a jsp page string or redirect
   */
  @RequestMapping("/route_show_site2d.htm")
  public String showRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "byscan", required = false) Boolean byscan,
      @RequestParam(value = "method", required = false) String method,
      @RequestParam(value = "prodpar", required = false) String prodpar,
      @RequestParam(value = "areaid", required = false) String areaid,
      @RequestParam(value = "interval", required = false) Integer interval,
      @RequestParam(value = "applygra", required = false) Boolean applygra,
      @RequestParam(value = "ZR_A", required = false) Double ZR_A,
      @RequestParam(value = "ZR_b", required = false) Double ZR_b,
      @RequestParam(value = "ignore_malfunc", required = false) Boolean ignore_malfunc,
      @RequestParam(value = "ctfilter", required = false) Boolean ctfilter,
      @RequestParam(value = "pcsid", required = false) String pcsid,
      @RequestParam(value = "xscale", required = false) Double xscale,
      @RequestParam(value = "yscale", required = false) Double yscale,
      @RequestParam(value = "options", required = false) String options,
      @RequestParam(value = "sources", required = false) List<String> sources,
      @RequestParam(value = "detectors", required = false) List<String> detectors,
      @RequestParam(value = "quality_control_mode", required = false) Integer quality_control_mode,
      @RequestParam(value = "filterJson", required = false) String filterJson,
      @RequestParam(value = "submitButton", required = false) String operation) {
    RouteDefinition def = manager.getDefinition(name);
    if (def == null) {
      return viewShowRoutes(model, "No route named \"" + name + "\"");
    }
    
    switch (getSubmitOperation(operation)) {
    case SAVE:
      return modifyRoute(model, name, author, active, description, byscan, method, prodpar, recipients, areaid, interval, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, pcsid, xscale, yscale, options, sources, detectors, quality_control_mode, filterJson);
    case DELETE:
      try {
        manager.deleteDefinition(name);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        return viewShowRoutes(model, "Failed to delete \"" + name + "\", have you verified that there are no reffering scheduled jobs");
      }
    case DUPLICATE:
      logger.info("Duplicating Site2DRule '" + name + "'");
      return viewCreateRoute(model, name + " (copy)", author, active, description, recipients, byscan, method, prodpar, 
                             areaid, interval, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, pcsid, xscale, yscale, options,
                             sources, detectors, quality_control_mode, filterJson, null);
          
    default:
      if (def.getRule() instanceof Site2DRule) {
        Site2DRule crule = (Site2DRule)def.getRule();
        
        String filterstr = null;
        if (crule.getFilter() != null) {
          try {
            filterstr = jsonMapper.writeValueAsString(crule.getFilter());
          } catch (IOException e) {
            logger.error("failed to create JSON string from filter", e);
          }
        }
        
        return viewShowRoute(model, def.getName(), def.getAuthor(), def.isActive(), def.getDescription(),
            def.getRecipients(), crule.isScanBased(), crule.getMethod(), crule.getProdpar(), 
            crule.getArea(), crule.getInterval(), crule.isApplyGRA(), crule.getZR_A(), crule.getZR_b(), 
            crule.isIgnoreMalfunc(), crule.isCtFilter(), crule.getPcsid(), crule.getXscale(), crule.getYscale(), crule.getOptions(),
            crule.getSources(), crule.getDetectors(), crule.getQualityControlMode(), filterstr, null);
      } else {
        return viewShowRoutes(model, "Atempting to show a route definition that not is a site2d rule");
      }
    }
  }
  
  
  /**
   * Sets up the model for directing to the site2d create page page
   * @param model the model
   * @param name the name of the route
   * @param author the author
   * @param active if active or not
   * @param description the description
   * @param recipients the recipients
   * @param areaid the area for the composite
   * @param interval the interval
   * @param sources the sources
   * @param emessage if a message should be shown
   * @return compositeroute_create
   */
  protected String viewCreateRoute(Model model, 
                                   String name, 
                                   String author,
                                   Boolean active, 
                                   String description, 
                                   List<String> recipients, 
                                   Boolean byscan, 
                                   String method, 
                                   String prodpar, 
                                   String areaid, 
                                   Integer interval, 
                                   Boolean applygra, 
                                   Double ZR_A, 
                                   Double ZR_b, 
                                   Boolean ignore_malfunc,
                                   Boolean ctfilter, 
                                   String pcsid, 
                                   Double xscale, 
                                   Double yscale, 
                                   String options,
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
    model.addAttribute("byscan", (byscan == null) ? new Boolean(false) : byscan);
    model.addAttribute("method", (method == null) ? CompositingRule.PCAPPI : method);
    model.addAttribute("prodpar", (prodpar == null) ? "1000" : prodpar);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("recipients",
        (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("arealist", pgfClientHelper.getUniqueAreaIds());    
    model.addAttribute("areaid", (areaid == null) ? "" : areaid);
    model.addAttribute("interval", (interval == null) ? new Integer(15) : interval);
    model.addAttribute("applygra", (applygra == null) ? new Boolean(false) : applygra);
    model.addAttribute("ZR_A", (ZR_A == null) ? new Double(200.0) : ZR_A);
    model.addAttribute("ZR_b", (ZR_b == null) ? new Double(1.6) : ZR_b);
    model.addAttribute("ignore_malfunc", (ignore_malfunc == null) ? new Boolean(false) : ignore_malfunc);
    model.addAttribute("ctfilter", (ctfilter == null) ? new Boolean(false) : ctfilter);
    model.addAttribute("pcslist", pgfClientHelper.getUniquePcsIds());
    model.addAttribute("pcsid", (pcsid == null) ? "" : pcsid);
    model.addAttribute("xscale", (xscale == null) ? new Double(2000.0) : xscale);
    model.addAttribute("yscale", (yscale == null) ? new Double(2000.0) : yscale);
    model.addAttribute("options", (options == null) ? "" : options);
    model.addAttribute("filterJson", jsonFilter);
    
    model.addAttribute("sources",
        (sources == null) ? new ArrayList<String>() : sources);
    model.addAttribute("detectors",
        (detectors == null) ? new ArrayList<String>() : detectors);
    model.addAttribute("quality_control_mode",
        (quality_control_mode == null) ? new Integer(0) : quality_control_mode);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "route_create_site2d";
  }
  
  protected String viewShowRoute(
      Model model,
      String name,
      String author,
      Boolean active,
      String description,
      List<String> recipients,
      Boolean byscan,
      String method,
      String prodpar,
      String areaid,
      Integer interval,
      Boolean applygra,
      Double ZR_A,
      Double ZR_b,
      Boolean ignore_malfunc,
      Boolean ctfilter, 
      String pcsid,
      Double xscale,
      Double yscale,
      String options,
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
    model.addAttribute("recipients",
        (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("byscan", (byscan == null) ? new Boolean(false) : byscan);
    model.addAttribute("method", (method == null) ? CompositingRule.PCAPPI : method);
    model.addAttribute("prodpar", (prodpar == null) ? "1000" : prodpar);
    model.addAttribute("arealist", pgfClientHelper.getUniqueAreaIds());    
    model.addAttribute("areaid", (areaid == null) ? "" : areaid);
    model.addAttribute("interval", (interval == null) ? new Integer(15) : interval);
    model.addAttribute("applygra", (applygra == null) ? new Boolean(false) : applygra);
    model.addAttribute("ZR_A", (ZR_A == null) ? new Double(200.0) : ZR_A);
    model.addAttribute("ZR_b", (ZR_b == null) ? new Double(1.6) : ZR_b);
    model.addAttribute("ignore_malfunc", (ignore_malfunc == null) ? new Boolean(false) : ignore_malfunc);
    model.addAttribute("ctfilter", (ctfilter == null) ? new Boolean(false) : ctfilter);
    model.addAttribute("pcslist", pgfClientHelper.getUniquePcsIds());
    model.addAttribute("pcsid", (pcsid == null) ? "" : pcsid);
    model.addAttribute("xscale", (xscale == null)?new Double(2000.0) : xscale);
    model.addAttribute("yscale", (yscale == null)?new Double(2000.0) : yscale);
    model.addAttribute("options", (options == null)? "" : options);
    model.addAttribute("sources",
        (sources == null) ? new ArrayList<String>() : sources);
    model.addAttribute("detectors",
        (detectors == null) ? new ArrayList<String>() : detectors);
    model.addAttribute("quality_control_mode",
        (quality_control_mode == null) ? new Integer(0) : quality_control_mode);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    if (jsonFilter != null && !jsonFilter.equals("")) {
      model.addAttribute("filterJson", jsonFilter);
    }
    return "route_show_site2d";
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
      String method,
      String prodpar,
      List<String> recipients,
      String area,
      Integer interval,
      Boolean applygra,
      Double ZR_A,
      Double ZR_b,
      Boolean ignore_malfunc,
      Boolean ctfilter, 
      String pcsid,
      Double xscale,
      Double yscale,
      String options,
      List<String> sources,
      List<String> detectors,
      Integer quality_control_mode,
      String jsonFilter) {
    List<String> newrecipients = (recipients == null) ? new ArrayList<String>() : recipients;
    List<String> newsources = (sources == null) ? new ArrayList<String>() : sources;
    List<String> newdetectors = (detectors == null) ? new ArrayList<String>() : detectors;
    
    boolean isactive = (active != null) ? active.booleanValue() : false;
    int iinterval = (interval != null) ? interval.intValue() : 15;
    String emessage = null;
    boolean bbyscan = (byscan != null) ? byscan.booleanValue() : false;
    boolean bapplygra = (applygra != null) ? applygra.booleanValue() : false;
    double dZR_A = (ZR_A != null) ? ZR_A.doubleValue() : 200.0;
    double dZR_b = (ZR_b != null) ? ZR_b.doubleValue() : 1.6;
    boolean bignore_malfunc = (ignore_malfunc != null) ? ignore_malfunc.booleanValue() : false;
    boolean bctfilter = (ctfilter != null) ? ctfilter.booleanValue() : false;
    double dxscale = (xscale != null) ? xscale.doubleValue() : 2000.0;
    double dyscale = (yscale != null) ? yscale.doubleValue() : 2000.0;
    int iquality_control_mode = (quality_control_mode == null) ? 0 : quality_control_mode.intValue();
    if (newsources.size() <= 0) {
      emessage = "You must specify at least one source.";
    } else if ((area == null || area.trim().equals("")) && 
        (pcsid == null || pcsid.trim().equals(""))) {
      emessage = "You must specify one of pcsid or area";
    }
    if (prodpar != null) {
      logger.info("prodpar != null");
      if (method != null && method.equals(CompositingRule.PMAX)) {
        logger.info("method == pmax, trying to split prodpar");
        String[] values = prodpar.split(",");
        if (values.length >= 1) {
          try {
            Double.parseDouble(values[0].trim());
            if (values.length > 1) {
              Double.parseDouble(values[1].trim());
            }
          } catch (NumberFormatException e) {
            emessage = "Product parameter must be <height>,<range> value for pmax.";
          }
        }
      } else {
        try { 
          Double.parseDouble(prodpar);
        } catch (NumberFormatException e) {
          e.printStackTrace();
          emessage = "Product parameter must be a floating point value for " + method;
        }
      }
    }
    if (emessage == null) {
      try {
        Site2DRule rule = createRule(area, iinterval, newsources, newdetectors, iquality_control_mode, bbyscan, method, prodpar, bapplygra, dZR_A, dZR_b, bignore_malfunc, bctfilter, pcsid, dxscale, dyscale, options, jsonFilter);
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
        newrecipients,byscan, method, prodpar, area, interval, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, pcsid, xscale, yscale, options, sources, detectors, quality_control_mode, emessage, jsonFilter);
  }
  
  protected List<Integer> getIntervals() {
    List<Integer> result = new ArrayList<Integer>();
    int[] valid = {1,2,3,4,5,6,10,12,15,20,30,60};
    for (int x : valid) {
      result.add(x);
    }
    return result;
  }
  
  protected Site2DRule createRule(String areaid, int interval,
      List<String> sources, List<String> detectors, int quality_control_mode, boolean byscan, 
      String method, String prodpar, boolean applygra, double ZR_A, double ZR_b, 
      boolean ignore_malfunc, boolean ctfilter,
      String pcsid, double xscale, double yscale, String options,
      String jsonFilter) {
    
    Site2DRule rule = (Site2DRule)manager.createRule(Site2DRule.TYPE);
    rule.setArea(areaid);
    rule.setInterval(interval);
    rule.setSources(sources);
    rule.setDetectors(detectors);
    rule.setQualityControlMode(quality_control_mode);
    rule.setScanBased(byscan);
    rule.setMethod(method);
    rule.setProdpar(prodpar);
    rule.setApplyGRA(applygra);
    rule.setZR_A(ZR_A);
    rule.setZR_b(ZR_b);
    rule.setIgnoreMalfunc(ignore_malfunc);
    rule.setCtFilter(ctfilter);
    rule.setPcsid(pcsid);
    rule.setXscale(xscale);
    rule.setYscale(yscale);
    rule.setOptions(options);
    
    if (jsonFilter != null && !jsonFilter.equals("")) {
      try {
        rule.setFilter(jsonMapper.readValue(jsonFilter, IFilter.class));
      } catch (Exception e) {
        logger.error("Failed to translate json to filter", e);
      }
    }
    
    return rule;
  }
  
  private SubmitOperation getSubmitOperation(String operationString) {
    SubmitOperation submitOperation = SubmitOperation.NONE;
    
    if (operationString != null) {
      if (operationString.equals("Save")) {
        submitOperation = SubmitOperation.SAVE;
      } else if (operationString.equals("Delete")) {
        submitOperation = SubmitOperation.DELETE;
      } else if (operationString.equals("Duplicate")) {
        submitOperation = SubmitOperation.DUPLICATE;
      }
    }
    
    return submitOperation;
  }
}
