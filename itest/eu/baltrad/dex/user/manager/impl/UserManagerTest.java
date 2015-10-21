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

import java.util.List;

import junit.framework.TestCase;

import org.dbunit.Assertion;
import org.dbunit.dataset.ITable;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.JdbcOperations;

import eu.baltrad.dex.db.itest.DexDBITestHelper;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;

/**
 * User manager integration test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class UserManagerTest extends TestCase{
    
    private UserManager classUnderTest;
    private RoleManager roleManager;
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this, null);
        classUnderTest = new UserManager();
        JdbcOperations jdbcTemplate = (JdbcOperations) context
                .getBean("jdbcTemplate");
        classUnderTest.setJdbcTemplate(jdbcTemplate);
        roleManager = new RoleManager();
        roleManager.setJdbcTemplate(jdbcTemplate);
        classUnderTest.setRoleManager(roleManager);
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
        verifyDBTables(null, "dex_users", null);
        verifyDBTables(null, "dex_roles", null);
        verifyDBTables(null, "dex_users_roles", null);
        verifyDBTables(null, "dex_subscriptions", null);
        verifyDBTables(null, "dex_subscriptions_users", null);
    }
    
    public void testLoadAll() throws Exception {
        List<User> users = classUnderTest.load();
        assertNotNull(users);
        assertEquals(5, users.size());
        verifyAll();
    }
    
    public void testLoadById() throws Exception {
        User user = classUnderTest.load(3);
        assertNotNull(user);
        assertEquals(3, user.getId());
        assertEquals("User3", user.getName());
        assertEquals("passw0rd", user.getPassword());
        assertEquals("org3", user.getOrgName());
        assertEquals("unit3", user.getOrgUnit());
        assertEquals("locality3", user.getLocality());
        assertEquals("state3", user.getState());
        assertEquals("ZZ", user.getCountryCode());
        assertEquals(Role.USER, user.getRole());
        assertEquals("http://test3.baltrad.eu", user.getNodeAddress());
        verifyAll();
    }
    
    public void testLoadByName() throws Exception {
        User user = classUnderTest.load("User2");
        assertNotNull(user);
        assertEquals(2, user.getId());
        assertEquals("User2", user.getName());
        assertEquals("passw0rd", user.getPassword());
        assertEquals("org2", user.getOrgName());
        assertEquals("unit2", user.getOrgUnit());
        assertEquals("locality2", user.getLocality());
        assertEquals("state2", user.getState());
        assertEquals("YY", user.getCountryCode());
        assertEquals(Role.OPERATOR, user.getRole());
        assertEquals("http://test2.baltrad.eu", user.getNodeAddress());
        verifyAll();
    }
    
    public void testLoadPeers() throws Exception {
        List<String> users = classUnderTest.loadPeerNames();
        assertNotNull(users);
        assertTrue(users.size() == 2);
        assertTrue(users.contains("User4"));
        assertTrue(users.contains("User5"));
        verifyAll();
    } 
    
    public void testLoadUsers() throws Exception {
        List<User> users = classUnderTest.loadUsers();
        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.contains(new User("User4", "peer", "passw0rd", "org4",
            "unit4", "locality4", "state4", "XY", "http://test4.baltrad.eu")));
        assertTrue(users.contains(new User("User5", "peer", "passw0rd", "org5",
            "unit5", "locality5", "state5", "XZ", "http://test5.baltrad.eu")));
        verifyAll();
    }
    
    public void testLoadOperators() throws Exception {
        List<User> users = classUnderTest.loadOperators();
        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.contains(new User("User1", "admin", "passw0rd", "org1",
            "unit1", "locality1", "state1", "XX", "http://test1.baltrad.eu")));
        assertTrue(users.contains(new User("User2", "operator", "passw0rd", 
            "org2", "unit2", "locality2", "state2", "YY", 
                "http://test2.baltrad.eu")));
        verifyAll();
    }
        
    public void testStore() throws Exception {
        helper.cleanInsert(this, "noid");
        User user = new User("User6", "operator", "passw0rd", "org6", "unit6", 
                "locality6", "state6", "VV", "http://test6.baltrad.eu");
        assertEquals(6, classUnderTest.store(user));
        verifyDBTables("store", "dex_users", "id");
    }
    
    public void testUpdate() throws Exception {
        User user = new User(3, "User3", "user", "passw0rd", "org4", "unit4", 
                "locality4", "state4", "VV", "http://test4.baltrad.eu");
        
        classUnderTest.update(user);
        verifyDBTables("update", "dex_users", null);
    }
    
    public void testUpdatePassword() throws Exception {
        assertEquals(1, classUnderTest.updatePassword(2, "n3wpassw0rd"));
        verifyDBTables("update_password", "dex_users", null);
    }
    
    public void testDelete() throws Exception {
        classUnderTest.delete(3);
        verifyDBTables("delete", "dex_users", null);
    }
    
}
