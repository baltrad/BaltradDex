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
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.util.IRuleUtilities;
import eu.baltrad.beast.rules.wrwp.WrwpRule;

/**
 * Manages the wrwp routes and routing rules.
 * 
 * @author Anders Henja
 */
@Controller
public class WrwpRoutesController {
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
  private static Logger logger = LogManager.getLogger(WrwpRoutesController.class);
  
  /**
   * The translator from string to json and vice versa
   */
  private ObjectMapper jsonMapper = new ObjectMapper();
  
  /**
   * Available fields
   */
  private List<String> availableFields = new ArrayList<>();
  
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
   * @param utils the rule utilities
   */
  @Autowired
  public void setRuleUtilities(IRuleUtilities utils) {
    this.utilities = utils;
  }
  
  /**
   * @param fields the available fields
   */
  @Autowired
  public void setAvailableFields(List<String> fields) {
    logger.info("Setting available fields");
    this.availableFields = fields;
  }
  
  /**
   * Called when creating or a wrwp route or when the create wrwp page should be shown
   * @param model
   * @param name
   * @param author
   * @param active
   * @param description
   * @param interval
   * @param maxheight
   * @param mindistance
   * @param maxdistance
   * @param minelangle
   * @param minvelocitythreshold
   * @param recipients
   * @param sources
   * @return
   */
  @RequestMapping("/route_create_wrwp.htm")
  public String createRoute(
    Model model,
    @RequestParam(value = "name", required = false) String name,
    @RequestParam(value = "author", required = false) String author,
    @RequestParam(value = "active", required = false) Boolean active,
    @RequestParam(value = "description", required = false) String description,
    @RequestParam(value = "interval", required = false) Integer interval,
    @RequestParam(value = "maxheight", required = false) Integer maxheight,
    @RequestParam(value = "mindistance", required = false) Integer mindistance,
    @RequestParam(value = "maxdistance", required = false) Integer maxdistance,
    @RequestParam(value = "minelangle", required = false) Double minelangle,
    @RequestParam(value = "minvelocitythreshold", required = false) Double minvelocitythreshold,
    @RequestParam(value = "fields", required = false) List<String> fields,
    @RequestParam(value = "recipients", required = false) List<String> recipients,
    @RequestParam(value = "sources", required = false) List<String> sources,
    @RequestParam(value = "filterJson", required=false) String filterJson) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    String emessage = null;
  
    logger.info("createRoute(....)");
    
    if (adaptors.size() == 0) {
      model.addAttribute("emessage",
          "No adaptors defined, please add one before creating a route.");
      return "redirect:adaptors.htm";
    }
  
