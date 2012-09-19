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

package eu.baltrad.dex.log.controller;

import eu.baltrad.dex.util.ITableScroller;
import eu.baltrad.dex.log.model.LogManager;
import eu.baltrad.dex.log.model.LogEntry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import org.apache.log4j.Logger;

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

    /** Log entries map key */
    private static final String LOG_ENTRIES = "entries";
    /** Page number map key */
    private static final String PAGE_NUMBER = "pagenum";

    /** References LogManager class object */
    private LogManager logManager;
    
    private Logger log;
    
    /** Success view name */
    private String successView;
    /** Holds current page number, used for page scrolling */
    private static int currentPage;

    /**
     * Constructor.
     */
    public JournalController() {
        this.log = Logger.getLogger("DEX");
        this.logManager = LogManager.getInstance();
    }
    
    /**
     * Get page numbers for a current set of entries.
     * @return Numbers of first, last and current page for a given dataset 
     */
    private int[] getPages() {
        long numEntries = logManager.count();
        int numPages = (int) Math.ceil(numEntries / LogManager.ENTRIES_PER_PAGE);
        if ((numPages * LogManager.ENTRIES_PER_PAGE) < numEntries) {
            ++numPages;
        }
        if (numPages < 1) {
            numPages = 1;
        }
        int curPage = getCurrentPage();
        int scrollStart = (LogManager.SCROLL_RANGE - 1) / 2;
        int firstPage = 1;
        int lastPage = LogManager.SCROLL_RANGE;
        if (numPages <= LogManager.SCROLL_RANGE && curPage 
                <= LogManager.SCROLL_RANGE) {
            firstPage = 1;
            lastPage = numPages;
        }
        if (numPages > LogManager.SCROLL_RANGE && curPage > scrollStart 
                && curPage < numPages - scrollStart) {
            firstPage = curPage - scrollStart;
            lastPage = curPage + scrollStart;
        }
        if (numPages > LogManager.SCROLL_RANGE && curPage > scrollStart 
                && curPage >= numPages - (LogManager.SCROLL_RANGE - 1)) {
            firstPage = numPages - (LogManager.SCROLL_RANGE - 1);
            lastPage = numPages;
        }
        return new int[] {firstPage, lastPage, curPage};
    }
    
    
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
                entries = logManager.load( 0, LogManager.ENTRIES_PER_PAGE );
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
                entries = logManager.load( offset, LogManager.ENTRIES_PER_PAGE );
            }
        } else {
            setCurrentPage( 1 );
            entries = logManager.load( 0, LogManager.ENTRIES_PER_PAGE );
        }
        
        int[] pages = getPages();
        
        ModelAndView modelAndView = new ModelAndView(successView);
        modelAndView.addObject("first_page", pages[0]);
        modelAndView.addObject("last_page", pages[1]);
        modelAndView.addObject("current_page", pages[2]);
        modelAndView.addObject(LOG_ENTRIES, entries);
        
        return modelAndView;
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
        int lastPage = ( int )Math.ceil( logManager.count() 
                / LogManager.ENTRIES_PER_PAGE );
        if( ( lastPage * LogManager.ENTRIES_PER_PAGE ) < logManager.count() ) {
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
        long numEntries = logManager.count();
        int lastPage = ( int )Math.ceil( numEntries 
                / LogManager.ENTRIES_PER_PAGE );
        if( ( lastPage * LogManager.ENTRIES_PER_PAGE ) 
                < logManager.count() ) {
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
    public void setSuccessView( String successView ) { 
        this.successView = successView; 
    }
}
