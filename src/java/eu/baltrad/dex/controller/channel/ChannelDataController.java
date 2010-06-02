/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller.channel;

import eu.baltrad.dex.model.data.DataManager;
import eu.baltrad.dex.util.FileCatalogConnector;
import eu.baltrad.fc.FileCatalog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.io.IOException;

/**
 * Data from data channel controller class implementing functionality allowing for
 * listing products available for a given data channel.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class ChannelDataController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    public static final String MAP_KEY = "channeldata";
    public static final String CHANNEL_NAME = "name";
//---------------------------------------------------------------------------------------- Variables
    private DataManager dataManager = null;
    private static FileCatalog fileCatalog = null;
    private FileCatalogConnector fcConnector = new FileCatalogConnector();
    private String successView;
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
        String channelName = request.getParameter( CHANNEL_NAME );
        // Initialize file catalog if null
        if( fileCatalog == null ) {
            fileCatalog = fcConnector.connect();
        }
        List dataList = dataManager.getDataFromChannel( fileCatalog, channelName );
        return new ModelAndView( getSuccessView(), MAP_KEY, dataList );
        
    }
    /**
     * Method returns reference to data manager object.
     *
     * @return Reference to data manager object
     */
    public DataManager getDataManager() { return dataManager; }
    /**
     * Method sets reference to data manager object.
     *
     * @param Reference to data manager object
     */
    public void setDataManager( DataManager dataManager ) { this.dataManager = dataManager; }
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
