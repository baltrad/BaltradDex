/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model;

import eu.baltrad.dex.util.FileCatalogConnector;
import eu.baltrad.fc.FileCatalog;

import java.io.File;
import java.util.List;
import java.util.Date;

/**
 * Class implementing data transmitter module functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class Transmitter extends Thread {
//---------------------------------------------------------------------------------------- Variables
    // Server state toggle variable
    private boolean doWait = false;
    // Servlet context path
    private String servletContextPath;
    // Subscription list
    private List subscriptionList;
    // Reference to UserManager class object
    private UserManager userManager;
    // Reference to DataManager class object
    private DataManager dataManager;
    // Reference to ChannelManager class object
    private ChannelManager channelManager;
    // Reference to SubscriptionManager class object
    private SubscriptionManager subscriptionManager;
    // Reference to DeliveryRegisterManager class object
    private DeliveryRegisterManager deliveryRegisterManager;
    // Reference to BaltradFrameHandler object
    private BaltradFrameHandler bfHandler;
    // Reference to FileCatalogConnector object
    private FileCatalogConnector fcConnector;
    // Reference FileCatalog object
    private static FileCatalog fileCatalog = null;
    // Reference to LogManager class object
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor method initializes transmitter module.
     */
    public Transmitter( String servletContextPath ) {
        this.servletContextPath = servletContextPath;
        this.setSubscriptionManager( new SubscriptionManager() );
        this.setUserManager( new UserManager() );
        this.setDataManager( new DataManager() );
        this.setChannelManager( new ChannelManager() );
        this.setDeliveryRegisterManager( new DeliveryRegisterManager() );
        this.setBFHandler( new BaltradFrameHandler() );
        this.setLogManager( new LogManager() );
        // Initialize FileCatalogConnector
        this.fcConnector = new FileCatalogConnector();
        // Initialize file catalog if null
        if( fileCatalog == null ) {
            fileCatalog = fcConnector.connect();
        }
        start();
    }
    /**
     * Method runs transmitter thread.
     */
    public void run() {
        while( true ) {
            synchronized( this ) {
                while( getDoWait() ) {
                    try{
                        wait();
                    } catch( Exception e ) {
                        logManager.addLogEntry( new Date(), LogManager.MSG_ERR,
                                                                "Error while reading request" );
                        logManager.addLogEntry( new Date(), LogManager.MSG_ERR, e.getMessage() );
                    }
                }
            }
            // Update current subscriptions status
            try {


                // here an exception is thrown
                this.setSubscriptionList( subscriptionManager.getSubscriptionList() );


            } catch( NullPointerException e ) {
                logManager.addLogEntry( new Date(), LogManager.MSG_ERR, "Failed to fetch " +
                        "subscription list: " + e.getMessage() );
            }
            // Iterate through subscriptions
            if( this.subscriptionList != null ) {
                for( int i = 0; i < subscriptionList.size(); i++ ) {
                    Subscription subscription = ( Subscription )subscriptionList.get( i );
                    int userId = subscription.getUserId();
                    int channelId = subscription.getChannelId();
                    User user = ( User )userManager.getUserByID( userId );
                    Channel channel = ( Channel )channelManager.getChannel( channelId );
                    List dataFromChannel = dataManager.getDataFromChannel( fileCatalog, 
                            channel.getName() );
                    // Iterate through product list
                    for( int j = 0; j < dataFromChannel.size(); j++ ) {
                        Data data = ( Data )dataFromChannel.get( j );
                        String filePath = data.getPath();
                        // Check the file in delivery register
                        DeliveryRegisterEntry deliveryRegisterEntry =
                             deliveryRegisterManager.getEntry( user.getId(), data.getId() );
                        // Data was not found in the register
                        if( deliveryRegisterEntry == null ) {
                            // Post the data
                            bfHandler.setUrl( user.getNodeAddress() );
                            BaltradFrame bf = new BaltradFrame(
                                    bfHandler.createBFDataHdr( BaltradFrameHandler.BF_MIME_MULTIPART,
                                    userManager.getUserByRole( User.ROLE_0 ).getNodeAddress(),
                                    data.getChannelName(), filePath, data.getId() ), filePath );

                            bfHandler.handleBF( bf );
                            /*
                            String relFileName = data.getPath().substring(
                                            filePath.lastIndexOf( File.separator ) + 1,
                                            filePath.length() );
                            */
                            logManager.addLogEntry( new Date(), LogManager.MSG_INFO,
                                "Sending data from " + data.getChannelName() + " to user "
                                + user.getName() + ", file ID: " + data.getId() );

                            // Data was not found in the register - add data to delivery register
                            DeliveryRegisterEntry dre = new DeliveryRegisterEntry();
                            dre.setUserId( user.getId() );
                            dre.setDataId( data.getId() );
                            deliveryRegisterManager.addEntry( dre );                            
                        }
                    }
                }
            }
        }
    }
    /**
     * Method gets server state toggle value.
     *
     * @return Server state toggle value
     */
    public boolean getDoWait() { return doWait; }
    /**
     * Method sets server state toggle value.
     *
     * @param doWait Server state toggle value
     */
    public void setDoWait( boolean doWait ) { this.doWait = doWait; }
    /**
     * Method returns reference to subscription list object.
     *
     * @return Subscription list object
     */
    public List getSubscriptionList() {
        return subscriptionList;
    }
    /**
     * Method sets reference to subscription list object.
     *
     * @param subscriptionList Reference to subscription list object
     */
    public void setSubscriptionList( List subscriptionList ) {
        this.subscriptionList = subscriptionList;
    }
    /**
     * Method returns reference to user manager object.
     *
     * @return Reference to user manager object
     */
    public UserManager getUserManager() {
        return userManager;
    }
    /**
     * Method sets reference to user manager object.
     *
     * @param userManager Reference to user manager object
     */
    public void setUserManager( UserManager userManager ) {
        this.userManager = userManager;
    }
    /**
     * Method returns reference to data manager object.
     *
     * @return Reference to data manager object
     */
    public DataManager getDataManager() {
        return dataManager;
    }
    /**
     * Method sets reference to data manager object.
     *
     * @param Reference to data manager object
     */
    public void setDataManager( DataManager dataManager) {
        this.dataManager = dataManager;
    }
    /**
     * Method returns reference to SubscriptionManager object.
     *
     * @return Reference to SubscriptionManager object
     */
    public SubscriptionManager getSubscriptionManager() {
        return subscriptionManager;
    }
    /**
     * Method sets reference to SubscriptionManager object.
     *
     * @param subscriptionManager SubscriptionManager object
     */
    public void setSubscriptionManager( SubscriptionManager subscriptionManager ) {
        this.subscriptionManager = subscriptionManager;
    }
    /**
     * Method returns reference to ChannelManager object.
     *
     * @return Reference to ChannelManager object
     */
    public ChannelManager getChannelManager() {
        return channelManager;
    }
    /**
     * Method sets reference to ChannelManager object.
     *
     * @param channelManager ChannelManager object
     */
    public void setChannelManager( ChannelManager channelManager ) {
        this.channelManager = channelManager;
    }
    /**
     * Method gets reference to DeliveryRegisterManager class object.
     *
     * @return deliveryRegisterManager Reference to DeliveryRegisterManager class object
     */
    public DeliveryRegisterManager getDeliveryRegisterManager() {
        return deliveryRegisterManager;
    }
    /**
     * Method sets reference to DeliveryRegisterManager class object.
     *
     * @param deliveryRegisterManager Reference to DeliveryRegisterManager class object
     */
    public void setDeliveryRegisterManager( DeliveryRegisterManager deliveryRegisterManager ) {
        this.deliveryRegisterManager = deliveryRegisterManager;
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
    /**
     * Method gets servlet context path.
     *
     * @return Servlet context path
     */
    public String getServletContextPath() { return servletContextPath; }
    /**
     * Method sets servlet context path.
     *
     * @param servletContextPath Servlet context path
     */
    public void setServletContextPath(String servletContextPath) {
        this.servletContextPath = servletContextPath;
    }
    /**
     * Method gets reference to BaltradFrameHandler object.
     *
     * @return Reference to BaltradFrameHandler object
     */
    public BaltradFrameHandler getBFHandler() { return bfHandler; }
    /**
     * Method sets reference to BaltradFrameHandler object.
     *
     * @param bfHandler Reference to BaltradFrameHandler object
     */
    public void setBFHandler( BaltradFrameHandler bfHandler ) { this.bfHandler = bfHandler; }
}
//--------------------------------------------------------------------------------------------------




