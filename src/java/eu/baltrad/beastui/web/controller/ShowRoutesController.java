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

import java.util.List;

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
  
  @RequestMapping("/showroutes.htm")
  public String showRoutes(Model model) {
    logger.debug("showRoutes(Model)");
    List<RouteDefinition> definitions = manager.getDefinitions();
    model.addAttribute("routes", definitions);
    return "showroutes";
  }
  
  @RequestMapping("/createroute.htm")
  public String createRoute(
      Model model,
      @RequestParam(value = "submitButton", required = false) String operation) {
    if (operation != null && operation.equals("Script")) {
      return "redirect:groovyroute_create.htm";
    } else if (operation != null && operation.equals("Composite")) {
      return "redirect:compositeroute_create.htm";
    } else if (operation != null && operation.equals("Volume")) {
      return "redirect:volumeroute_create.htm";
    } else if (operation != null && operation.equals("BdbTrimAge")) {
      return "redirect:bdbtrimageroute_create.htm";
    }
    model.addAttribute("emessage", "Unknown operation: '"+operation+"'");
    return "redirect:showroutes.htm";
  }
  
  @RequestMapping("/showroute.htm")
  public String showRoute(
      Model model,
      @RequestParam(value = "name") String name) {
    RouteDefinition def = manager.getDefinition(name);
    String result = null;
    if (def != null) {
      String type = def.getRuleType();
      if (type.equals("groovy")) {
        result = "redirect:groovyroute_show.htm";
      } else if (type.equals("blt_composite")) {
        result = "redirect:compositeroute_show.htm";
      } else if (type.equals("blt_volume")) {
        result = "redirect:volumeroute_show.htm";
      } else if (type.equals("bdb_trim_age")) {
        result = "redirect:bdbtrimageroute_show.htm";
      }
      if (result != null) {
        model.addAttribute("name", name);
      }
    }
  
    if (result == null) {
      result = "redirect:showroutes.htm";
    }
    return result;
  }
}
