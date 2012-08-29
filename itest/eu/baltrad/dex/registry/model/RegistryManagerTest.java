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

package eu.baltrad.dex.registry.model;

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
    
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    
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
        helper.cleanInsert(this);
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
    
    private void verifyDBTables(String suffix, String tableName) 
            throws Exception {
        ITable expected = helper.getXMLTable(this, suffix, tableName);
        ITable actual = helper.getDBTable(tableName);
        Assertion.assertEquals(expected, actual);
    }
    
    private void verifyAll() throws Exception {
        verifyDBTables(null, "dex_delivery_registry");
    }
    
    private boolean containsEntry(List<RegistryEntry> entries, 
            RegistryEntry entry) {
        boolean contains = false;
        for (RegistryEntry e : entries) {
            if (e.getId() == entry.getId() && 
                    e.getUserId() == entry.getUserId() &&
                    e.getUuid().equals(entry.getUuid()) &&
                    e.getUserName().equals(entry.getUserName()) &&
                    /*e.getTimeStamp().compareTo(entry.getTimeStamp()) == 0 &&*/
                    e.getDeliveryStatus().equals(entry.getDeliveryStatus())) { 
                contains = true;
                break;
            }
        }
        return contains;
    }
    
    
    public void testCount() throws Exception {
        assertEquals(11, classUnderTest.count());
        verifyAll();
    }
    
    public void testLoadAll() throws Exception {
        List<RegistryEntry> entries = classUnderTest.load();
        assertNotNull(entries);
        assertEquals(11, entries.size());
        verifyAll();
    }
    
    public void testLoadByUserAndUuid() throws Exception {
        RegistryEntry entryInvalidUserId = classUnderTest
                .load(3, "555555555555");
        assertNull(entryInvalidUserId);
        RegistryEntry entryInvalidUuid = classUnderTest
                .load(2, "555555555556");
        assertNull(entryInvalidUuid);
        RegistryEntry entryOK = classUnderTest.load(2, "555555555555");
        assertNotNull(entryOK);
        assertEquals(5, entryOK.getId());
        assertEquals("SUCCESS", entryOK.getDeliveryStatus());
        verifyAll();
    }
    
    public void testLoadWithOffsetAndLimit() throws Exception {
        List<RegistryEntry> entries = classUnderTest.load(4, 3);
        assertNotNull(entries);
        assertEquals(3, entries.size());
        RegistryEntry entry5 = new RegistryEntry(5, 2, "555555555555", "User2",
                format.parse("2012-08-24 11:00:04.0"), "SUCCESS");
        RegistryEntry entry6 = new RegistryEntry(6, 2, "666666666666", "User2",
                format.parse("2012-08-24 11:20:04.0"), "FAILURE");
        RegistryEntry entry7 = new RegistryEntry(7, 2, "777777777777", "User2",
                format.parse("2012-08-24 11:30:04.0"), "SUCCESS");
        RegistryEntry entry8 = new RegistryEntry(8, 2, "888888888888", "User2",
                format.parse("2012-08-24 11:40:04.0"), "SUCCESS");
        assertTrue(containsEntry(entries, entry5));
        assertTrue(containsEntry(entries, entry6));
        assertTrue(containsEntry(entries, entry7));
        assertFalse(containsEntry(entries, entry8));
    }
    
    public void testStore() throws Exception {
        RegistryEntry entry = new RegistryEntry(12, 2, "121212121212", "User2",
                format.parse("2012-08-24 12:20:04.0"), "SUCCESS");
        int store = classUnderTest.store(entry);
        assertEquals(1, store);
        verifyDBTables("store", "dex_delivery_registry");
    }
    
    public void testDeleteAll() {
        int delete = classUnderTest.delete();
        assertEquals(11, delete);
        List<RegistryEntry> entries = classUnderTest.load();
        assertEquals(0, entries.size());
    }
    
    public void testDelete() throws Exception {
        int delete = classUnderTest.delete(5);
        assertEquals(1, delete);
        delete = classUnderTest.delete(8);
        assertEquals(1, delete);
        verifyDBTables("delete", "dex_delivery_registry");
    }
    
    public void testSetRecordNumberTrimmer() throws Exception {
        List<RegistryEntry> entries = classUnderTest.load();
        assertEquals(11, entries.size());
        classUnderTest.setTrimmer(8);
        RegistryEntry entry = new RegistryEntry(12, 2, "121212121212", "User2",
                format.parse("2012-08-24 12:20:04.0"), "SUCCESS");
        int store = classUnderTest.store(entry);
        assertEquals(1, store);
        entries = classUnderTest.load();
        assertEquals(8, entries.size());
        classUnderTest.removeTrimmer("dex_trim_registry_by_number_tg");
    }
    
    public void testSetExpiryDateTrimmer() throws Exception {
        long start = format.parse("2012-08-24 11:20:04.0").getTime();
        long now = System.currentTimeMillis();
        long deltaMillis = now - start;
        
        int deltaDays = (int) Math.floor(deltaMillis / MILLIS_PER_DAY);
        int deltaHours = (int) Math.floor((deltaMillis - 
                (deltaDays * MILLIS_PER_DAY)) / MILLIS_PER_HOUR);
        int deltaMinutes = (int) Math.floor((deltaMillis - 
                (deltaDays * MILLIS_PER_DAY + deltaHours * MILLIS_PER_HOUR))
                    / MILLIS_PER_MINUTE);
        
        List<RegistryEntry> entries = classUnderTest.load();
        assertEquals(11, entries.size());
        classUnderTest.setTrimmer(deltaDays, deltaHours, deltaMinutes);
        RegistryEntry entry = new RegistryEntry(12, 2, "121212121212", "User2",
                format.parse("2012-08-27 12:20:04.0"), "SUCCESS");
        int store = classUnderTest.store(entry);
        assertEquals(1, store);
        entries = classUnderTest.load();
        assertEquals(6, entries.size());
        classUnderTest.removeTrimmer("dex_trim_registry_by_age_tg");
    }
    
}
