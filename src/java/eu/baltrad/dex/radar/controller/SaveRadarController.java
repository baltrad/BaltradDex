/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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

import eu.baltrad.dex.radar.manager.impl.RadarManager;
import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import org.apache.log4j.Logger;

import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;

import javax.servlet.http.HttpServletRequest;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

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
    private static final String ODIM_SOURCE_FILE = "conf/odim_source.xml";
    
    private static final String SAVE_RADAR_OK_MSG_KEY = "saveradar.success";
    private static final String SAVE_RADAR_ERROR_MSG_KEY = "saveradar.failure";
    
    private static final String OK_MSG_KEY = "radar_save_success";
    private static final String ERROR_MSG_KEY = "radar_save_error";
    
    private RadarManager radarManager;
    private MessageResourceUtil messages;
    private Document xmlDoc;
    private Logger log;

    /**
     * Constructor.
     */
    public SaveRadarController() {
        this.log = Logger.getLogger("DEX");
        try {
            xmlDoc = parse(this.getClass().getResource(ODIM_SOURCE_FILE));
        } catch (DocumentException e) {
            log.error("Failed to load odim sources definition file", e);
        }
    }
    
    /**
     * Parse XML document.
     * @param url Document's URL
     * @return XML document instance
     * @throws DocumentException 
     */
    private Document parse(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(url);
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
        String radarId = request.getParameter("radar_id");
        String viewName = "";
        if (centerId == null && radarId == null) {
            viewName = FORM_VIEW;
        }
        if (centerId != null && radarId == null) {
            request.setAttribute("center_selected", centerId);
            viewName = FORM_VIEW;
        }
        if (centerId != null && radarId != null) {
            Radar radar = null;
            try {
                String[] centerParms = centerId.split(" - ");
                String[] radarParms = radarId.split(" - ");
                radar = new Radar(centerParms[0], centerParms[1], 
                        Integer.parseInt(centerParms[2]), 
                        radarParms[0], radarParms[1], radarParms[2]);
                radarManager.store(radar);
                String msg = messages.getMessage(SAVE_RADAR_OK_MSG_KEY, 
                            new Object[] {radar.getRadarPlace(), 
                                radar.getRadarCode(), radar.getRadarWmo()});
                model.addAttribute(OK_MSG_KEY, msg);
                log.warn(msg);
            } catch (Exception e) {
                String msg = messages.getMessage(SAVE_RADAR_ERROR_MSG_KEY, 
                            new Object[] {radar.getRadarPlace(), 
                                radar.getRadarCode(), radar.getRadarWmo()});
                model.addAttribute(ERROR_MSG_KEY, msg);
                log.error(msg, e);
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
    public List<String> loadCenters() {
        Element root = xmlDoc.getRootElement();
        List<String> centers = new ArrayList<String>();
        for (Iterator i = root.elementIterator(); i.hasNext();) {   
            Element element = (Element) i.next();
            String centerCode = element.getName();
            for (Iterator j = element.attributeIterator(); j.hasNext();) {
                Attribute attribute = (Attribute) j.next();
                centerCode += " - " + attribute.getValue();
            }
            centers.add(centerCode.toUpperCase());
        }
        return centers;
    }
    
    /**
     * Load radar names from XML document.
     * @param request Http servlet request
     * @return List containing radar names
     */
    @ModelAttribute("radars")
    public List<String> loadRadars(HttpServletRequest request) {
        String centerId = request.getParameter("center_id"); 
        List<String> radars = new ArrayList<String>();
        if (centerId != null) {
            String countryCode = centerId.substring(0, 2).toLowerCase();
            Element root = xmlDoc.getRootElement();
            for (Iterator i = root.elementIterator(countryCode); i.hasNext();) {
                Element element = (Element) i.next();
                for (Iterator j = element.elementIterator(); j.hasNext();) {
                    Element elem = (Element) j.next();
                    String radar = "";
                    for (Iterator k = elem.attributeIterator(); k.hasNext();) {
                        Attribute attribute = (Attribute) k.next();
                        radar += attribute.getValue();
                        radar += " - ";
                    }
                    radars.add(radar.trim().substring(0, radar.length() - 2));
                }
            }
        }
        return radars;
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
    
}
