/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller.admin;

import eu.baltrad.dex.model.channel.ChannelManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

/**
 * Controller class registering new channel in the system.
 *
 * @author szewczenko
 */
public class EditChannelController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    private static final String MODEL_KEY = "registered_channels";
//---------------------------------------------------------------------------------------- Variables
    private String successView;
    private ChannelManager channelManager;
//------------------------------------------------------------------------------------------ Methods
     public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
         List channels = channelManager.getAllChannels();
         return new ModelAndView( getSuccessView(), MODEL_KEY, channels );
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
     * Method returns reference to data channel manager object.
     *
     * @return Reference to data channel manager object
     */
    public ChannelManager getChannelManager() { return channelManager; }
    /**
     * Method sets reference to data channel manager object.
     *
     * @param Reference to data channel manager object
     */
    public void setChannelManager( ChannelManager channelManager ) {
        this.channelManager = channelManager;
    }
}
//--------------------------------------------------------------------------------------------------
