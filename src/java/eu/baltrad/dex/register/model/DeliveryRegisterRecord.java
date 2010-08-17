/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
***************************************************************************************************/

package eu.baltrad.dex.register.model;

/**
 * Class implementing delivery register record model.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
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
    public DeliveryRegisterRecord( /*long dataId, int userId, String channelName,
            String receiverAddress*/) {
        /*this.dataId = dataId;
        this.userId = userId;
        this.channelName = channelName;
        this.receiverAddress = receiverAddress;*/
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