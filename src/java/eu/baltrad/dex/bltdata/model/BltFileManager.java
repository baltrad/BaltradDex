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

import eu.baltrad.dex.util.FileCatalogConnector;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.DataSourceManager;

import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.db.CoreFilterManager;

import eu.baltrad.fc.FileCatalog;
import eu.baltrad.fc.ExpressionFactory;
import eu.baltrad.fc.FileQuery;
import eu.baltrad.fc.FileResult;
import eu.baltrad.fc.FileEntry;
import eu.baltrad.fc.AttributeQuery;
import eu.baltrad.fc.AttributeResult;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.io.File;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.sql.SQLException;

/**
 * Data manager class implementing data handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class BltFileManager {
//---------------------------------------------------------------------------------------- Constants
    /** HDF5 source key */
    private static final String FC_SRC_PLC_ATTR = "what/source:PLC";
    /** HDF5 date attribute */
    private static final String FC_DATE_ATTR = "what/date";
    /** HDF5 time attribute */
    private static final String FC_TIME_ATTR = "what/time";
    /** File UUID key */
    private static final String FC_FILE_UUID = "file:uuid";
    /** Date format string */
    private static final String FC_DATE_STR = "yyyyMMdd";
    /** Time format string */
    private static final String FC_TIME_STR = "HHmmss";
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
    /** Reference to FileCatalogConnector object */
    private FileCatalogConnector fileCatalogConnector;
    /** Reference to FileCatalog object */
    private FileCatalog fc;
    /** References DataSourceManager */
    private DataSourceManager dataSourceManager;
    /** References CoreFilterManager */
    private CoreFilterManager coreFilterManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to FileCatalogConnector instance.
     */
    public BltFileManager() {
        this.fileCatalogConnector = FileCatalogConnector.getInstance();
        this.fc = fileCatalogConnector.getFileCatalog();
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
    public long countEntries( String dsName ) {
        IFilter attributeFilter = getFilter( dsName );
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
<<<<<<< HEAD
        q.filter( attributeFilter.getExpression() );
=======
        q.filter( xpr.eq( xpr.attribute( FC_SRC_PLC_ATTR ), xpr.string( dataChannel ) ) );
>>>>>>> 299cb355f341dc6af2655355839cfab37d4d359d
        q.fetch( "fileCount", xpr.count( xpr.attribute( "file:uuid" ) ) );
        AttributeResult r = fc.database().execute( q );
        r.next();
        long count = r.int64_( "fileCount" );
        r.delete();
        return count;
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
<<<<<<< HEAD
        //q.filter( xpr.attribute( FC_SRC_PLC_ATTR ).eq( xpr.string( dataChannel ) ) );
        q.filter( attributeFilter.getExpression() );
=======
        // filter the query with a given channel name
        q.filter( xpr.eq( xpr.attribute( FC_SRC_PLC_ATTR ), xpr.string( dataChannel ) ) );
>>>>>>> 299cb355f341dc6af2655355839cfab37d4d359d
        FileResult r = fc.database().execute( q );
        List< BltFile > bltFiles = new ArrayList<BltFile>();
        while( r.next() ) {
            try {
                FileEntry fileEntry = r.entry();
                BltFile bltFile = new BltFile(
                    fileEntry.uuid(), fc.storage().store( fileEntry ),
                    format.parse( fileEntry.what_date().to_iso_string() + "T" +
                                  fileEntry.what_time().to_iso_string() ),
                    format.parse( fileEntry.stored_at().to_iso_string() ),
                    fileEntry.what_source(), fileEntry.what_object(),
                    InitAppUtil.getThumbsStorageFolder() + File.separator +
                    fileEntry.uuid() + IMAGE_FILE_EXT );
                bltFiles.add( bltFile );
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
        FileResult r = fc.database().execute( q );
        BltFile bltFile = null;
        while( r.next() ) {
            try {
            FileEntry fileEntry = r.entry();
            bltFile = new BltFile(
                fileEntry.uuid(), fc.storage().store( fileEntry ),
                format.parse( fileEntry.what_date().to_iso_string() + "T" +
                              fileEntry.what_time().to_iso_string() ),
                format.parse( fileEntry.stored_at().to_iso_string() ),
                fileEntry.what_source(), fileEntry.what_object(),
                InitAppUtil.getThumbsStorageFolder() + File.separator + fileEntry.uuid() +
                IMAGE_FILE_EXT );
            } catch( ParseException e ) {
                System.err.println( "Error while parsing file's timestamp: " + e.getMessage() );
            }
        }
        // delete the result set
        r.delete();
        return bltFile;
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
}
//--------------------------------------------------------------------------------------------------
