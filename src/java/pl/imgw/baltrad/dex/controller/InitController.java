/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.controller;

import pl.imgw.baltrad.dex.model.Transmitter;
import pl.imgw.baltrad.dex.model.LogManager;
import pl.imgw.baltrad.dex.util.ApplicationSecurityManager;
import pl.imgw.baltrad.dex.util.ServletContextUtil;

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
        logManager.addLogEntry( new Date(), logManager.MSG_INFO, "Application initialized" );
    }
}
//--------------------------------------------------------------------------------------------------