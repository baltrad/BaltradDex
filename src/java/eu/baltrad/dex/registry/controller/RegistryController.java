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

package eu.baltrad.dex.registry.controller;

import eu.baltrad.dex.registry.model.RegistryEntry;
import eu.baltrad.dex.registry.model.RegistryManager;
import eu.baltrad.dex.db.model.BltFileManager;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.util.ITableScroller;
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.log.model.LogManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.List;

/**
 * Multi-action controller for handling delivery register functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class RegistryController extends MultiActionController implements ITableScroller {
//---------------------------------------------------------------------------------------- Constants
    /** Data delivery register entries map key */
    private static final String REGISTER_ENTRIES = "entries";
    private static final String CLEAR_REGISTER_KEY = "number_of_entries";
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";
    // view names
    private static final String SHOW_REGISTER_VIEW = "registry";
    private static final String CLEAR_REGISTER_VIEW = "clear_registry";
    private static final String SHOW_CLEAR_REGISTER_STATUS_VIEW = "clear_registry_status";
    /** Page number map key */
    private static final String PAGE_NUMBER = "pagenum";
//---------------------------------------------------------------------------------------- Variables
    private Logger log;
    private LogManager logManager;
    private String successView;
    private RegistryManager registryManager;
    private BltFileManager bltFileManager;
    private UserManager userManager;
    /** Holds current page number, used for page scrolling */
    private static int currentPage;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor
     */
    public RegistryController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    
    /**
     * Get page numbers for a current set of entries.
     * @return Numbers of first, last and current page for a given dataset 
     */
    private int[] getPages() {
        long numEntries = registryManager.count();
        int numPages = ( int )Math.ceil( numEntries 
                / RegistryManager.ENTRIES_PER_PAGE );
        if( numPages < 1 ) {
            numPages = 1;
        }
        int curPage = getCurrentPage();
        int scrollStart = ( RegistryManager.SCROLL_RANGE - 1 ) / 2;
        int firstPage = 1;
        int lastPage = RegistryManager.SCROLL_RANGE;
        if( numPages <= RegistryManager.SCROLL_RANGE && curPage <=
                RegistryManager.SCROLL_RANGE ) {
            firstPage = 1;
            lastPage = numPages;
        }
        if( numPages > RegistryManager.SCROLL_RANGE && curPage > scrollStart &&
                curPage < numPages - scrollStart ) {
            firstPage = curPage - scrollStart;
            lastPage = curPage + scrollStart;
        }
        if( numPages > RegistryManager.SCROLL_RANGE && curPage > scrollStart &&
                curPage >= numPages - ( RegistryManager.SCROLL_RANGE - 1 ) ) {
            firstPage = numPages - ( RegistryManager.SCROLL_RANGE - 1 );
            lastPage = numPages;
        }
        return new int[] {firstPage, lastPage, curPage};
    }
    
    /**
     * Creates delivery entries list.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView object holding data delivery register
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView registry( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        String pageNum = request.getParameter( PAGE_NUMBER );
        List<RegistryEntry> entries = null;
        if( pageNum != null ) {
            if( pageNum.matches( "<<" ) ) {
                firstPage();
                entries = getRegistryManager().load( 0,
                        RegistryManager.ENTRIES_PER_PAGE );
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
                int offset = ( getCurrentPage() * RegistryManager.ENTRIES_PER_PAGE )
                        - RegistryManager.ENTRIES_PER_PAGE;
                entries = getRegistryManager().load( offset,
                        RegistryManager.ENTRIES_PER_PAGE );
            }
        } else {
            setCurrentPage( 1 );
            entries = getRegistryManager().load( 0,
                    RegistryManager.ENTRIES_PER_PAGE );
        }
        
        int pages[] = getPages();
        
        ModelAndView modelAndView = new ModelAndView(SHOW_REGISTER_VIEW);
        
        modelAndView.addObject("first_page", pages[0]);
        modelAndView.addObject("last_page", pages[1]);
        modelAndView.addObject("current_page", pages[2]);
        modelAndView.addObject(REGISTER_ENTRIES, entries);
        
        return modelAndView;
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
    public ModelAndView clear_registry( HttpServletRequest request,
            HttpServletResponse response ) throws ServletException, IOException {
        return new ModelAndView( CLEAR_REGISTER_VIEW, CLEAR_REGISTER_KEY,
                getRegistryManager().count() );
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
    public ModelAndView clear_registry_status( HttpServletRequest request,
            HttpServletResponse response ) throws ServletException, IOException {
        try {
            int deletedEntries = getRegistryManager().delete();
            String msg = "Successfully deleted " + Integer.toString( deletedEntries )
                    + " registry entries.";
            request.getSession().setAttribute( OK_MSG_KEY, msg );
            log.warn( msg );
        } catch( Exception e ) {
            String msg = "Failed to clear data delivery registry";
            request.getSession().setAttribute( ERROR_MSG_KEY, msg );
            log.error( msg, e );
        }
        return new ModelAndView( SHOW_CLEAR_REGISTER_STATUS_VIEW );
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
        int lastPage = ( int )Math.ceil( getRegistryManager().count() /
                RegistryManager.ENTRIES_PER_PAGE );
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
        long numEntries = getRegistryManager().count();
        int lastPage = ( int )Math.ceil( numEntries / 
                RegistryManager.ENTRIES_PER_PAGE );
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
    /**
     * Gets reference to log manager object.
     *
     * @return logManager Reference to log manager object
     */
    public LogManager getLogManager() { return logManager; }
    /**
     * Sets reference to log manager object.
     *
     * @param logManager Reference to log manager object
     */
    public void setLogManager( LogManager logManager ) { this.logManager = logManager; }

    /**
     * @return the registryManager
     */
    public RegistryManager getRegistryManager() {
        return registryManager;
    }

    /**
     * @param registryManager the registryManager to set
     */
    public void setRegistryManager(RegistryManager registryManager) {
        this.registryManager = registryManager;
    }
}
//--------------------------------------------------------------------------------------------------
