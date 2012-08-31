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

import org.dbunit.dataset.ITable;
import org.dbunit.Assertion;

import junit.framework.TestCase;

import java.util.List;

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
        helper.cleanInsert(this);
        classUnderTest = new FileObjectManager();
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
        verifyDBTables(null, "dex_file_objects");
    }
    
    public void testLoadAll() throws Exception {
        List<FileObject> fileObjects = classUnderTest.load();
        assertNotNull(fileObjects);
        assertTrue(fileObjects.size() == 5);
        verifyAll();
    }
    
    public void testLoadById() throws Exception {
        FileObject fileObject = classUnderTest.load(3);
        assertEquals(3, fileObject.getId());
        assertEquals("FileObject3", fileObject.getFileObject());
        assertEquals("File object 3", fileObject.getDescription());
        verifyAll();
    }
    
    public void testLoadByName() throws Exception {
        FileObject fileObject = classUnderTest.load("FileObject4");
        assertEquals(4, fileObject.getId());
        assertEquals("FileObject4", fileObject.getFileObject());
        assertEquals("File object 4", fileObject.getDescription());
        verifyAll();
    }
    
    public void testStore() throws Exception {
        FileObject fileObject = new FileObject(6, "FileObject6", 
                "File object 6");
        assertEquals(1, classUnderTest.store(fileObject));
        fileObject = classUnderTest.load(6);
        assertEquals("FileObject6", fileObject.getFileObject());
        assertEquals("File object 6", fileObject.getDescription());
        verifyDBTables("store", "dex_file_objects");
    }
    
    public void testDelete() throws Exception {
        assertEquals(1, classUnderTest.delete(1));
        assertEquals(1, classUnderTest.delete(4));
        verifyDBTables("delete", "dex_file_objects");
    }
    
}
