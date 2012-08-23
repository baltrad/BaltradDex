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

package eu.baltrad.dex.user.model;

import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.db.itest.DexDBITestHelper;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import junit.framework.TestCase;

import org.dbunit.dataset.ITable;
import org.dbunit.Assertion;

import java.util.List;

/**
 * User manager integration test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class UserManagerTest extends TestCase{
    
    private UserManager classUnderTest;
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this);
        classUnderTest = new UserManager();
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
        verifyDBTables(null, "dex_users");
        verifyDBTables(null, "dex_roles");
    }
    
    public void testLoadAll() throws Exception {
        List<User> users = classUnderTest.load();
        assertNotNull(users);
        assertTrue(users.size() == 4);
        verifyAll();
    }
    
    public void testLoadById() throws Exception {
        User user = classUnderTest.load(3);
        assertNotNull(user);
        assertEquals(3, user.getId());
        assertEquals("User3", user.getName());
        assertEquals("user", user.getRoleName());
        verifyAll();
    }
    
    public void testLoadByName() throws Exception {
        User user = classUnderTest.load("User2");
        assertNotNull(user);
        assertEquals(2, user.getId());
        assertEquals("User2", user.getName());
        assertEquals("operator", user.getRoleName());
        verifyAll();
    }
    
    public void testLoadRoles() throws Exception {
        List<Role> roles = classUnderTest.loadRoles();
        assertNotNull(roles);
        assertEquals(3, roles.size());
        verifyAll();
    }
    
    public void testStore() throws Exception {
        User user = new User(5, "User5", "operator", "s3cret", "org5", "unit5", 
                "locality5", "state5", "PL", "http://localhost");
        assertEquals(1, classUnderTest.store(user));
        user = classUnderTest.load(5);
        assertNotNull(user);
        assertEquals(5, user.getId());
        assertEquals("User5", user.getName());
        assertEquals("operator", user.getRoleName());
        verifyDBTables("store", "dex_users");
    }
    
    public void testUpdate() throws Exception {
        User user = new User(4, "User6", "user", "s3cret", "org6", "unit6", 
                "locality6", "state6", "PL", "http://localhost");
        assertEquals(1, classUnderTest.update(user));
        user = classUnderTest.load(4);
        assertNotNull(user);
        assertEquals(4, user.getId());
        assertEquals("User6", user.getName());
        assertEquals("user", user.getRoleName());
        verifyDBTables("update", "dex_users");
    }
    
    public void testUpdatePassword() throws Exception {
        assertEquals(1, classUnderTest.updatePassword(3, "newpassword"));
        verifyDBTables("update_password", "dex_users");
    }
    
    public void testDelete() throws Exception {
        classUnderTest.delete(2);
        verifyDBTables("delete", "dex_users");
    }
    
}
