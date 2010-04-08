/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model;

/**
 * Class implements auxiliary data subscription object.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class AuxSubscription {
//---------------------------------------------------------------------------------------- Variables
    private int id;
    private int userId;
    private int channelId;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public AuxSubscription() {}

    /**
     * Constructor creating new auxiliary subscription object with given field values.
     *
     * @param userId User ID
     * @param channelId Data channel ID
     */
    public AuxSubscription( int userId, int channelId ) {
        this.userId = userId;
        this.channelId = channelId;
    }
    /**
     * Method gets subscription id.
     *
     * @return Subscription id
     */
    public int getId() { return id; }
    /**
     * Method sets subscription id.
     *
     * @param id Dubscription id
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Method gets user id.
     *
     * @return User id
     */
    public int getUserId() { return userId; }
    /**
     * Method sets user id.
     *
     * @param userId User id
     */
    public void setUserId( int userId ) { this.userId = userId; }
    /**
     * Method gets channel id.
     *
     * @return Channel id
     */
    public int getChannelId() { return channelId; }
    /**
     * Method sets channel id.
     *
     * @param channelId Channel id
     */
    public void setChannelId( int channelId ) { this.channelId = channelId; }
}
//--------------------------------------------------------------------------------------------------