    if (name == null && author == null && active == null && description == null &&
        interval == null && maxheight == null && mindistance == null && maxdistance == null &&
        minelangle == null && minvelocitythreshold == null && recipients == null && sources == null && fields == null) {
      logger.info("Everything is null");
      return viewCreateRoute(model, name, author, active, description,
                interval, maxheight, mindistance, maxdistance, minelangle, minvelocitythreshold, fields,
                recipients, sources, filterJson, null);
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
        int iinterval = (interval == null) ? 200 : interval.intValue();
        int imaxheight = (maxheight == null) ? 12000 : maxheight.intValue();
        int imindistance = (mindistance == null) ? 4000 : mindistance.intValue();
        int imaxdistance = (maxdistance == null) ? 40000 : maxdistance.intValue();
        double dminelangle = (minelangle == null) ? 2.5 : minelangle.doubleValue();
        double dminvelocity = (minvelocitythreshold == null) ? 2.0 : minvelocitythreshold.doubleValue();
        WrwpRule rule = createRule(iinterval, imaxheight, imindistance, imaxdistance, dminelangle, dminvelocity, fields, sources, filterJson);
        RouteDefinition def = manager.create(name, author, bactive, description, recipients, rule);
        manager.storeDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        t.printStackTrace();
        emessage = "Failed to create definition: '" + t.getMessage() + "'";
      }
    }

    return viewCreateRoute(model, name, author, active, description,
        interval, maxheight, mindistance, maxdistance, minelangle, minvelocitythreshold, fields,
        recipients, sources, filterJson, emessage);
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
  @RequestMapping("/route_show_wrwp.htm")
  public String showRoute(
      Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "interval", required = false) Integer interval,
      @RequestParam(value = "maxheight", required = false) Integer maxheight,
      @RequestParam(value = "mindistance", required = false) Integer mindistance,
      @RequestParam(value = "maxdistance", required = false) Integer maxdistance,
      @RequestParam(value = "minelangle", required = false) Double minelangle,
      @RequestParam(value = "minvelocitythreshold", required = false) Double minvelocitythreshold,
      @RequestParam(value = "fields", required = false) List<String> fields,
      @RequestParam(value = "recipients", required = false) List<String> recipients,
      @RequestParam(value = "sources", required = false) List<String> sources,
      @RequestParam(value = "filterJson", required = false) String filterJson,
      @RequestParam(value = "submitButton", required = false) String operation) {
    RouteDefinition def = manager.getDefinition(name);
    if (def == null) {
      return viewShowRoutes(model, "No route named \"" + name + "\"");
    }
    if (operation != null && operation.equals("Save")) {
      return modifyRoute(model, name, author, active, description, interval, maxheight, mindistance, maxdistance, minelangle, minvelocitythreshold, fields, recipients, sources, filterJson);
    } else if (operation != null && operation.equals("Delete")) {
      try {
        manager.deleteDefinition(name);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        return viewShowRoutes(model, "Failed to delete \"" + name + "\", have you verified that there are no reffering scheduled jobs");
      }
    } else {
      if (def.getRule() instanceof WrwpRule) {
        WrwpRule vrule = (WrwpRule)def.getRule();
        
        String filterstr = null;
        if (vrule.getFilter() != null) {
          try {
            filterstr = jsonMapper.writeValueAsString(vrule.getFilter());
          } catch (IOException e) {
            logger.error("failed to create JSON string from filter", e);
          }
        }
        
        return viewShowRoute(model, def.getName(), def.getAuthor(), def.isActive(), def.getDescription(),
            vrule.getInterval(), vrule.getMaxheight(), vrule.getMindistance(), vrule.getMaxdistance(), 
            vrule.getMinelevationangle(), vrule.getMinvelocitythreshold(), vrule.getFields(), def.getRecipients(), vrule.getSources(), filterstr, null);
      } else {
        return viewShowRoutes(model, "Atempting to show a route definition that not is a wrwp rule");
      }
    }
  }

  /**
   * Called when modifying a wrwp route. 
   * @param model
   * @param name
   * @param author
   * @param active
   * @param description
   * @param interval
   * @param maxheight
   * @param mindistance
   * @param maxdistance
   * @param minelangle
   * @param minvelocitythreshold
   * @param recipients
   * @param sources
   * @return
   */
  protected String modifyRoute(
      Model model, 
      String name, 
      String author, 
      Boolean active, 
      String description, 
      Integer interval, 
      Integer maxheight, 
      Integer mindistance, 
      Integer maxdistance, 
      Double minelangle, 
      Double minvelocitythreshold, 
      List<String> fields,
      List<String> recipients, 
      List<String> sources, 
      String jsonFilter) {
    List<String> newrecipients = (recipients == null) ? new ArrayList<String>() : recipients;
    List<String> newsources = (sources == null) ? new ArrayList<String>() : sources;
    List<String> newfields = (fields == null) ? new ArrayList<String>() : fields;
    boolean isactive = (active != null) ? active.booleanValue() : false;
    int iinterval = (interval != null) ? interval.intValue() : 200;
    int imaxheight = (maxheight != null) ? maxheight.intValue() : 12000;
    int imindistance = (mindistance != null) ? mindistance.intValue() : 4000;
    int imaxdistance = (maxdistance != null) ? maxdistance.intValue() : 40000;
    double dminelangle = (minelangle != null) ? minelangle.doubleValue() : 2.5;
    double dminvelocity = (minvelocitythreshold != null) ? minvelocitythreshold.doubleValue() : 2.0;
    String emessage = null;
    
    if (newsources.size() <= 0) {
      emessage = "You must specify at least one source.";
    } else if (newrecipients.size() <= 0) {
      emessage = "You must specify at least one recipient.";
    }
    
    if (emessage == null) {
      try {
        WrwpRule rule = createRule(iinterval, imaxheight, imindistance, imaxdistance, dminelangle, dminvelocity, newfields, newsources, jsonFilter);
        RouteDefinition def = manager.create(name, author, isactive, description, newrecipients, rule);
        manager.updateDefinition(def);
        return "redirect:routes.htm";
      } catch (Throwable t) {
        t.printStackTrace();
        emessage = "Failed to update definition: '" + t.getMessage() + "'";
      }
    }
    
    return viewShowRoute(model, name, author, active, description,
        interval, maxheight, mindistance, maxdistance, minelangle, minvelocitythreshold, newfields, recipients, newsources, jsonFilter, emessage);
  }
  
  /**
   * Sets the model with relevant information for showing the route_create_wrwp jsp page
   * @param model
   * @param name
   * @param author
   * @param active
   * @param description
   * @param interval
   * @param maxheight
   * @param mindistance
   * @param maxdistance
   * @param minelangle
   * @param minvelocitythreshold
   * @param recipients
   * @param sources
   * @param emessage
   * @return
   */
  protected String viewCreateRoute(
      Model model, 
      String name, 
      String author, 
      Boolean active, 
      String description,
      Integer interval, 
      Integer maxheight, 
      Integer mindistance, 
      Integer maxdistance, 
      Double minelangle, 
      Double minvelocitythreshold,
      List<String> fields,
      List<String> recipients, 
      List<String> sources,
      String jsonFilter,
      String emessage) {
    return viewJspRoute(
        model,
        name,
        author,
        active,
        description,
        interval,
        maxheight,
        mindistance,
        maxdistance,
        minelangle,
        minvelocitythreshold,
        fields,
        recipients,
        sources,
        jsonFilter,
        emessage,
        "route_create_wrwp");    
  }
  
  /**
   * Sets the model with relevant information for showing the route_show_wrwp jsp page
   * @param model
   * @param name
   * @param author
   * @param active
   * @param description
   * @param interval
   * @param maxheight
   * @param mindistance
   * @param maxdistance
   * @param minelangle
   * @param minvelocitythreshold
   * @param recipients
   * @param sources
   * @param emessage
   * @return
   */
  protected String viewShowRoute(
      Model model, 
      String name, 
      String author, 
      Boolean active, 
      String description,
      Integer interval, 
      Integer maxheight, 
      Integer mindistance, 
      Integer maxdistance, 
      Double minelangle, 
      Double minvelocitythreshold,
      List<String> fields,
      List<String> recipients, 
      List<String> sources,
      String filterJson,
      String emessage) {
    return viewJspRoute(
        model,
        name,
        author,
        active,
        description,
        interval,
        maxheight,
        mindistance,
        maxdistance,
        minelangle,
        minvelocitythreshold,
        fields,
        recipients,
        sources,
        filterJson,
        emessage,
        "route_show_wrwp");
  }
  
  /**
   * Utility page for setting the model and return the jsp page to load
   * @param model
   * @param name
   * @param author
   * @param active
   * @param description
   * @param interval
   * @param maxheight
   * @param mindistance
   * @param maxdistance
   * @param minelangle
   * @param minvelocitythreshold
   * @param recipients
   * @param sources
   * @param emessage
   * @param jsppage
   * @return
   */
  protected String viewJspRoute(
      Model model, 
      String name, 
      String author, 
      Boolean active, 
      String description,
      Integer interval, 
      Integer maxheight, 
      Integer mindistance, 
      Integer maxdistance, 
      Double minelangle, 
      Double minvelocitythreshold,
      List<String> fields,
      List<String> recipients, 
      List<String> sources,
      String jsonFilter,
      String emessage,
      String jsppage) {
    List<String> adaptors = adaptormanager.getAdaptorNames();
    model.addAttribute("name", (name == null) ? "" : name);
    model.addAttribute("author", (author == null) ? "" : author);
    model.addAttribute("active", (active == null) ? new Boolean(true) : active);
    model.addAttribute("description", (description == null) ? "" : description);
    model.addAttribute("interval", (interval == null) ? new Integer(200) : interval);
    model.addAttribute("maxheight", (maxheight == null) ? new Integer(12000) : maxheight);
    model.addAttribute("mindistance", (mindistance == null) ? new Integer(4000) : mindistance);
    model.addAttribute("maxdistance", (maxdistance == null) ? new Integer(40000) : maxdistance);
    model.addAttribute("minelangle", (minelangle == null) ? new Double(2.5) : minelangle);
    model.addAttribute("minvelocitythreshold", (minvelocitythreshold == null) ? new Double(2.0) : minvelocitythreshold);
    model.addAttribute("fields", (fields==null) ? new ArrayList<String>() : fields);
    model.addAttribute("recipients",
        (recipients == null) ? new ArrayList<String>() : recipients);
    model.addAttribute("sources",
        (sources == null) ? new ArrayList<String>() : sources);
    model.addAttribute("adaptors", adaptors);
    model.addAttribute("sourceids", utilities.getRadarSources());
    logger.info("Setting available_fields in model: " + availableFields.size());
    model.addAttribute("available_fields", availableFields);
    
    if (jsonFilter != null && !jsonFilter.equals("")) {
      model.addAttribute("filterJson", jsonFilter);
    }
    
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    
    return jsppage;
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
   * Creates the wrwp rule
   * @param interval
   * @param maxheight
   * @param mindistance
   * @param maxdistance
   * @param elangle
   * @param velocitythreshold
   * @return the wrwp rule
   */
  protected WrwpRule createRule(int interval, int maxheight, int mindistance, int maxdistance, double elangle, double velocitythreshold, List<String> fields, List<String> sources, String jsonFilter) {
    WrwpRule rule = (WrwpRule)manager.createRule(WrwpRule.TYPE);
    rule.setInterval(interval);
    rule.setMaxheight(maxheight);
    rule.setMindistance(mindistance);
    rule.setMaxdistance(maxdistance);
    rule.setMinelevationangle(elangle);
    rule.setMinvelocitythreshold(velocitythreshold);
    rule.setFields(fields);
    rule.setSources(sources);
    
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
