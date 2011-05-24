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

package eu.baltrad.dex.log.controller;

import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.log.model.LogManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.io.IOException;

/**
 * Controller deleting records from message table.
 *
 *@author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class ClrMsgsController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    private static final String OK_MSG_KEY = "ok_message";
    private static final String ERROR_MSG_KEY = "error_message";
//---------------------------------------------------------------------------------------- Variables
    private String successView;
    private Logger log;
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public ClrMsgsController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
        this.logManager = new LogManager();
    }
    /**
     * Deletes all mesages from message stack and returns model and view.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView object containing number of deleted records
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        try {
            int deletedEntries = logManager.deleteEntries();
            String msg = "Successfully deleted " + Integer.toString( deletedEntries ) 
                    + " message(s).";
            request.getSession().setAttribute( OK_MSG_KEY, msg );
            log.warn( "User removed all system messages" );
        } catch( SQLException e ) {
            String msg = "Failed to remove system messages:" + e.getMessage();
            request.getSession().removeAttribute( OK_MSG_KEY );
            request.getSession().setAttribute( ERROR_MSG_KEY, msg );
            log.error( "Failed to remove system messages" );
        } catch( Exception e ) {
            String msg = "Failed to remove system messages:" + e.getMessage();
            request.getSession().removeAttribute( OK_MSG_KEY );
            request.getSession().setAttribute( ERROR_MSG_KEY, msg );
            log.error( "Failed to remove system messages" );
        }
        return new ModelAndView( getSuccessView() );
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
}
//--------------------------------------------------------------------------------------------------
