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
 * Creates list of data channels registered in the system.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class ShowChannelsController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    private static final String MODEL_KEY = "channels";
//---------------------------------------------------------------------------------------- Variables
    private String successView;
    private ChannelManager channelManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Gets list of all registered channels.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return List of all data channels available in the system
     * @throws ServletException
     * @throws IOException
     */
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
