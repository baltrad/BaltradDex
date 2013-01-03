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

package eu.baltrad.dex.net.manager.impl;

import eu.baltrad.dex.net.model.impl.Node;
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
public class NodeManagerTest extends TestCase {
    
    private NodeManager classUnderTest;
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this, null);
        classUnderTest = new NodeManager();
        SimpleJdbcOperations jdbcTemplate = (SimpleJdbcOperations) context
                .getBean("jdbcTemplate");
        classUnderTest.setJdbcTemplate(jdbcTemplate);
    }
    
    @Override
    public void tearDown() throws Exception {
        context.close();
        classUnderTest = null;
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
    
    private void verifyAll() throws Exception {
        verifyDBTables(null, "dex_nodes", null);
        verifyDBTables(null, "dex_users_nodes", null);
        verifyDBTables(null, "dex_subscriptions", null);
        verifyDBTables(null, "dex_subscriptions_nodes", null);
    }
    
    public void testLoadAll() throws Exception {
        List<Node> nodes = classUnderTest.load();
        assertNotNull(nodes);
        assertEquals(5, nodes.size());
        verifyAll();
    }
    
    public void testLoadById() throws Exception {
        Node node = classUnderTest.load(6);
        assertNull(node);
        node = classUnderTest.load(4);
        assertNotNull(node);
        assertEquals(4, node.getId());
        assertEquals("TestNode4", node.getName());
        assertEquals("http://test4.eu", node.getAddress());
        verifyAll();
    }
    
    public void testLoadByName() throws Exception {
        Node node = classUnderTest.load("TestNode3");
        assertNotNull(node);
        assertEquals(3, node.getId());
        assertEquals("TestNode3", node.getName());
        assertEquals("http://test3.eu", node.getAddress());
        verifyAll();
    }
    
    public void testLoadByUser() throws Exception {
        Node node = classUnderTest.loadByUser(3);
        assertNotNull(node);
        assertEquals(3, node.getId());
        assertEquals("TestNode3", node.getName());
        assertEquals("http://test3.eu", node.getAddress());
        verifyAll();
    }
    
    public void testLoadOperators() throws Exception {
        List<Node> operators = classUnderTest.loadOperators();
        assertNotNull(operators);
        assertEquals(2, operators.size());
        verifyAll();
    }
    
    public void testLoadPeers() throws Exception {
        List<Node> peers = classUnderTest.loadPeers();
        assertNotNull(peers);
        assertEquals(3, peers.size());
        verifyAll();
    }
    
    public void testStore() throws Exception {
        helper.cleanInsert(this, "noid");
        Node node = new Node("TestNode6", "http://test6.eu");
        
        assertEquals(6, classUnderTest.store(node));
        verifyDBTables("store", "dex_nodes", "id");
    }
    
    public void testUpdate() throws Exception {
        classUnderTest.update(1, 2);
        classUnderTest.update(2, 3);
        verifyDBTables("update", "dex_users_nodes", null);
    }
    
    public void testDelete() throws Exception {
        int delete = classUnderTest.delete(3);
        assertEquals(1, delete);
        delete = classUnderTest.delete(1);
        assertEquals(1, delete);
        verifyDBTables("delete", "dex_nodes", null);
        verifyDBTables("delete", "dex_users_nodes", null);
        verifyDBTables("delete", "dex_subscriptions_nodes", null);
    }
    
}
