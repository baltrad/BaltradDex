/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.core.controller;

import eu.baltrad.dex.core.model.NodeConnection;
import eu.baltrad.dex.core.model.NodeConnectionManager;

import junit.framework.TestCase;

import java.util.List;

/**
 * Node connection controller test class.
 *
 * @author szewczenko
 * @version 1.6
 * @since 1.6
 */
public class NodeConnectionControllerTest extends TestCase {
//------------------------------------------------------------------------------------------ Methods
    @Override
    public void setUp() throws Exception {
        /*NodeConnection nodeConn = new NodeConnection( "baltrad.imgw.pl",
                "http://baltrad.imgw.pl:8084/BaltradDex/dispatch.htm", "test_user", "test_passwd" );

        assertEquals( nodeConn.getConnectionName(), "baltrad.imgw.pl" );
        assertEquals( nodeConn.getNodeAddress(),
                "http://baltrad.imgw.pl:8084/BaltradDex/dispatch.htm" );
        assertEquals( nodeConn.getUserName(), "test_user" );
        assertEquals( nodeConn.getPassword(), "test_passwd" );*/
    }

    public void testAddConnection() {
        /*NodeConnectionManager manager = new NodeConnectionManager();
        NodeConnection nodeConn = new NodeConnection( "baltrad.imgw.pl",
                "http://baltrad.imgw.pl:8084/BaltradDex/dispatch.htm", "test_user", "test_passwd" );
        manager.addConnection( nodeConn );
        NodeConnection connById = manager.getConnection( nodeConn.getId() );
        NodeConnection connByName = manager.getConnection( nodeConn.getConnectionName() );

        assertNotNull( connById );
        assertNotNull( connByName );
        assertEquals( connById.getId(), connByName.getId() );
        assertEquals( connById.getConnectionName(), connByName.getConnectionName() );
        assertEquals( connById.getNodeAddress(), connByName.getNodeAddress() );
        assertEquals( connById.getUserName(), connByName.getUserName() );
        assertEquals( connById.getPassword(), connByName.getPassword() );
        
        manager.removeConnections();*/
    }

    public void testRemoveConnection() {
        /*NodeConnectionManager manager = new NodeConnectionManager();
        NodeConnection nodeConn = new NodeConnection( "baltrad.imgw.pl",
                "http://baltrad.imgw.pl:8084/BaltradDex/dispatch.htm", "test_user", "test_passwd" );
        manager.addConnection( nodeConn );
        List conns = manager.getConnections();

        assertNotNull( conns );

        int res = manager.removeConnections();
        
        assertTrue( res > 0 );*/
    }
}
//--------------------------------------------------------------------------------------------------
