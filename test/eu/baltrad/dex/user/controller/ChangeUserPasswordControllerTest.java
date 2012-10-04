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

package eu.baltrad.dex.user.controller;

import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.Password;
import eu.baltrad.dex.user.model.IUserManager;
import eu.baltrad.dex.user.util.ChangePasswordValidator;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.ui.ModelMap;

import static org.easymock.EasyMock.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.springframework.validation.*;

/**
 * Change user password controller test case.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class ChangeUserPasswordControllerTest {

    private ChangeUserPasswordController classUnderTest;
    private MockHttpServletRequest request;
    private IUserManager userManagerMock;
    
    @Before
    public void setUp() {
        classUnderTest = new ChangeUserPasswordController();
        request = new MockHttpServletRequest();
        classUnderTest.setValidator(new ChangePasswordValidator());
        userManagerMock = createMock(IUserManager.class);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        request = null;
        reset(userManagerMock);
    }
    
    @Test
    public void setupForm() {
        User user = new User("baltrad", "s3cret");
        request.getSession().setAttribute("session_user", user);
        ModelMap model = new ModelMap();
        String viewName = classUnderTest.setupForm(model, request);
        assertEquals("user_settings", viewName);
        assertTrue(model.containsAttribute("password"));
        Password passwd = (Password) model.get("password");
        assertEquals("baltrad", passwd.getUserName());
    }
    
    @Test
    public void processSubmit_ValidationFailure() {
        ModelMap model = new ModelMap();
        Password passwd = new Password("baltrad", "s3cret", "secret");
        model.addAttribute("password", passwd);
        BindingResult result = new BeanPropertyBindingResult(passwd, 
                                "password");
        String viewName = classUnderTest.processSubmit(model, passwd, result);
        assertEquals("user_settings", viewName);
        assertTrue(result.hasErrors());
        assertTrue(model.containsAttribute("password"));
    }
    
    @Test
    public void processSubmit_UpdateFailure() {
        ModelMap model = new ModelMap();
        Password passwd = new Password("baltrad", "passw0rd", "passw0rd");
        model.addAttribute("password", passwd);
        BindingResult result = new BeanPropertyBindingResult(passwd, 
                                "password");
        User user = new User("baltrad", "s3cret");
        user.setId(222);
        
        expect(userManagerMock.load("baltrad")).andReturn(user);
        expect(userManagerMock.updatePassword(222, 
                "bed128365216c019988915ed3add75fb")).andReturn(0);
        replay(userManagerMock);
        
        classUnderTest.setUserManager(userManagerMock);
        String viewName = classUnderTest.processSubmit(model, passwd, result);
        
        assertEquals("user_settings", viewName);
        assertTrue(model.containsAttribute("error"));
        assertEquals("Failed to change password for user baltrad", 
                (String) model.get("error"));
        assertTrue(model.containsAttribute("password"));
    }
    
    @Test
    public void processSubmit_OK() {
        ModelMap model = new ModelMap();
        Password passwd = new Password("baltrad", "passw0rd", "passw0rd");
        model.addAttribute("password", passwd);
        BindingResult result = new BeanPropertyBindingResult(passwd, 
                                "password");
        User user = new User("baltrad", "s3cret");
        user.setId(222);
        
        expect(userManagerMock.load("baltrad")).andReturn(user);
        expect(userManagerMock.updatePassword(222, 
                "bed128365216c019988915ed3add75fb")).andReturn(1);
        replay(userManagerMock);
        
        classUnderTest.setUserManager(userManagerMock);
        String viewName = classUnderTest.processSubmit(model, passwd, result);
        
        assertEquals("user_settings", viewName);
        assertTrue(model.containsAttribute("message"));
        assertEquals("Password successfully changed for user baltrad", 
                (String) model.get("message"));
        assertTrue(model.containsAttribute("password"));
    }
    
}
