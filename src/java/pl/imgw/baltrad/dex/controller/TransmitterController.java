/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.controller;

import pl.imgw.baltrad.dex.model.Transmitter;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

/**
 * Class implementing controller for data transmitter module.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class TransmitterController implements Controller {

//---------------------------------------------------------------------------------- Constant fields

    private static final String VIRTUAL_PATH = "/";

//---------------------------------------------------------------------------------------- Variables

    // Reference to Transmitter object
    private Transmitter transmitter;
    // Servlet context path
    private static String servletCtxPath;
    // Server thread state
    private boolean isOn;

//------------------------------------------------------------------------------------------ Methods

    /**
     * Method handles HTTP request and returnes ModelAndView object. Method is used only 
     * to determine servlet context path.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return Model and view
     * @throws javax.servlet.ServletException Servlet exception
     * @throws java.io.IOException IO Exception
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
                                                            throws ServletException, IOException {
        setServletCtxPath( request.getSession().getServletContext().getRealPath( VIRTUAL_PATH ) );
        return new ModelAndView();
    }

    /**
     * Method starts data transmitter module.
     */
    public void setTransmitterOn() {
        synchronized( transmitter ) {
            transmitter.setIsIdle( false );
            transmitter.notify();
        }
        this.isOn = true;
    }

    /**
     * Method stops data transmitter module.
     */
    public void setTransmitterOff() {
        synchronized( transmitter ){
            transmitter.setIsIdle( true );
        }
        this.isOn = false;
    }

    /**
     * Method returns transmitter status.
     *
     * @return True if transmitter is active, false if idle.
     */
    public boolean getTransmitterStatus() {
        return this.isOn;
    }

    /**
     * Method returns transmitter module object.
     *
     * @return Transmitter object
     */
    public Transmitter getTransmitter() {
        return transmitter;
    }

    /**
     * Method sets reference to transmitter module object.
     *
     * @param transmitter Reference to Transmitter object
     */
    public void setTransmitter( Transmitter transmitter ) {
        this.transmitter = transmitter;
    }

    /**
     * Method returns servlet context path.
     *
     * @return Servlet context path
     */
    public String getServletCtxPath() {
        return servletCtxPath;
    }

    /**
     * Method sets servlet context path.
     *
     * @param servletCtxPath Reference to servlet context path
     */
    public void setServletCtxPath( String servletCtxPath ) {
        this.servletCtxPath = servletCtxPath;
    }
    
}
//--------------------------------------------------------------------------------------------------


