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

package eu.baltrad.dex.log.controller;

import eu.baltrad.dex.util.ITableScroller;
import eu.baltrad.dex.log.model.LogManager;
import eu.baltrad.dex.log.model.LogEntry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.io.IOException;

/**
 * Handles system messages display.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class JournalController implements Controller, ITableScroller {
//---------------------------------------------------------------------------------------- Constants
    /** Log entries map key */
    private static final String LOG_ENTRIES = "entries";
    /** Page number map key */
    private static final String PAGE_NUMBER = "pagenum";
//---------------------------------------------------------------------------------------- Variables
    /** References LogManager class object */
    private LogManager logManager;
    /** Success view name */
    private String successView;
    /** Holds current page number, used for page scrolling */
    private static int currentPage;
//------------------------------------------------------------------------------------------ Methods
     /**
     * Returns ModelAndView object containing log entry list.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    public ModelAndView handleRequest( HttpServletRequest request,
            HttpServletResponse response ) throws ServletException, IOException {
        String pageNum = request.getParameter( PAGE_NUMBER );
        List<LogEntry> entries = null;
        if( pageNum != null ) {
            if( pageNum.matches( "<<" ) ) {
                firstPage();
                entries = logManager.getEntries( 0, LogManager.ENTRIES_PER_PAGE );
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
                int offset = ( getCurrentPage() * LogManager.ENTRIES_PER_PAGE )
                        - LogManager.ENTRIES_PER_PAGE;
                entries = logManager.getEntries( offset, LogManager.ENTRIES_PER_PAGE );
            }
        } else {
            setCurrentPage( 1 );
            entries = logManager.getEntries( 0, LogManager.ENTRIES_PER_PAGE );
        }
        return new ModelAndView( successView, LOG_ENTRIES, entries );
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
        int lastPage = ( int )Math.ceil( logManager.countEntries() / LogManager.ENTRIES_PER_PAGE );
        if( ( lastPage * LogManager.ENTRIES_PER_PAGE ) < logManager.countEntries() ) {
            ++lastPage;
        }
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
        long numEntries = logManager.countEntries();
        int lastPage = ( int )Math.ceil( numEntries / LogManager.ENTRIES_PER_PAGE );
        if( ( lastPage * LogManager.ENTRIES_PER_PAGE ) < logManager.countEntries() ) {
            ++lastPage;
        }
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
     * Method gets reference to LogManager class instance.
     *
     * @return Reference to LogManager class instance
     */
    public LogManager getLogManager() { return logManager; }

    /**
     * Method sets reference to LogManager class instance.
     *
     * @param logManager Reference to LogManager class instance
     */
    public void setLogManager( LogManager logManager ) { this.logManager = logManager; }
}
//--------------------------------------------------------------------------------------------------