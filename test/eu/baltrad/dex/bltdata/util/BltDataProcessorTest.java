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

package eu.baltrad.dex.bltdata.util;

import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.Dataset;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.util.List;
import java.util.ArrayList;

import java.io.File;

/**
 * Test case for data processor class.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 1.7.0
 */
public class BltDataProcessorTest {
    
    private static final String TEST_FILE = 
            "fixtures/Z_PVOL_2013031310500600_plleg.h5";
    
    private DataProcessor classUnderTest;
    
    @Before
    public void setUp() {
        classUnderTest = new DataProcessor();
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
    }
    
    private String getFilePath(String resource) throws Exception {
        File f = new File(this.getClass().getResource(resource).getFile());
        return f.getAbsolutePath();
    }
    
    @Test
    public void openH5File() throws Exception { 
        H5File file = classUnderTest.openH5File(getFilePath(TEST_FILE));
        assertNotNull(file);
        assertTrue(file.getFID() > 0);
    }
    
    @Test
    public void closeH5File() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(TEST_FILE));
        classUnderTest.closeH5File(file);
        assertEquals(-1, file.getFID());
    }
    
    @Test
    public void getH5Root() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(TEST_FILE));
        Group root = classUnderTest.getH5Root(file);
        assertNotNull(root);
        assertTrue(root.isRoot());
    }
    
    @Test
    public void getH5DatasetPaths() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(TEST_FILE));
        Group root = classUnderTest.getH5Root(file);
        List<String> paths = new ArrayList<String>();
        classUnderTest.getH5DatasetPaths(root, paths);
        assertEquals(10, paths.size());
    }
    
    @Test
    public void getH5Dataset() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(TEST_FILE));
        Group root = classUnderTest.getH5Root(file);
        List<String> paths = new ArrayList<String>();
        classUnderTest.getH5DatasetPaths(root, paths);
        classUnderTest.getH5Dataset(root, paths.get(2));
        Dataset dataset = classUnderTest.getH5Dataset();
        assertNotNull(dataset);
    }
    
    @Test
    public void getH5Attribute() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(TEST_FILE));
        Group root = classUnderTest.getH5Root(file);
        classUnderTest.getH5Attribute(root, "/what", "object");
        assertEquals("object", classUnderTest.getH5Attribute().getName());
        classUnderTest.getH5Attribute(root, "/dataset2/what", "product");
        assertEquals("product", classUnderTest.getH5Attribute().getName());
        classUnderTest.getH5Attribute(root, "/dataset5/where", "elangle");
        assertEquals("elangle", classUnderTest.getH5Attribute().getName());
    }
    
    @Test
    public void getH5AttributeValue() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(TEST_FILE));
        Group root = classUnderTest.getH5Root(file);
        classUnderTest.getH5Attribute(root, "/what", "object");
        assertEquals("PVOL", classUnderTest.getH5AttributeValue());
        classUnderTest.getH5Attribute(root, "/dataset2/what", "product");
        assertEquals("SCAN", classUnderTest.getH5AttributeValue());
        classUnderTest.getH5Attribute(root, "/dataset5/where", "elangle");
        assertEquals(5.3, classUnderTest.getH5AttributeValue());
    }
    
    @Test
    public void createColorPalette() {
        Color[] palette = classUnderTest
                .createColorPalette("conf/color_palette.txt");
        assertEquals(256, palette.length);
    }
    
    @Test 
    public void polarDataset2CartImage() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(TEST_FILE));
        Group root = classUnderTest.getH5Root(file);
        classUnderTest.getH5Attribute(root, "/dataset1/where", "nbins");
        
        assertEquals("nbins", classUnderTest.getH5Attribute().getName());
        
        long nbins = (Long) classUnderTest.getH5AttributeValue();
        int range = (int) nbins;
        
        assertEquals(250, range);
        
        classUnderTest.getH5Dataset(root, "/dataset1/data1/data");
        Dataset dataset = classUnderTest.getH5Dataset();
        
        assertNotNull(dataset);
        
        Color[] palette = classUnderTest
                .createColorPalette("conf/color_palette.txt");
        BufferedImage bi = classUnderTest.polarDataset2CartImage(nbins, dataset, 
                palette, 500, 50, 0.1f, "#FFFFFF", "#FFFFFF");
        
        assertNotNull(bi);
        assertTrue(classUnderTest
                .saveImageToFile(bi, "legionowo_dataset1.png"));
    }
    
}
