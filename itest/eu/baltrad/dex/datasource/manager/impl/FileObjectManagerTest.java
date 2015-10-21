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

package eu.baltrad.dex.datasource.manager.impl;

import java.util.List;

import junit.framework.TestCase;

import org.dbunit.Assertion;
import org.dbunit.dataset.ITable;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.JdbcOperations;

import eu.baltrad.dex.datasource.model.FileObject;
import eu.baltrad.dex.db.itest.DexDBITestHelper;

/**
 * File object manager integration test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class FileObjectManagerTest extends TestCase {

    private FileObjectManager classUnderTest;
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this, null);
        classUnderTest = new FileObjectManager();
        JdbcOperations jdbcTemplate = (JdbcOperations) context
                .getBean("jdbcTemplate");
        classUnderTest.setJdbcTemplate(jdbcTemplate);
    }
    
    @Override
    public void tearDown() throws Exception {
        context.close();
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
    
    public void verifyAll() throws Exception {
        verifyDBTables(null, "dex_file_objects", null);
    }
    
    public void testLoadAll() throws Exception {
        List<FileObject> fileObjects = classUnderTest.load();
        assertNotNull(fileObjects);
        assertTrue(fileObjects.size() == 5);
        verifyAll();
    }
    
    public void testLoadById() throws Exception {
        FileObject fileObject = classUnderTest.load(2);
        assertEquals(2, fileObject.getId());
        assertEquals("SCAN", fileObject.getName());
        assertEquals("Polar scan", fileObject.getDescription());
        verifyAll();
    }
    
    public void testLoadByName() throws Exception {
        FileObject fileObject = classUnderTest.load("COMP");
        assertEquals(5, fileObject.getId());
        assertEquals("COMP", fileObject.getName());
        assertEquals("Composite map", fileObject.getDescription());
        verifyAll();
    }
    
    public void testStore() throws Exception {
        helper.cleanInsert(this, "noid");
        FileObject fileObject = new FileObject("IMAGE", 
                "2-D cartesian image");
        
        assertEquals(6, classUnderTest.store(fileObject));
        verifyDBTables("store", "dex_file_objects", "id");
    }
    
    public void testDelete() throws Exception {
        assertEquals(1, classUnderTest.delete(1));
        assertEquals(1, classUnderTest.delete(4));
        verifyDBTables("delete", "dex_file_objects", null);
    }
    
}
