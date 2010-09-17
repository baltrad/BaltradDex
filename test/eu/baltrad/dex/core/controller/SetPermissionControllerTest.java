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

import eu.baltrad.dex.channel.model.ChannelManager;
import eu.baltrad.dex.channel.model.ChannelPermission;
import eu.baltrad.dex.channel.model.Channel;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.user.model.User;

import junit.framework.TestCase;

/**
 * Set channel permission controller test class.
 *
 * @author szewczenko
 * @version 1.6
 * @since 1.6
 */
public class SetPermissionControllerTest extends TestCase {
//---------------------------------------------------------------------------------------- Variables
    private UserManager userManager = new UserManager();
    private ChannelManager channelManager = new ChannelManager();
    private static User testUser;
    private static Channel testChannel;
//------------------------------------------------------------------------------------------ Methods
    public void testInit() {
        testUser = new User( "testUser", "testUser", "user", "passwd", "passwd", "address", "company",
                "country", "city", "666", "street", "666", "666-666", "email" );
        testChannel = new Channel( "testChannel", "666" );
        ChannelPermission perm = new ChannelPermission( testChannel.getId(), testUser.getId() );
        assertNotNull( testUser );
        assertNotNull( testChannel );
        assertNotNull( perm );
        assertEquals( perm.getChannelId(), testChannel.getId() );
        assertEquals( perm.getUserId(), testUser.getId() );
    }

    public void testAddPermission() {
        channelManager.addChannel( testChannel );
        userManager.addUser( testUser );
        channelManager.addChannelPermission( new ChannelPermission( testChannel.getId(),
                testUser.getId() ) );
        ChannelPermission perm = channelManager.getChannelPermission( testChannel.getId(),
                testUser.getId() );

        assertNotNull( perm );
        assertEquals( perm.getChannelId(), testChannel.getId() );
        assertEquals( perm.getUserId(), testUser.getId() );
    }

    public void testRemovePermission() {
        channelManager.removeChannelPermission( testChannel.getId(), testUser.getId() );
        
        assertNull( channelManager.getChannelPermission( testChannel.getId(), testUser.getId() ) );
    }
}
//--------------------------------------------------------------------------------------------------
