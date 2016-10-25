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
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;

/**
 * Controller for showing routes and directing you to correct
 * route creator/modifier.
 * @author Anders Henja
 */
@Controller
public class ShowRoutesController {
  /**
   * The router manager
   */
  private IRouterManager manager = null;
  
  /**
   * We need a logger here
   */
  private static Logger logger = LogManager.getLogger(AdaptorsController.class);
  
  public static final String ROUTES_COMPARATORS_ATTR = "routes:comparators";
  
  public static final String SORT_BY_NAME_TAG = "Name";
  public static final String SORT_BY_TYPE_TAG = "Type";
  public static final String SORT_BY_ACTIVE_TAG = "Active";
  public static final String SORT_BY_DESCRIPTION_TAG = "Description";

  /**
   * Constructor
   */
  public ShowRoutesController() {
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
  
  @SuppressWarnings("unchecked")
  @RequestMapping("/routes.htm")
  public String showRoutes(Model model, 
      HttpServletRequest request,
      HttpSession httpSession,
      @RequestParam(value = "sortBy", required = false) String sortBy) {
    
    logger.debug("showRoutes(Model)");

    List<RouteDefinition> definitions = manager.getDefinitions();
    
    Object orderingObject = httpSession.getAttribute(ROUTES_COMPARATORS_ATTR);
    
    ArrayList<RouteDefinition.RouteComparator> routeComparators;
    
    if (orderingObject == null) {
      routeComparators = createDefaultComparators();
    } else if (orderingObject instanceof ArrayList) {
      routeComparators = (ArrayList<RouteDefinition.RouteComparator>) orderingObject;
    } else {
      logger.warn("Invalid ordering information in HttpSession. Cannot perform ordering of routes.");
      model.addAttribute("routes", definitions); 
      return "routes";
    }
    
    if (sortBy != null) {
      updateOrdering(routeComparators, sortBy);
    }
 
    sortRouteDefinitions(definitions, routeComparators);
    httpSession.setAttribute(ROUTES_COMPARATORS_ATTR, routeComparators);
    
    model.addAttribute("routes", definitions); 
    
    return "routes";
  }
  
  private void updateOrdering(ArrayList<RouteDefinition.RouteComparator> comparators, String sortOnCategory) {
    RouteDefinition.RouteComparator newComparator = getRouteDefinitinoComparator(sortOnCategory);
    
    boolean comparatorExists = false;
    int comparatorIndex = 0;
    for (RouteDefinition.RouteComparator comparator : comparators) {
      if (comparator.getClass().equals(newComparator.getClass())) {
        comparatorExists = true;
        comparatorIndex = comparators.indexOf(comparator);
        break;
      }
    }
    
    if (comparatorExists) {
      newComparator = comparators.remove(comparatorIndex);
      newComparator.switchOrder();      
    }
    
    comparators.add(newComparator);
  }
  
  private ArrayList<RouteDefinition.RouteComparator> createDefaultComparators() {
    ArrayList<RouteDefinition.RouteComparator> routeComparators = new ArrayList<RouteDefinition.RouteComparator>();
    
    routeComparators.add(new RouteDefinition.NameComparator());
    routeComparators.add(new RouteDefinition.TypeComparator());
    routeComparators.add(new RouteDefinition.ActiveComparator());
    
    return routeComparators;
  }
  
  private void sortRouteDefinitions(List<RouteDefinition> definitions, ArrayList<RouteDefinition.RouteComparator> comparators) {
    for (RouteDefinition.RouteComparator comparator : comparators) {
      Collections.sort(definitions, comparator);
    }
  }
  
  private RouteDefinition.RouteComparator getRouteDefinitinoComparator(String category) {
    if (category.equals(SORT_BY_NAME_TAG)) {
      return new RouteDefinition.NameComparator();
    } else if (category.equals(SORT_BY_DESCRIPTION_TAG)) {
      return new RouteDefinition.DescriptionComparator();
    } else if (category.equals(SORT_BY_TYPE_TAG)) {
      return new RouteDefinition.TypeComparator();
    } else if (category.equals(SORT_BY_ACTIVE_TAG)) {
      return new RouteDefinition.ActiveComparator();
    }
    
    return null;
  }
  
  @RequestMapping("/route_create.htm")
  public String createRoute(
      Model model,
      @RequestParam(value = "submitButton", required = false) String operation) {
    if (operation != null && operation.equals("Script")) {
      return "redirect:route_create_groovy.htm";
    } else if (operation != null && operation.equals("Composite")) {
      return "redirect:route_create_composite.htm";
    } else if (operation != null && operation.equals("Volume")) {
      return "redirect:route_create_volume.htm";
    } else if (operation != null && operation.equals("BdbTrimAge")) {
      return "redirect:route_create_bdb_trim_age.htm";
    } else if (operation != null && operation.equals("BdbTrimCount")) {
      return "redirect:route_create_bdb_trim_count.htm";
    } else if (operation != null && operation.equals("GoogleMap")) {
      return "redirect:route_create_google_map.htm";
    } else if (operation != null && operation.equals("ACRR")) {
      return "redirect:route_create_acrr.htm";
    } else if (operation != null && operation.equals("GRA")) {
      return "redirect:route_create_gra.htm";
    } else if (operation != null && operation.equals("WRWP")) {
      return "redirect:route_create_wrwp.htm";
    } else if (operation != null && operation.equals("ScanSun")) {
      return "redirect:route_create_scansun.htm";
    } else if (operation != null && operation.equals("Site2D")) {
      return "redirect:route_create_site2d.htm";
    }
    model.addAttribute("emessage", "Unknown operation: '"+operation+"'");
    return "redirect:routes.htm";
  }
  
  @RequestMapping("/route.htm")
  public String showRoute(
      Model model,
      @RequestParam(value = "name") String name) {
    logger.debug("showRoute("+name+")");
    RouteDefinition def = manager.getDefinition(name);
    String result = null;
    if (def != null) {
      String type = def.getRuleType();
      if (type.equals("groovy")) {
        result = "redirect:route_show_groovy.htm";
      } else if (type.equals("blt_composite")) {
        result = "redirect:route_show_composite.htm";
      } else if (type.equals("blt_volume")) {
        result = "redirect:route_show_volume.htm";
      } else if (type.equals("distribution")) {
        result = "redirect:route_create_distribution.htm";
      } else if (type.equals("bdb_trim_age")) {
        result = "redirect:route_show_bdb_trim_age.htm";
      } else if (type.equals("bdb_trim_count")) {
        result = "redirect:route_show_bdb_trim_count.htm";
      } else if (type.equals("blt_gmap")) {
        result = "redirect:route_show_google_map.htm";
      } else if (type.equals("blt_acrr")) {
        result = "redirect:route_show_acrr.htm";
      } else if (type.equals("blt_gra")) {
        result = "redirect:route_show_gra.htm";
      } else if (type.equals("blt_wrwp")) {
        result = "redirect:route_show_wrwp.htm";
      } else if (type.equals("blt_scansun")) {
        result = "redirect:route_show_scansun.htm";
      } else if (type.equals("blt_site2d")) {
        result = "redirect:route_show_site2d.htm";
      }

      if (result != null) {
        model.addAttribute("name", name);
      }
    }
  
    if (result == null) {
      result = "redirect:routes.htm";
    }
    return result;
  }
}
