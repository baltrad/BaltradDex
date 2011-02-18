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

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Class implements ThreadPoolExecutor used to execute concurrent tasks.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class FramePublisher {
//---------------------------------------------------------------------------------------- Constants
    private static final int POOL_SIZE = 20;
    private static final int KEEP_ALIVE_TIME = 60;
//---------------------------------------------------------------------------------------- Variables
    // ThreadPoolExectutor object
    private static ThreadPoolExecutor executor;
    // Task queue
    private static ArrayBlockingQueue<Runnable> queue;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor
     */
    public FramePublisher() {
        queue = new ArrayBlockingQueue<Runnable>( POOL_SIZE );
        executor = new ThreadPoolExecutor( POOL_SIZE, POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                queue );
    }
    /**
     * Adds and executes task.
     *
     * @param task Task class implementing Runnable interface
     */
    public void addTask( Runnable task ) {
        executor.execute( task );
    }  
}
//--------------------------------------------------------------------------------------------------
