/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.core.util;

import eu.baltrad.frame.model.BaltradFrameHandler;
import eu.baltrad.frame.model.BaltradFrame;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.core.model.NodeConnection;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.register.model.DeliveryRegisterManager;
import eu.baltrad.dex.register.model.DeliveryRegisterEntry;
import eu.baltrad.dex.log.model.LogManager;

import eu.baltrad.fc.db.FileEntry;

import java.io.File;
import java.util.Date;

/**
 * Implements data delivery task executed in a separate thread.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class HandleFrameTask implements Runnable {
//---------------------------------------------------------------------------------------- Variables
    private String channelName;
    private User user;
    private FileEntry fileEntry;
    private File fileItem;
    private BaltradFrameHandler baltradFrameHandler;
    private DeliveryRegisterManager deliveryRegisterManager;
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     *
     * @param channelName Data channel name
     * @param user User object
     * @param fileEntry File catalog's file entry object
     * @param fileItem BaltradFrame file item
     */
    public HandleFrameTask( String channelName, User user, FileEntry fileEntry, File fileItem ) {
        this.channelName = channelName;
        this.user = user;
        this.fileEntry = fileEntry;
        this.fileItem = fileItem;
        this.logManager = new LogManager();
        this.deliveryRegisterManager = new DeliveryRegisterManager();
        this.baltradFrameHandler = new BaltradFrameHandler( NodeConnection.HTTP_PREFIX +
                user.getShortAddress() + NodeConnection.PORT_SEPARATOR + user.getPortNumber() +
                NodeConnection.ADDRESS_SEPARATOR + NodeConnection.APP_CONTEXT +
                NodeConnection.ADDRESS_SEPARATOR + NodeConnection.ENTRY_ADDRESS );
    }
    /**
     * Implements Runnable interface. Runs frame delivery task in a separate thread.
     */
    public void run() {
        // prepare frame header
        String header = baltradFrameHandler.createDataHdr( BaltradFrameHandler.MIME_MULTIPART,
            InitAppUtil.getNodeName(), channelName, fileEntry.uuid() + ".h5" );
        // prepare frame
        BaltradFrame baltradFrame = new BaltradFrame( header, fileItem.getAbsolutePath() );
        // handle the frame
        int res = baltradFrameHandler.handleBF( baltradFrame );
        // update data delivery register
        String status = ( ( res == 0 ) ? DeliveryRegisterEntry.MSG_SUCCESS :
            DeliveryRegisterEntry.MSG_FAILURE );
        DeliveryRegisterEntry drEntry = new DeliveryRegisterEntry( user.getId(), fileEntry.uuid(),
                user.getName(), new Date(), status );
        deliveryRegisterManager.addEntry( drEntry );
        if( res == 0 ) {
            logManager.addEntry( new Date(), LogManager.MSG_INFO, "Data from " +
                    channelName + " sent to user " + user.getName() );
        } else {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Failed to send data " +
                    "from " + channelName + " to user " + user.getName() );
        }
   }
}
//--------------------------------------------------------------------------------------------------
