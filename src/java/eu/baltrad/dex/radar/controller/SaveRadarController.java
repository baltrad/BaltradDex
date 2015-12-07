/*******************************************************************************
*
* Copyright (C) 2009-2014 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
*******************************************************************************/

package eu.baltrad.dex.radar.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import eu.baltrad.bdb.db.Database;
import eu.baltrad.bdb.db.SourceManager;
import eu.baltrad.bdb.oh5.Source;
import eu.baltrad.dex.radar.manager.impl.RadarManager;
import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.util.MessageResourceUtil;

/**
 * Save radar controller.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.1.6
 */
@Controller
@RequestMapping("/radars_save.htm")
@SessionAttributes("radar")
public class SaveRadarController {

    private static final String FORM_VIEW = "radars_save";
    private static final String SUCCESS_VIEW = "radars_save_status";
    
    private static final String ODIM_LOAD_ERROR = "odim_load_error";
    
    private static final String SAVE_RADAR_OK_MSG_KEY = "saveradar.success";
    private static final String SAVE_RADAR_ERROR_MSG_KEY = "saveradar.failure";
    private static final String ODIM_LOAD_ERROR_MSG_KEY = 
            "saveradar.odim_load_failure";
    
    private static final String OK_MSG_KEY = "radar_save_success";
    private static final String ERROR_MSG_KEY = "radar_save_error";
    
    private RadarManager radarManager;
    private MessageResourceUtil messages;
    private Logger log;
    
    /**
     * The BDB database that keeps track of the sources
     */
    private Database database;
    
    /**
     * The logger 
     */
    private final static Logger logger = LogManager.getLogger(SaveRadarController.class);
    
    /**
     * Constructor.
     */
    public SaveRadarController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Set up form. 
     * @return View name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm() {
        return FORM_VIEW;
    }
    
    /**
     * Save radar.  
     * @param request Http servlet request
     * @return View name
     */
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(HttpServletRequest request, ModelMap model) {
        String centerId = request.getParameter("center_id");
        String[] radarIds = request.getParameterValues("radar_id");
        String viewName = "";
        String okmsg = "";
        String errormsg = "";
        if (centerId == null && radarIds == null) {
            viewName = FORM_VIEW;
        }
        if (centerId != null && radarIds == null) {
            request.setAttribute("center_selected", centerId);
            viewName = FORM_VIEW;
        }
        if (centerId != null && radarIds != null) {
          SourceManager sourceManager = database.getSourceManager();
          Radar radar = null;
          Source cSource = null;
          try {
            cSource = sourceManager.getSource(centerId);
          } catch (Exception e) {
            log.error("Failed to fetch source: " + e.getMessage());
            logger.error("Failed to fetch source: ", e);
            String msg = messages.getMessage(SAVE_RADAR_ERROR_MSG_KEY, 
              new Object[] {"", "", ""});
            model.addAttribute(ERROR_MSG_KEY, msg);
          }

          for (String radarId : radarIds) {
            try {
              Source rSource = sourceManager.getSource(radarId);
              radar = createRadar(cSource.getName().toUpperCase(), cSource.get("CCCC"), Integer.parseInt(cSource.get("ORG")), rSource.get("PLC"), rSource.get("RAD"), rSource.get("WMO"));
              radarManager.store(radar);
              String msg = messages.getMessage(SAVE_RADAR_OK_MSG_KEY, new Object[] {radar.getRadarPlace(), radar.getRadarCode(), radar.getRadarWmo()});
              okmsg = okmsg + msg + " " ;
              log.warn(msg);
            } catch (Exception e) {
              if (radar != null) {
                String msg = messages.getMessage(SAVE_RADAR_ERROR_MSG_KEY, 
                    new Object[] {radar.getRadarPlace(), radar.getRadarCode(), radar.getRadarWmo()});
                log.error(msg, e);
                errormsg = errormsg + msg + " " ;
              } else {
                String msg = messages.getMessage(SAVE_RADAR_ERROR_MSG_KEY, 
                    new Object[] {centerId, radarId, ""});
                log.error(msg, e);
                errormsg = errormsg + msg + " " ;
              }
            }
          }
          if (okmsg.length() > 0) {
            model.addAttribute(OK_MSG_KEY, okmsg);
          }
          if (errormsg.length() > 0) {
            model.addAttribute(ERROR_MSG_KEY, errormsg);
          }
          viewName = SUCCESS_VIEW;
        }
        return viewName;
    }
    
    /**
     * Load center names from XML document.
     * @return List containing center names
     */
    @ModelAttribute("centers")
    public List<KeyValuePair> loadCenters(ModelMap model) {
      List<KeyValuePair> centers = new ArrayList<KeyValuePair>();
      try {
        List<Source> parentSources = database.getSourceManager()
            .getParentSources();
        for (Source src : parentSources) {
          String centerCode = src.getName();
          if (src.has("CCCC")) {
            centerCode = centerCode + " - " + src.get("CCCC");
          }
          if (src.has("ORG")) {
            centerCode = centerCode + " - " + src.get("ORG");
          }
          centers.add(new KeyValuePair(src.getName(), centerCode.toUpperCase()));
        }
      } catch (Exception e) {
        model.addAttribute(ODIM_LOAD_ERROR, messages.getMessage(ODIM_LOAD_ERROR_MSG_KEY));
        logger.error("Failed to load odim", e);
      }
      
      return centers;
    }
    
    /**
     * Load radar names from XML document.
     * @param request Http servlet request
     * @return List containing radar names
     */
    @ModelAttribute("radars")
    public List<KeyValuePair> loadRadars(HttpServletRequest request, ModelMap model) {
      List<KeyValuePair> radars = new ArrayList<KeyValuePair>();
      try {
        String centerId = request.getParameter("center_id");
        if (centerId != null) { 
          String countryCode = centerId.toLowerCase().trim();
          List<Source> sources = database.getSourceManager().getSourcesWithParent(countryCode);
          for (Source src : sources) {
            String radar = "";
            if (src.has("PLC")) {
              radar = src.get("PLC");
            }
            if (src.has("RAD")) {
              radar = radar + " - " + src.get("RAD");
            }
            if (src.has("WMO")) {
              radar = radar + " - " + src.get("WMO");
            }
            radars.add(new KeyValuePair(src.getName(), radar));
          }
        }
      } catch (Exception e) {
        model.addAttribute(ODIM_LOAD_ERROR, messages.getMessage(ODIM_LOAD_ERROR_MSG_KEY));
      }
      return radars;
    }
    
    protected Radar createRadar(String countryid, String cccc, int org, String plc, String rad, String wmo) {
      return new Radar(countryid, cccc, org, plc, rad, wmo);
    }
    /**
     * @param radarManager 
     */
    @Autowired
    public void setRadarManager(RadarManager radarManager) {
        this.radarManager = radarManager;
    }

    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
    @Autowired
    public void setRestfulDatabase(Database database) {
      this.database = database;
    }
    
}
