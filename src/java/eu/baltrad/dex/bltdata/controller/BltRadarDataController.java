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

package eu.baltrad.dex.bltdata.controller;

import eu.baltrad.dex.bltdata.model.BltFileManager;
import eu.baltrad.dex.bltdata.model.BltFile;
import eu.baltrad.dex.util.ITableScroller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.io.IOException;

/**
 * Implemens functionality allowing for listing products available for a given data channel.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class BltRadarDataController implements Controller, ITableScroller {
//---------------------------------------------------------------------------------------- Constants
    /** File entries key */
    public static final String FILE_ENTRIES = "file_entries";
    /** Channel name key */
    public static final String CHANNEL_NAME = "channelName";
    /** Page number map key */
    private static final String PAGE_NUMBER = "pagenum";
//---------------------------------------------------------------------------------------- Variables
    /** Reference to file manager object */
    private BltFileManager bltFileManager;
    /** Success view name */
    private String successView;
    /** Holds current page number, used for page scrolling */
    private static int currentPage;
    /** Holds channel name value extracted from request parameter */
    private static String channelName;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method handles http request
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request,
            HttpServletResponse response )
            throws ServletException, IOException {
        // Set static channel name to be used with next requests
        if( request.getParameter( CHANNEL_NAME ) != null &&
                !request.getParameter( CHANNEL_NAME ).isEmpty() ) {
            setChannelName( request.getParameter( CHANNEL_NAME ) );
        }
        String pageNum = request.getParameter( PAGE_NUMBER );
        List<BltFile> fileEntries = null;
        if( pageNum != null ) {
            if( pageNum.matches( "<<" ) ) {
                firstPage();
                fileEntries = bltFileManager.getFileEntries( getChannelName(), 0,
                        BltFileManager.ENTRIES_PER_PAGE );
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
                int offset = ( getCurrentPage() * BltFileManager.ENTRIES_PER_PAGE )
                        - BltFileManager.ENTRIES_PER_PAGE;
                fileEntries = bltFileManager.getFileEntries( getChannelName(), offset,
                        BltFileManager.ENTRIES_PER_PAGE );
            }
        } else {
            setCurrentPage( 1 );
            fileEntries = bltFileManager.getFileEntries( getChannelName(), 0,
                    BltFileManager.ENTRIES_PER_PAGE );
        }
        return new ModelAndView( successView, FILE_ENTRIES, fileEntries );
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
        int lastPage = ( int )Math.ceil( bltFileManager.countEntries( getChannelName() ) /
                BltFileManager.ENTRIES_PER_PAGE );
        if( ( lastPage * BltFileManager.ENTRIES_PER_PAGE ) < bltFileManager.countEntries(
                getChannelName() ) ) {
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
        long numEntries = bltFileManager.countEntries( getChannelName() );
        int lastPage = ( int )Math.ceil( numEntries / BltFileManager.ENTRIES_PER_PAGE );
        if( ( lastPage * BltFileManager.ENTRIES_PER_PAGE ) < bltFileManager.countEntries(
                getChannelName() ) ) {
            ++lastPage;
        }
        if( lastPage == 0 ) {
            ++lastPage;
        }
        currentPage = lastPage;
    }
    /**
     * Gets data channel name.
     *
     * @return channelName Data channel name
     */
    public static String getChannelName() {
        return channelName;
    }
    /**
     * Sets data channel name.
     *
     * @param _channelName Data channnel name to set
     */
    public static void setChannelName( String _channelName ) {
        channelName = _channelName;
    }
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
     * Method returns reference to success view name string.
     *
     * @return Reference to success view name string
     */
    public String getSuccessView() { return successView; }
    /**
     * Method sets reference to success view name string.
     *
     * @param Reference to success view name string
     */
    public void setSuccessView( String successView ) { this.successView = successView; }
}
//--------------------------------------------------------------------------------------------------
