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
import java.io.FileNotFoundException;

/**
 * Tests FrameDispatcherController by injecting test frame.
 *
 * @author szewczenko
 * @version 1.6
 * @since 1.6
 */
public class FrameDispatcherControllerTest extends TestCase {
//---------------------------------------------------------------------------------------- Constants
    private static final String SCHEME = "https";
    private static final String HOST_ADDR = "localhost";
    private static final int PORT_NUMBER = 8443;
    private static final String APP_CTX = "BaltradDex";
    private static final String ENTRY_ADDR = "dispatch.htm";
    private final static int CONN_TIMEOUT = 60000;
    private final static int SO_TIMEOUT = 60000;
//---------------------------------------------------------------------------------------- Variables
    private static BaltradFrameHandler handler;
    private static File[] testFiles;
    private static BaltradFrame[] testFrames;
//------------------------------------------------------------------------------------------ Methods

    public void testInit() {
        handler = new BaltradFrameHandler( SCHEME, HOST_ADDR, PORT_NUMBER, APP_CTX, ENTRY_ADDR,
                SO_TIMEOUT, CONN_TIMEOUT );
        assertNotNull( handler );
        assertEquals( handler.getScheme(), SCHEME );
        assertEquals( handler.getHostAddress(), HOST_ADDR );
        assertEquals( handler.getPort(), PORT_NUMBER );
        assertEquals( handler.getAppCtx(), APP_CTX );
        assertEquals( handler.getEntryAddress(), ENTRY_ADDR );
    }

    public void testPrepareFiles() {
        File dir = new File( "." );
        testFiles = dir.listFiles();
        assertTrue( testFiles.length > 0 );
        testFrames = new BaltradFrame[ testFiles.length ];
        assertTrue( testFrames.length > 0 );
    }

    public void testPrepareFrames() {
        try {
            for( int i = 0; i < testFiles.length; i++ ) {
                if( testFiles[ i ].getName().startsWith( "test" ) ) {
                    String header = BaltradFrameHandler.createDataHdr( 
                            BaltradFrameHandler.MIME_MULTIPART, "TestSender", "TestSource",
                            testFiles[ i ].getAbsolutePath() );
                    BaltradFrame frame = new BaltradFrame( handler.getServletPath(), header,
                            testFiles[ i ] );
                    testFrames[ i ] = frame;
                    assertNotNull( testFrames[ i ] );
                }
            }
        } catch( FileNotFoundException e ) {
             System.out.println( "File not found: " + e.getMessage() );
        }
    }

    /*public void testHttpPost() {
        int statusCode = 0;
        for( int i = 0; i < testFrames.length; i++ ) {
            if( testFrames[ i ] != null ) {
                statusCode = handler.handleBF( testFrames[ i ] );
                assertEquals( HttpStatus.SC_OK, statusCode );
            }
        }
    }*/
}
//--------------------------------------------------------------------------------------------------
