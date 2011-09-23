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

package eu.baltrad.dex.bltdata.model;

import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.DataSourceManager;
import eu.baltrad.dex.util.JDBCConnectionManager;

import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.db.CoreFilterManager;

import eu.baltrad.fc.AttributeQuery;
import eu.baltrad.fc.AttributeResult;
import eu.baltrad.fc.ExpressionFactory;
import eu.baltrad.fc.FileCatalog;
import eu.baltrad.fc.FileEntry;
import eu.baltrad.fc.FileQuery;
import eu.baltrad.fc.FileResult;
import eu.baltrad.fc.Oh5Metadata;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;


import java.io.File;

import java.text.ParseException;


import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Data manager class implementing data handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class BltFileManager {
//---------------------------------------------------------------------------------------- Constants
    /** File catalog source radar station key */
    private static final String FC_SRC_PLC_ATTR = "what/source:PLC";
    /** File catalog file object key */
    private static final String FC_FILE_OBJECT_ATTR = "what/object";
    /** File catalog date attribute */
    private static final String FC_DATE_ATTR = "what/date";
    /** File catalog time attribute */
    private static final String FC_TIME_ATTR = "what/time";
    /** File UUID key */
    private static final String FC_FILE_UUID = "file:uuid";
    /** Date format string */
    private static final String FC_DATE_STR = "yyyyMMdd";
    /** Time format string */
    private static final String FC_TIME_STR = "HHmmss";
    /** HDF5 file extension */
    private static final String H5_FILE_EXT = ".h5";
    /** Image file extension */
    private static final String IMAGE_FILE_EXT = ".png";
    /** Number of file entries per page */
    public final static int ENTRIES_PER_PAGE = 12;
    /** Number of pages in the scroll bar, must be an odd number >= 3 */
    public final static int SCROLL_RANGE = 11;
//---------------------------------------------------------------------------------------- Variables
    /** Date and time format string */
    private static SimpleDateFormat format = new SimpleDateFormat( FC_DATE_STR + "'T'" +
            FC_TIME_STR );
    /** Reference to FileCatalog object */
    private FileCatalog fileCatalog;
    /** References DataSourceManager */
    private DataSourceManager dataSourceManager;
    /** References CoreFilterManager */
    private CoreFilterManager coreFilterManager;
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor
     */
    public BltFileManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
    }
    /**
     * Gets filter associated with a given data source.
     *
     * @param dsName Data source name
     * @return Filter associated with a given data source
     */
    public IFilter getFilter( String dsName ) {
        IFilter attributeFilter = null;
        try {
            DataSource dataSource = dataSourceManager.getDataSource( dsName );
            attributeFilter = coreFilterManager.load( dataSourceManager.getFilterId(
                dataSource.getId() ) );
        } catch( SQLException e ){
            System.out.println( "getFilter(): Failed to get filter ID: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "getFilter(): Failed to get filter ID: " + e.getMessage() );
        }
        return attributeFilter;
    }
    /**
     * Counts file entries from a given data source.
     *
     * @param dsName Data source name
     * @return Number of file entries.
     */
    public long countDSFileEntries( String dsName ) {
        IFilter attributeFilter = getFilter( dsName );
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        q.filter( attributeFilter.getExpression() );
        q.fetch( "fileCount", xpr.count( xpr.attribute( "file:uuid" ) ) );
        AttributeResult r = fileCatalog.database().execute( q );
        r.next();
        long count = r.int64_( "fileCount" );
        r.delete();
        return count;
    }
    
    /**
     * Convert a FileEntry instance to a BltFile instance
     */
    protected BltFile fileEntryToBltFile(FileEntry entry) throws ParseException {
        // XXX: this method does too much!
        Oh5Metadata metadata = entry.metadata();
        return new BltFile(
            entry.uuid(), fileCatalog.storage().store( entry ),
            format.parse( metadata.what_date().to_iso_string() + "T" +
                          metadata.what_time().to_iso_string() ),
            format.parse( entry.stored_at().to_iso_string() ),
            metadata.what_source(), metadata.what_object(),
            InitAppUtil.getThumbsStorageFolder() + File.separator +
            entry.uuid() + IMAGE_FILE_EXT
        );
    }


    /**
     * Gets data set for a given data source.
     *
     * @param dsName Data source name
     * @param offset Dataset offset
     * @param limit Dataset size limit
     * @return Dataset from a given data source
     */
    public List<BltFile> getFileEntries( String dsName, int offset, int limit ) {
        ExpressionFactory xpr = new ExpressionFactory();
        FileQuery q = new FileQuery();
        IFilter attributeFilter = getFilter( dsName );
        // set offset and limit
        q.limit( limit );
        q.skip( offset );
        q.order_by( xpr.combined_datetime( FC_DATE_ATTR, FC_TIME_ATTR ), FileQuery.SortDir.DESC );
        q.filter( attributeFilter.getExpression() );
        FileResult r = fileCatalog.database().execute( q );
        List< BltFile > bltFiles = new ArrayList<BltFile>();
        while( r.next() ) {
            try {
                bltFiles.add( fileEntryToBltFile( r.entry() ) );
            } catch( ParseException e ) {
                System.err.println( "Error while parsing file's timestamp: " + e.getMessage() );
            }
        }
        // delete the result set
        r.delete();
        // sort data list
        Collections.sort( bltFiles );
        return bltFiles;
    }
    /**
     * Gets file entry with a given identity string.
     *
     * @param uuid File entry's identity string
     * @return File entry with a given ID
     */
    public BltFile getFileEntry( String uuid ) {
        ExpressionFactory xpr = new ExpressionFactory();
        FileQuery q = new FileQuery();
        // filter the query with a given file identity string
        q.filter( xpr.eq( xpr.attribute( FC_FILE_UUID ), xpr.string( uuid ) ) );
        FileResult r = fileCatalog.database().execute( q );
        BltFile bltFile = null;
        while( r.next() ) {
            try {
                bltFile = fileEntryToBltFile( r.entry() );
            } catch( ParseException e ) {
                System.err.println( "Error while parsing file's timestamp: " + e.getMessage() );
            }
        }
        // delete the result set
        r.delete();
        return bltFile;
    }
    /**
     * Gets distinct radar stations. Stations are discovered based on the content of
     * data files saved in the local storage.
     *
     * @return List containing distinct radar stations names.
     */
    public List<String> getDistinctRadarStations() {
        Connection conn = null;
        List<String> sourceNames = new ArrayList<String>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT value FROM bdb_source_kvs WHERE " +
                    "source_id IN (SELECT id FROM bdb_sources WHERE id IN (SELECT DISTINCT " +
                    "source_id FROM bdb_files)) AND key = 'PLC';" );
            while( resultSet.next() ) {
                String value = resultSet.getString( 1 );
                sourceNames.add( value );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select distinct radar stations: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select distinct radar stations: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return sourceNames;
    }
    /**
     * Constructs file selection SQL query based on parameters provided.
     *
     * @param select Method builds select query if true, count query otherwise
     * @param radarStation Source radar station name
     * @param fileObject File object type
     * @param startDate Dataset timespan - start date
     * @param startTime Dataset timespan - start time
     * @param endDate Dataset timespan - end date
     * @param endTime Dataset timespan - end time
     * @param offset Dataset offset
     * @param limit Dataset size limit
     * @return SQL query as string
     */
    public String buildQuery( boolean select, String radarStation, String fileObject,
            String startDate, String startTime, String endDate, String endTime, String offset,
            String limit ) {
        StringBuffer stmt = new StringBuffer();
        if( select ) {
            stmt.append( "SELECT * FROM bdb_files" );
        } else {
            stmt.append( "SELECT COUNT(*) FROM bdb_files" );
        }
        int numberOfParameters = 0;
        if( radarStation != null && !radarStation.isEmpty() ) {
            stmt.append( " WHERE source_id IN (SELECT source_id FROM bdb_source_kvs WHERE value = '" +
                    radarStation + "' AND key = 'PLC')" );
            numberOfParameters++;
        }
        if( fileObject != null && !fileObject.isEmpty() ) {
            if( numberOfParameters > 0 ) {
                stmt.append( " AND what_object = '" + fileObject + "'" );
            } else {
                stmt.append( " WHERE what_object = '" + fileObject + "'" );
                numberOfParameters++;
            }
        }
        if( startDate != null && !startDate.isEmpty() ) {
            if( numberOfParameters > 0 ) {
                stmt.append( " AND what_date >= '" + startDate + "'" );
            } else {
                stmt.append( " WHERE what_date >= '" + startDate + "'"  );
                numberOfParameters++;
            }
        }
        if( startTime != null && !startTime.isEmpty() ) {
            if( numberOfParameters > 0 ) {
                stmt.append( " AND what_time >= '" + startTime + "'" );
            } else {
                stmt.append( " WHERE what_time >= '" + startTime + "'" );
                numberOfParameters++;
            }
        }
        if( endDate != null && !endDate.isEmpty() ) {
            if( numberOfParameters > 0 ) {
                stmt.append( " AND what_date <= '" + endDate + "'" );
            } else {
                stmt.append( " WHERE what_date <= '" + endDate + "'" );
                numberOfParameters++;
            }
        }
        if( endTime != null && !endTime.isEmpty() ) {
            if( numberOfParameters > 0 ) {
                stmt.append( " AND what_time <= '" + endTime + "'" );
            } else {
                stmt.append( " WHERE what_time <= '" + endTime + "'" );
                numberOfParameters++;
            }
        }
        if( offset != null && !offset.isEmpty() ) {
            stmt.append( " OFFSET " + offset );
        }
        if( limit != null && !limit.isEmpty() ) {
            stmt.append( " LIMIT " + limit );
        }
        return stmt.toString();
    }
    /**
     * Counts file entries returned by a query passed as parameter.
     *
     * @param sql Count query
     * @return Number of records / file entries
     */
    public long countSelectedFileEntries( String sql ) {
        Connection conn = null;
        long count = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( sql );
            while( resultSet.next() ) {
                count = resultSet.getLong( 1 );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to count file entries: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to count file entries: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return count;
    }
    /**
     * Selects fileset matching criteria given by SQL query parameter.
     *
     * @param sql SQL query string
     * @return Fileset matching search criteria
     */
    public List<BltFile> getFileEntries( String sql ) {
        Connection conn = null;
        List<BltFile> bltFiles = new ArrayList<BltFile>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( sql );
            BltFile bltFile = null;
            while( resultSet.next() ) {
                String uuid = resultSet.getString( "uuid" );
                // XXX: this is not correct. If the result is larger than the size of
                // LocalStorage used by FileCatalog, old files might get deleted and the
                // paths become invalid. Path to the local file should only be asked
                // when it's actually used.
                String path = fileCatalog.local_path_for_uuid(uuid);
                String thumbPath = InitAppUtil.getThumbsStorageDirectory();
                Date storageTime = resultSet.getTimestamp( "stored_at" );
                String type = resultSet.getString( "what_object" );
                Date sqlDate = resultSet.getDate( "what_date" );
                Time sqlTime = resultSet.getTime( "what_time" );
                DateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
                Date timeStamp = df.parse( sqlDate.toString() + " " + sqlTime.toString() );
                int sourceId = resultSet.getInt( "source_id" );
                String source = "";
                String subQuery = "SELECT value FROM bdb_source_kvs WHERE source_id = " +
                        sourceId + " AND key ='PLC';";
                Statement subStmt = conn.createStatement();
                ResultSet subResultSet = subStmt.executeQuery( subQuery );
                while( subResultSet.next() ) {
                    String value = subResultSet.getString( 1 );
                    source = value;
                }
                subStmt.close();
                bltFile = new BltFile( uuid, path, timeStamp, storageTime, source, type,
                        thumbPath );
                bltFiles.add( bltFile );
            }
        } catch( SQLException e ) {
            System.err.println( "Failed to select file entries: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select file entries: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return bltFiles;
    }
    /**
     * Gets reference to data source manager object.
     *
     * @return Reference to data source manager object
     */
    public DataSourceManager getDataSourceManager() { return dataSourceManager; }
    /**
     * Sets reference to data source manager object.
     *
     * @param dataSourceManager Reference to data source manager object
     */
    public void setDataSourceManager( DataSourceManager dataSourceManager ) {
        this.dataSourceManager = dataSourceManager;
    }
    /**
     * Gets reference to CoreFilterManager.
     *
     * @return Reference to CoreFilterManager
     */
    public CoreFilterManager getCoreFilterManager() {
        return coreFilterManager;
    }
    /**
     * Sets reference to CoreFilterManager.
     *
     * @param coreFilterManager Reference tp CoreFilterManager to set
     */
    public void setCoreFilterManager( CoreFilterManager coreFilterManager ) {
        this.coreFilterManager = coreFilterManager;
    }

    public FileCatalog getFileCatalog() { return fileCatalog; }

    public void setFileCatalog(FileCatalog fileCatalog) {
      this.fileCatalog = fileCatalog;
    }
}
//--------------------------------------------------------------------------------------------------
