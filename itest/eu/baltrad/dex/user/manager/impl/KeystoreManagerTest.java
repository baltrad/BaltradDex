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

package eu.baltrad.dex.user.manager.impl;

import eu.baltrad.dex.keystore.manager.impl.KeystoreManager;
import eu.baltrad.dex.db.itest.DexDBITestHelper;
import eu.baltrad.dex.keystore.model.Key;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import junit.framework.TestCase;

import org.dbunit.dataset.ITable;
import org.dbunit.Assertion;

import java.util.List;

/**
 * Keystore manager integration test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 1.7.0
 */
public class KeystoreManagerTest extends TestCase {
    
    private KeystoreManager classUnderTest;
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this, null);
        classUnderTest = new KeystoreManager();
        SimpleJdbcOperations jdbcTemplate = (SimpleJdbcOperations) context
                .getBean("jdbcTemplate");
        classUnderTest.setJdbcTemplate(jdbcTemplate);
    }
    
    @Override
    public void tearDown() {
        context.close();
        classUnderTest = null;
    }
    
    private void verifyDBTable(String suffix, String ignoreColumn) 
            throws Exception {
        ITable expected = helper.getXMLTable(this, suffix, "dex_keys");
        ITable actual = helper.getDBTable("dex_keys");
        if (ignoreColumn != null) {
            Assertion.assertEqualsIgnoreCols(expected, actual, 
                new String[] {ignoreColumn});
        } else {
            Assertion.assertEquals(expected, actual);
        }
    }
    
    public void testLoadAll() throws Exception {
        List<Key> keys = classUnderTest.load();
        assertNotNull(keys);
        assertEquals(4, keys.size());
        verifyDBTable(null, null);
    }
    
    public void testLoadById() throws Exception {
        Key key = classUnderTest.load(2);
        assertNotNull(key);
        assertEquals(2, key.getId());
        assertEquals("test.baltrad.imgw.pl", key.getName());
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", key.getChecksum());
        assertEquals(false, key.isAuthorized());
        
        key = classUnderTest.load(4);
        assertEquals(4, key.getId());
        assertEquals("test.baltrad.se", key.getName());
        assertEquals("ut56789djk38duy386jdu73926jd9dm3", key.getChecksum());
        assertEquals(false, key.isAuthorized());
        verifyDBTable(null, null);        
    }
    public void testLoadByName() throws Exception {
        Key key = classUnderTest.load("test.baltrad.eu");
        assertNotNull(key);
        assertEquals(1, key.getId());
        assertEquals("test.baltrad.eu", key.getName());
        assertEquals("409201f7793e7a15b34c37cad27773b5", key.getChecksum());
        assertEquals(true, key.isAuthorized());
        
        key = classUnderTest.load("dev.baltrad.imgw.pl");
        assertEquals(3, key.getId());
        assertEquals("dev.baltrad.imgw.pl", key.getName());
        assertEquals("2345we4509huji788956gt4e64ji92gi", key.getChecksum());
        assertEquals(true, key.isAuthorized());
        verifyDBTable(null, null);
    }
    
    public void testStore() throws Exception {
        helper.cleanInsert(this, "noid");
        Key key = new Key("test.baltrad.lv", "df987sf7sa9875674sadf9708967876s",
                false);
        assertEquals(5, classUnderTest.store(key));
        verifyDBTable("store", "id");
    }
    
    public void testUpdate() throws Exception {
        Key key = new Key(2, "test.baltrad.imgw.pl", 
                "d41d8cd98f00b204e9800998ecf8427e", true);
        assertEquals(1, classUnderTest.update(key));
        verifyDBTable("update", null);
    }
    
    public void testDelete() throws Exception {
        assertEquals(1, classUnderTest.delete(3));
        verifyDBTable("delete", null);
    }
    
}
