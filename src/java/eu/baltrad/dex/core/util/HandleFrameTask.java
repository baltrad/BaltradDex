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

import eu.baltrad.dex.registry.model.DeliveryRegisterManager;
import eu.baltrad.dex.registry.model.DeliveryRegisterEntry;
import eu.baltrad.frame.model.BaltradFrameHandler;
import eu.baltrad.frame.model.BaltradFrame;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.InitAppUtil;

import eu.baltrad.fc.FileEntry;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

/**
 * Implements data delivery task executed in a separate thread.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class HandleFrameTask implements Runnable {
//---------------------------------------------------------------------------------------- Variables
    private DeliveryRegisterManager deliveryRegisterManager;
    private BaltradFrameHandler bfHandler;
    private Logger log;
    private User user;
    private String remoteNodeAddress;
    private String dataSource;
    private FileEntry fileEntry;
    private File fileItem;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     *
     * @param deliveryRegisterManager References delivery register manager object
     * @see DeliveryRegisterManager
     * @param log Logger object
     * @param user User for which this task is performed
     * @see User
     * @param dataSource Data source name
     * @param fileEntry File catalog's file entry object
     * @param fileItem BaltradFrame file item
     */
    public HandleFrameTask( DeliveryRegisterManager deliveryRegisterManager, Logger log,
            User user, String dataSource, FileEntry fileEntry, File fileItem ) {
        this.deliveryRegisterManager = deliveryRegisterManager;
        this.log = log;
        this.user = user;
        this.dataSource = dataSource;
        this.fileEntry = fileEntry;
        this.fileItem = fileItem;
        this.remoteNodeAddress = user.getNodeAddress();
        this.bfHandler = new BaltradFrameHandler( InitAppUtil.getConf().getSoTimeout(),
            InitAppUtil.getConf().getConnTimeout() );
    }
    /**
     * Implements Runnable interface. Runs frame delivery task in a separate thread.
     */
    public void run() {
        try {
            String header = BaltradFrameHandler.createDataHdr( BaltradFrameHandler.MIME_MULTIPART,
                InitAppUtil.getConf().getNodeName(), dataSource, fileEntry.uuid() + ".h5" );
            BaltradFrame baltradFrame = new BaltradFrame( header, fileItem );
            int httpStatusCode = bfHandler.handleBF( remoteNodeAddress, baltradFrame );
            // update data delivery register
            String status = ( ( httpStatusCode == BaltradFrameHandler.HTTP_STATUS_CODE_200 ) ?
                DeliveryRegisterEntry.MSG_SUCCESS : DeliveryRegisterEntry.MSG_FAILURE );
            DeliveryRegisterEntry drEntry = new DeliveryRegisterEntry( user.getId(), 
                    fileEntry.uuid(), user.getName(), new Date(), status );
            deliveryRegisterManager.addEntry( drEntry );
            if( httpStatusCode == BaltradFrameHandler.HTTP_STATUS_CODE_200 ) {
                log.info( "FileEntry " + fileEntry.uuid() + " sent to user " + user.getName() );
            } else {
                log.error( "Failed to send FileEntry " + fileEntry.uuid() + " to user " +
                        user.getName() );
            }
        } catch( Exception e ) {
            log.error( "Failed to complete handle frame task", e );
        }
    }
}
//--------------------------------------------------------------------------------------------------
