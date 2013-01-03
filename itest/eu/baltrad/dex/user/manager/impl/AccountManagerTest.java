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

package eu.baltrad.dex.user.manager.impl;

import eu.baltrad.dex.user.manager.impl.AccountManager;
import eu.baltrad.dex.user.model.Account;
import eu.baltrad.dex.db.itest.DexDBITestHelper;
import eu.baltrad.dex.net.manager.impl.NodeManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.util.MessageDigestUtil;

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
public class AccountManagerTest extends TestCase{
    
    private AccountManager classUnderTest;
    private NodeManager nodeManager;
    private RoleManager roleManager;
    private AbstractApplicationContext context;
    private DexDBITestHelper helper;
    
    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        helper.cleanInsert(this, null);
        classUnderTest = new AccountManager();
        SimpleJdbcOperations jdbcTemplate = (SimpleJdbcOperations) context
                .getBean("jdbcTemplate");
        classUnderTest.setJdbcTemplate(jdbcTemplate);
        nodeManager = new NodeManager();
        nodeManager.setJdbcTemplate(jdbcTemplate);
        classUnderTest.setNodeManager(nodeManager);
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
        verifyDBTables(null, "dex_nodes", null);
        verifyDBTables(null, "dex_users_roles", null);
        verifyDBTables(null, "dex_users_nodes", null);
    }
    
    public void testLoadAll() throws Exception {
        List<Account> users = classUnderTest.load();
        assertNotNull(users);
        assertTrue(users.size() == 3);
        verifyAll();
    }
    
    public void testLoadById() throws Exception {
        Account account = classUnderTest.load(3);
        assertNotNull(account);
        assertEquals(3, account.getId());
        assertEquals("User3", account.getName());
        assertEquals("passw0rd", account.getPassword());
        assertEquals("org3", account.getOrgName());
        assertEquals("unit3", account.getOrgUnit());
        assertEquals("locality3", account.getLocality());
        assertEquals("state3", account.getState());
        assertEquals("ZZ", account.getCountryCode());
        assertEquals(Role.USER, account.getRoleName());
        assertEquals("http://test3.eu", account.getNodeAddress());
        verifyAll();
    }
    
    public void testLoadByName() throws Exception {
        Account account = classUnderTest.load("User2");
        assertNotNull(account);
        assertEquals(2, account.getId());
        assertEquals("User2", account.getName());
        assertEquals("passw0rd", account.getPassword());
        assertEquals("org2", account.getOrgName());
        assertEquals("unit2", account.getOrgUnit());
        assertEquals("locality2", account.getLocality());
        assertEquals("state2", account.getState());
        assertEquals("YY", account.getCountryCode());
        assertEquals(Role.OPERATOR, account.getRoleName());
        assertEquals("http://test2.eu", account.getNodeAddress());
        verifyAll();
    }
    
    public void testStore() throws Exception {
        helper.cleanInsert(this, "noid");
        Account account = new Account("User4", "http://test4.eu", "org4", 
                "unit4", "locality4", "state4", "VV");
        account.setPassword("passw0rd");
        account.setRoleName(Role.OPERATOR);
        
        assertEquals(4, classUnderTest.store(account));
        verifyDBTables("store", "dex_users", "id");
    }
    
    public void testUpdate() throws Exception {
        Account account = new Account(3, "User4", "passw0rd", "org4", 
                "unit4", "locality4", "state4", "VV", Role.USER,
                "http://test4.eu");
        
        classUnderTest.update(account);
        
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
