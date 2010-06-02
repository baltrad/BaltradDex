/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller.admin;

import eu.baltrad.dex.model.register.DeliveryRegisterManager;
import eu.baltrad.dex.model.log.LogManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.hibernate.HibernateException;

import java.io.IOException;
import java.util.Date;

/**
 * Controller deleting records from data delivery register table.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class ClrRegController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    private static final String MODEL_KEY = "operation_status";
//---------------------------------------------------------------------------------------- Variables
    private String successView;
    private LogManager logManager;
    private DeliveryRegisterManager deliveryRegisterManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Deletes all records from data delivery register table and returns model and view.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView object
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        String msg = null;
        try {
            int deletedEntries = deliveryRegisterManager.deleteAllEntries();
            msg = "Successfully deleted " + Integer.toString( deletedEntries ) + " entries.";
            logManager.addEntry( new Date(), LogManager.MSG_WRN, "All records have been deleted\n" +
                " from data delivery register" );
        } catch( HibernateException e) {
            msg = "Error while clearing data delivery register: <br/>" + e.getMessage();
        }
        return new ModelAndView( getSuccessView(), MODEL_KEY, msg );
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
}
//--------------------------------------------------------------------------------------------------