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

import eu.baltrad.dex.subscription.model.SubscriptionManager;
import eu.baltrad.dex.subscription.model.Subscription;
import eu.baltrad.dex.log.model.LogManager;
import eu.baltrad.frame.model.BaltradFrameHandler;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.register.model.DeliveryRegisterManager;

import eu.baltrad.fc.FileCatalog;
import eu.baltrad.fc.FileCatalogError;
import eu.baltrad.fc.DuplicateEntry;
import eu.baltrad.fc.db.FileEntry;

import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.message.mo.BltDataMessage;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Implements file storage task and triggering of data delivery task.
 * File is stored using reference to existing FileCatalog instance.
 * File item is deleted upon storage.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class StoreFileTask implements Runnable {
//---------------------------------------------------------------------------------------- Variables
    private FramePublisherManager framePublisherManager;
    private SubscriptionManager subscriptionManager;
    private BaltradFrameHandler bfHandler;
    private DeliveryRegisterManager deliveryRegisterManager;
    private UserManager userManager;
    private LogManager logManager;
    private FileCatalog fc;
    private IBltMessageManager bltMessageManager;
    private String headerItem;
    private File fileItem;
    private FileEntry fileEntry;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     *
     * @param framePublisherManager Reference to FramePublisherManager object
     * @param bltMessageManager Reference to IBltMessageManager object
     * @param fc Reference to FileCatalog object
     * @param headerItem BaltradFrame header item
     * @param fileItem BaltradFrame file item
     */
    public StoreFileTask( FramePublisherManager framePublisherManager,
            IBltMessageManager bltMessageManager, FileCatalog fc,
            String headerItem, File fileItem ) {
        this.framePublisherManager = framePublisherManager;
        this.bltMessageManager = bltMessageManager;
        this.fc = fc;
        this.headerItem = headerItem;
        this.fileItem = fileItem;
        this.subscriptionManager = new SubscriptionManager();
        this.bfHandler = new BaltradFrameHandler();
        this.deliveryRegisterManager = new DeliveryRegisterManager();
        this.userManager = new UserManager();
        this.logManager = new LogManager();
    }
    /**
     * Implements Runnable interface - runs file storage task in a thread and triggers data
     * delivery task
     */
    public void run() {
        try {
            // store file
            fileEntry = fc.store( fileItem.getAbsolutePath() );
            // interface with Beast framework
            BltDataMessage message = new BltDataMessage();
            message.setFileEntry( fileEntry );
            bltMessageManager.manage( message );
            // trigger data delivery tasks
            List<Subscription> subs = subscriptionManager.getSubscriptionsByType(
                    Subscription.REMOTE_SUBSCRIPTION );
            // iterate through subscription list
            for( int i = 0; i < subs.size(); i++ ) {
                if( subs.get( i ).getChannelName().equals( bfHandler.getChannel( headerItem ) ) ) {
                    // make sure that user exists locally
                    User user = userManager.getUserByName( subs.get( i ).getUserName() );
                    // look up file item in data register - if file item doesn't exist, trigger
                    // data delivery task and leave file item to it
                    if( deliveryRegisterManager.getEntry(
                            user.getId(), fileEntry.uuid() ) == null ) {
                        HandleFrameTask handleFrameTask = new HandleFrameTask(
                                subs.get( i ).getChannelName(), user, fileEntry, fileItem );
                        // assign a task to a user-specific publisher
                        framePublisherManager.getFramePublisher( user.getName() ).addTask(
                                handleFrameTask );
                    } 
                }
            }
        } catch( DuplicateEntry e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Duplicate entry error: " +
                    e.getMessage() );
        } catch( FileCatalogError e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "General file catalog error: " +
                    e.getMessage() );
        } catch( Exception e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Failed to execute file storage " +
                    "task: " + e.getMessage() );
        }
    }
    /**
     * Gets reference to FramePublisherManager object.
     *
     * @return Reference to FramePublisherManager object
     */
    public FramePublisherManager getFramePublisherManager() {
        return framePublisherManager;
    }
    /**
     * Sets reference to FramePublisherManager object.
     *
     * @param framePublisherManager Reference to FramePublisherManager class object
     */
    public void setFramePublisherManager( FramePublisherManager framePublisherManager ) {
        this.framePublisherManager = framePublisherManager;
    }
}
//--------------------------------------------------------------------------------------------------
