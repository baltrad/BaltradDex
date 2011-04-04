/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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
***************************************************************************************************/

package eu.baltrad.dex.subscription.controller;

import eu.baltrad.dex.subscription.model.SubscriptionManager;
import eu.baltrad.dex.subscription.model.Subscription;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.List;

/**
 * Controller renders data download status page presented to te user upon successfull login.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.6
 * @since 1.6
 */
public class DownloadStatusController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    /** Operators key */
    private final static String OPERATORS = "operators";
    /** References "operator_name" field in dex_subscriptions table */
    private final static String OPERATOR_NAME_FIELD = "operator_name";
    /** Local subscriprions key */
    private static final String LOCAL_SUBSCRIPTION = "local";
//---------------------------------------------------------------------------------------- Variables
    private SubscriptionManager subscriptionManager;
    private String successView;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Gets local subscriptions grouped by operators and remote subscriptions grouped by users.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return ModelAndView holding grouped lists of subscriptions
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException {
        ModelAndView modelAndView = new ModelAndView( getSuccessView() );
        // get operators
        List<String> operators = subscriptionManager.getDistinct( OPERATOR_NAME_FIELD );
        modelAndView.addObject( OPERATORS, operators );
        // get local subscriptions
        List<Subscription> localSubscriptions = subscriptionManager.getSubscriptions(
                Subscription.LOCAL_SUBSCRIPTION );
        modelAndView.addObject( LOCAL_SUBSCRIPTION, localSubscriptions );
        return modelAndView;
    }
    /**
     * Gets reference to SubscriptionManager object.
     *
     * @return Reference to SubscriptionManager object
     */
    public SubscriptionManager getSubscriptionManager() { return subscriptionManager; }
    /**
     * Sets reference to SubscriptionManager object.
     *
     * @param subscriptionManager Reference to SubscriptionManager object
     */
    public void setSubscriptionManager( SubscriptionManager subscriptionManager ) {
        this.subscriptionManager = subscriptionManager;
    }
    /**
     * Gets success view name.
     *
     * @return Success view name
     */
    public String getSuccessView() { return successView; }
    /**
     * Sets success view name.
     *
     * @param successView Success view name
     */
    public void setSuccessView( String successView ) { this.successView = successView; }
}
//--------------------------------------------------------------------------------------------------
