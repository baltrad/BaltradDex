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

import eu.baltrad.beast.adaptor.AdaptorException;
import eu.baltrad.beast.adaptor.IAdaptor;
import eu.baltrad.beast.adaptor.IAdaptorConfiguration;
import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.adaptor.xmlrpc.XmlRpcAdaptor;
import eu.baltrad.beast.adaptor.xmlrpc.XmlRpcAdaptorConfiguration;

/**
 * Controller managing the adaptor configuration
 * 
 * @author Anders Henja
 */
@Controller
public class AdaptorsController {
  /**
   * The manager instance for taking care of the adaptors
   */
  private IBltAdaptorManager manager = null;
  
  /**
   * We need a logger here
   */
  private static Logger logger = LogManager.getLogger(AdaptorsController.class);

  /**
   * Default constructor
   */
  public AdaptorsController() {
  }

  @Autowired
  public void setManager(IBltAdaptorManager manager) {
    logger.debug("setManager(IBltAdaptorManager)");
    this.manager = manager;
  }

  @RequestMapping("/adaptors.htm")
  public String showAdaptors(Model model,
      @RequestParam(value="emessage", required=false) String emessage) {
    logger.debug("showAdaptors(Model)");
    model.addAttribute("emessage", emessage);
    model.addAttribute("adaptors", manager.getRegisteredAdaptors());
    return "adaptors";
  }

  @RequestMapping("/showadaptor.htm")
  public String showAdaptor(Model model, @RequestParam("name") String name) {
    logger.debug("showAdaptor(Model,..)");
    IAdaptor adaptor = manager.getAdaptor(name);
    if (adaptor != null) {
      List<String> types = manager.getAvailableTypes();
      model.addAttribute("types", types);
      model.addAttribute("name", adaptor.getName());
      model.addAttribute("type", adaptor.getType());
      if (adaptor instanceof XmlRpcAdaptor) {
        XmlRpcAdaptor xmladaptor = (XmlRpcAdaptor) adaptor;
        model.addAttribute("uri", xmladaptor.getUrl());
        model.addAttribute("timeout", xmladaptor.getTimeout());
      }
      return "showadaptor";
    } else {
      logger.debug("showAdaptor(Model,..): Could not find adaptor: " + name);
      model.addAttribute("emessage", "Could not retrieve adaptor " + name);
      model.addAttribute("adaptors", manager.getRegisteredAdaptors());
      return "adaptors";
    }
  }

  @RequestMapping("/modifyadaptor.htm")
  public String modifyAdaptor(Model model, @RequestParam("name") String name,
      @RequestParam("type") String type,
      @RequestParam(value = "uri", required = false) String uri,
      @RequestParam(value = "timeout", required = false) Long timeout,
      @RequestParam(value = "submitButton") String operation) {
    logger.debug("modifyAdaptor(Model,..)");
    
    String result = null;
    String emessage = null;
    if (operation.equals("Delete")) {
      try {
        manager.unregister(name);
        result = "redirect:adaptors.htm";
      } catch (Throwable t) {
        logger.error("Failed to remove adaptor: " + name);
        emessage = "Failed to remove adaptor";
      }
    } else {
      try {
        IAdaptorConfiguration conf = manager.createConfiguration(type, name);
        if (conf instanceof XmlRpcAdaptorConfiguration) {
          XmlRpcAdaptorConfiguration xmlrpcconf = (XmlRpcAdaptorConfiguration) conf;
          if (uri != null && !uri.equals("") && timeout != null && timeout != 0) {
            xmlrpcconf.setURL(uri);
            xmlrpcconf.setTimeout(timeout);
            manager.reregister(xmlrpcconf);
            result = "redirect:adaptors.htm";
          } else {
            emessage = "Uri or timeout not specified correctly.";
          }
        }
      } catch (Throwable t) {
        logger.error("Failed to update configuration", t);
        emessage = "Bad arguments: " + t.getMessage();
      }
    }

    if (result == null) {
      model.addAttribute("types", manager.getAvailableTypes());
      model.addAttribute("name", name);
      model.addAttribute("type", type);
      model.addAttribute("uri", uri);
      model.addAttribute("timeout", timeout);
      model.addAttribute("emessage", emessage);
      result = "showadaptor";
    }
    return result;
  }

  @RequestMapping("/createadaptor.htm")
  public String createAdaptor(Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "uri", required = false) String uri,
      @RequestParam(value = "timeout", required = false) Long timeout,
      @RequestParam(value = "submitButton", required = false) String op) {
    logger.debug("createAdaptor(Model,..)");

    List<String> types = manager.getAvailableTypes();
    
    if (op != null && op.equals("Add")) {
      try {
        IAdaptorConfiguration conf = manager.createConfiguration(type, name);
        if (conf != null) {
          ((XmlRpcAdaptorConfiguration) conf).setURL(uri);
          ((XmlRpcAdaptorConfiguration) conf).setTimeout(timeout);
          manager.register(conf);
          return "redirect:adaptors.htm";
        }
      } catch (AdaptorException e) {
        logger.error("Failed to register adaptor", e);
        model.addAttribute("name", name);
        model.addAttribute("type", type);
        model.addAttribute("uri", uri);
        model.addAttribute("timeout", timeout);
        model.addAttribute("emessage", "Failed to register adaptor: " + e.getMessage());
        logger.debug("createAdaptor(Model,..): Failed to register adaptor " + name);
      }
    }
    
    model.addAttribute("types", types);
    return "createadaptor";
  }
}
