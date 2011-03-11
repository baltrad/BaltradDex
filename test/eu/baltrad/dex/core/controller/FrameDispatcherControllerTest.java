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

package eu.baltrad.dex.core.controller;

import eu.baltrad.frame.model.BaltradFrame;
import eu.baltrad.frame.model.BaltradFrameHandler;

import junit.framework.TestCase;

import java.io.File;

/**
 * Tests FrameDispatcherController by injecting test frame.
 *
 * @author szewczenko
 * @version 1.6
 * @since 1.6
 */
public class FrameDispatcherControllerTest extends TestCase {    
//---------------------------------------------------------------------------------------- Variables
    private static BaltradFrameHandler handler;
    private static File[] testFiles;
    private static BaltradFrame[] testFrames;
//------------------------------------------------------------------------------------------ Methods

    public void testInit() {
        handler = new BaltradFrameHandler( "http://localhost:8084/BaltradDex/dispatch.htm" );
        assertNotNull( handler );
        assertEquals( handler.getUrl(), "http://localhost:8084/BaltradDex/dispatch.htm" );
    }

    public void testPrepareFiles() {
        File dir = new File( "." );
        testFiles = dir.listFiles();

        assertTrue( testFiles.length > 0 );

        testFrames = new BaltradFrame[ testFiles.length ];

        assertTrue( testFrames.length > 0 );
    }

    public void testPrepareFrames() {
        for( int i = 0; i < testFiles.length; i++ ) {
            if( testFiles[ i ].getName().startsWith( "test" ) ) {
                BaltradFrame frame = new BaltradFrame( handler.createDataHdr(
                    BaltradFrameHandler.MIME_MULTIPART, "TestNode", "TestRadar",
                    testFiles[ i ].getName() ), testFiles[ i ].getAbsolutePath() );
                testFrames[ i ] = frame;
            
                assertNotNull( testFrames[ i ] );
            }
        }
    }

    /*public void testInjection() {
        for( int i = 0; i < testFrames.length; i++ ) {
            if( testFrames[ i ] != null ) {
                int code = handler.handleBF( testFrames[ i ] );

                assertTrue( code == 200 );
            }
        }
    }*/

}
//--------------------------------------------------------------------------------------------------
