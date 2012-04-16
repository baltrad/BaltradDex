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

package eu.baltrad.dex.net.util;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * URL validator utility test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class UrlValidatorUtilTest {
    
    private UrlValidatorUtil classUnderTest;
    
    @Before
    public void setUp() {
        classUnderTest = new UrlValidatorUtil();
    }
    
    @Test
    public void validate() {
        assertTrue(classUnderTest.validate(
                "http://example.com:8084/BaltradDex/resource.htm"));
        assertTrue(classUnderTest.validate(
                "https://example.com:8084/BaltradDex/resource.htm"));
        assertFalse(classUnderTest.validate(
                "smb://example.com/resource.htm"));
        assertFalse(classUnderTest.validate(
                "http://example/BaltradDex/resource.htm"));
        assertFalse(classUnderTest.validate(
                "example.com:8084/BaltradDex/resource.htm"));
    }
}
