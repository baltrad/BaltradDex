/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

import pl.imgw.baltrad.dex.controller.TransmitterController;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

/**
 * Class implementing data transmitter module functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class Transmitter extends Thread {

//---------------------------------------------------------------------------------------- Variables

    // Server thread-control variable.
    private boolean isIdle = true;
    // Subscription list
    private List subscriptionList;
    // Data from channel list
    private List dataFromChannel = new ArrayList();;
    // Reference to UserManager class object
    private UserManager userManager;
    // Reference to DataManager class object
    private DataManager dataManager;
    // Reference to DataChannelManager class object
    private DataChannelManager dataChannelManager;
    // Reference to SubscriptionManager class object
    private SubscriptionManager subscriptionManager;
    // Reference to TransmitterController class object
    private TransmitterController transmitterController;
    // Reference to DeliveryRegisterManager class object
    private DeliveryRegisterManager deliveryRegisterManager;
    // Reference to DataPusher class object
    private DataPusher dataPusher;

    
//------------------------------------------------------------------------------------------ Methods

    /**
     * Constructor method initializes transmitter module.
     */
    public Transmitter() {
        start();
        dataPusher = new DataPusher();
    }

    /**
     * Method runs transmitter thread.
     */
    public void run() {

        while( true ) {
            // Check if thread should wait.
            synchronized( this ) {
                while( isIdle ) {
                    try{
                        wait();
                    }catch( Exception e ) {
                        System.err.println( "Transmitter thread exception: " + e.getMessage() );
                    }
                }
            }
            subscriptionList = subscriptionManager.getSubscriptionList();
            // Iterate through subscriptions
            if( this.subscriptionList != null ) {
                for( int i = 0; i < subscriptionList.size(); i++ ) {

                    Subscription subscription = ( Subscription )subscriptionList.get( i );
                    int userID = subscription.getUserID();
                    int dataChannelID = subscription.getDataChannelID();
                    User user = ( User )userManager.getUserByID( userID );
                    DataChannel dataChannel = 
                            ( DataChannel )dataChannelManager.getDataChannel( dataChannelID );
                    dataFromChannel =
                            dataManager.getProductsFromDataChannel( dataChannel.getName() );
                    // Iterate through product list
                    for( int j = 0; j < dataFromChannel.size(); j++ ) {
                        Data data = ( Data )dataFromChannel.get( j );
                        String localPath = data.getAbsolutePath();
                        String ctxPath = transmitterController.getServletCtxPath();
                        String fileName = ctxPath + localPath;
                        //File f = new File( fileName );
                        //int fileSize = ( int )f.length();
                        //if( fileSize > 0 ) {
                        //    System.out.println( "File size: " + fileSize );

                        //}

                        // Check the file in delivery register
                        DeliveryRegisterEntry deliveryRegisterEntry =
                             deliveryRegisterManager.getEntry( user.getId(), data.getId() );
                        // Data was not found in the register
                        if( deliveryRegisterEntry == null ) {
                            // Send the data
                            dataPusher.setUrlAddress( user.getNodeAddress() );
                            dataPusher.setFileName( fileName );
                            dataPusher.push();
                            
                            // Data was not found in the register - add data to delivery register
                            DeliveryRegisterEntry dre = new DeliveryRegisterEntry();
                            dre.setUserID( user.getId() );
                            dre.setDataID( data.getId() );
                            deliveryRegisterManager.addEntry( dre );
                            
                        }



                        /*System.out.println( "...DATA FROM CHANNEL LIST: " + dataFromChannel.size() );
                        System.out.println( "...FILE NAME: " + fileName );

                        dataPusher = new DataPusher( "http://localhost:8084/BaltradDex/receiver.htm",
                                                    "user", "password", fileName );
                        System.out.println( "___________FILE: " + fileName );
                        dataPusher.start();
                        */

                        
                       

                    }
                    // Cancel subscription once data is delivered ( just for the time being )
                    //subscriptionManager.cancelSubscription( subscription.getSubscriptionID() );
                    
                }
            }
        }
    }

    /**
     * Method returns transmitter thread-control variable.
     *
     * @return Server thread-control variable.
     */
    public boolean getIsIdle() {
        return this.isIdle;
    }

    /**
     * Method sets transmitter thread-control variable.
     *
     * @param Server thread-control variable value.
     */
    public void setIsIdle( boolean isIdle ) {
        this.isIdle = isIdle;
    }

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
    public void setDataManager(DataManager dataManager) {
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
     * Method returns reference to DataChannelManager object.
     *
     * @return Reference to DataChannelManager object
     */
    public DataChannelManager getDataChannelManager() {
        return dataChannelManager;
    }

    /**
     * Method sets reference to DataChannelManager object.
     *
     * @param dataChannelManager DataChannelManager object
     */
    public void setDataChannelManager( DataChannelManager dataChannelManager ) {
        this.dataChannelManager = dataChannelManager;
    }

    /**
     * Method returns reference to TransmitterController class object.
     *
     * @return Reference to TransmitterController class object
     */
    public TransmitterController getTransmitterController() {
        return transmitterController;
    }

    /**
     * Method sets reference to TransmitterController class object.
     *
     * @param transmitterController Reference to TransmitterController class object
     */
    public void setTransmitterController(TransmitterController transmitterController) {
        this.transmitterController = transmitterController;
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

}

//--------------------------------------------------------------------------------------------------




