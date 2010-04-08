/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller;

import eu.baltrad.dex.model.Transmitter;
import eu.baltrad.dex.model.LogManager;
import eu.baltrad.dex.util.ApplicationSecurityManager;
import eu.baltrad.dex.util.ServletContextUtil;

import java.util.Date;

/**
 * Class implements application initialization functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class InitController {
//---------------------------------------------------------------------------------------- Variables
    private ApplicationSecurityManager applicationSecurityManager;
    private LogManager logManager;
    private Transmitter transmitter;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor
     */
    public InitController() {
        ServletContextUtil servletContextUtil = new ServletContextUtil();
        transmitter = new Transmitter( servletContextUtil.getServletContextPath() );
        applicationSecurityManager = new ApplicationSecurityManager();
        logManager = new LogManager();
        // Set reference to transmitter in ApplicationSecurityManager
        applicationSecurityManager.setTransmitter( transmitter );
        // Set initial value of server control toggle
        applicationSecurityManager.setServerRunning( true );
        logManager.addLogEntry( new Date(), LogManager.MSG_INFO, "Application initialized" );

    }
}
//--------------------------------------------------------------------------------------------------