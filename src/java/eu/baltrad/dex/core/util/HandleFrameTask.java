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

import eu.baltrad.dex.registry.model.*;
import eu.baltrad.frame.model.Handler;
import eu.baltrad.frame.model.Frame;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.InitAppUtil;

import eu.baltrad.fc.FileEntry;

import org.apache.log4j.Logger;
import org.apache.http.HttpResponse;

import java.util.Date;
import javax.servlet.http.HttpServletResponse;

/**
 * Implements data delivery task executed in a separate thread.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class HandleFrameTask implements Runnable {
//---------------------------------------------------------------------------------------- Variables
    private DeliveryRegisterManager drManager;
    private Handler handler;
    private Logger log;
    private User user;
    private FileEntry entry;
    private Frame frame;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     * 
     * @param drManager Reference to data delivery register manager 
     * @param log Reference to logger object
     * @param user Receiving user
     * @param entry File entry
     * @param frame Data delivery frame 
     */
    public HandleFrameTask(DeliveryRegisterManager drManager, Logger log, User user, 
            FileEntry entry, Frame frame) 
    {
        this.drManager = drManager;
        this.log = log;
        this.user = user;
        this.entry = entry;
        this.frame = frame;
        this.handler = new Handler(InitAppUtil.getConf().getConnTimeout(), 
                InitAppUtil.getConf().getSoTimeout());
    }
    /**
     * Implements Runnable interface. Runs frame delivery task in a separate thread.
     */
    public void run() {
        try {
            HttpResponse response = handler.post(frame);
            DeliveryRegisterEntry dre = new DeliveryRegisterEntry();
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
                dre = new DeliveryRegisterEntry(user.getId(), entry.uuid(), user.getName(), 
                         new Date(), DeliveryRegisterEntry.MSG_SUCCESS);
                log.info("File entry " + entry.uuid() + " sent to user " + user.getName());
            } else {
                dre = new DeliveryRegisterEntry(user.getId(), entry.uuid(), user.getName(), 
                         new Date(), DeliveryRegisterEntry.MSG_FAILURE);
                log.error("Failed to send file entry " + entry.uuid() + " to user " +
                        user.getName());
            }
            drManager.addEntry(dre);
        } catch( Exception e ) {
            log.error("Failed to complete handle frame task", e);
        }
    }
}
//--------------------------------------------------------------------------------------------------
