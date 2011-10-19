/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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
***************************************************************************************************/

package eu.baltrad.dex.config.model;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *
 * @author szewczenko
 */
public class ConfigurationManagerTest extends TestCase {
    
    private Properties defaultProps;
    private Properties userProps;
    
    public void testLoadProperties() throws IOException {
        FileInputStream in = new FileInputStream( "conf/dex.default.properties" );
        
        assertNotNull( in );
        
        defaultProps = new Properties();
        defaultProps.load( in );
        
        in.close();
        
        assertNotNull( defaultProps );
        
        in = new FileInputStream( "conf/dex.user.properties" );
        
        assertNotNull( in );
        
        userProps = new Properties( defaultProps );
        userProps.load( in );
        in.close();
        
        assertNotNull( userProps );
    }
    public void rrtestReadProperty() throws IOException {
        FileInputStream in = new FileInputStream( "conf/dex.default.properties" );
        
        assertNotNull( in );
        
        defaultProps = new Properties();
        defaultProps.load( in );
        
        assertNotNull( defaultProps );
        assertEquals( "baltrad", defaultProps.getProperty( "node.name" ) );
    }
    public void testSaveProperty() throws IOException {
        FileInputStream in = new FileInputStream( "conf/dex.user.properties" );
        
        assertNotNull( in );
        
        userProps = new Properties();
        userProps.load( in );
        in.close();
        
        assertNotNull( userProps );
        
        userProps.setProperty( "organization.name", "" );
        FileOutputStream out = new FileOutputStream( "conf/dex.user.properties" );
        
        assertNotNull( out );
        
        userProps.store( out, null );
        out.close();
        
        assertEquals( "", userProps.getProperty( "organization.name" ) );
    }
    
    public void testPropertyFallback() throws IOException {
        // read default props
        FileInputStream in = new FileInputStream( "conf/dex.default.properties" );
        
        assertNotNull( in );
        
        defaultProps = new Properties();
        defaultProps.load( in );
        
        in.close();
        
        assertNotNull( defaultProps );
        
        // read user props
        in = new FileInputStream( "conf/dex.user.properties" );
        
        assertNotNull( in );
        
        userProps = new Properties();
        userProps.load( in );
        
        assertNotNull( userProps );
        
        
        // modify user props
        userProps.setProperty( "organization.name", "" );
        FileOutputStream out = new FileOutputStream( "conf/dex.user.properties" );
        
        assertNotNull( out );
        
        userProps.store( out, null );
        out.close();
        
        // initialize user props again
        userProps = new Properties( defaultProps );
        userProps.load( in );
        in.close();
        
        
        assertNotNull( userProps );
        // see if the fallback works
        assertEquals( "Organization", userProps.getProperty( "organization.name" ) );
    }
    
}
//--------------------------------------------------------------------------------------------------
