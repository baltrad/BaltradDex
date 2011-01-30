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
    private static BaltradFrameHandler bltFrameHandler = new BaltradFrameHandler();
    private static String xmlHdr = null;
//------------------------------------------------------------------------------------------ Methods

    public void testInit() {
        bltFrameHandler.setUrl( "http://localhost:8084/BaltradDex/dispatch.htm" );
        assertNotNull( bltFrameHandler );
        assertEquals( bltFrameHandler.getUrl(), "http://localhost:8084/BaltradDex/dispatch.htm" );
    }
    
    public void testPrepareFrame() {
        xmlHdr = bltFrameHandler.createDataHdr( BaltradFrameHandler.MIME_MULTIPART,
                "TestNode", "Arlanda", "arlanda.h5" );
        assertEquals( bltFrameHandler.getMimeType( xmlHdr ), BaltradFrameHandler.MIME_MULTIPART );
        assertEquals( bltFrameHandler.getSenderNodeName( xmlHdr ), "TestNode" );
        assertEquals( bltFrameHandler.getChannel( xmlHdr ), "Arlanda" );
        assertEquals( bltFrameHandler.getFileName( xmlHdr ), "arlanda.h5" );
        assertEquals( bltFrameHandler.getContentType( xmlHdr ), "file" );
    }

    public void testInjection() {
        File f1 = new File( "arlanda.h5" );
        BaltradFrame frame1 = new BaltradFrame( bltFrameHandler.createDataHdr(
            BaltradFrameHandler.MIME_MULTIPART, "TestNode", "Arlanda", "arlanda.h5" ),
            f1.getAbsolutePath() );
        try {
            bltFrameHandler.handleBF( frame1 );
        } catch( Exception e ) {
            System.out.println( "Frame handler error: " + e.getMessage() );
        }
    }

}
//--------------------------------------------------------------------------------------------------
