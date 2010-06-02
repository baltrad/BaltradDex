/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model.register;

/**
 * Auxiliary class implementing delivery register record model.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class DeliveryRegisterRecord {
//---------------------------------------------------------------------------------------- Variables
    private long dataId;
    private int userId;
    private String channelName;
    private String receiverAddress;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     * 
     * @param dataId Data object ID
     * @param userId User object ID
     * @param channelName Data channel name
     * @param receiverAddress Receiver'saddress
     */
    public DeliveryRegisterRecord( long dataId, int userId, String channelName,
            String receiverAddress) {
        this.dataId = dataId;
        this.userId = userId;
        this.channelName = channelName;
        this.receiverAddress = receiverAddress;
    }
    /**
     * Gets data ID
     *
     * @return Data ID
     */
    public long getDataId() { return dataId; }
    /**
     * Sets data ID
     *
     * @param dataId Data ID
     */
    public void setDataId( long dataId ) { this.dataId = dataId; }
    /**
     * Gets user ID
     *
     * @return User ID
     */
    public int getUserId() { return userId; }
    /**
     * Sets user ID
     *
     * @param userId User ID
     */
    public void setUserId( int userId ) { this.userId = userId; }
    /**
     * Gets data channel name
     *
     * @return Data channel name
     */
    public String getChannelName() { return channelName; }
    /**
     * Sets data channel name
     *
     * @param channelName Data channel name
     */
    public void setChannelName( String channelName ) { this.channelName = channelName; }
    /**
     * Gets receiver's address
     *
     * @return Receiver's address
     */
    public String getReceiverAddress() { return receiverAddress; }
    /**
     * Sets receiver's address
     *
     * @param receiverAddress Receiver's address
     */
    public void setReceiverAddress( String receiverAddress ) {
        this.receiverAddress = receiverAddress;
    }
}
//--------------------------------------------------------------------------------------------------