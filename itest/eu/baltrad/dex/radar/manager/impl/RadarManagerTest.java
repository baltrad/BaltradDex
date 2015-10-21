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

package eu.baltrad.dex.radar.manager.impl;

import java.util.List;

import junit.framework.TestCase;

import org.dbunit.Assertion;
import org.dbunit.dataset.ITable;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.JdbcOperations;

import eu.baltrad.dex.db.itest.DexDBITestHelper;
import eu.baltrad.dex.radar.model.Radar;

/**
 * Radar manager integration test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class RadarManagerTest extends TestCase {
    
    private RadarManager classUnderTest;
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this, null);
        classUnderTest = new RadarManager();
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
        verifyDBTables(null, "dex_radars", null);
    }
    
    public void testLoadAll() throws Exception {
        List<Radar> radars = classUnderTest.load();
        assertNotNull(radars);
        assertTrue(radars.size() == 4);
        verifyAll();
    }
    
    public void testLoadById() throws Exception {
        Radar radar = classUnderTest.load(3);
        assertNotNull(radar);
        assertEquals(3, radar.getId());
        assertEquals("XX", radar.getCountryCode());
        assertEquals("SOWR", radar.getCenterCode());
        assertEquals(220, radar.getCenterNumber());
        assertEquals("Somewhere", radar.getRadarPlace());
        assertEquals("XX33", radar.getRadarCode());
        assertEquals("33333", radar.getRadarWmo());
        verifyAll();
    }
    
    public void testLoadByPlace() throws Exception {
        Radar radar = classUnderTest.load("Nowhere");
        assertNotNull(radar);
        assertEquals(2, radar.getId());
        assertEquals("XX", radar.getCountryCode());
        assertEquals("SOWR", radar.getCenterCode());
        assertEquals(220, radar.getCenterNumber());
        assertEquals("Nowhere", radar.getRadarPlace());
        assertEquals("XX22", radar.getRadarCode());
        assertEquals("22222", radar.getRadarWmo());
        verifyAll();
    }
    
    public void testStore() throws Exception {
        helper.cleanInsert(this, "noid");
        Radar radar = new Radar("XX", "SOWR", 220, "Elsewhere", "XX55", 
                "55555");
        
        assertTrue(classUnderTest.store(radar) > 0);
        
        radar = new Radar("XX", "SOWR", 220, "Everywhere", "XX66", "66666");
        
        assertTrue(classUnderTest.store(radar) > 0);
        verifyDBTables("store", "dex_radars", "id");
    }
    
    public void testDelete() throws Exception {
        classUnderTest.delete(1);
        classUnderTest.delete(3);
        verifyDBTables("delete", "dex_radars", null);
    }
    
}
