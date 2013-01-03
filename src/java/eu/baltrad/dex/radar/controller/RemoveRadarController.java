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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;

import java.io.IOException;

/**
 * Remove radar.
 *
 * @author szewczenko
 * @version 1.2.2
 * @since 1.0.0
 */
@Controller
public class RemoveRadarController {
    
    private static final String REMOVE_RADAR_VIEW = "remove_radar";
    private static final String REMOVE_SELECTED_RADAR_VIEW = 
            "remove_selected_radar";
    private static final String REMOVE_RADAR_STATUS_VIEW = 
            "remove_radar_status";
    
    private static final String RADARS_KEY = "radars";
    private static final String REMOVE_RADAR_OK_MSG_KEY = "removeradar.success";
    private static final String REMOVE_RADAR_COMPLETED_OK_MSG_KEY = 
            "removeradar.completed_success";
    private static final String REMOVE_RADAR_COMPLETED_ERROR_MSG_KEY = 
            "removeradar.completed_failure";
    
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";   

    private RadarManager radarManager;
    private PlatformTransactionManager transactionManager;
    private MessageResourceUtil messages;
    private Logger log;

    /**
     * Constructor.
     */
    public RemoveRadarController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Loads all radars.
     * @param model Model map 
     * @return View name
     */
    @RequestMapping("/remove_radar.htm")
    public String removeRadar(ModelMap model) {
        model.addAttribute(RADARS_KEY, radarManager.load());
        return REMOVE_RADAR_VIEW;
    }
    
    /**
     * Lists radars selected for removal.
     * @param request Http request
     * @param response Http response
     * @param model Model map
     * @return View name
     * @throws IOException 
     */
    @RequestMapping("/remove_selected_radar.htm")
    public String removeSelectedRadar(HttpServletRequest request,
            HttpServletResponse response, ModelMap model) throws IOException {
        String[] radarIds = request.getParameterValues(RADARS_KEY);
        if (radarIds != null) {
            List<Radar> radars = new ArrayList<Radar>();
            for (int i = 0; i < radarIds.length; i++) {
                radars.add(radarManager.load(Integer.parseInt(radarIds[i])));
            }
            model.addAttribute(RADARS_KEY, radars);
        } else {
            response.sendRedirect("remove_radar.htm");
        }
        return REMOVE_SELECTED_RADAR_VIEW;
    }
    
    /**
     * Removes radars and renders status page.
     * @param request Http request
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/remove_radar_status.htm")
    public String removeRadarStatus(HttpServletRequest request, 
                    ModelMap model) {
        String[] radarIds = request.getParameterValues(RADARS_KEY);
        // begin transaction
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            for (int i = 0; i < radarIds.length; i++) {
                Radar radar = radarManager.load(
                        Integer.parseInt(radarIds[i]));
                radarManager.delete(Integer.parseInt(radarIds[i]));
                String msg = messages.getMessage(REMOVE_RADAR_OK_MSG_KEY, 
                                new Object[] {radar.getRadarPlace(),
                                radar.getRadarCode(), radar.getRadarWmo()});
                log.warn(msg);
            }    
            transactionManager.commit(status);
            request.getSession().setAttribute(OK_MSG_KEY, 
                messages.getMessage(REMOVE_RADAR_COMPLETED_OK_MSG_KEY));
        } catch (Exception e) {   
            transactionManager.rollback(status);
            request.getSession().setAttribute(ERROR_MSG_KEY, 
                messages.getMessage(REMOVE_RADAR_COMPLETED_ERROR_MSG_KEY));
        }
        return REMOVE_RADAR_STATUS_VIEW;
    }
    
    /**
     * @param radarManager 
     */
    @Autowired
    public void setRadarManager(RadarManager radarManager) {
        this.radarManager = radarManager;
    }

    /**
     * @param transactionManager the transactionManager to set
     */
    @Autowired
    public void setTransactionManager(
            PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
}

