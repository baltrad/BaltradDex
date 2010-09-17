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

package eu.baltrad.dex.channel.model;

/**
 * Class implements data channel permission object.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.6
 * @since 1.6
 */
public class ChannelPermission {
//---------------------------------------------------------------------------------------- Variables
    private int id;
    private int channelId;
    private int userId;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public ChannelPermission() {}
    /**
     * Constructor sets field values.
     *
     * @param channelId Channel ID
     * @param userId User ID
     */
    public ChannelPermission( int channelId, int userId ) {
        this.channelId = channelId;
        this.userId= userId;
    }
    /**
     * Gets object ID.
     *
     * @return Channel permission object ID
     */
    public int getId() { return id; }
    /**
     * Sets object ID.
     *
     * @param id Channel permission object ID
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Gets channel ID.
     *
     * @return Channel ID
     */
    public int getChannelId() { return channelId; }
    /**
     * Sets channel ID.
     *
     * @param channelId Channel ID
     */
    public void setChannelId( int channelId ) { this.channelId = channelId; }
    /**
     * Gets user ID.
     *
     * @return User ID
     */
    public int getUserId() { return userId; }
    /**
     * Sets user ID.
     *
     * @param userId the userId to set
     */
    public void setUserId(int userId) { this.userId = userId; }
}
//--------------------------------------------------------------------------------------------------