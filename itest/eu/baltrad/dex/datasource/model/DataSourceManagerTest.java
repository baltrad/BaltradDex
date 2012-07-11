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

package eu.baltrad.dex.datasource.model;

import eu.baltrad.dex.db.itest.DexDBITestHelper;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.Assertion;

import junit.framework.TestCase;

import java.util.List;
import java.util.Date;

import java.text.DateFormat; 
import java.text.SimpleDateFormat;

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
    private FlatXmlDataSet dataSet;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this);
        classUnderTest = new DataSourceManager();
        SimpleJdbcOperations jdbcTemplate = (SimpleJdbcOperations) context
                .getBean("jdbcTemplate");
        classUnderTest.setJdbcTemplate(jdbcTemplate);
    }
    
    @Override
    public void tearDown() throws Exception {
        context.close();
    }
    
    private void verifyDBTables(String suffix, String tableName) 
            throws Exception {
        ITable expected = helper.getXMLTable(this, suffix, tableName);
        ITable actual = helper.getDBTable(tableName);
        Assertion.assertEquals(expected, actual);
    }
    
    public void verifyAll() throws Exception {
        verifyDBTables(null, "dex_data_sources");
        verifyDBTables(null, "dex_users");
        verifyDBTables(null, "dex_roles");
        verifyDBTables(null, "dex_data_source_users");
    }
    
    public void testLoadByUserId() throws Exception {
        List<DataSource> userDataSources = classUnderTest.load(2);
        verifyAll();
        assertNotNull(userDataSources);
        assertEquals(2, userDataSources.size());
    } 
    
    
    
    /*public void testLoadById() throws Exception {
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
        assertEquals(2, subs.size());
    }
    
    public void testStore() throws Exception {
        Subscription s = new Subscription();
        s.setId(4);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
        Date date = format.parse("2012-04-25 15:00:00.00");
        s.setTimeStamp(new Timestamp(date.getTime()));
        s.setUserName("User4");
        s.setDataSourceName("DataSource4");
        s.setOperatorName("Operator4");
        s.setType("upload");
        s.setActive(true);
        s.setSynkronized(false);
        s.setNodeAddress("http://test.baltrad.eu");
        
        classUnderTest.store(s);
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
        
        classUnderTest.update(s);
        verifyDBTables("update");
    }
    
    public void testDelete() throws Exception {
        Subscription s = new Subscription();
        s.setId(2);
        
        classUnderTest.delete(s);
        verifyDBTables("delete");   
    }*/
}
