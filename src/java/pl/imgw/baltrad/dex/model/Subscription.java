/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

/**
 * Class implements data subscription object.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class Subscription {

    private int subscriptionID;
    private int userID;
    private int dataChannelID;

    /**
     * Default constructor.
     */
    public Subscription() {}

    /**
     * Constructor creating new subscription object with given field values.
     *
     * @param userID User ID
     * @param dataChannelID Data channel ID
     */
    public Subscription( int userID, int dataChannelID ) {
        this.userID = userID;
        this.dataChannelID = dataChannelID;
    }

    /**
     * @return the subscriptionID
     */
    public int getSubscriptionID() {
        return subscriptionID;
    }

    /**
     * @param subscriptionID the subscriptionID to set
     */
    public void setSubscriptionID(int subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    /**
     * @return the userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return the dataChannelID
     */
    public int getDataChannelID() {
        return dataChannelID;
    }

    /**
     * @param dataChannelID the dataChannelID to set
     */
    public void setDataChannelID(int dataChannelID) {
        this.dataChannelID = dataChannelID;
    }

}
