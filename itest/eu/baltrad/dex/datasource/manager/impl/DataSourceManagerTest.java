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

package eu.baltrad.dex.datasource.manager.impl;

import eu.baltrad.dex.datasource.manager.impl.DataSourceManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.FileObject;
import eu.baltrad.dex.db.itest.DexDBITestHelper;
import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.user.model.User;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import org.dbunit.dataset.ITable;
import org.dbunit.Assertion;

import junit.framework.TestCase;

import java.util.List;

/**
 * Data source manager integration test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.1
 * @since 1.1.1
 */
public class DataSourceManagerTest extends TestCase {

    private DataSourceManager classUnderTest;
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this, null);
        classUnderTest = new DataSourceManager();
        SimpleJdbcOperations jdbcTemplate = (SimpleJdbcOperations) context
                .getBean("jdbcTemplate");
        classUnderTest.setJdbcTemplate(jdbcTemplate);
    }
    
    @Override
    public void tearDown() throws Exception {
        context.close();
    }
    
    private void verifyDBTables(String suffix, String tableName,
            String ignoreColumn) throws Exception {
        ITable expected = helper.getXMLTable(this, suffix, tableName);
        ITable actual = helper.getDBTable(tableName);
        if (ignoreColumn != null) {
            Assertion.assertEqualsIgnoreCols(expected, actual, 
                new String[] {ignoreColumn});   
        } else {
            Assertion.assertEquals(expected, actual);
        }
    }
    
    public void verifyAll() throws Exception {
        verifyDBTables(null, "dex_data_sources", null);
        verifyDBTables(null, "dex_users", null);
        verifyDBTables(null, "dex_roles", null);
        verifyDBTables(null, "dex_users_roles", null);
        verifyDBTables(null, "dex_data_source_users", null);
        verifyDBTables(null, "dex_radars", null);
        verifyDBTables(null, "dex_data_source_radars", null);
        verifyDBTables(null, "dex_file_objects", null);
        verifyDBTables(null, "dex_data_source_file_objects", null);
        verifyDBTables(null, "dex_data_source_filters", null);
    }
    
    public void testLoadLocal() throws Exception {
        List<DataSource> dataSources = classUnderTest.load(DataSource.LOCAL);
        assertNotNull(dataSources);
        assertTrue(dataSources.size() == 2);
        verifyAll();
    }
    
    public void testLoadPeer() throws Exception {
        List<DataSource> dataSources = classUnderTest.load(DataSource.PEER);
        assertNotNull(dataSources);
        assertTrue(dataSources.size() == 1);
        verifyAll();
    }
    
    public void testLoadById() throws Exception {
        DataSource dataSource = classUnderTest.load(2);
        assertEquals(2, dataSource.getId());
        assertEquals("DataSource2", dataSource.getName());
        assertEquals("local", dataSource.getType());
        assertEquals("Another test data source", dataSource.getDescription());
        verifyAll();
    }
    
    public void testLoadByName() throws Exception {
        DataSource dataSource = classUnderTest.load("DataSource3", 
                DataSource.PEER);
        assertEquals(3, dataSource.getId());
        assertEquals("DataSource3", dataSource.getName());
        assertEquals("peer", dataSource.getType());
        assertEquals("Yet one more test data source", 
                dataSource.getDescription());
        verifyAll();
    }
    
    public void testLoadByUser() throws Exception {
        List<DataSource> userDataSources = classUnderTest.load(2, 
                DataSource.LOCAL);
        assertNotNull(userDataSources);
        assertEquals(2, userDataSources.size());
        verifyAll();
    }
    
    public void testStore() throws Exception {
        helper.cleanInsert(this, "noid");
        DataSource dataSource = new DataSource("DataSource4", 
                DataSource.PEER, "Stored data source");
        
        assertEquals(4, classUnderTest.store(dataSource));
        assertEquals("DataSource4", dataSource.getName());
        assertEquals("Stored data source", dataSource.getDescription());
        verifyDBTables("store", "dex_data_sources", "id");
    }
    
    public void testUpdate() throws Exception {
        DataSource dataSource = new DataSource("DataSource3", 
                DataSource.PEER, "Updated data source");
        dataSource.setId(3);
        assertEquals(1, classUnderTest.update(dataSource));
        assertEquals("DataSource3", dataSource.getName());
        assertEquals("peer", dataSource.getType());
        assertEquals("Updated data source", dataSource.getDescription());
        verifyDBTables("update", "dex_data_sources", null);
    }
    
    public void testDelete() throws Exception {
        assertEquals(1, classUnderTest.delete(2));
        verifyDBTables("delete", "dex_data_sources", null);
    }
    
    public void testLoadRadar() throws Exception {
        List<Radar> radars = classUnderTest.loadRadar(2);
        assertNotNull(radars);
        assertEquals(2, radars.size());
        verifyAll();
    }
    
    public void testDeleteRadar() throws Exception {
        assertEquals(2, classUnderTest.deleteRadar(2));
        verifyDBTables("delete", "dex_data_source_radars", null);
    }
    
    public void testLoadFileObject() throws Exception {
        List<FileObject> fobjects = classUnderTest.loadFileObject(1);
        assertNotNull(fobjects);
        assertEquals(2, fobjects.size());
        verifyAll();
    } 
    
    public void testDeleteFileObject() throws Exception {
        assertEquals(2, classUnderTest.deleteFileObject(1));
        verifyDBTables("delete", "dex_data_source_file_objects", null);
    }
    
    public void testLoadUser() throws Exception {
        List<User> users = classUnderTest.loadUser(3);
        assertNotNull(users);
        assertEquals(2, users.size());
        verifyAll();
    }
    
    public void testDeleteUser() throws Exception {
        assertEquals(2, classUnderTest.deleteUser(3));
        verifyDBTables("delete", "dex_data_source_users", null);
    }
    
    public void testLoadFilterId() throws Exception {
        assertEquals(1, classUnderTest.loadFilterId(1));
        verifyAll();
    }
    
    public void testDeleteFilter() throws Exception {
        assertEquals(2, classUnderTest.deleteFilter(2));
        verifyDBTables("delete", "dex_data_source_filters", null);
    }
    
}
