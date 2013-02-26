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

package eu.baltrad.dex.net.manager.impl;

import eu.baltrad.dex.datasource.manager.impl.DataSourceManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.db.itest.DexDBITestHelper;
import eu.baltrad.dex.user.manager.impl.UserManager;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import org.dbunit.dataset.ITable;
import org.dbunit.Assertion;

import junit.framework.TestCase;

import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Subscription manager integration test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.1
 * @since 1.1.1
 */
public class SubscriptionManagerTest extends TestCase {

    private SubscriptionManager classUnderTest;
    
    private UserManager accountManager;
    private DataSourceManager dataSourceManager;
    
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    private DateFormat format;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this, null);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        classUnderTest = new SubscriptionManager();
        SimpleJdbcOperations jdbcTemplate = (SimpleJdbcOperations) context
                .getBean("jdbcTemplate");
        classUnderTest.setJdbcTemplate(jdbcTemplate);
        
        accountManager = new UserManager();
        accountManager.setJdbcTemplate(jdbcTemplate);
        classUnderTest.setAccountManager(accountManager);
        
        dataSourceManager = new DataSourceManager();
        dataSourceManager.setJdbcTemplate(jdbcTemplate);
        classUnderTest.setDataSourceManager(dataSourceManager);
    }
    
    @Override
    public void tearDown() throws Exception {
        context.close();
    }
    
    private void verifyDBTables(String suffix, String table,
            String ignoreColumn) throws Exception {
        ITable expected = helper.getXMLTable(this, suffix, table);
        ITable actual = helper.getDBTable(table);
        if (ignoreColumn != null) {
            Assertion.assertEqualsIgnoreCols(expected, actual, 
                new String[] {ignoreColumn});   
        } else {
            Assertion.assertEquals(expected, actual);
        }
    }
    
    private void verifyAll() throws Exception {
        verifyDBTables(null, "dex_roles", null);
        verifyDBTables(null, "dex_users", null);
        verifyDBTables(null, "dex_users_roles", null);
        verifyDBTables(null, "dex_nodes", null);
        verifyDBTables(null, "dex_users_nodes", null);
        verifyDBTables(null, "dex_data_sources", null);
        verifyDBTables(null, "dex_subscriptions", null);
        verifyDBTables(null, "dex_subscriptions_users", null);
        verifyDBTables(null, "dex_subscriptions_nodes", null);
        verifyDBTables(null, "dex_subscriptions_data_sources", null);
    }
    
    private boolean compare(Subscription s1, Subscription s2) {
        return s1.getId() == s2.getId() &&
               s1.getDate().equals(s2.getDate()) &&
               s1.getType().equals(s2.getType()) &&
               s1.getUser().equals(s2.getUser()) &&
               s1.getDataSource().equals(s2.getDataSource()) &&
               s1.isSyncronized() == s2.isSyncronized() && 
               s1.isActive() == s2.isActive();
    }
    
    public void testLoadById() throws Exception {
        Subscription expected = new Subscription();
        expected.setId(2);
        expected.setDate(format.parse("2012-04-24 14:10:00"));
        expected.setUser("User2");
        expected.setDataSource("DataSource2");
        expected.setType("local");
        expected.setActive(true);
        expected.setSyncronized(true);
        verifyAll();
        Subscription actual = classUnderTest.load(2);
        assertNotNull(actual);
        assertTrue(compare(expected, actual));
    }
    
    public void testLoadByType() throws Exception {
        List<Subscription> subs = classUnderTest.load(Subscription.LOCAL);
        verifyAll();
        assertNotNull(subs);
        assertEquals(2, subs.size());
    }
    
    public void testLoadByOperator() throws Exception {
        List<Subscription> subs = classUnderTest.load(Subscription.LOCAL, 
                "TestNode1");
        verifyAll();
        assertNotNull(subs);
        assertEquals(2, subs.size());
    }
    
    public void testLoadByUser() throws Exception {
        Subscription expected = new Subscription();
        expected.setId(3);
        expected.setDate(format.parse("2012-04-24 14:20:00"));
        expected.setUser("User2");
        expected.setDataSource("DataSource3");
        expected.setType("peer");
        expected.setActive(false);
        expected.setSyncronized(true);
        
        verifyAll();
        Subscription actual = classUnderTest.load(Subscription.PEER, 
                "User2", "DataSource3");
        assertNotNull(actual);
        assertTrue(compare(expected, actual));
        
    }
    
    public void testStore() throws Exception {
        helper.cleanInsert(this, "noid");
        Subscription s = new Subscription(
                format.parse("2012-04-24 14:30:00").getTime(),
                Subscription.PEER, "User1", "DataSource1", true, true);
        
        assertEquals(4, classUnderTest.store(s));
        verifyDBTables("store", "dex_subscriptions", "id");
    }
    
    public void testUpdate() throws Exception {
        Subscription expected = new Subscription();
        expected.setId(3);
        expected.setDate(format.parse("2012-04-24 15:20:00"));
        expected.setUser("User2");
        expected.setDataSource("DataSource3");
        expected.setType("local");
        expected.setActive(true);
        expected.setSyncronized(false); 
        
        classUnderTest.update(expected);
        
        verifyDBTables("update", "dex_subscriptions", null);
    }
    
    public void testDelete() throws Exception {
        classUnderTest.delete(1);
        classUnderTest.delete(3);
        
        verifyDBTables("delete", "dex_subscriptions", null);
        verifyDBTables("delete", "dex_subscriptions_users", null);
        verifyDBTables("delete", "dex_subscriptions_nodes", null);
        verifyDBTables("delete", "dex_subscriptions_data_sources", null);   
    }
    
}
