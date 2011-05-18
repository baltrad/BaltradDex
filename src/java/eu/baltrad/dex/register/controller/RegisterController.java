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

package eu.baltrad.dex.register.controller;

import eu.baltrad.dex.register.model.DeliveryRegisterEntry;
import eu.baltrad.dex.register.model.DeliveryRegisterManager;
import eu.baltrad.dex.bltdata.model.BltFileManager;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.util.ITableScroller;

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
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class RegisterController extends MultiActionController implements ITableScroller {
//---------------------------------------------------------------------------------------- Constants
    // model keys
    /** Data delivery register entries map key */
    private static final String REGISTER_ENTRIES = "entries";
    private static final String CLEAR_REGISTER_KEY = "number_of_entries";
    private static final String SHOW_CLEAR_REGISTER_STATUS_KEY = "deleted_entries";
    // view names
    private static final String SHOW_REGISTER_VIEW = "showRegister";
    private static final String CLEAR_REGISTER_VIEW = "clearRegister";
    private static final String SHOW_CLEAR_REGISTER_STATUS_VIEW = "showClearRegisterStatus";
    /** Page number map key */
    private static final String PAGE_NUMBER = "pagenum";
//---------------------------------------------------------------------------------------- Variables
    private String successView;
    private DeliveryRegisterManager deliveryRegisterManager;
    private BltFileManager bltFileManager;
    private UserManager userManager;
    /** Holds current page number, used for page scrolling */
    private static int currentPage;
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
        String pageNum = request.getParameter( PAGE_NUMBER );
        List<DeliveryRegisterEntry> entries = null;
        if( pageNum != null ) {
            if( pageNum.matches( "<<" ) ) {
                firstPage();
                entries = deliveryRegisterManager.getEntries( 0,
                        DeliveryRegisterManager.ENTRIES_PER_PAGE );
            } else {
                if( pageNum.matches( ">>" ) ) {
                    lastPage();
                } else if( pageNum.matches( ">" ) ) {
                    nextPage();
                } else if( pageNum.matches( "<" ) ) {
                    previousPage();
                } else {
                    int page = Integer.parseInt( pageNum );
                    setCurrentPage( page );
                }
                int offset = ( getCurrentPage() * DeliveryRegisterManager.ENTRIES_PER_PAGE )
                        - DeliveryRegisterManager.ENTRIES_PER_PAGE;
                entries = deliveryRegisterManager.getEntries( offset,
                        DeliveryRegisterManager.ENTRIES_PER_PAGE );
            }
        } else {
            setCurrentPage( 1 );
            entries = deliveryRegisterManager.getEntries( 0,
                    DeliveryRegisterManager.ENTRIES_PER_PAGE );
        }
        return new ModelAndView( SHOW_REGISTER_VIEW, REGISTER_ENTRIES, entries );
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
                deliveryRegisterManager.countEntries() );
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
        int deletedEntries = deliveryRegisterManager.deleteEntries();
        return new ModelAndView( SHOW_CLEAR_REGISTER_STATUS_VIEW, SHOW_CLEAR_REGISTER_STATUS_KEY,
                deletedEntries);
    }
    /**
     * Gets current page number.
     *
     * @return Current page number
     */
    public int getCurrentPage() { return currentPage; }
    /**
     * Sets current page number.
     *
     * @param page Current page number to set
     */
    public void setCurrentPage( int page ) { currentPage = page; }
    /**
     * Sets page number to the next page number.
     */
    public void nextPage() {
        int lastPage = ( int )Math.ceil( deliveryRegisterManager.countEntries() /
                DeliveryRegisterManager.ENTRIES_PER_PAGE );
        if( lastPage == 0 ) {
            ++lastPage;
        }
        if( getCurrentPage() != lastPage ) {
            ++currentPage;
        }
    }
    /**
     * Sets page number to the previous page number.
     */
    public void previousPage() {
        if( getCurrentPage() != 1 ) {
            --currentPage;
        }
    }
    /**
     * Sets page number to the first page.
     */
    public void firstPage() {
        currentPage = 1;
    }
    /**
     * Sets page number to the last page.
     */
    public void lastPage() {
        long numEntries = deliveryRegisterManager.countEntries();
        int lastPage = ( int )Math.ceil( numEntries / DeliveryRegisterManager.ENTRIES_PER_PAGE );
        if( lastPage == 0 ) {
            ++lastPage;
        }
        currentPage = lastPage;
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
     * Method returns reference to file manager object.
     *
     * @return Reference to file manager object
     */
    public BltFileManager getBltFileManager() { return bltFileManager; }
    /**
     * Method sets reference to file manager object.
     *
     * @param Reference to file manager object
     */
    public void setBltFileManager( BltFileManager bltFileManager ) {
        this.bltFileManager = bltFileManager;
    }
}
//--------------------------------------------------------------------------------------------------
