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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.baltrad.beast.system.ISystemSupervisor;
import eu.baltrad.beast.system.XmlSystemStatusGenerator;
import eu.baltrad.beast.system.host.IHostFilterManager;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;

/**
 * Controller providing supervisor status information.
 * 
 * @author Anders Henja
 */
@Controller
public class SupervisorController {
  /**
   * The system supervisor
   */
  private ISystemSupervisor supervisor = null;

  /**
   * The host manager
   */
  private IHostFilterManager hostManager = null;

  /**
   * The user manager for peer information
   */
  private IUserManager userManager;
  
  /**
   * The logger
   */
  private static Logger logger = LogManager
      .getLogger(SupervisorController.class);

  /**
   * Default constructor
   */
  public SupervisorController() {
  }

  @Autowired
  public void setSupervisor(ISystemSupervisor supervisor) {
    this.supervisor = supervisor;
  }

  @Autowired
  public void setHostManager(IHostFilterManager hostManager) {
    this.hostManager = hostManager;
  }

  @Autowired
  public void setUserManager(IUserManager userManager) {
    this.userManager = userManager;
  }
  
  @RequestMapping(value = "/supervisor_settings.htm")
  public String supervisorSettings(Model model,
      HttpServletRequest request) {
    logger.debug("supervisorSettings(Model)");
    List<String> filters = hostManager.getPatterns();
    model.addAttribute("filters", filters);
    model.addAttribute("currentip", request.getRemoteAddr());
    return "supervisor_settings";
  }

  @RequestMapping(value = "/supervisor_add_setting.htm")
  public String addSupervisorSetting(Model model,
      @RequestParam(value = "filter", required = false) String filter,
      HttpServletRequest request) {
    String emessage = null;
    String nfilter = filter;
    logger.debug("createSupervisor(Model)");
    if (filter != null) {
      if (!hostManager.isRegistered(filter)) {
        try {
          hostManager.add(filter);
          nfilter = null;
        } catch (Exception e) {
          emessage = "Failed to add filter: " + e.getMessage();
        }
      } else {
        emessage = "Filter already registered";
      }
    }
    if (nfilter == null) {
      nfilter = "";
    }
    List<String> filters = hostManager.getPatterns();
    model.addAttribute("filter", nfilter);
    model.addAttribute("filters", filters);
    model.addAttribute("currentip", request.getRemoteAddr());
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "supervisor_settings";
  }

  @RequestMapping(value = "/supervisor_remove_setting.htm")
  public String removeSupervisorSetting(Model model,
      @RequestParam(value = "filter", required = false) String filter,
      HttpServletRequest request) {
    String emessage = null;
    logger.debug("removeSupervisorSetting");
    if (filter != null) {
      try {
        hostManager.remove(filter);
      } catch (Exception e) {
        emessage = "Failed to remove filter: " + e.getMessage();
      }
    }
    List<String> filters = hostManager.getPatterns();
    model.addAttribute("filters", filters);
    model.addAttribute("currentip", request.getRemoteAddr());
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "supervisor_settings";
  }

