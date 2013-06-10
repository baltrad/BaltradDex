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

package eu.baltrad.dex.user.controller;

import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.validator.PasswordValidator;
import eu.baltrad.dex.util.MessageResourceUtil;

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
public class UserSettingsControllerTest {

    private UserSettingsController classUnderTest;
    private MockHttpServletRequest request;
    private IUserManager userManagerMock;
    private MessageResourceUtil messages;
    private PasswordValidator validator;
    private User test;
    
    @Before
    public void setUp() {
        classUnderTest = new UserSettingsController();
        request = new MockHttpServletRequest();
        messages = new MessageResourceUtil();
        messages.setBasename("resources/messages");
        validator = new PasswordValidator();
        validator.setMessages(messages);
        classUnderTest.setValidator(validator);
        classUnderTest.setMessages(messages);
        userManagerMock = createMock(IUserManager.class);
        test = new User(1, "baltrad", "user", "password", "org", "unit", 
                "locality", "state", "XX", "http://test.baltrad.eu");
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        request = null;
        reset(userManagerMock);
    }
    
    @Test
    public void setupForm() {
        expect(userManagerMock.load(1)).andReturn(test);
        replay(userManagerMock);
        
        classUnderTest.setUserManager(userManagerMock);
        request.getSession().setAttribute("session_user", test);
        ModelMap model = new ModelMap();
        String viewName = classUnderTest.setupForm(model, request);
        
        assertEquals("user_settings", viewName);
        assertTrue(model.containsAttribute("user_account"));
        
        User user = (User) model.get("user_account");
        
        assertEquals("baltrad", user.getName());
    }
    
    @Test
    public void processSubmit_ValidationFailure() {
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(test, 
                                "user_account");
        test.setPassword("passw0rd");
        request.setParameter("repeat_password", "password");
        String viewName = classUnderTest.processSubmit(request, model, test, 
                result);
        assertEquals("user_settings", viewName);
        assertTrue(result.hasErrors());
    }
    
    @Test
    public void processSubmit_UpdateFailure() throws Exception {
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(test, 
                                "user_account");
        test.setPassword("passw0rd");
        request.setParameter("repeat_password", "passw0rd");
        
        expect(userManagerMock.updatePassword(test.getId(), 
                "bed128365216c019988915ed3add75fb")).andThrow(new Exception());
        replay(userManagerMock);
        
        classUnderTest.setUserManager(userManagerMock);
        String viewName = classUnderTest.processSubmit(request, model, test, 
                result);
        
        verify(userManagerMock);
        
        assertEquals("user_settings", viewName);
        assertTrue(model.containsAttribute("change_password_error"));
        assertEquals("Failed to change password for user baltrad", 
                (String) model.get("change_password_error"));
    }
    
    @Test
    public void processSubmit_OK() throws Exception {
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(test, 
                                "user_account");
        test.setPassword("passw0rd");
        request.setParameter("repeat_password", "passw0rd");
        
        expect(userManagerMock.updatePassword(test.getId(), 
                "bed128365216c019988915ed3add75fb")).andReturn(1);
        replay(userManagerMock);
        
        classUnderTest.setUserManager(userManagerMock);
        String viewName = classUnderTest.processSubmit(request, model, test, 
                result);
        
        verify(userManagerMock);
        
        assertEquals("user_settings", viewName);
        assertTrue(model.containsAttribute("change_password_success"));
        assertEquals("Password successfully changed for user baltrad", 
                (String) model.get("change_password_success"));
    }
    
}
