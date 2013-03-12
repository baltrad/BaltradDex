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

package eu.baltrad.dex.db.itest;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.util.Map;
import java.util.HashMap;

import javax.sql.DataSource;
import java.sql.Connection;

import junit.framework.TestCase;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

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
    
    private DataSource dataSource = null;
    private IDataTypeFactory factory = null;
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
    
    public DexDBITestHelper() {
        
    }
    
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
     * Gets database connection.
     * @param conn
     * @return
     * @throws Exception 
     */
    private IDatabaseConnection getConnection(Connection conn) throws Exception 
    {
        IDatabaseConnection connection = new DatabaseConnection(conn);
        connection.getConfig().setProperty(
                DatabaseConfig.PROPERTY_DATATYPE_FACTORY, getFactory());
        return connection;
    }
    
    /**
     * Delete test data from database.
     */
    private void deleteFromDB() {
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);
        template.update("DELETE FROM dex_data_source_radars");
        template.update("DELETE FROM dex_data_source_filters");
        template.update("DELETE FROM dex_data_source_file_objects");
        template.update("DELETE FROM dex_data_source_users");
        template.update("DELETE FROM dex_data_sources");
        template.update("DELETE FROM dex_users_roles");
        template.update("DELETE FROM dex_users");
        template.update("DELETE FROM dex_roles");
        template.update("DELETE FROM dex_file_objects");
        template.update("DELETE FROM dex_radars");
        template.update("DELETE FROM dex_subscriptions");
        template.update("DELETE FROM dex_subscriptions_users");
        template.update("DELETE FROM dex_subscriptions_data_sources");
        template.update("DELETE FROM dex_messages");
        template.update("DELETE FROM dex_delivery_registry_users");
        template.update("DELETE FROM dex_delivery_registry");
        template.update("DELETE FROM dex_keys");
    }
    
    /**
     * Inserts test dataset for a given test case.
     * @param tc
     * @throws Exception 
     */
    public void cleanInsert(Object tc, String suffix) throws Exception {
        Connection conn = dataSource.getConnection();
        try {
            deleteFromDB();
            IDatabaseConnection connection = getConnection(conn);
            DatabaseOperation.CLEAN_INSERT.execute(connection, 
                    getXMLDataset(tc, suffix));
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
    
    /**
     * Gets test dataset from XML file.
     * @param tc
     * @param suffix
     * @return
     * @throws Exception 
     */
    private FlatXmlDataSet getXMLDataset(Object tc, String suffix) 
            throws Exception {
        String className = getClassName(tc.getClass());
        String resourceName = className;
        if (suffix != null) {
            resourceName += "-" + suffix;
        }
        resourceName += ".xml";
        File f = new File(tc.getClass().getResource(resourceName).getFile());
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        return builder.build(f);
    }
    
    /**
     * Gets database table.
     * @param name
     * @return
     * @throws Exception 
     */
    public ITable getDBTable(String name) throws Exception {
        Connection conn = dataSource.getConnection();
        try {
            IDatabaseConnection connection = getConnection(conn);
            IDataSet dataset = connection.createDataSet();
            return dataset.getTable(name);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
    
    /**
     * Gets XML table.
     * @param tc
     * @param name
     * @return
     * @throws Exception 
     */
    public ITable getXMLTable(Object tc, String suffix, String name) 
            throws Exception {
        IDataSet dataset = getXMLDataset(tc, suffix);
        return dataset.getTable(name);
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

    /**
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return the factory
     */
    public IDataTypeFactory getFactory() {
        return factory;
    }

    /**
     * @param factory the factory to set
     */
    public void setFactory(IDataTypeFactory factory) {
        this.factory = factory;
    }
}