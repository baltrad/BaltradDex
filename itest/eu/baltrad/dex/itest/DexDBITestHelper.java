/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.itest;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.util.Map;
import java.util.HashMap;

import junit.framework.TestCase;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eu.baltrad.bdb.db.FileEntry;
import eu.baltrad.bdb.db.Database;
import eu.baltrad.bdb.db.rest.RestfulDatabase;

/**
 * Baltrad-db integration test helper.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0.7
 * @since 1.0.7
 */
public class DexDBITestHelper extends TestCase {
    
    private String bdbServerUri = null;
    private String bdbStoragePath = null;
    private static Map<String, String> uuidMap;
    
    // test files 
    private static String[] FIXTURES = {
        "fixtures/Z_PVOL_20120315074000_plbrz.h5",
        "fixtures/Z_PVOL_20120315074000_plrze.h5",
        "fixtures/Z_PVOL_20120315074000_plleg.h5",
        "fixtures/Z_PVOL_20120315074000_plpoz.h5",
        "fixtures/Z_PVOL_20120315074000_plpas.h5",
        "fixtures/Z_PVOL_20120315074000_plram.h5",
        "fixtures/Z_PVOL_20120315074000_plgda.h5",
        "fixtures/Z_PVOL_20120315074000_plswi.h5",
        "fixtures/Z_PVOL_20120321073000_plbrz.h5",
        "fixtures/Z_PVOL_20120321073000_plrze.h5",
        "fixtures/Z_PVOL_20120321073000_plpoz.h5",
        "fixtures/Z_PVOL_20120321073000_plpas.h5",
        "fixtures/Z_PVOL_20120321073000_plram.h5",
        "fixtures/Z_PVOL_20120321073000_plgda.h5",
        "fixtures/Z_PVOL_20120321073000_plswi.h5",
        "fixtures/Z_PVOL_20120322074000_plbrz.h5",
        "fixtures/Z_PVOL_20120322075000_plbrz.h5",
        "fixtures/Z_PVOL_20120322080000_plbrz.h5",
        "fixtures/Z_PVOL_20120322081000_plbrz.h5",
        "fixtures/Z_SCAN_C_ESWI_20101023180000_seang_000000.h5",
        "fixtures/Z_SCAN_C_ESWI_20101023180000_searl_000000.h5",
        "fixtures/Z_SCAN_C_ESWI_20101023180000_sease_000000.h5",
        "fixtures/Z_SCAN_C_ESWI_20101023180000_sehud_000000.h5",
        "fixtures/Z_SCAN_C_ESWI_20101023180000_sekir_000000.h5",
        "fixtures/Z_SCAN_C_ESWI_20101023180000_sekkr_000000.h5",
        "fixtures/Z_SCAN_C_ESWI_20101023180000_selek_000000.h5",
        "fixtures/Z_SCAN_C_ESWI_20101023180000_selul_000000.h5",
        "fixtures/Z_SCAN_C_ESWI_20101023180000_seosu_000000.h5",
        "fixtures/Z_SCAN_C_ESWI_20101023180000_seovi_000000.h5",
        "fixtures/Z_SCAN_C_ESWI_20101023180000_sevar_000000.h5",
        "fixtures/Z_SCAN_C_ESWI_20101023180000_sevil_000000.h5"
    };
    
    public DexDBITestHelper(String bdbServerUri, String bdbStoragePath) {
        this.bdbServerUri = bdbServerUri;
        this.bdbStoragePath = bdbStoragePath;
        // we don't want to insert and purge with each test method
        if (uuidMap == null) {
            try {
                purgeBaltradDB();
                insertIntoBaltradDB();
            } catch(Exception e) {
                System.out.println("failed to initialize DexDBITestHelper: " 
                                                              + e.getMessage());
            }
        }
    }
    
    public static String getClassName(Class clazz) {
        String name = clazz.getName();
        int li = name.lastIndexOf(".");
        if (li > 0) {
            name = name.substring(li + 1);
        }
        return name; 
    }
    
    /**
     * Loads an application context from a test case using the
     * <ClassName>-context.xml descriptor and loads it as a resource.
     * 
     * @param tc Test case
     * @return Application context
     */
    public static AbstractApplicationContext loadContext(TestCase tc) {
        String className = getClassName(tc.getClass());
        String ctxName = className + "-context.xml";
        File f = new File(tc.getClass().getResource(ctxName).getFile());
        return new ClassPathXmlApplicationContext("file:" 
                                                         + f.getAbsolutePath());
    }
    
    /**
     * Gets a file as a resource and returns its absolute path.
     * 
     * @param resource
     * @return
     * @throws Exception 
     */
    private String getFilePath(String resource) throws Exception {
        File f = new File(DexDBITestHelper.class.getResource(
                                                           resource).getFile());
        return f.getAbsolutePath();
    }
    
    /**
     * Removes all file entries from baltrad-db and purges storage directory
     * 
     * @throws Exception 
     */
    private void purgeBaltradDB() throws Exception {
        String bdbStoragePth = getBdbStoragePath();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".h5");
            }
        };
        // remove storage content
        File dir = new File(bdbStoragePth);
        String[] files = dir.list(filter);
        if (files != null) {
            for (String file : files) {
                File f = new File(bdbStoragePth, file);
                f.delete();
            }
        }
        // remove all file entries
        Database bdb = new RestfulDatabase(bdbServerUri);
        bdb.removeAllFileEntries();
    }
    
    /**
     * Inserts file entries into baltrad-db.
     * 
     * @throws Exception 
     */
    private void insertIntoBaltradDB() throws Exception {
        Database bdb = new RestfulDatabase(getBdbServerUri());
        uuidMap = new HashMap<String, String>();
        long startTime = System.currentTimeMillis();
        for (String fileName : FIXTURES) {
            FileInputStream fis = new FileInputStream(getFilePath(fileName));
            FileEntry fileEntry = bdb.store(fis);
            assertNotNull(fileEntry);
            uuidMap.put(fileEntry.getUuid().toString(), fileName);
        }
        System.out.println("Stored " + FIXTURES.length + " files in " + 
                (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     * @return the bdbServerUri
     */
    public String getBdbServerUri() {
        return bdbServerUri;
    }

    /**
     * @param bdbServerUri the bdbServerUri to set
     */
    public void setBdbServerUri(String bdbServerUri) {
        this.bdbServerUri = bdbServerUri;
    }

    /**
     * @return the bdbStoragePath
     */
    public String getBdbStoragePath() {
        return bdbStoragePath;
    }

    /**
     * @param bdbStoragePath the bdbStoragePath to set
     */
    public void setBdbStoragePath(String bdbStoragePath) {
        this.bdbStoragePath = bdbStoragePath;
    }

    /**
     * @return the uuidMap
     */
    public Map<String, String> getUuidMap() {
        return uuidMap;
    }

    /**
     * @param uuidMap the uuidMap to set
     */
    public void setUuidMap(Map<String, String> _uuidMap) {
        uuidMap = _uuidMap;
    }
}