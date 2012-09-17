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

import eu.baltrad.dex.net.model.SubscriptionManager;
import eu.baltrad.dex.net.model.Subscription;
import eu.baltrad.dex.db.itest.DexDBITestHelper;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import org.dbunit.dataset.ITable;
import org.dbunit.Assertion;

import junit.framework.TestCase;

import java.sql.Timestamp;
import java.util.Date;
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
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this);
        classUnderTest = new SubscriptionManager();
        SimpleJdbcOperations jdbcTemplate = (SimpleJdbcOperations) context
                .getBean("jdbcTemplate");
        classUnderTest.setJdbcTemplate(jdbcTemplate);
    }
    
    @Override
    public void tearDown() throws Exception {
        context.close();
    }
    
    private void verifyDBTables(String suffix) throws Exception {
        ITable expected = helper.getXMLTable(this, suffix, "dex_subscriptions");
        ITable actual = helper.getDBTable("dex_subscriptions");
        Assertion.assertEquals(expected, actual);
    }
    
    public void testLoadById() throws Exception {
        Subscription expected = new Subscription();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
        Date date = format.parse("2012-04-24 13:00:00.00");
        expected.setTimeStamp(new Timestamp(date.getTime()));
        expected.setUserName("User2");
        expected.setDataSourceName("DataSource2");
        expected.setOperatorName("Operator2");
        expected.setType("upload");
        expected.setActive(false);
        expected.setSynkronized(true);
        expected.setNodeAddress("http://baltrad.org");
        
        verifyDBTables(null);
        Subscription actual = classUnderTest.load(2);
        assertNotNull(actual);
        assertTrue(classUnderTest.compare(expected, actual));
    }
    
    public void testLoadByType() throws Exception {
        List<Subscription> subs = classUnderTest.load("download");
        verifyDBTables(null);
        assertNotNull(subs);
        assertEquals(3, subs.size());
    }
    
    public void testLoadByNameAndType() throws Exception {
        Subscription expected = new Subscription();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
        Date date = format.parse("2012-04-24 14:00:00.00");
        expected.setTimeStamp(new Timestamp(date.getTime()));
        expected.setUserName("User3");
        expected.setDataSourceName("DataSource3");
        expected.setOperatorName("Operator3");
        expected.setType("download");
        expected.setActive(true);
        expected.setSynkronized(false);
        expected.setNodeAddress("http://baltrad.com");
        
        verifyDBTables(null);
        Subscription actual = classUnderTest.load("User3", "DataSource3", 
                "download");
        assertNotNull(actual);
        assertTrue(classUnderTest.compare(expected, actual));
    }
    
    public void testLoadByOperatorAndType() throws Exception {
        List<Subscription> subs = classUnderTest.load("Operator1", 
                Subscription.SUBSCRIPTION_DOWNLOAD);
        verifyDBTables(null);
        assertNotNull(subs);
        assertEquals(2, subs.size());
    }
    
    public void testStore() throws Exception {
        Subscription s = new Subscription();
        s.setId(5);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
        Date date = format.parse("2012-04-25 15:00:00.00");
        s.setTimeStamp(new Timestamp(date.getTime()));
        s.setUserName("User5");
        s.setDataSourceName("DataSource5");
        s.setOperatorName("Operator5");
        s.setType("upload");
        s.setActive(true);
        s.setSynkronized(false);
        s.setNodeAddress("http://test.baltrad.eu");
        
        assertEquals(1, classUnderTest.store(s));
        verifyDBTables("store");
    }
    
    public void testUpdate() throws Exception {
        Subscription s = new Subscription();
        s.setId(2);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
        Date date = format.parse("2012-04-24 13:00:00.00");
        s.setTimeStamp(new Timestamp(date.getTime()));
        s.setUserName("User2");
        s.setDataSourceName("DataSource2");
        s.setOperatorName("Operator2");
        s.setType("download");
        s.setActive(true);
        s.setSynkronized(false);
        s.setNodeAddress("http://baltrad.org");
        
        assertEquals(1, classUnderTest.update(s));
        verifyDBTables("update");
    }
    
    public void testDelete() throws Exception {
        Subscription s = new Subscription();
        s.setId(2);
        
        assertEquals(1, classUnderTest.delete(s));
        verifyDBTables("delete");   
    }
    
    public void testLoadUsers() throws Exception {
        List<Subscription> subscriptions = classUnderTest.loadUsers();
        verifyDBTables(null);
        assertEquals(1, subscriptions.size());
    }
    
    public void testLoadOperators() throws Exception {
        List<Subscription> subscriptions = classUnderTest.loadOperators();
        verifyDBTables(null);
        assertEquals(2, subscriptions.size());
    }
    
}
