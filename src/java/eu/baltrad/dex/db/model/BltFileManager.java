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

package eu.baltrad.dex.db.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.db.CoreFilterManager;

import eu.baltrad.bdb.FileCatalog;
import eu.baltrad.bdb.db.AttributeQuery;
import eu.baltrad.bdb.db.AttributeResult;
import eu.baltrad.bdb.db.FileEntry;
import eu.baltrad.bdb.db.FileQuery;
import eu.baltrad.bdb.db.FileResult;
import eu.baltrad.bdb.expr.ExpressionFactory;
import eu.baltrad.bdb.expr.Expression;
import eu.baltrad.bdb.oh5.Metadata;

import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.DataSourceManager;
import eu.baltrad.dex.util.JDBCConnectionManager;
import eu.baltrad.dex.log.model.MessageLogger;

/**
 * Data manager - interacts with baltrad-db component.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 0.1.6
 */
public class BltFileManager implements IBltFileManager {
//-------------------------------------------------------------------- Constants
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
//-------------------------------------------------------------------- Variables
    /** Date and time format string */
    private static SimpleDateFormat format = new SimpleDateFormat( FC_DATE_STR + 
            "'T'" + FC_TIME_STR );
    /** Reference to FileCatalog object */
    private FileCatalog fileCatalog;
    /** References DataSourceManager */
    private DataSourceManager dataSourceManager;
    /** References CoreFilterManager */
    private CoreFilterManager coreFilterManager;
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
    /** Logger */
    private Logger log;
//---------------------------------------------------------------------- Methods
    /**
     * Constructor
     */
    public BltFileManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
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
            DataSource dataSource = dataSourceManager.load(dsName);
            attributeFilter = coreFilterManager.load(
                    dataSourceManager.loadFilterId(dataSource.getId()));
        } catch( Exception e ) {
            log.error( "getFilter(): Failed to get filter ID:", e );
        }
        return attributeFilter;
    }
    /**
     * Counts file entries from a given data source.
     *
     * @param dsName Data source name
     * @return Number of file entries.
     */
    public long countDSFileEntries(String dsName) {
        IFilter attributeFilter = getFilter( dsName );
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        q.setFilter(attributeFilter.getExpression());
        q.fetch("fileCount", xpr.count( xpr.attribute("_bdb/uuid")));
        AttributeResult r = fileCatalog.getDatabase().execute(q);
        long count;
        try {
          r.next();
          count = r.getLong("fileCount");
        } finally {
          r.close();
        }
        return count;
    }
    
    /**
     * Convert a FileEntry instance to a BltFile instance
     */
    protected BltFile fileEntryToBltFile(FileEntry entry) 
            throws ParseException {
        Metadata metadata = entry.getMetadata();
        return new BltFile(
            entry.getUuid().toString(),
            fileCatalog.getLocalStorage().store( entry ).toString(),
            format.parse(
                metadata.getWhatDate().toIsoString() + "T" + 
                metadata.getWhatTime().toIsoString()
            ),
            format.parse(
                entry.getStoredDate().toIsoString() + "T" + 
                entry.getStoredTime().toIsoString()
            ),
            metadata.getWhatSource(),
            metadata.getWhatObject(),
            InitAppUtil.getConf().getThumbsDir() + File.separator + 
                entry.getUuid().toString() + IMAGE_FILE_EXT);
    }
    /**
     * Gets data set for a given data source.
     *
     * @param dsName Data source name
     * @param offset Dataset offset
     * @param limit Dataset size limit
     * @return Dataset from a given data source
     */
    public List<BltFile> getFileEntries(String dsName, int offset, int limit) {
        ExpressionFactory xpr = new ExpressionFactory();
        FileQuery q = new FileQuery();
        IFilter attributeFilter = getFilter(dsName);
        q.setLimit(limit);
        q.setSkip(offset);
        q.appendOrderClause(xpr.desc(xpr.combinedDateTime("what/date", 
                                                                "what/time")));
        q.setFilter(attributeFilter.getExpression());
        FileResult r = fileCatalog.getDatabase().execute(q);
        List<BltFile> bltFiles = new ArrayList<BltFile>();
        try {
            while (r.next()) {
                try {
                    bltFiles.add(fileEntryToBltFile(r.getFileEntry()));
                } catch (ParseException e) {
                    log.error("Error while parsing file's timestamp", e);
                }
            }
        } finally {
            r.close();
        }
        return bltFiles;
    }
    /**
     * Gets file entry with a given identity string.
     *
     * @param uuid File entry's identity string
     * @return File entry with a given ID
     */
    public BltFile getFileEntry(String uuid) {
        ExpressionFactory xpr = new ExpressionFactory();
        FileQuery q = new FileQuery();
        // filter the query with a given file identity string
        q.setFilter(xpr.eq(xpr.attribute("_bdb/uuid"), xpr.literal(uuid)));
        FileResult r = fileCatalog.getDatabase().execute(q);
        BltFile bltFile = null;
        try {
            if (r.next()) {
                try {
                    bltFile = fileEntryToBltFile(r.getFileEntry());
                } catch (ParseException e) {
                    log.error("Error while parsing file's timestamp", e);
                }
            }
        } finally {
          r.close();
        }
        return bltFile;
    }
    /**
     * Gets distinct radar stations. Stations are discovered based on the 
     * content of data files saved in the local storage.
     *
     * @return List containing distinct radar stations names.
     */
    public List<String> getDistinctRadarStations() {
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        q.fetch("plc", xpr.attribute("_bdb/source:PLC"));
        // Workaround for #56: bdb doesn't seem to handle diacritics properly,
        // so we fetch WMO identifier to be used with file queries
        q.fetch("wmo", xpr.attribute("_bdb/source:WMO"));
        q.setDistinct(true);
        AttributeResult r = fileCatalog.getDatabase().execute(q);
        List<String> result = new ArrayList<String>();
        while (r.next()) {
            String identifier = r.getString("wmo") + " " + r.getString("plc");
            result.add(identifier);
        }
        Collections.sort(result);
        return result;
    }
    /**
     * Counts file entries matching criteria given by parameters. 
     * 
     * @param radarStation Radar station name 
     * @param fileObject File object type
     * @param startDate Lower date limit 
     * @param startTime Lower time limit
     * @param endDate Upper date limit
     * @param endTime Lower time limit
     * @return Number of file entries matching giver criteria
     */
    public long countFileEntries(String radarStation, String fileObject,
            String startDate, String startTime, String endDate, String endTime) 
    {
        ExpressionFactory xpr = new ExpressionFactory();
        AttributeQuery q = new AttributeQuery();
        Expression ex = getExpression(radarStation, fileObject, startDate, 
                startTime, endDate, endTime); 
        if (ex != null) {
            q.setFilter(ex);
        }
        q.fetch("entryCount", xpr.count(xpr.attribute("_bdb/uuid")));
        AttributeResult r = fileCatalog.getDatabase().execute(q);
        long count = 0;
        try {
            r.next();
            count = r.getLong("entryCount");
        } catch (Exception e) {
            log.error("Failed to get number of file entries", e);
        } finally {
          r.close();
        }
        return count;
    }
    
    /**
     * Fetches set of file entries matching criteria given by parameters.
     * Dataset is also formatted based on parameters.
     * 
     * @param radarStation Radar station name 
     * @param fileObject File object type
     * @param startDate Lower date limit 
     * @param startTime Lower time limit
     * @param endDate Upper date limit
     * @param endTime Lower time limit
     * @param offset Cursor's offset
     * @param limit Dataset size limit
     * @param sortByDateAsc Sort by date ascending
     * @param sortByDateDesc Sort by date descending
     * @param sortByTimeAsc Sort by time ascending
     * @param sortByTimeDesc Sort by time descending
     * @param sortBySourceAsc Sort by source ascending
     * @param sortBySourceDesc Sort by source descending
     * @param sortByObjectAsc Sort by object ascending
     * @param sortByObjectDesc Sort by object descending
     * @return Set of file entries matching given criteria
     */
    public List<BltFile> getFileEntries(String radarStation, String fileObject,
            String startDate, String startTime, String endDate, String endTime, 
            String offset, String limit, boolean sortByDateAsc,
            boolean sortByDateDesc, boolean sortByTimeAsc, 
            boolean sortByTimeDesc, boolean sortBySourceAsc, 
            boolean sortBySourceDesc, boolean sortByObjectAsc, 
            boolean sortByObjectDesc) {
        ExpressionFactory xpr = new ExpressionFactory();
        FileQuery q = new FileQuery();    
        Expression ex = getExpression(radarStation, fileObject, startDate, 
                startTime, endDate, endTime); 
        if (ex != null) {
            q.setFilter(ex);
        }
        if (InitAppUtil.validate(offset)) {
            q.setSkip(Integer.parseInt(offset));
        }
        if (InitAppUtil.validate(limit)) {
            q.setLimit(Integer.parseInt(limit));
        }
        if (sortByDateAsc) {
            q.appendOrderClause(xpr.asc(xpr.combinedDateTime("what/date", 
                                                                "what/time")));
        }
        if (sortByDateDesc) {
            q.appendOrderClause(xpr.desc(xpr.combinedDateTime("what/date", 
                                                                "what/time")));
        }
        if (sortByTimeAsc) {
            q.appendOrderClause(xpr.asc(xpr.combinedDateTime("what/date", 
                                                                "what/time")));
        }
        if (sortByTimeDesc) {
            q.appendOrderClause(xpr.desc(xpr.combinedDateTime("what/date", 
                                                                "what/time")));
        }
        if (sortBySourceAsc) {
            q.appendOrderClause(xpr.asc(xpr.attribute("_bdb/source:PLC")));
        }
        if (sortBySourceDesc) {
            q.appendOrderClause(xpr.desc(xpr.attribute("_bdb/source:PLC")));
        }
        if (sortByObjectAsc) {
            q.appendOrderClause(xpr.asc(xpr.attribute("what/object")));
        }
        if (sortByObjectDesc) {
            q.appendOrderClause(xpr.desc(xpr.attribute("what/object")));
        }
        FileResult r = fileCatalog.getDatabase().execute(q);
        List<BltFile> bltFiles = new ArrayList<BltFile>();
        try {
            while (r.next()) {
                bltFiles.add(fileEntryToBltFile(r.getFileEntry()));
            }
        } catch (Exception e) {
            log.error("Failed to fetch file entry", e);
        } finally {
            r.close();
        }
        return bltFiles;
    }
    
    /**
     * Helper method constructs query expression.
     * 
     * @param radarStation Radar station name 
     * @param fileObject File object type
     * @param startDate Lower date limit 
     * @param startTime Lower time limit
     * @param endDate Upper date limit
     * @param endTime Lower time limit
     * @return Query expression constructed based on given parameters
     */
    private Expression getExpression(String radarStation, String fileObject,
            String startDate, String startTime, String endDate, String endTime) 
    {
        if (validateParms(radarStation, fileObject, startDate, startTime, 
                endDate, endTime)) {
            ExpressionFactory xpr = new ExpressionFactory();
            List<Expression> xprs = new ArrayList<Expression>();
            if (InitAppUtil.validate(radarStation)) {
                String wmoNumber = radarStation.substring(0, 
                                                     radarStation.indexOf(" "));
                Expression e = xpr.eq(xpr.attribute("_bdb/source:WMO"), 
                                                        xpr.literal(wmoNumber));
                xprs.add(e);
            }
            if (InitAppUtil.validate(fileObject)) {
                Expression e = xpr.eq(xpr.attribute("what/object"), 
                                                       xpr.literal(fileObject));
                xprs.add(e);
            }
            if (InitAppUtil.validate(startDate)) {
                eu.baltrad.bdb.util.Date startDt = new eu.baltrad.bdb.util.Date(
                        Integer.parseInt(startDate.substring(0, 4)),
                        Integer.parseInt(startDate.substring(5, 7)),
                        Integer.parseInt(startDate.substring(8, 
                        startDate.length())));
                Expression e = xpr.ge(xpr.attribute("what/date"), 
                                                          xpr.literal(startDt));
                xprs.add(e);
            }
            if (InitAppUtil.validate(endDate)) {
                eu.baltrad.bdb.util.Date endDt = new eu.baltrad.bdb.util.Date(
                        Integer.parseInt(endDate.substring(0, 4)),
                        Integer.parseInt(endDate.substring(5, 7)),
                        Integer.parseInt(endDate.substring(8, 
                        endDate.length())));
                Expression e = xpr.le(xpr.attribute("what/date"), 
                                                            xpr.literal(endDt));
                xprs.add(e);
            }
            if (InitAppUtil.validate(startTime)) {
                eu.baltrad.bdb.util.Time startTm = new eu.baltrad.bdb.util.Time(
                        Integer.parseInt(startTime.substring(0, 2)),
                        Integer.parseInt(startTime.substring(3, 5)),
                        Integer.parseInt(startTime.substring(7, 
                            startTime.length())));
                Expression e = xpr.ge(xpr.attribute("what/time"), 
                                                          xpr.literal(startTm));
                xprs.add(e);
            }
            if (InitAppUtil.validate(endTime)) {
                eu.baltrad.bdb.util.Time endTm = new eu.baltrad.bdb.util.Time(
                        Integer.parseInt(endTime.substring(0, 2)),
                        Integer.parseInt(endTime.substring(3, 5)),
                        Integer.parseInt(endTime.substring(7, 
                                                            endTime.length())));
                Expression e = xpr.le(xpr.attribute("what/time"), 
                                                            xpr.literal(endTm));
                xprs.add(e);
            }
            return xpr.and(xprs);
        } else {
            return null;
        }
    }
    
    /**
     * Validate query parameters.
     * 
    @param radarStation Radar station name 
     * @param fileObject File object type
     * @param startDate Lower date limit 
     * @param startTime Lower time limit
     * @param endDate Upper date limit
     * @param endTime Lower time limit
     * @return True if at least one parameters is provided, false otherwise
     */
    private boolean validateParms(String radarStation, String fileObject,
        String startDate, String startTime, String endDate, String endTime) {
        boolean result = false;
        if(InitAppUtil.validate(radarStation) ||
            InitAppUtil.validate(fileObject) || InitAppUtil.validate(startDate)
            || InitAppUtil.validate(startTime) || InitAppUtil.validate(endDate) 
            || InitAppUtil.validate(endTime)) {
            result = true;
        }
        return result;
        
    }
    /**
     * Gets reference to data source manager object.
     *
     * @return Reference to data source manager object
     */
    public DataSourceManager getDataSourceManager() { 
        return dataSourceManager; 
    }
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
    /**
     * Get reference to FileCatalog
     *
     * @return Reference to file catalog
     */
    public FileCatalog getFileCatalog() { return fileCatalog; }
    /**
     * Set reference to file catalog
     *
     * @param fileCatalog Reference to file catalog
     */
    public void setFileCatalog(FileCatalog fileCatalog) { 
        this.fileCatalog = fileCatalog; 
    }
}
//--------------------------------------------------------------------------------------------------
