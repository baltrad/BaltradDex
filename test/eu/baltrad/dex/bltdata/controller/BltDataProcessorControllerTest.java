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

package eu.baltrad.dex.bltdata.controller;

import eu.baltrad.dex.bltdata.util.DataProcessor;

import junit.framework.TestCase;

import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.Group;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Test class for data processor model.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class BltDataProcessorControllerTest extends TestCase {
//---------------------------------------------------------------------------------------- Variables
    private static DataProcessor dataProcessor;
    //private static DataProcessorController dataProcessorController;
    private static H5File h5File;
    private static Group root;

    private static long nbins_val;
    private static long nrays_val;
    private static long a1gate_val;

    private static Color[] colorPalette;
    private static BufferedImage radarImage;
//------------------------------------------------------------------------------------------ Methods
    
    
    public void testFoo() {}
    
    /*public void testInit() {
        dataProcessor = new BltDataProcessor();
        dataProcessorController = new BltDataProcessorController();

        assertNotNull( dataProcessor );
        assertNotNull( dataProcessorController );
    }

    public void testOpenH5File() {
        h5File = dataProcessor.openH5File( "arlanda.h5" );
        assertNotNull( h5File );
    }

    public void testGetH5Root() {
        root = dataProcessor.getH5Root( h5File );
        assertNotNull( root );
    }

    public void testGetH5Datasets() {
        dataProcessor.getH5Datasets( root );
        assertNotNull( dataProcessor.getDatasetFullNames() );
    }
    
    public void testGetH5Dataset() {
        dataProcessor.getH5Dataset( root, "/dataset1/data1/data" );
        assertNotNull( dataProcessor.getDataset() );
    }

    public void testGetH5Attribute() {
        dataProcessor.getH5Attribute( root, "/dataset1/where", "nbins" );
        nbins_val = ( Long )dataProcessor.getAttributeValue();
        assertNotNull( nbins_val );

        dataProcessor.getH5Attribute( root, "/dataset1/where", "nrays" );
        nrays_val = ( Long )dataProcessor.getAttributeValue();
        assertNotNull( nrays_val );

        dataProcessor.getH5Attribute( root, "/dataset1/where", "a1gate" );
        a1gate_val = ( Long )dataProcessor.getAttributeValue();
        assertNotNull( a1gate_val );
    }

    public void testCreateColorPalette() {
        colorPalette = dataProcessor.createColorPalette( "color_palette.txt" );
        for( int i = 0; i < 256; i++ ) {
            assertNotNull( colorPalette[ i ] );
        }
    }

    public void testPolarH5Dataset2Image() {
        radarImage = dataProcessor.polarH5Dataset2Image( dataProcessor.getDataset(), nbins_val, nrays_val,
                a1gate_val, 64, colorPalette, ( short )0, 12, "#FFFFFF", "#FFFFFF" );
        assertNotNull( radarImage );
    }

    public void testSaveImageToFile() {
        int res = dataProcessor.saveImageToFile( radarImage, "arlanda.png" );
        assertEquals( 0, res );
    }

    public void testCloseH5File() {
        int res = dataProcessor.closeH5File( h5File );
        assertEquals( 0, res );
    }*/
}
//--------------------------------------------------------------------------------------------------
