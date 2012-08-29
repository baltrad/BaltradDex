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

package eu.baltrad.dex.net.util;

import eu.baltrad.dex.registry.model.*;
import eu.baltrad.frame.model.Handler;
import eu.baltrad.frame.model.Frame;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.InitAppUtil;

import eu.baltrad.bdb.db.FileEntry;

import org.apache.log4j.LogManager;
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
    private RegistryManager drManager;
    private Handler handler;
    private Logger log;
    private User user;
    private FileEntry entry;
    private Frame frame;
    private static Logger logger = LogManager.getLogger(HandleFrameTask.class);
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
    public HandleFrameTask(RegistryManager drManager, Logger log, 
            User user, FileEntry entry, Frame frame) 
    {
        this.drManager = drManager;
        this.log = log;
        this.user = user;
        this.entry = entry;
        this.frame = frame;
        this.handler = InitAppUtil.getHandler();
    }
    /**
     * Implements Runnable interface. Runs frame delivery task in a separate thread.
     */
    public void run() {
        try {
            HttpResponse response = handler.post(frame);
            RegistryEntry dre = new RegistryEntry();
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
                dre = new RegistryEntry(user.getId(), entry.getUuid().toString(), 
                        user.getName(), new Date(), RegistryEntry.MSG_SUCCESS);
                log.info("File entry " + entry.getUuid().toString() + " sent to user " + 
                        user.getName());
            } else {
                dre = new RegistryEntry(user.getId(), entry.getUuid().toString(), 
                        user.getName(), new Date(), RegistryEntry.MSG_FAILURE);
                log.error("Failed to send file entry " + entry.getUuid().toString() + " to user " +
                        user.getName());
            }
            drManager.store(dre);
        } catch( Exception e ) {
          logger.debug("Caught exception in HandleFrameTask", e);
            log.error("Failed to complete handle frame task", e);
        }
    }
}
//--------------------------------------------------------------------------------------------------
