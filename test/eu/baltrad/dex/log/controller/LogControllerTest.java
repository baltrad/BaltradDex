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

package eu.baltrad.dex.log.controller;

import eu.baltrad.dex.util.JDBCConnectionManager;
import eu.baltrad.dex.log.model.LogEntry;
import eu.baltrad.dex.log.model.LogManager;

import junit.framework.TestCase;

import java.sql.Connection;

/**
 * Tests log handling with direct usage of JDBC
 *
 * @author szewczenko
 * @version 1.6
 * @since 1.6
 */
public class LogControllerTest extends TestCase {
//---------------------------------------------------------------------------------------- Variables
    private static JDBCConnectionManager jdbcConn;
    private static Connection conn;
    private static LogManager manager;
//------------------------------------------------------------------------------------------ Methods
    @Override
    public void setUp() {
        jdbcConn = JDBCConnectionManager.getInstance();
        assertNotNull( jdbcConn );
        manager = new LogManager();
        assertNotNull( manager );
    }

    public void testGetConnection() {
        conn = jdbcConn.getConnection();
        assertNotNull( conn );
    }

    /*public void testInsertEntries() {
        ThreadedLogger tl1 = new ThreadedLogger( manager, 1, 1000 );
        ThreadedLogger tl2 = new ThreadedLogger( manager, 2, 1000 );
        ThreadedLogger tl3 = new ThreadedLogger( manager, 3, 1000 );

        tl1.start();
        tl2.start();
        tl3.start();

        // wait a minute or so until threads finish
        try {
            Thread.sleep( 60000 );
        } catch( InterruptedException e ) {}
    }*/

    class ThreadedLogger extends Thread {

        private LogManager manager;
        private int label;
        private int numOp;

        public ThreadedLogger( LogManager manager, int label, int numOp ) {
            this.manager = manager;
            this.label = label;
            this.numOp = numOp;
        }

        @Override
        public void run() {
            for( int i = 0; i < numOp; i++ ) {
                LogEntry entry = new LogEntry( System.currentTimeMillis(), LogManager.MSG_INFO,
                        "Logger " + label + " entry " + i );
                manager.addEntry( entry );
            }
        }
    }
        
}
//--------------------------------------------------------------------------------------------------
