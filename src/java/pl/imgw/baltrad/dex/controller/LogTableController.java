/*
 * BaltradDex :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.controller;

import pl.imgw.baltrad.dex.model.LogManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.io.IOException;


/**
 * Log controller class implements log messages display functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class LogTableController implements Controller {

//---------------------------------------------------------------------------------------- Constants
    private static final String MAP_KEY = "log_entry_list";
    private static final int LOG_ENTRY_LIST_SIZE = 12;
//---------------------------------------------------------------------------------------- Variables
    private LogManager logManager;
    private String successView;
//------------------------------------------------------------------------------------------ Methods

     /**
     * Method handles http request. Returns ModelAndView object containing log entry list.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request,
            HttpServletResponse response ) throws ServletException, IOException {
        List logEntryList = logManager.getLastLogEntries( LOG_ENTRY_LIST_SIZE );
        return new ModelAndView( successView, MAP_KEY, logEntryList );
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
