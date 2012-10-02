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

package eu.baltrad.dex.log.model;

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
 * Log manager integration test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class LogManagerTest extends TestCase {
    
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    
    private final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
    private final static long MILLIS_PER_HOUR = 60 * 60 * 1000;
    private final static long MILLIS_PER_MINUTE = 60 * 1000;
    
    private LogManager classUnderTest;
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    private DateFormat format;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this);
        classUnderTest = new LogManager();
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
        verifyDBTables(null, "dex_messages");
    }
    
    private boolean containsEntry(List<LogEntry> entries, LogEntry entry) {
        boolean contains = false;
        for (LogEntry e : entries) {
            if (e.getId() == entry.getId() &&
                    /*e.getTimeStamp().compareTo(entry.getTimeStamp()) == 0 &&*/
                    e.getSystem().equals(entry.getSystem()) &&
                    e.getType().equals(entry.getType()) &&
                    e.getMessage().equals(entry.getMessage())) { 
                contains = true;
                break;
            }
        }
        return contains;
    }
    
    
    public void testCount() throws Exception {
        assertEquals(12, classUnderTest.count(
                classUnderTest.createQuery(new LogParameter(), true)));
        verifyAll();
    }
    
    public void testLoadAll() throws Exception {
        List<LogEntry> entries = classUnderTest.load();
        assertNotNull(entries);
        assertEquals(12, entries.size());
        verifyAll();
    }
    
    public void testLoadWithLimit() throws Exception {
        List<LogEntry> entries = classUnderTest.load(4);
        assertNotNull(entries);
        assertEquals(4, entries.size());
        verifyAll();
    }
    
    public void testLoadWithOffsetAndLimit() throws Exception {
        List<LogEntry> entries = classUnderTest.load(5, 3);
        assertNotNull(entries);
        
        LogEntry entry5 = new LogEntry(5, format.parse("2012-08-24 11:00:04.0"),
                "BEAST", "WARN", "BEAST message");
        LogEntry entry6 = new LogEntry(6, format.parse("2012-08-24 11:10:04.0"),
                "DEX", "WARN", "DEX message");
        LogEntry entry7 = new LogEntry(7, format.parse("2012-08-24 11:20:04.0"),
                "BEAST", "INFO", "BEAST message");
        LogEntry entry8 = new LogEntry(8, format.parse("2012-08-24 11:30:04.0"),
                "DEX", "INFO", "DEX message");
        
        assertEquals(3, entries.size());
        assertTrue(containsEntry(entries, entry5));
        assertTrue(containsEntry(entries, entry6));
        assertTrue(containsEntry(entries, entry7));
        assertFalse(containsEntry(entries, entry8));
    }
    
    public void testLoadByLogger() {
        LogParameter param = new LogParameter();
        param.setLogger("DEX");
        List<LogEntry> entries = classUnderTest
                .load(classUnderTest.createQuery(param, false), 0, 20);
        assertEquals(7, entries.size());
        param.setLogger("BEAST");
        entries = classUnderTest
                .load(classUnderTest.createQuery(param, false), 0, 20);
        assertEquals(3, entries.size());
        param.setLogger("PGF");
        entries = classUnderTest
                .load(classUnderTest.createQuery(param, false), 0, 20);
        assertEquals(2, entries.size());
    }
    
    public void testLoadByFlag() {
        LogParameter param = new LogParameter();
        param.setFlag("INFO");
        List<LogEntry> entries = classUnderTest
                .load(classUnderTest.createQuery(param, false), 0, 20);
        assertEquals(8, entries.size());
        param.setFlag("WARN");
        entries = classUnderTest
                .load(classUnderTest.createQuery(param, false), 0, 20);
        assertEquals(2, entries.size());
        param.setFlag("ERROR");
        entries = classUnderTest
                .load(classUnderTest.createQuery(param, false), 0, 20);
        assertEquals(2, entries.size());
    }
    
    public void testLoadByDate() {
        LogParameter param = new LogParameter();
        param.setStartDate("2012/08/24");
        param.setStartHour("11");
        param.setStartMinutes("0");
        param.setStartSeconds("4");
        param.setEndDate("2012/08/24");
        param.setEndHour("11");
        param.setEndMinutes("30");
        param.setEndSeconds("4");
        List<LogEntry> entries = classUnderTest
                .load(classUnderTest.createQuery(param, false), 0, 20);
        assertEquals(4, entries.size());
    }
    
    public void testLoadByPhrase() {
        LogParameter param = new LogParameter();
        param.setPhrase("DEX");
        List<LogEntry> entries = classUnderTest
                .load(classUnderTest.createQuery(param, false), 0, 20);
        assertEquals(7, entries.size());
        param.setPhrase("BEAST");
        entries = classUnderTest
                .load(classUnderTest.createQuery(param, false), 0, 20);
        assertEquals(3, entries.size());
        param.setPhrase("PGF");
        entries = classUnderTest
                .load(classUnderTest.createQuery(param, false), 0, 20);
        assertEquals(2, entries.size());
    }
    
    public void testLoadWithSeveralParams() throws Exception {
        LogParameter param = new LogParameter();
        param.setLogger("DEX");
        param.setFlag("INFO");
        param.setStartDate("2012/08/24");
        param.setStartHour("10");
        param.setStartMinutes("40");
        param.setStartSeconds("4");
        param.setEndDate("2012/08/24");
        param.setEndHour("11");
        param.setEndMinutes("30");
        param.setEndSeconds("4");
        param.setPhrase("DEX");
        List<LogEntry> entries = classUnderTest
                .load(classUnderTest.createQuery(param, false), 0, 20);
        assertEquals(2, entries.size());
        LogEntry entry3 = new LogEntry(3, format.parse("2012-08-24 10:40:04.0"),
                "DEX", "INFO", "DEX message");
        LogEntry entry8 = new LogEntry(8, format.parse("2012-08-24 11:30:04.0"),
                "DEX", "INFO", "DEX message");
        assertTrue(containsEntry(entries, entry3));
        assertTrue(containsEntry(entries, entry8));
    }
    
    public void testStore() throws Exception {
        LogEntry entry = new LogEntry(13, format.parse("2012-08-24 12:20:04.0"),
                "DEX", "ERROR", "DEX message");
        int store = classUnderTest.store(entry);
        assertEquals(1, store);
        verifyDBTables("store", "dex_messages");
    }
    
    public void testDeleteAll() {
        int delete = classUnderTest.delete();
        assertEquals(12, delete);
        List<LogEntry> entries = classUnderTest.load();
        assertEquals(0, entries.size());
    }
    
    public void testSetRecordNumberTrimmer() throws Exception {
        List<LogEntry> entries = classUnderTest.load();
        assertEquals(12, entries.size());
        classUnderTest.setTrimmer(7);
        LogEntry entry = new LogEntry(13, format.parse("2012-08-24 12:20:04.0"),
                "DEX", "ERROR", "Message 13");
        int store = classUnderTest.store(entry);
        assertEquals(1, store);
        entries = classUnderTest.load();
        assertEquals(7, entries.size());
        classUnderTest.removeTrimmer("dex_trim_messages_by_number_tg");
    }
    
    public void testSetExpiryDateTrimmer() throws Exception {
        long start = format.parse("2012-08-24 11:30:04.0").getTime();
        long now = System.currentTimeMillis();
        long deltaMillis = now - start;
        
        int deltaDays = (int) Math.floor(deltaMillis / MILLIS_PER_DAY);
        int deltaHours = (int) Math.floor((deltaMillis - 
                (deltaDays * MILLIS_PER_DAY)) / MILLIS_PER_HOUR);
        int deltaMinutes = (int) Math.floor((deltaMillis - 
                (deltaDays * MILLIS_PER_DAY + deltaHours * MILLIS_PER_HOUR))
                    / MILLIS_PER_MINUTE);
        List<LogEntry> entries = classUnderTest.load();
        assertEquals(12, entries.size());
        classUnderTest.setTrimmer(deltaDays, deltaHours, deltaMinutes);
        LogEntry entry = new LogEntry(13, format.parse("2012-08-24 12:20:04.0"),
                "DEX", "ERROR", "Message 13");
        int store = classUnderTest.store(entry);
        assertEquals(1, store);
        entries = classUnderTest.load();
        assertEquals(5, entries.size());
        classUnderTest.removeTrimmer("dex_trim_messages_by_age_tg");
    }
    
}
