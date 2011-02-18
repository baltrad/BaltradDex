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

import junit.framework.TestCase;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Test case for FramePublisherManager class.
 *
 * @author szewczenko
 * @version 1.6
 * @since 1.6
 */
public class FramePublisherManagerTest extends TestCase {
//---------------------------------------------------------------------------------------- Constants
    private static final int NUMBER_OF_PUBLISHERS = 3;
//---------------------------------------------------------------------------------------- Variables
    private static BaltradFrameHandler handler;
    private static FramePublisherManager manager;
    private static File[] testFiles;
    private static List<BaltradFrame> testFrames;
    private static List<TestTask> testTasks;
    private static int queueSize;
//------------------------------------------------------------------------------------------ Methods

    public void testInit() {
        handler = new BaltradFrameHandler();
        handler.setUrl( "http://localhost:8084/BaltradDex/dispatch.htm" );
        manager = new FramePublisherManager();
        testFrames = new ArrayList<BaltradFrame>();
        testTasks = new ArrayList<TestTask>();

        assertNotNull( handler );
        assertEquals( handler.getUrl(), "http://localhost:8084/BaltradDex/dispatch.htm" );
        assertNotNull( manager );
        assertNotNull( testFrames );
        assertNotNull( testTasks );
    }

    public void testPrepareData() {
        File dir = new File( "." );
        testFiles = dir.listFiles();
        assertTrue( testFiles.length > 0 );
    }

    public void testPrepareFrames() {
        for( int i = 0; i < testFiles.length; i++ ) {
            BaltradFrame frame = new BaltradFrame( handler.createDataHdr(
                BaltradFrameHandler.MIME_MULTIPART, "TestNode", "TestRadar",
                testFiles[ i ].getName() ), testFiles[ i ].getAbsolutePath() );
            testFrames.add( frame );
        }
        assertTrue( testFrames.size() > 0 );
    }

    public void testPrepareTasks() {
        for( int i = 0; i < testFrames.size(); i++ ) {
            TestTask task = new TestTask( handler, testFrames.get( i ) );
            testTasks.add( task );
        }
        assertTrue( testTasks.size() > 0 );
    }

    public void testDispatchTasks() {
        int taskPoolSize = testTasks.size();
        // assign tasks to several publishers
        if( taskPoolSize >= NUMBER_OF_PUBLISHERS ) {
            queueSize = ( int )Math.floor( taskPoolSize / NUMBER_OF_PUBLISHERS );
            for( int i = 0; i < NUMBER_OF_PUBLISHERS; i++ ) {
                FramePublisher publisher = new FramePublisher();
                manager.addFramePublisher( "User_" + i , publisher );
                assertNotNull( manager.getFramePublisher( "User_" + i ) );
            }
        } else {
            System.err.println( "Number of publishers is too small" );
        }
    }
    
    public void testExecuteTasks() {
        int taskIndex = 0;
        for( int i = 0; i < NUMBER_OF_PUBLISHERS; i++ ) {
            FramePublisher publisher = manager.getFramePublisher( "User_" + i );
            for( int j = 0; j < queueSize; j++ ) {
                publisher.addTask( testTasks.get( taskIndex ) );
                taskIndex++;
            }
        }
    }
    
    class TestTask implements Runnable {

        private BaltradFrameHandler handler;
        private BaltradFrame frame;

        public TestTask( BaltradFrameHandler handler, BaltradFrame frame ) {
            this.handler = handler;
            this.frame = frame;
        }
        public void run() {
            handler.handleBF( frame );
        }
    }
}
//--------------------------------------------------------------------------------------------------
