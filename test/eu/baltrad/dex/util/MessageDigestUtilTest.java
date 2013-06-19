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

package eu.baltrad.dex.util;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;

/**
 * Message digest utility test.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0.0
 * @since 1.0.0
 */
public class MessageDigestUtilTest {
    
    @Test
    public void createMessageHash() {
        String hash = MessageDigestUtil.createHash("MD5", "Message body");
        
        assertEquals(32, hash.length());
    }
    
    @Test
    public void getBytes() throws Exception {
        File f = new File("keystore/localhost.pub");
        byte[] bytes = MessageDigestUtil.getBytes(f);
        
        assertTrue(bytes.length > 0);
    }
    
}
