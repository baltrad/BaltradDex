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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Implements ThreadPoolExecutor used to execute concurrent tasks.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class FramePublisher {

    private static final int DEFAULT_KEEP_ALIVE_TIME = 60;

    private ThreadPoolExecutor executor;
    
    private int maxPoolSize;
    private int minPoolSize;
    
    private final static Logger logger = LogManager.getLogger(FramePublisher.class);
    
    protected class FrameRejectedExecutionHandler extends ThreadPoolExecutor.DiscardOldestPolicy {

      @Override
      public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        super.rejectedExecution(runnable, executor);
        
        logger.error("Discarded PostFile-task due to full work queue for thread pool. " + 
                     "Data will not be sent to subscriber. No of queued tasks: " + executor.getQueue().size() + 
                     ". No of active threads: " + executor.getActiveCount());   
      }
      
    }
    
    /**
     * Constructor
     */    
    public FramePublisher(int queueSize, int corePoolSize, int maxPoolSize) {
      this(queueSize, corePoolSize, maxPoolSize, DEFAULT_KEEP_ALIVE_TIME);
    }

    /**
     * Constructor
     * 
     * @param queueSize     The size of the queue to use for the thread pool executor.
     * @param corePoolSize  The core pool size of the thread pool, i.e., the number for 
     *                      threads that will always be available there. 
     * @param maxPoolSize   The maximum pool size of the thread pool. Threads above the 
     *                      core pool size will be created up to this level when the number 
     *                      of tasks in the the thread pool queue reaches 'queueSize'.
     * @param keepAliveTime The maximum time in seconds that excess idle threads in the pool 
     *                      will wait for new tasks before terminating.
     * 
     */
    public FramePublisher(int queueSize, int corePoolSize, int maxPoolSize, int keepAliveTime) {
        
        this.maxPoolSize = maxPoolSize;
        this.minPoolSize = corePoolSize;
        
        logger.info("Creating FramePublisher - queueSize: " + queueSize + ", corePoolSize: " + corePoolSize + ", maxPoolSize: " + maxPoolSize);
        
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(queueSize);

        executor = new ThreadPoolExecutor(1, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue);
        executor.setThreadFactory(
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = Executors.defaultThreadFactory().newThread(r);
                    t.setDaemon(true);
                    return t;
                }
            });
        executor.setRejectedExecutionHandler(
                new FrameRejectedExecutionHandler());
        executor.prestartCoreThread();
        
    }
    
    /**
     * Adds and executes task.
     *
     * @param task Task class implementing Runnable interface
     */
    public void addTask(Runnable task) {
        int currentPoolSize = executor.getCorePoolSize();
        
        int queueSize = executor.getQueue().size();
  
        logger.info("Adding frame publisher task. Queued tasks: " + queueSize + ". Active threads in pool: " + executor.getActiveCount() + ". Core pool size: " + currentPoolSize);
        
        int newPoolSize = currentPoolSize;
        if (queueSize == 0) {
            newPoolSize = Math.max(minPoolSize, currentPoolSize / 2);
        } else if (queueSize >= executor.getActiveCount()) {
            if (currentPoolSize < maxPoolSize) {
                newPoolSize = Math.min(maxPoolSize, currentPoolSize * 2);
            }
        }
        
        if (newPoolSize != currentPoolSize) {
            logger.info("Adjusting core pool size from " + currentPoolSize + " to " + newPoolSize);
            executor.setCorePoolSize(newPoolSize);
        }
        
        executor.execute(task);
    }
    
    /**
     * Sets the thread pool executor to use. Usable for testing purposes.
     *
     * @param executor The thread pool executor
     */
    public void setThreadPoolExecutor(ThreadPoolExecutor executor) {
      this.executor = executor;
    }
}

