/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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
*******************************************************************************/

package eu.baltrad.dex.net.util;

import java.util.HashMap;

/**
 * Implements functionality allowing to assign frame publisher object to the 
 * user identified by name, allowing to access publisher tasks executed 
 * by a given user.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class FramePublisherManager {
  
    private static final int DEFAULT_QUEUE_SIZE = 20;
    private static final int DEFAULT_CORE_POOL_SIZE = 2;
    private static final int DEFAULT_MAX_POOL_SIZE = 10;

    private static HashMap<String, FramePublisher> framePublishers;
    
    private int queueSize;
    private int corePoolSize;
    private int maxPoolSize;

    /**
     * Constructor.
     */
    public FramePublisherManager(){
        this(DEFAULT_QUEUE_SIZE, DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE);
    }
    
    /**
     * Constructor.
     */
    public FramePublisherManager(int queueSize, int corePoolSize, int maxPoolSize){
        framePublishers = new HashMap<String, FramePublisher>();
        
        this.queueSize = queueSize;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
    }
    
    /**
     * Adds frame publisher identified by user name.
     * @param userName User name
     * @param framePublisher Frame publisher object
     */
    public synchronized void addFramePublisher(
            String userName, FramePublisher framePublisher) {
        framePublishers.put(userName, framePublisher);
    }
    /**
     * Gets existing frame publisher associated with a given user name or 
     * creates a new one.
     * @param userName User name
     * @return New or existing FramePublisher object
     */
    public synchronized FramePublisher getFramePublisher(String userName) {
        if (!framePublishers.containsKey(userName)) {
            FramePublisher publisher = new FramePublisher(queueSize, corePoolSize, maxPoolSize);
            framePublishers.put(userName, publisher);
        }
        return framePublishers.get(userName);
    }
}

