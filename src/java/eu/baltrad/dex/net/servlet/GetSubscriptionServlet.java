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

package eu.baltrad.dex.net.servlet;

import eu.baltrad.dex.net.model.NodeResponse;
import eu.baltrad.dex.net.util.*;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.Subscription;
import eu.baltrad.dex.net.model.ISubscriptionManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletInputStream;

import org.apache.log4j.Logger;

import java.io.StringWriter;
import java.io.PrintWriter;

import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

/**
 * Receives and handles subscription requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@Controller
public class GetSubscriptionServlet extends HttpServlet{
    
    /**
     * Implements Controller interface.
     * @param request Http servlet request
     * @param response Http servlet response
     * @return Model and view
     */
    @RequestMapping("/get_subscription.htm")
    public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        doPost(request, response);
        return new ModelAndView();
    }
    
     /**
     * Actual mathod processing requests.
     * @param request Http servlet request
     * @param response Http servlet response 
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
    {
        
    }
    
    
}
