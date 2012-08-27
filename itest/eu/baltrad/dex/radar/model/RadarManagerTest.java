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

package eu.baltrad.dex.radar.model;

import eu.baltrad.dex.db.itest.DexDBITestHelper;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import junit.framework.TestCase;

import org.dbunit.dataset.ITable;
import org.dbunit.Assertion;

import java.util.List;

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
        helper.cleanInsert(this);
        classUnderTest = new RadarManager();
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
        verifyDBTables(null, "dex_radars");
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
        assertEquals("Radar3", radar.getName());
        assertEquals("33333", radar.getWmoNumber());
        verifyAll();
    }
    
    public void testLoadByName() throws Exception {
        Radar radar = classUnderTest.load("Radar2");
        assertNotNull(radar);
        assertEquals(2, radar.getId());
        assertEquals("Radar2", radar.getName());
        assertEquals("22222", radar.getWmoNumber());
        verifyAll();
    }
    
    public void testStore() throws Exception {
        Radar radar = new Radar(5, "Radar5", "55555");
        assertEquals(1, classUnderTest.store(radar));
        radar = classUnderTest.load(5);
        assertNotNull(radar);
        assertEquals(5, radar.getId());
        assertEquals("Radar5", radar.getName());
        assertEquals("55555", radar.getWmoNumber());
        verifyDBTables("store", "dex_radars");
    }
    
    public void testUpdate() throws Exception {
        Radar radar = new Radar(3, "Radar345", "33345");
        assertEquals(1, classUnderTest.update(radar));
        radar = classUnderTest.load(3);
        assertNotNull(radar);
        assertEquals(3, radar.getId());
        assertEquals("Radar345", radar.getName());
        assertEquals("33345", radar.getWmoNumber());
        verifyDBTables("update", "dex_radars");
    }
    
    public void testDelete() throws Exception {
        classUnderTest.delete(2);
        verifyDBTables("delete", "dex_radars");
    }
    
}
