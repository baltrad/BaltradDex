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

import eu.baltrad.fc.FileCatalog;
import eu.baltrad.fc.expr.ExpressionFactory;
import eu.baltrad.fc.db.FileQuery;
import eu.baltrad.fc.db.FileResult;
import eu.baltrad.fc.db.FileEntry;
import eu.baltrad.fc.db.AttributeQuery;
import eu.baltrad.fc.db.AttributeResult;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.io.File;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Data manager class implementing data handling functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
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
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to FileCatalogConnector instance.
     */
    public BltFileManager() {
        System.out.println("BltFileManager()");
        this.fileCatalogConnector = FileCatalogConnector.getInstance();
        this.fc = fileCatalogConnector.getFileCatalog();
    }
    /**
     * Counts file entries from a given data channel.
     *
     * @param dataChannel Data channel name
     * @return Number of file entries.
     */
    public long countEntries( String dataChannel ) {
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        q.filter( xpr.eq( xpr.attribute( FC_SRC_PLC_ATTR ), xpr.string( dataChannel ) ) );
        q.fetch( "fileCount", xpr.count( xpr.attribute( "file:uuid" ) ) );
        AttributeResult r = fc.database().execute( q );
        r.next();
        long count = r.int64_( "fileCount" );
        r.delete();
        return count;
    }
    /**
     * Gets files from a given data channel.
     *
     * @param dataChannel Data channel name
     * @return List containing data from a given radar station
     */
    public List<BltFile> getFileEntries( String dataChannel, int offset, int limit ) {
        ExpressionFactory xpr = new ExpressionFactory();
        FileQuery q = new FileQuery();
        // set offset and limit
        q.limit( limit );
        q.skip( offset );
        q.order_by( xpr.combined_datetime( FC_DATE_ATTR, FC_TIME_ATTR ), FileQuery.SortDir.DESC );
        // filter the query with a given channel name
        q.filter( xpr.eq( xpr.attribute( FC_SRC_PLC_ATTR ), xpr.string( dataChannel ) ) );
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
                    dataChannel, fileEntry.what_object(),
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
}
//--------------------------------------------------------------------------------------------------
