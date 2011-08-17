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
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class BltDataSourceController implements Controller, ITableScroller {
//---------------------------------------------------------------------------------------- Constants
    /** File entries key */
    public static final String FILE_ENTRIES = "fileEntries";
    /** Data source name key */
    public static final String DS_NAME = "dsName";
    /** Page number map key */
    private static final String PAGE_NUMBER = "pagenum";
//---------------------------------------------------------------------------------------- Variables
    /** Reference to file manager object */
    private static BltFileManager bltFileManager;
    /** Success view name */
    private String successView;
    /** Holds current page number, used for page scrolling */
    private static int currentPage;
    /** Holds data source name value extracted from request parameter */
    private static String dsName;
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
        if( request.getParameter( DS_NAME ) != null &&
                !request.getParameter( DS_NAME ).isEmpty() ) {
            setDSName( request.getParameter( DS_NAME ) );
        }
        String pageNum = request.getParameter( PAGE_NUMBER );
        List<BltFile> fileEntries = null;
        if( pageNum != null ) {
            if( pageNum.matches( "<<" ) ) {
                firstPage();
                fileEntries = bltFileManager.getFileEntries( getDSName(), 0,
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
                fileEntries = bltFileManager.getFileEntries( getDSName(), offset,
                        BltFileManager.ENTRIES_PER_PAGE );
            }
        } else {
            setCurrentPage( 1 );
            fileEntries = bltFileManager.getFileEntries( getDSName(), 0,
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
     * Gets current page number.
     *
     * @return Current page number
     */
    public static int getCurPage() { return currentPage; }
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
        int lastPage = ( int )Math.ceil( bltFileManager.countDSFileEntries( getDSName() ) /
                BltFileManager.ENTRIES_PER_PAGE );
        if( ( lastPage * BltFileManager.ENTRIES_PER_PAGE ) < bltFileManager.countDSFileEntries(
                getDSName() ) ) {
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
        long numEntries = bltFileManager.countDSFileEntries( getDSName() );
        int lastPage = ( int )Math.ceil( numEntries / BltFileManager.ENTRIES_PER_PAGE );
        if( ( lastPage * BltFileManager.ENTRIES_PER_PAGE ) < bltFileManager.countDSFileEntries(
                getDSName() ) ) {
            ++lastPage;
        }
        if( lastPage == 0 ) {
            ++lastPage;
        }
        currentPage = lastPage;
    }
    /**
     * Gets data data source name.
     *
     * @return dsName Data source name
     */
    public static String getDSName() {
        return dsName;
    }
    /**
     * Sets data source name.
     *
     * @param _dsName Data source name to set
     */
    public static void setDSName( String _dsName ) {
        dsName = _dsName;
    }
    /**
     * Method returns reference to file manager object.
     *
     * @return Reference to file manager object
     */
    public static BltFileManager getBltFileManager() { return bltFileManager; }
    /**
     * Method sets reference to file manager object.
     *
     * @param Reference to file manager object
     */
    public void setBltFileManager( BltFileManager _bltFileManager ) {
        bltFileManager = _bltFileManager;
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
