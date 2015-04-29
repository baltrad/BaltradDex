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

package eu.baltrad.dex.util;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.File;

import java.util.Map;
import java.util.HashMap;

import java.io.ByteArrayInputStream;

/**
 * Data compression utility test.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 1.7.0
 */
public class CompressDataUtilTest {
    
    class ZipDataUtil extends CompressDataUtil {
        
        public ZipDataUtil(String rootFolder) {
            this.rootFolder = rootFolder;
            this.fileListing = new HashMap<String, String>();
        }
        public Map<String, String> getFileListing() {
            return fileListing;
        }
    }
    
    private ZipDataUtil classUnderTest;
    
    private static int folderSize(File directory) {
        int length = 0;
        File[] files = directory.listFiles();
        if (files != null) {
        for (File file : files) {
            if (file.isFile()) {
                length += file.length();
            } else {
                length += folderSize(file); 
            }    
        }
        }
        return length;
    }
    
    @Before
    public void setUp() {
        classUnderTest = new ZipDataUtil("keystore/localhost.pub");
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
    }
    
    @Test
    public void getZipEntry() {
        File f = new File("keystore/localhost.pub/meta");
        assertEquals("meta", classUnderTest.getZipEntry(f.getAbsolutePath()));
        f = new File("keystore/localhost.pub/1");
        assertEquals("1", classUnderTest.getZipEntry(f.getAbsolutePath()));
    }
    
    @Test
    public void listFiles() {
        classUnderTest.listFiles(new File("keystore/localhost.pub"));
        assertTrue(classUnderTest.getFileListing().containsKey("1"));
        assertTrue(classUnderTest.getFileListing().containsKey("meta"));
    }
    
    @Test 
    public void fileToByteArray() {
        byte[] bytes = classUnderTest
                .fileToByteArray("keystore/localhost.pub/meta");
        assertEquals(145, bytes.length);
    }
    
    @Test
    public void zip() throws Exception {
        byte[] bytes = classUnderTest.zip();
        
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }
    
    @Test
    public void unzipToFile() throws Exception {
        byte[] bytes = classUnderTest.zip();
        classUnderTest.unzip("localhost.pub", bytes);
        
        assertEquals((new File("keystore/localhost.pub/1")).length(), 
                (new File("localhost.pub/1")).length()); 
        assertEquals((new File("keystore/localhost.pub/meta")).length(), 
                (new File("localhost.pub/meta")).length()); 
    } 
    
    @Test
    public void unzipToStream() throws Exception {
        byte[] zip = classUnderTest.zip();
        byte[] unzip = classUnderTest
                .unzip(new ByteArrayInputStream(zip));
        
        assertNotNull(unzip);
        assertEquals(folderSize(new File("keystore/localhost.pub")), unzip.length);
    }
    
}
