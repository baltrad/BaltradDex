/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.register.controller;

import eu.baltrad.dex.register.model.DeliveryRegisterEntry;
import eu.baltrad.dex.register.model.DeliveryRegisterManager;
import eu.baltrad.dex.data.model.DataManager;
import eu.baltrad.dex.user.model.UserManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;


/**
 * Multi-action controller for handling delivery register functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class RegisterController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    // model keys
    private static final String SHOW_REGISTER_KEY = "register_entries";
    private static final String CLEAR_REGISTER_KEY = "number_of_entries";
    private static final String SHOW_CLEAR_REGISTER_STATUS_KEY = "deleted_entries";
    // view names
    private static final String SHOW_REGISTER_VIEW = "showRegister";
    private static final String CLEAR_REGISTER_VIEW = "clearRegister";
    private static final String SHOW_CLEAR_REGISTER_STATUS_VIEW = "showClearRegisterStatus";
//---------------------------------------------------------------------------------------- Variables
    private String successView;
    private DeliveryRegisterManager deliveryRegisterManager;
    private DataManager dataManager;
    private UserManager userManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Creates delivery entries list.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView object holding data delivery register
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView showRegister( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        List<DeliveryRegisterEntry> register = deliveryRegisterManager.getRegister();
        return new ModelAndView( SHOW_REGISTER_VIEW, SHOW_REGISTER_KEY, register );
    }
    /**
     * Gets the number of entries
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView object holding number of entries in the data delivery register
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView clearRegister( HttpServletRequest request,
            HttpServletResponse response ) throws ServletException, IOException {
        return new ModelAndView( CLEAR_REGISTER_VIEW, CLEAR_REGISTER_KEY,
                deliveryRegisterManager.getNumberOfEntries() );
    }
    /**
     * Removes all entries from data delivery register
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView object holding number of deleted entries
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView showClearRegisterStatus( HttpServletRequest request,
            HttpServletResponse response ) throws ServletException, IOException {
        int deletedEntries = deliveryRegisterManager.deleteAllEntries();
        return new ModelAndView( SHOW_CLEAR_REGISTER_STATUS_VIEW, SHOW_CLEAR_REGISTER_STATUS_KEY,
                deletedEntries);
    }
    /**
     * Method returns reference to success view name string.
     *
     * @return Reference to success view name string
     */
    public String getSuccessView() { return successView; }
    /**
     * Method sets reference to success view name string.
     *
     * @param successView Reference to success view name string
     */
    public void setSuccessView( String successView ) { this.successView = successView; }
    /**
     * Method gets reference to DeliveryRegisterManager class instance.
     *
     * @return Reference to DeliveryRegisterManager class instance
     */
    public DeliveryRegisterManager getDeliveryRegisterManager() { return deliveryRegisterManager; }
    /**
     * Method sets reference to DeliveryRegisterManager class instance.
     *
     * @param deliveryRegisterManager Reference to DeliveryRegisterManager class instance
     */
    public void setDeliveryRegisterManager( DeliveryRegisterManager deliveryRegisterManager ) {
        this.deliveryRegisterManager = deliveryRegisterManager;
    }
    /**
     * Method gets reference to user manager object.
     *
     * @return Reference to user manager object
     */
    public UserManager getUserManager() { return userManager; }
    /**
     * Method sets reference to user manager object.
     *
     * @param userManager Reference to user manager object
     */
    public void setUserManager( UserManager userManager ) { this.userManager = userManager; }
    /**
     * Method returns reference to data manager object.
     *
     * @return Reference to data manager object
     */
    public DataManager getDataManager() { return dataManager; }
    /**
     * Method sets reference to data manager object.
     *
     * @param Reference to data manager object
     */
    public void setDataManager( DataManager dataManager ) { this.dataManager = dataManager; }
}
//--------------------------------------------------------------------------------------------------
