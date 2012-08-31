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

package eu.baltrad.dex.net.model;

import eu.baltrad.dex.db.itest.DexDBITestHelper;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import junit.framework.TestCase;

import org.dbunit.dataset.ITable;
import org.dbunit.Assertion;

import java.util.List;

/**
 * Node connection manager integration test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class NodeConnectionManagerTest extends TestCase {
    
    private NodeConnectionManager classUnderTest;
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this);
        classUnderTest = new NodeConnectionManager();
        SimpleJdbcOperations jdbcTemplate = (SimpleJdbcOperations) context
                .getBean("jdbcTemplate");
        classUnderTest.setJdbcTemplate(jdbcTemplate);
    }
    
    @Override
    public void tearDown() throws Exception {
        context.close();
        classUnderTest = null;
    }
    
    private void verifyDBTables(String suffix, String tableName) 
            throws Exception {
        ITable expected = helper.getXMLTable(this, suffix, tableName);
        ITable actual = helper.getDBTable(tableName);
        Assertion.assertEquals(expected, actual);
    }
    
    private void verifyAll() throws Exception {
        verifyDBTables(null, "dex_node_connections");
    }
    
    public void testLoadAll() throws Exception {
        List<NodeConnection> conns = classUnderTest.load();
        assertNotNull(conns);
        assertEquals(5, conns.size());
        verifyAll();
    }
    
    public void testLoadById() throws Exception {
        NodeConnection conn = classUnderTest.load(6);
        assertNull(conn);
        conn = classUnderTest.load(4);
        assertNotNull(conn);
        assertEquals(4, conn.getId());
        assertEquals("TestNode4", conn.getNodeName());
        assertEquals("NodeAddress4", conn.getNodeAddress());
        verifyAll();
    }
    
    public void testLoadByNodeName() throws Exception {
        NodeConnection conn = classUnderTest.load("TestNode3");
        assertNotNull(conn);
        assertEquals(3, conn.getId());
        assertEquals("TestNode3", conn.getNodeName());
        assertEquals("NodeAddress3", conn.getNodeAddress());
        verifyAll();
    }
    
    public void testStore() throws Exception {
        NodeConnection conn = new NodeConnection(6, "TestNode6", 
                "NodeAddress6");
        int store = classUnderTest.store(conn);
        assertEquals(1, store);
        verifyDBTables("store", "dex_node_connections");
    }
    
    public void testDeleteAll() {
        int delete = classUnderTest.delete();
        assertEquals(5, delete);
        List<NodeConnection> conns = classUnderTest.load();
        assertEquals(0, conns.size());
    }
    
    public void testDelete() throws Exception {
        int delete = classUnderTest.delete(3);
        assertEquals(1, delete);
        delete = classUnderTest.delete(1);
        assertEquals(1, delete);
        verifyDBTables("delete", "dex_node_connections");
    }
    
}
