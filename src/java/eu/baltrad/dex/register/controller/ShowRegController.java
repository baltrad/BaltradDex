/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.register.controller;

import eu.baltrad.dex.register.model.DeliveryRegisterEntry;
import eu.baltrad.dex.register.model.DeliveryRegisterManager;
import eu.baltrad.dex.data.model.Data;
import eu.baltrad.dex.data.model.DataManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.util.FileCatalogConnector;
import eu.baltrad.fc.FileCatalog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller fetching records from data delivery register table.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class ShowRegController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    private static final String MODEL_KEY = "register_records";
//---------------------------------------------------------------------------------------- Variables
    private String successView;
    private DeliveryRegisterManager deliveryRegisterManager;
    private DataManager dataManager;
    private UserManager userManager;
    private static FileCatalog fileCatalog = null;
    private FileCatalogConnector fcConnector = new FileCatalogConnector();
//------------------------------------------------------------------------------------------ Methods
    /**
     * Fetches records from data delivery register table and returns model and view.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView object
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        List drEntries = deliveryRegisterManager.getAllEntries();
        //List<DeliveryRegisterRecord> drRecords = new ArrayList<DeliveryRegisterRecord>();
        if( fileCatalog == null ) {
            fileCatalog = fcConnector.connect();
        }
        // Prepare data delivery list
        for( int i = 0; i < drEntries.size(); i++ ) {
            // Initialize file catalog if null
            DeliveryRegisterEntry dre = ( DeliveryRegisterEntry )drEntries.get( i );
            //Data data = dataManager.getDataById( fileCatalog, dre.getDataId() );
            //User user = userManager.getUserByID( dre.getUserId() );



            //DeliveryRegisterRecord drr = new DeliveryRegisterRecord( data.getId(), user.getId(),
            //        data.getChannelName(), user.getNodeAddress() );
            //drRecords.add( drr );



            
        }
        return new ModelAndView( getSuccessView()/*, MODEL_KEY, drRecords*/ );
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
     * Method gets reference to DeliveryRegisterManager class instance.
     *
     * @return Reference to DeliveryRegisterManager class instance
     */
    public DeliveryRegisterManager getDeliveryRegisterManager() { return deliveryRegisterManager; }
    /**
     * Method sets reference to DeliveryRegisterManager class instance.
     *
     * @param deliveryRegisterManager Reference to DeliveryRegisterManager class instance
     */
    public void setDeliveryRegisterManager( DeliveryRegisterManager deliveryRegisterManager ) {
        this.deliveryRegisterManager = deliveryRegisterManager;
    }
    /**
     * Method gets reference to user manager object.
     *
     * @return Reference to user manager object
     */
    public UserManager getUserManager() { return userManager; }
    /**
     * Method sets reference to user manager object.
     *
     * @param userManager Reference to user manager object
     */
    public void setUserManager( UserManager userManager ) { this.userManager = userManager; }
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
//--------------------------------------------------------------------------------------------------
}
