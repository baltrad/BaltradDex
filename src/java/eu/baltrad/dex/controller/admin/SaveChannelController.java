/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller.admin;

import eu.baltrad.dex.model.channel.ChannelManager;
import eu.baltrad.dex.model.channel.Channel;
import eu.baltrad.dex.model.log.LogManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import java.util.Date;

/**
 * Controller class registers new channel in the system or modifies existing data channel.
 *
 * @author szewczenko
 */
public class SaveChannelController extends SimpleFormController {
//---------------------------------------------------------------------------------------- Constants
    public static final String CHANNEL_ID = "id";
    public static final String MSG = "message";
//---------------------------------------------------------------------------------------- Variables
    private ChannelManager channelManager;
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Fetches Channel object with a given CHANNEL_ID passed as request parameter,
     * or creates new Channel instance in case CHANNEL_ID is not set in request.
     *
     * @param request HttpServletRequest
     * @return Channel class object
     */
    protected Object formBackingObject( HttpServletRequest request ) {
        Channel channel = null;
        if( request.getParameter( CHANNEL_ID ) != null
                && request.getParameter( CHANNEL_ID ).trim().length() > 0 ) {
            channel = channelManager.getChannel( Integer.parseInt(
                    request.getParameter( CHANNEL_ID ) ) );
        } else {
            channel = new Channel();
        }
        return channel;
    }
    /**
     * Saves Channel object
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param command Command object
     * @param errors Errors object
     * @return ModelAndView object
     */
    protected ModelAndView onSubmit( HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) throws Exception {
        Channel channel = ( Channel )command;
        channelManager.addChannel( channel );
        request.getSession().setAttribute( MSG, getMessageSourceAccessor().getMessage(
                "message.addchannel.savesuccess" ) );
        logManager.addEntry( new Date(), LogManager.MSG_WRN, "Data channel saved: " +
                channel.getName() );
        return new ModelAndView( getSuccessView() );
    }
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