  /**
   * The actual supervisor.htm interface for producing information to the user
   * 
   * @param format
   * @param system
   * @param radars
   * @param request
   * @param response
   */
  @RequestMapping(value = "/supervisor.htm")
  public void supervisorStatus(
      @RequestParam(value = "format", required = false) String format,
      @RequestParam(value = "reporters", required = false) String reportersstr,
      @RequestParam(value = "sources", required = false) String sources,
      @RequestParam(value = "areas", required = false) String areas,
      @RequestParam(value = "peers", required = false) String peers,
      @RequestParam(value = "objects", required = false) String objects,
      @RequestParam(value = "minutes", required = false) String minutes,
      HttpServletRequest request, HttpServletResponse response) {
    logger.info("supervisor: format='" + format + "', reporters='"
        + reportersstr + "', sources='" + sources + "', areas='" + areas
        + "', objects='" + objects + "', minutes='" + minutes);

    XmlSystemStatusGenerator generator = getXmlGenerator();
    if (isAuthorized(request)) {
      if (reportersstr == null) {
        reportersstr = "bdb.status,db.status"; // The minimum information the user should get
      }
      String[] reporters = reportersstr.split(",");
      
      Map<String, Object> values = createMap(sources, areas, peers, objects, minutes);

      // Add dynamic attributes from the odim information model that resides in what,where and how.
      //
      Enumeration<String> names = request.getParameterNames();
      Map<String, String> optional = new HashMap<String,String>();
      
      while (names.hasMoreElements()) {
        String n = names.nextElement();
        String lcn = n.toLowerCase();
        if (lcn.startsWith("where/") || lcn.startsWith("what/") || lcn.startsWith("how/")) {
          String v = request.getParameter(n);
          optional.put(lcn, v);
          values.put(lcn,  v);
        }
        logger.info("Parameter name (" + n + ") = " + request.getParameter(n));
      }
      
      for (String s : reporters) {
        String str = s.trim();
        String statusvalue = createValueString(str, sources, areas, peers, objects, minutes, optional);
        if (supervisor.supportsMappableStatus(str)) {
          generator.add(str, supervisor.getMappedStatus(str, values).get(str));
        } else {
          generator.add(str, statusvalue, supervisor.getStatus(str, values));
        }
      }
      
      try {
        byte[] bytes = generator.getXmlString().getBytes("UTF-8");
        response.getOutputStream().write(bytes);
        response.setStatus(HttpServletResponse.SC_OK); 
      } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
      }
    } else {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
  }
  
  /**
   * Creates a hash map to be used for passing values to the supervisor
   * @param sources the sources
   * @param areas the areas
   * @param peers the peers
   * @param objects the objects
   * @param minutes the minutes
   * @return the map
   */
  protected Map<String, Object> createMap(String sources, String areas, String peers, String objects, String minutes) {
    Map<String, Object> values = new HashMap<String, Object>();
    if (sources != null) {
      values.put("sources", sources);
    }
    if (areas != null) {
      values.put("areas", areas);
    }
    if (peers != null) {
      values.put("peers", peers);
    }
    if (objects != null) {
      values.put("objects", objects);
    }
    if (minutes != null) {
      values.put("minutes", minutes);
    }
    return values;
  }

  /**
   * Creates the value string that should exist in the xml report
   * @param reporter the reporter
   * @param sources the sources if any
   * @param areas the areas if any
   * @param peers the peers if any
   * @param objects the objects if any
   * @param minutes the minutes if any
   * @return the string
   */
  protected String createValueString(String reporter, String sources, String areas, String peers, String objects, String minutes, Map<String, String> optional) {
    StringBuffer vbuf = new StringBuffer();
    Set<String> attrs = supervisor.getSupportedAttributes(reporter);
    if (attrs != null) {
      if (sources != null && attrs.contains("sources")) {
        vbuf.append("sources=").append(sources);
      }
      if (areas != null && attrs.contains("areas")) {
        if (vbuf.length()>0) {
          vbuf.append("&");
        }
        vbuf.append("areas=").append(areas);
      }
      if (peers != null && attrs.contains("peers")) {
        if (vbuf.length()>0) {
          vbuf.append("&");
        }
        vbuf.append("peers=").append(peers);
      }
      if (objects != null && attrs.contains("objects")) {
        if (vbuf.length()>0) {
          vbuf.append("&");
        }
        vbuf.append("objects=").append(objects);
      }
      if (minutes != null && attrs.contains("minutes")) {
        if (vbuf.length()>0) {
          vbuf.append("&");
        }
        vbuf.append("minutes=").append(minutes);
      }
      for (String v : optional.keySet()) {
        if (optional.get(v) != null) {
          if (vbuf.length() > 0) {
            vbuf.append("&");
          }
          vbuf.append(v + "=" + optional.get(v));
        }
      }
    }
    return vbuf.toString();
  }
  
  /**
   * @return a fresh xml generator instance
   */
  protected XmlSystemStatusGenerator getXmlGenerator() {
    return new XmlSystemStatusGenerator();
  }

  /**
   * Returns if this request is allowed to be processed.
   * 
   * @param request
   *          the request
   * @return true if this request can be handled.
   */
  protected boolean isAuthorized(HttpServletRequest request) {
    String remote = request.getRemoteAddr();
    logger.info("Got request from " + remote);
    if (remote != null
        && (remote.equals("127.0.0.1") || hostManager.accepted(remote))) {
      return true;
    }
    return false;
  }
}
