/*******************************************************************************
*
* Copyright (C) 2009-2014 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.status.manager.impl;

import java.util.List;

import junit.framework.TestCase;

import org.dbunit.Assertion;
import org.dbunit.dataset.ITable;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.JdbcOperations;

import eu.baltrad.dex.db.itest.DexDBITestHelper;
import eu.baltrad.dex.status.model.Status;

/**
 * Node status manager integration test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 2.0.0
 * @since 2.0.0
 */
public class NodeStatusManagerTest extends TestCase {
    
    private NodeStatusManager classUnderTest;
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this, null);
        classUnderTest = new NodeStatusManager();
        JdbcOperations jdbcTemplate = (JdbcOperations) context
                .getBean("jdbcTemplate");
        classUnderTest.setJdbcTemplate(jdbcTemplate);
    }
    
    @Override
    public void tearDown() throws Exception {
        context.close();
        classUnderTest = null;
    }
    
    private void verifyDBTables(String suffix, String tableName,
                String ignoreColumn) 
            throws Exception {
        ITable expected = helper.getXMLTable(this, suffix, tableName);
        ITable actual = helper.getDBTable(tableName);
        if (ignoreColumn != null) {
            Assertion.assertEqualsIgnoreCols(expected, actual, 
                new String[] {ignoreColumn});
        } else {
            Assertion.assertEquals(expected, actual);
        }
    }
    
    public void testLoadNodeNames() throws Exception {
        List<String> nodeNames = classUnderTest.loadNodeNames();
        
        assertTrue(nodeNames.contains("se.baltrad.eu"));
        assertTrue(nodeNames.contains("no.met.beast"));
        assertTrue(nodeNames.contains("dk.dmi"));
        
        verifyDBTables(null, "dex_status", null);
        verifyDBTables(null, "dex_status_subscriptions", "id");
    }
    
    public void testLoadBySubscription() throws Exception {
        Status status = classUnderTest.load(3);
        
        assertEquals("no.met.beast", status.getNodeName());
        assertEquals("StadPVOLs", status.getDataSource());
        assertEquals("local", status.getSubscriptionType());
        assertEquals(true, status.getSubscriptionActive());
        assertEquals(1364407277970L, status.getSubscriptionStart());
        assertEquals(53453245, status.getDownloads());
        assertEquals(0, status.getUploads());
        assertEquals(0, status.getUploadFailures());
        
        status = classUnderTest.load(7);
        
        assertEquals("no.met.beast", status.getNodeName());
        assertEquals("LegionowoPVOLs", status.getDataSource());
        assertEquals("peer", status.getSubscriptionType());
        assertEquals(true, status.getSubscriptionActive());
        assertEquals(1364409277970L, status.getSubscriptionStart());
        assertEquals(0, status.getDownloads());
        assertEquals(7868769, status.getUploads());
        assertEquals(123, status.getUploadFailures());
        
        verifyDBTables(null, "dex_status", null);
        verifyDBTables(null, "dex_status_subscriptions", "id");
    }
    
    public void testLoadByPeerAndSubscription() throws Exception {
        List<Status> stats = classUnderTest.load("se.baltrad.eu", "peer");
        
        assertEquals(2, stats.size());
        
        Status st = stats.get(0);
        
        assertEquals("se.baltrad.eu", st.getNodeName());
        assertEquals("LegionowoPVOLs", st.getDataSource());
        assertEquals("peer", st.getSubscriptionType());
        assertEquals(true, st.getSubscriptionActive());
        assertEquals(1364405477970L, st.getSubscriptionStart());
        assertEquals(0, st.getDownloads());
        assertEquals(543532455, st.getUploads());
        assertEquals(45, st.getUploadFailures());
        
        st = stats.get(1);
        
        assertEquals("se.baltrad.eu", st.getNodeName());
        assertEquals("PoznańPVOLs", st.getDataSource());
        assertEquals("peer", st.getSubscriptionType());
        assertEquals(true, st.getSubscriptionActive());
        assertEquals(1364408277970L, st.getSubscriptionStart());
        assertEquals(0, st.getDownloads());
        assertEquals(45634453, st.getUploads());
        assertEquals(55, st.getUploadFailures());
        
        stats = classUnderTest.load("no.met.beast", "peer");
        
        assertEquals(2, stats.size());
        
        st = stats.get(0);
        
        assertEquals("no.met.beast", st.getNodeName());
        assertEquals("LegionowoPVOLs", st.getDataSource());
        assertEquals("peer", st.getSubscriptionType());
        assertEquals(true, st.getSubscriptionActive());
        assertEquals(1364409277970L, st.getSubscriptionStart());
        assertEquals(0, st.getDownloads());
        assertEquals(7868769, st.getUploads());
        assertEquals(123, st.getUploadFailures());
        
        st = stats.get(1);
        
        assertEquals("no.met.beast", st.getNodeName());
        assertEquals("PoznańPVOLs", st.getDataSource());
        assertEquals("peer", st.getSubscriptionType());
        assertEquals(true, st.getSubscriptionActive());
        assertEquals(1364434527970L, st.getSubscriptionStart());
        assertEquals(0, st.getDownloads());
        assertEquals(987989879, st.getUploads());
        assertEquals(987, st.getUploadFailures());
        
        stats = classUnderTest.load("se.baltrad.eu", "local");
        
        assertEquals(2, stats.size());
        
        st = stats.get(0);
        
        assertEquals("se.baltrad.eu", st.getNodeName());
        assertEquals("ArlandaSCANs", st.getDataSource());
        assertEquals("local", st.getSubscriptionType());
        assertEquals(true, st.getSubscriptionActive());
        assertEquals(1364405277970L, st.getSubscriptionStart());
        assertEquals(4123412, st.getDownloads());
        assertEquals(0, st.getUploads());
        assertEquals(0, st.getUploadFailures());
        
        st = stats.get(1);
        
        assertEquals("se.baltrad.eu", st.getNodeName());
        assertEquals("KarlskronaSCANs", st.getDataSource());
        assertEquals("local", st.getSubscriptionType());
        assertEquals(true, st.getSubscriptionActive());
        assertEquals(1364406277970L, st.getSubscriptionStart());
        assertEquals(8790935, st.getDownloads());
        assertEquals(0, st.getUploads());
        assertEquals(0, st.getUploadFailures());
        
        verifyDBTables(null, "dex_status", null);
        verifyDBTables(null, "dex_status_subscriptions", "id");
    }
    
    public void testStoreStatus() throws Exception {
        helper.cleanInsert(this, "noid");
        
        assertEquals(10, classUnderTest.store(new Status(0, 0, 0)));
        assertEquals(11, classUnderTest.store(new Status(0, 0, 0)));
        
        verifyDBTables("store", "dex_status", "id");
    }
    
    public void testStoreStatusReference() throws Exception {
        helper.cleanInsert(this, "noid");
        
        int statusId = classUnderTest.store(new Status(0, 0, 0));
                
        assertEquals(1, classUnderTest.store(statusId, 3));
        
        statusId = classUnderTest.store(new Status(0, 0, 0));
        
        assertEquals(1, classUnderTest.store(statusId, 4));
        
        verifyDBTables("store", "dex_status", "id");
        verifyDBTables("store", "dex_status_subscriptions", "id");
    }
    
    public void testUpdate() throws Exception {
        helper.cleanInsert(this, "noid");
        
        Status st = classUnderTest.load(3);
        
        assertNotNull(st);
        
        st.incrementDownloads();
        st.incrementUploads();
        st.incrementUploadFailures();
        
        assertEquals(1, classUnderTest.update(st, 3));
        
        st = classUnderTest.load(7);
        
        assertNotNull(st);
        
        st.incrementDownloads();
        st.incrementUploads();
        st.incrementUploadFailures();
        
        assertEquals(1, classUnderTest.update(st, 7));
        
        verifyDBTables("update", "dex_status", "id");
        verifyDBTables("update", "dex_status_subscriptions", "id");
    }
    
    public void testDelete() throws Exception {
        helper.cleanInsert(this, "noid");
        
        assertEquals(1, classUnderTest.delete(3));
        assertEquals(1, classUnderTest.delete(5));
        assertEquals(1, classUnderTest.delete(8));
        
        verifyDBTables("delete", "dex_status", "id");
        verifyDBTables("delete", "dex_status_subscriptions", "id");
    }
    
}