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

package eu.baltrad.dex.datasource.controller;

import junit.framework.TestCase;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.DataSourceManager;
import eu.baltrad.dex.channel.model.Channel;
import eu.baltrad.dex.channel.model.ChannelManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;

import java.sql.SQLException;
import java.util.List;

/**
 * Test case for SaveDataSourceController class.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class SaveDataSourceControllerTest extends TestCase {
//---------------------------------------------------------------------------------------- Constants
    private static final String DS_NAME = "TestDS";
    private static final String DS_DESC_INIT = "Iniatial data source description";
    private static final String DS_DESC_UPDATED = "Updated data source description";
    private static final int filterId = 1001;
//---------------------------------------------------------------------------------------- Variables
    private DataSourceManager dsManager;
    private ChannelManager radarManager;
    private UserManager userManager;
    private static int dsId;
//------------------------------------------------------------------------------------------ Methods
    @Override
    public void setUp() throws Exception {
        dsManager = new DataSourceManager();
        radarManager = new ChannelManager();
        userManager = new UserManager();
    }

    public void testSaveDS() {
        DataSource ds = new DataSource( DS_NAME, DS_DESC_INIT );
        try {
            int save = dsManager.saveOrUpdate( ds );
            assertEquals( 1, save );
        } catch( SQLException e ) {
            System.out.println( "testSaveDS(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testSaveDS(): Exception: " + e.getMessage() );
        }
    }

    public void testGetDSByName() {
        DataSource ds = null;
        try {
            ds = dsManager.getDataSource( DS_NAME );
            dsId = ds.getId();
            assertNotNull( ds );
            assertEquals( dsId, ds.getId() );
            assertEquals( DS_NAME, ds.getName() );
            assertEquals( DS_DESC_INIT, ds.getDescription() );
        } catch( SQLException e ) {
            System.out.println( "testGetDSByName(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testGetDSByName(): Exception: " + e.getMessage() );
        }
    }

    public void testGetDSById() {
        DataSource ds = null;
        try {
            ds = dsManager.getDataSource( dsId );
            assertNotNull( ds );
            assertEquals( dsId, ds.getId() );
            assertEquals( DS_NAME, ds.getName() );
            assertEquals( DS_DESC_INIT, ds.getDescription() );
        } catch( SQLException e ) {
            System.out.println( "testGetDSById(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testGetDSById(): Exception: " + e.getMessage() );
        }
    }

    public void testUpdateDS() {
        DataSource ds = new DataSource( DS_NAME, DS_DESC_UPDATED );
        DataSource dsUpdated = null;
        try {
            int save = dsManager.saveOrUpdate( ds );
            dsUpdated = dsManager.getDataSource( DS_NAME );
            assertEquals( 1, save );
            assertNotNull( dsUpdated );
            assertEquals( dsId, dsUpdated.getId() );
            assertEquals( DS_NAME, dsUpdated.getName() );
            assertEquals( DS_DESC_UPDATED, dsUpdated.getDescription() );
        } catch( SQLException e ) {
            System.out.println( "testUpdateDS(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testUpdateDS(): Exception: " + e.getMessage() );
        }
    }

    public void testDeleteDS() {
        try {
            int delete = dsManager.deleteDataSource( dsId );
            assertEquals( 1, delete );
            assertNull( dsManager.getDataSource( dsId ) );
        } catch( SQLException e ) {
            System.out.println( "testDeleteDS(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testDeleteDS(): Exception: " + e.getMessage() );
        }
    }

    public void testGetAllDS() {
        DataSource ds1 = new DataSource( "DS1", "Data source 1");
        DataSource ds2 = new DataSource( "DS2", "Data source 2");
        DataSource ds3 = new DataSource( "DS3", "Data source 3");
        try {
            int save = 0;
            save = dsManager.saveOrUpdate( ds1 );
            assertEquals( 1, save );
            save = dsManager.saveOrUpdate( ds2 );
            assertEquals( 1, save );
            save = dsManager.saveOrUpdate( ds3 );
            assertEquals( 1, save );
            List<DataSource> dsList = dsManager.getDataSources();
            assertNotNull( dsList );
            assertTrue( dsList.size() > 0 );
        } catch( SQLException e ) {
            System.out.println( "testGetAllDS(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testGetAllDS(): Exception: " + e.getMessage() );
        }
    }

    public void testSaveRadar() {
        try {
            int save = 0;
            if( dsManager.getDataSource( DS_NAME ) == null ) {
                save = dsManager.saveOrUpdate( new DataSource( DS_NAME, DS_DESC_INIT ) );
                assertEquals( 1, save );
            }
            DataSource ds = dsManager.getDataSource( DS_NAME );
            assertNotNull( ds );
            if( radarManager.getChannel( "TestRadar" ) == null ) {
                save = radarManager.saveOrUpdate( new Channel( "TestRadar", "12345" ) );
                assertEquals( 1, save );
            }
            Channel radar = radarManager.getChannel( "TestRadar" );
            save = dsManager.saveRadar( ds.getId(), radar.getId() );
            assertEquals( 1, save );
        } catch( SQLException e ) {
            System.out.println( "testSaveRadar(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testSaveRadar(): Exception: " + e.getMessage() );
        }
    }

    public void testGetRadarIds() {
        try {
            List<Integer> radarIds = dsManager.getRadarIds(
                    dsManager.getDataSource( DS_NAME ).getId() );
            assertTrue( radarIds.size() > 0 );
        } catch( SQLException e ) {
            System.out.println( "testGetRadarIds(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testGetRadarIds(): Exception: " + e.getMessage() );
        }
    }

    public void testDeleteRadars() {
        try {
            int delete = 0;
            delete = dsManager.deleteRadars( dsManager.getDataSource( DS_NAME ).getId() );
            assertTrue( delete > 0 );
            delete = radarManager.deleteChannel( radarManager.getChannel( "TestRadar" ).getId() );
            assertTrue( delete > 0 );
        } catch( SQLException e ) {
            System.out.println( "testDeleteRadars(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testDeleteRadars(): Exception: " + e.getMessage() );
        }
    }

    public void testSaveUser() {
        try {
            int save = 0;
            if( dsManager.getDataSource( DS_NAME ) == null ) {
                save = dsManager.saveOrUpdate( new DataSource( DS_NAME, DS_DESC_INIT ) );
                assertEquals( 1, save );
            }
            DataSource ds = dsManager.getDataSource( DS_NAME );
            assertNotNull( ds );
            if( userManager.getUserByName( "TestUser" ) == null ) {
                User user = new User( "TestUser", "s3cret" );
                user.setRoleName( "user" );
                save = userManager.saveOrUpdate( user );
                assertEquals( 1, save );
            }
            User user = userManager.getUserByName( "TestUser" );
            save = dsManager.saveUser( ds.getId(), user.getId() );
            assertEquals( 1, save );
        } catch( SQLException e ) {
            System.out.println( "testSaveUser(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testSaveUser(): Exception: " + e.getMessage() );
        }
    }

    public void testGetUserIds() {
        try {
            List<Integer> idList = dsManager.getUserIds(
                    dsManager.getDataSource( DS_NAME ).getId() );
            //assertTrue( idList.size() > 0 );
        } catch( SQLException e ) {
            System.out.println( "testGetUserIds(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testGetUserIds(): Exception: " + e.getMessage() );
        }
    }

    public void testGetDSIds() {
        try {
            List<Integer> idList = dsManager.getDataSourceIds(
                    userManager.getUserByName( "TestUser" ).getId() );
            assertTrue( idList.size() > 0 );
        } catch( SQLException e ) {
            System.out.println( "testGetDSIds(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testGetDSIds(): Exception: " + e.getMessage() );
        }
    }

    public void testDeleteUsers() {
        try {
            int delete = 0;
            delete = dsManager.deleteUsers( dsManager.getDataSource( DS_NAME ).getId() );
            //assertTrue( delete > 0 );
            delete = userManager.deleteUser( userManager.getUserByName( "TestUser" ).getId() );
            assertTrue( delete > 0 );
        } catch( SQLException e ) {
            System.out.println( "testDeleteUsers(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testDeleteUsers(): Exception: " + e.getMessage() );
        }
    }

    public void testSaveFilter() {
        try {
            DataSource ds = dsManager.getDataSource( DS_NAME );
            assertNotNull( ds );
            int save = dsManager.saveFilter( ds.getId(), filterId );
            assertEquals( 1, save );
        } catch( SQLException e ) {
            System.out.println( "testSaveFilter(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testSaveFilter(): Exception: " + e.getMessage() );
        }
    }

    public void testGetFilterId() {
        try {
            DataSource ds = dsManager.getDataSource( DS_NAME );
            assertNotNull( ds );
            int id = dsManager.getFilterId( ds.getId() );
            assertEquals( filterId, id );
        } catch( SQLException e ) {
            System.out.println( "testGetFilterId(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testGetFilterId(): Exception: " + e.getMessage() );
        }
    }

    public void testDeleteFilters() {
        try {
            DataSource ds = dsManager.getDataSource( DS_NAME );
            assertNotNull( ds );
            int delete = dsManager.deleteFilters( ds.getId() );
            assertEquals( 1, delete );
        } catch( SQLException e ) {
            System.out.println( "testDeleteFilters(): SQLException: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "testDeleteFilters(): Exception: " + e.getMessage() );
        }
    }
    
}
//--------------------------------------------------------------------------------------------------
