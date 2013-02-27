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
import eu.baltrad.dex.user.manager.impl.UserManager;
import eu.baltrad.dex.user.manager.impl.RoleManager;

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
    private UserManager accountManager;
    private RoleManager roleManager;
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
        accountManager = new UserManager();
        accountManager.setJdbcTemplate(jdbcTemplate);
        
        roleManager = new RoleManager();
        roleManager.setJdbcTemplate(jdbcTemplate);
        accountManager.setRoleManager(roleManager);
        
        classUnderTest.setAccountManager(accountManager);
        
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
    
    private boolean containsEntry(List<RegistryEntry> entries, 
            RegistryEntry entry) {
        boolean contains = false;
        for (RegistryEntry e : entries) {
            if (e.getId() == entry.getId() && 
                    e.getUuid().equals(entry.getUuid()) &&
                    e.getUser().equals(entry.getUser()) &&
                    e.getStatus().equals(entry.getStatus()) &&
                    e.getTimeStamp() == entry.getTimeStamp()) { 
                contains = true;
                break;
            }
        }
        return contains;
    }
    
    
    public void testCount() throws Exception {
        assertEquals(11, classUnderTest.count());
    }
    
    public void testLoadWithOffsetAndLimit() throws Exception {
        List<RegistryEntry> entries = classUnderTest.load(4, 3);
        assertNotNull(entries);
        assertEquals(3, entries.size());
        RegistryEntry entry5 = new RegistryEntry(
                5, format.parse("2012-08-24 11:00:00").getTime(), 
                "555555555555", "SUCCESS", "User2");
        RegistryEntry entry6 = new RegistryEntry(
                6, format.parse("2012-08-24 11:10:00").getTime(), 
                "666666666666", "SUCCESS", "User2");
        RegistryEntry entry7 = new RegistryEntry(
                7, format.parse("2012-08-24 11:20:00").getTime(), 
                "777777777777", "SUCCESS", "User2");
        RegistryEntry entry8 = new RegistryEntry(
                8, format.parse("2012-08-24 11:30:00").getTime(), 
                "888888888888", "FAILURE", "User1");    
       
        assertTrue(containsEntry(entries, entry5));
        assertTrue(containsEntry(entries, entry6));
        assertTrue(containsEntry(entries, entry7));
        assertFalse(containsEntry(entries, entry8));
        verifyAll();
    }
    
    public void testStore() throws Exception {
        helper.cleanInsert(this, "noid");
        RegistryEntry entry = new RegistryEntry(
                format.parse("2012-08-24 12:10:00").getTime(), "121212121212", 
                "SUCCESS", "User1");
        
        assertTrue(classUnderTest.store(entry) > 0);
        
        verifyDBTables("store", "dex_delivery_registry", "id");
    }
    
    public void testDeleteAll() throws Exception {
        classUnderTest.delete();
        assertEquals(0, classUnderTest.count());
    }
    
    public void testSetRecordNumberTrimmer() throws Exception {
        helper.cleanInsert(this, "noid");
        classUnderTest.setTrimmer(8);
        RegistryEntry entry = new RegistryEntry(
                format.parse("2012-08-24 12:10:00").getTime(), "121212121212", 
                "SUCCESS", "User1");
        classUnderTest.store(entry);
        
        verifyDBTables("trim_by_number", "dex_delivery_registry", "id");
        
        classUnderTest.removeTrimmer("dex_trim_registry_by_number_tg");
    }
    
    public void testSetExpiryDateTrimmer() throws Exception {
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
        
        RegistryEntry entry = new RegistryEntry(
                format.parse("2012-08-24 12:10:00").getTime(), "121212121212", 
                "SUCCESS", "User1");
        
        assertTrue(classUnderTest.store(entry) > 0);
        assertEquals(5, classUnderTest.count());
        verifyDBTables("trim_by_age", "dex_delivery_registry", "id");
        
        classUnderTest.removeTrimmer("dex_trim_registry_by_age_tg");
    }
    
}
