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

package eu.baltrad.dex.registry.manager.impl;

import eu.baltrad.dex.registry.model.impl.RegistryEntry;
import eu.baltrad.dex.db.itest.DexDBITestHelper;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import junit.framework.TestCase;

import org.dbunit.dataset.ITable;
import org.dbunit.Assertion;

import java.util.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Data delivery registry manager integration test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class RegistryManagerTest extends TestCase {
    
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    private final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
    private final static long MILLIS_PER_HOUR = 60 * 60 * 1000;
    private final static long MILLIS_PER_MINUTE = 60 * 1000;
    
    private RegistryManager classUnderTest;
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    private DateFormat format;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this, null);
        classUnderTest = new RegistryManager();
        SimpleJdbcOperations jdbcTemplate = (SimpleJdbcOperations) context
                .getBean("jdbcTemplate");
        classUnderTest.setJdbcTemplate(jdbcTemplate);
        
        format = new SimpleDateFormat(DATE_FORMAT);
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
    
    private void verifyAll() throws Exception {
        verifyDBTables(null, "dex_delivery_registry", null);
        verifyDBTables(null, "dex_delivery_registry_users", null);
    }
    
    public void testCount() {
        assertEquals(5, classUnderTest.count(RegistryEntry.DOWNLOAD));
        assertEquals(6, classUnderTest.count(RegistryEntry.UPLOAD));
    }
    
    public void testLoad() throws Exception {
        List<RegistryEntry> entries = 
                classUnderTest.load(RegistryEntry.DOWNLOAD, 1, 3);
        
        assertNotNull(entries);
        assertEquals(3, entries.size());
        
        RegistryEntry entry7 = new RegistryEntry(
                7, format.parse("2012-08-24 11:20:00").getTime(), 
                "download", "777777777777", "User2", true);
        RegistryEntry entry8 = new RegistryEntry(
                8, format.parse("2012-08-24 11:30:00").getTime(), 
                "download", "888888888888", "User1", false);
        RegistryEntry entry9 = new RegistryEntry(
                9, format.parse("2012-08-24 11:40:00").getTime(), 
                "download", "999999999999", "User2", false);
        RegistryEntry entry10 = new RegistryEntry(
                10, format.parse("2012-08-24 11:50:00").getTime(), 
                "download", "101010101010", "User1", true);
       
        assertFalse(entries.contains(entry7));
        assertTrue(entries.contains(entry8));
        assertTrue(entries.contains(entry9));
        assertTrue(entries.contains(entry10));
        
        entries = classUnderTest.load(RegistryEntry.UPLOAD, 1, 2);
        
        assertNotNull(entries);
        assertEquals(2, entries.size());
        
        RegistryEntry entry4 = new RegistryEntry(
                4, format.parse("2012-08-24 10:50:00").getTime(), 
                "upload", "444444444444", "User1", false);
        RegistryEntry entry5 = new RegistryEntry(
                5, format.parse("2012-08-24 11:00:00").getTime(), 
                "upload", "555555555555", "User2", true);
        RegistryEntry entry6 = new RegistryEntry(
                6, format.parse("2012-08-24 11:10:00").getTime(), 
                "upload", "666666666666", "User2", false);
        
        assertTrue(entries.contains(entry4));
        assertTrue(entries.contains(entry5));
        assertFalse(entries.contains(entry6));
        
        verifyAll();
    }
    
    public void testStore() throws Exception {
        helper.cleanInsert(this, "noid");
        RegistryEntry entry = new RegistryEntry(1, 1, 
                format.parse("2012-08-24 12:10:00").getTime(), "upload",
                "121212121212", "User1", true);
        
        assertTrue(classUnderTest.store(entry) > 0);
        
        verifyDBTables("store", "dex_delivery_registry", "id");
    }
    
    public void testDeleteAll() throws Exception {
        classUnderTest.delete();
        assertEquals(0, classUnderTest.count(RegistryEntry.DOWNLOAD));
        assertEquals(0, classUnderTest.count(RegistryEntry.UPLOAD));
    }
    
    public void testSetRecordNumberTrimmer() throws Exception {
        helper.cleanInsert(this, "noid");
        classUnderTest.setTrimmer(8);
        RegistryEntry entry = new RegistryEntry(1, 1,
                format.parse("2012-08-24 12:10:00").getTime(), "download", 
                "121212121212", "User1", true);
        classUnderTest.store(entry);
        
        verifyDBTables("trim_by_number", "dex_delivery_registry", "id");
        
        classUnderTest.removeTrimmer("dex_trim_registry_by_number_tg");
    }
    
    /*public void testSetExpiryDateTrimmer() throws Exception {
        long start = format.parse("2012-08-24 11:20:00").getTime();
        long now = System.currentTimeMillis();
        long deltaMillis = now - start;
        
        int deltaDays = (int) Math.floor(deltaMillis / MILLIS_PER_DAY);
        int deltaHours = (int) Math.floor((deltaMillis - 
                (deltaDays * MILLIS_PER_DAY)) / MILLIS_PER_HOUR);
        int deltaMinutes = (int) Math.floor((deltaMillis - 
                (deltaDays * MILLIS_PER_DAY + deltaHours * MILLIS_PER_HOUR))
                    / MILLIS_PER_MINUTE);
        
        classUnderTest.setTrimmer(deltaDays, deltaHours, deltaMinutes);
        helper.cleanInsert(this, "noid");
        
        RegistryEntry entry = new RegistryEntry(1, 1,
                format.parse("2012-08-24 12:10:00").getTime(), "upload",
                "121212121212", "User1", true);
        
        assertTrue(classUnderTest.store(entry) > 0);
        assertEquals(5, classUnderTest.count(RegistryEntry.DOWNLOAD) + 
                classUnderTest.count(RegistryEntry.UPLOAD));
        verifyDBTables("trim_by_age", "dex_delivery_registry", "id");
        
        classUnderTest.removeTrimmer("dex_trim_registry_by_age_tg");
    }*/
}
