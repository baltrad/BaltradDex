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

package eu.baltrad.dex.db.util;

import eu.baltrad.dex.db.util.BltDataProcessor;
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
    
    private static final String FILE_PLLEG = 
            "fixtures/Z_PVOL_2013062801500700_plleg.h5";
    private static final String FILE_LVRIX =
            "fixtures/Z_PVOL_2013081422004400_lvrix.h5";
    private static final String FILE_BYMIN =
            "fixtures/Z_PVOL_2013081413301200_bymin.h5";
    private static final String FILE_COMP = 
            "fixtures/Z_COMP_20130927101000_pl.h5";
    private static final String FILE_16BIT =
            "fixtures/sebaa_scan_0.5_20200225T161500Z_0x73fc7b.h5";
    
    private BltDataProcessor classUnderTest;
    
    @Before
    public void setUp() {
        classUnderTest = new BltDataProcessor();
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
        H5File file = classUnderTest.openH5File(getFilePath(FILE_PLLEG));
        assertNotNull(file);
        assertTrue(file.getFID() > 0);
    }
    
    @Test
    public void closeH5File() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(FILE_PLLEG));
        classUnderTest.closeH5File(file);
        assertEquals(-1, file.getFID());
    }
    
    @Test
    public void getH5Root() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(FILE_PLLEG));
        Group root = classUnderTest.getH5Root(file);
        assertNotNull(root);
        assertTrue(root.isRoot());
    }
    
    @Test
    public void getH5DatasetPaths() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(FILE_PLLEG));
        Group root = classUnderTest.getH5Root(file);
        List<String> paths = new ArrayList<String>();
        classUnderTest.getH5DatasetPaths(root, paths);
        assertEquals(10, paths.size());
    }
    
    @Test
    public void getH5Dataset() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(FILE_PLLEG));
        Group root = classUnderTest.getH5Root(file);
        List<String> paths = new ArrayList<String>();
        classUnderTest.getH5DatasetPaths(root, paths);
        classUnderTest.getH5Dataset(root, paths.get(2));
        Dataset dataset = classUnderTest.getH5Dataset();
        assertNotNull(dataset);
    }
    
    @Test
    public void getH5Attribute() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(FILE_PLLEG));
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
        H5File file = classUnderTest.openH5File(getFilePath(FILE_PLLEG));
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
    public void polar2CartImage_PL_LEG() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(FILE_PLLEG));
        Group root = classUnderTest.getH5Root(file);
        classUnderTest.getH5Attribute(root, "/dataset1/where", "nbins");
        
        assertEquals("nbins", classUnderTest.getH5Attribute().getName());
        
        long nbins = (Long) classUnderTest.getH5AttributeValue();
        classUnderTest.getH5Attribute(root, "/dataset1/where", "rscale");
        
        assertEquals("rscale", classUnderTest.getH5Attribute().getName());
        
        double rscale = (Double) classUnderTest.getH5AttributeValue();
        
        assertEquals(1000, rscale, 0);
        
        classUnderTest.getH5Dataset(root, "/dataset1/data1/data");
        Dataset dataset = classUnderTest.getH5Dataset();
        
        assertNotNull(dataset);
        
        Color[] palette = classUnderTest
                .createColorPalette("conf/color_palette.txt");
        BufferedImage bi = classUnderTest.polar2Image(nbins, rscale, 0.0, 0.0, 0.0, 0.0, dataset, 
                palette, 0, true, true);
        
        assertNotNull(bi);
        assertTrue(classUnderTest
                .saveImageToFile(bi, "legionowo_dataset1.png"));
    }
    
    @Test 
    public void polar2CartImage_LV_RIX() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(FILE_LVRIX));
        Group root = classUnderTest.getH5Root(file);
        classUnderTest.getH5Attribute(root, "/dataset1/where", "nbins");
        
        assertEquals("nbins", classUnderTest.getH5Attribute().getName());
        
        long nbins = (Long) classUnderTest.getH5AttributeValue();
        classUnderTest.getH5Attribute(root, "/dataset1/where", "rscale");
        
        assertEquals("rscale", classUnderTest.getH5Attribute().getName());
        
        double rscale = (Double) classUnderTest.getH5AttributeValue();
        
        assertEquals(500, rscale, 0);
        
        classUnderTest.getH5Dataset(root, "/dataset1/data1/data");
        Dataset dataset = classUnderTest.getH5Dataset();
        
        assertNotNull(dataset);
        
        Color[] palette = classUnderTest
                .createColorPalette("conf/color_palette.txt");
        BufferedImage bi = classUnderTest.polar2Image(nbins, rscale, 0.0, 0.0, 0.0, 0.0, 
                dataset, palette, 0, true, true);
        
        assertNotNull(bi);
        assertTrue(classUnderTest
                .saveImageToFile(bi, "riga_dataset1.png"));
    }
    
    @Test 
    public void polar2CartImage_BY_MIN() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(FILE_BYMIN));
        Group root = classUnderTest.getH5Root(file);
        classUnderTest.getH5Attribute(root, "/dataset1/where", "nbins");
        
        assertEquals("nbins", classUnderTest.getH5Attribute().getName());
        
        long nbins = (Long) classUnderTest.getH5AttributeValue();
        classUnderTest.getH5Attribute(root, "/dataset1/where", "rscale");
        
        assertEquals("rscale", classUnderTest.getH5Attribute().getName());
        
        double rscale = (Double) classUnderTest.getH5AttributeValue();
        
        assertEquals(250, rscale, 0);
        
        classUnderTest.getH5Dataset(root, "/dataset1/data1/data");
        Dataset dataset = classUnderTest.getH5Dataset();
        
        assertNotNull(dataset);
        
        Color[] palette = classUnderTest
                .createColorPalette("conf/color_palette.txt");
        BufferedImage bi = classUnderTest.polar2Image(nbins, rscale, 0.0, 0.0, 0.0, 0.0, 
                dataset, palette, 0, true, true);
        
        assertNotNull(bi);
        assertTrue(classUnderTest
                .saveImageToFile(bi, "minsk_dataset1.png"));
    }
    
    @Test 
    public void polar2CartImage_16bitData() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(FILE_16BIT));
        Group root = classUnderTest.getH5Root(file);
        
        classUnderTest.getH5Attribute(root, "/dataset1/data1/what", "gain");
        double gain = (Double)classUnderTest.getH5AttributeValue();
        classUnderTest.getH5Attribute(root, "/dataset1/data1/what", "offset");
        double offset = (Double)classUnderTest.getH5AttributeValue();
        classUnderTest.getH5Attribute(root, "/dataset1/data1/what", "nodata");
        double nodata = (Double)classUnderTest.getH5AttributeValue();
        classUnderTest.getH5Attribute(root, "/dataset1/data1/what", "undetect");
        double undetect = (Double)classUnderTest.getH5AttributeValue();
        //System.out.println("GAIN:" + gain + ", OFFSET: " + offset + "NODATA: " + nodata + ", UNDETECT: " + undetect);
        
        classUnderTest.getH5Attribute(root, "/dataset1/where", "nbins");
        assertEquals("nbins", classUnderTest.getH5Attribute().getName());
        
        long nbins = (Long) classUnderTest.getH5AttributeValue();
        classUnderTest.getH5Attribute(root, "/dataset1/where", "rscale");
        
        assertEquals("rscale", classUnderTest.getH5Attribute().getName());
        
        double rscale = (Double) classUnderTest.getH5AttributeValue();
        
        assertEquals(500, rscale, 0);
        
        classUnderTest.getH5Dataset(root, "/dataset1/data1/data");
        Dataset dataset = classUnderTest.getH5Dataset();
        
        assertNotNull(dataset);
        
        Color[] palette = classUnderTest
                .createColorPalette("conf/color_palette.txt");
        BufferedImage bi = classUnderTest.polar2Image(nbins, rscale, -32768.0, 0.0, 0.0, 0.0, 
                dataset, palette, 0, true, true);
        
        assertNotNull(bi);
        assertTrue(classUnderTest
                .saveImageToFile(bi, "16bit_dataset1.png"));
    }
    
    @Test 
    public void comp2CartImage() throws Exception {
        H5File file = classUnderTest.openH5File(getFilePath(FILE_COMP));
        Group root = classUnderTest.getH5Root(file);
        classUnderTest.getH5Attribute(root, "/where", "xsize");
        
        assertEquals("xsize", classUnderTest.getH5Attribute().getName());
        
        long xsize = (Long) classUnderTest.getH5AttributeValue();
        
        assertEquals(754, xsize);
        
        classUnderTest.getH5Attribute(root, "/where", "ysize");
        
        assertEquals("ysize", classUnderTest.getH5Attribute().getName());
        
        long ysize = (Long) classUnderTest.getH5AttributeValue();
        
        assertEquals(810, ysize);
        
        classUnderTest.getH5Attribute(root, "/dataset1/data1/what", "nodata");
        
        assertEquals("nodata", classUnderTest.getH5Attribute().getName());
        
        double nodata = (Double) classUnderTest.getH5AttributeValue();
        
        assertEquals(255, nodata, 0);
        
        classUnderTest.getH5Dataset(root, "/dataset1/data1/data");
        Dataset dataset = classUnderTest.getH5Dataset();
        
        assertNotNull(dataset);
        
        Color[] palette = classUnderTest
                .createColorPalette("conf/color_palette.txt");
        BufferedImage bi = classUnderTest.cart2Image(xsize, ysize, nodata, 
                dataset, palette, 0, 0, true);
        
        assertNotNull(bi);
        assertTrue(classUnderTest.saveImageToFile(bi, "polcomp.png"));
        
    }
    
}
