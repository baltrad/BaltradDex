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

package eu.baltrad.dex.db.manager.impl;

import eu.baltrad.dex.db.manager.IBltFileManager;
import eu.baltrad.dex.util.InitAppUtil;
import static eu.baltrad.dex.util.InitAppUtil.validate;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.manager.impl.DataSourceManager;
import eu.baltrad.dex.db.model.BltFile;

import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.db.CoreFilterManager;

import eu.baltrad.bdb.FileCatalog;
import eu.baltrad.bdb.db.AttributeQuery;
import eu.baltrad.bdb.db.AttributeResult;
import eu.baltrad.bdb.db.DatabaseError;
import eu.baltrad.bdb.db.FileEntry;
import eu.baltrad.bdb.db.FileQuery;
import eu.baltrad.bdb.db.FileResult;
import eu.baltrad.bdb.expr.ExpressionFactory;
import eu.baltrad.bdb.expr.Expression;
import eu.baltrad.bdb.oh5.Metadata;
import eu.baltrad.dex.db.model.BltQueryParameter;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Data manager - interacts with baltrad-db component.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.1.6
 */
public class BltFileManager implements IBltFileManager {

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

    /** Date and time format string */
    private static SimpleDateFormat format = new SimpleDateFormat( FC_DATE_STR + 
            "'T'" + FC_TIME_STR );
    /** Reference to FileCatalog object */
    private FileCatalog fileCatalog;
    /** References DataSourceManager */
    private DataSourceManager dataSourceManager;
    /** References CoreFilterManager */
    private CoreFilterManager coreFilterManager;
    /** Logger */
    private Logger log;

    /**
     * Constructor
     */
    public BltFileManager() {
        this.log = Logger.getLogger("DEX");
    }
    /**
     * Gets filter associated with a given data source.
     *
     * @param dsName Data source name
     * @return Filter associated with a given data source
     */
    public IFilter loadFilter(String dsName) {
        IFilter attributeFilter = null;
        try {
            DataSource dataSource = dataSourceManager.load(dsName);
            attributeFilter = coreFilterManager.load(
                    dataSourceManager.loadFilterId(dataSource.getId()));
        } catch (Exception e) {
            log.error("getFilter(): Failed to get filter ID:", e);
        }
        return attributeFilter;
    }
    /**
     * Counts file entries from a given data source.
     *
     * @param dsName Data source name
     * @return Number of file entries.
     * @throws DatabaseError In case when file catalog is not available
     */
    public long count(String dsName) throws DatabaseError {
        try {
            IFilter attributeFilter = loadFilter( dsName );
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
        } catch (Exception e) {
            throw new DatabaseError(e);
        }
    }
    
    /**
     * Counts file entries matching criteria given by parameters. 
     * @param param Query parameters
     * @return Number of file entries matching giver criteria
     * @throws DatabaseError In case when file catalog is not available
     */
    public long count(BltQueryParameter param) throws DatabaseError {
        try {
            ExpressionFactory xpr = new ExpressionFactory();
            AttributeQuery q = new AttributeQuery();
            Expression ex = getExpression(param); 
            if (ex != null) {
                q.setFilter(ex);
            }
            q.fetch("entryCount", xpr.count(xpr.attribute("_bdb/uuid")));
            AttributeResult r = fileCatalog.getDatabase().execute(q);
            long count = 0;
            try {
                r.next();
                count = r.getLong("entryCount");
            } finally {
                r.close();
            }
            return count;
        } catch (Exception e) {
            throw new DatabaseError(e);
        }
    }
    
    /**
     * Convert a FileEntry instance to a BltFile instance.
     * @param entry File entry to convert
     * @return BltFile
     * @throws DatabaseError In case when file catalog is not available
     * @throws ParseException 
     */
    private BltFile fileEntryToBltFile(FileEntry entry) throws DatabaseError {
        BltFile file = null;
        try {
            Metadata metadata = entry.getMetadata();
            file = new BltFile(
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
        } catch (ParseException e) {
            log.error("Failed to parse file's timestamp", e);
        } catch (Exception e) {
            throw new DatabaseError(e);
        }
        return file;
    }
    /**
     * Gets data set for a given data source.
     * @param dsName Data source name
     * @param offset Dataset offset
     * @param limit Dataset size limit
     * @return Dataset from a given data source
     * @throws DatabaseError In case when file catalog is not available
     */
    public List<BltFile> load(String dsName, int offset, int limit) 
                    throws DatabaseError {
        try {
            ExpressionFactory xpr = new ExpressionFactory();
            FileQuery q = new FileQuery();
            IFilter attributeFilter = loadFilter(dsName);
            q.setLimit(limit);
            q.setSkip(offset);
            q.appendOrderClause(xpr.desc(xpr.combinedDateTime("what/date", 
                                                                    "what/time")));
            q.setFilter(attributeFilter.getExpression());
            FileResult r = fileCatalog.getDatabase().execute(q);
            List<BltFile> bltFiles = new ArrayList<BltFile>();
            try {
                while (r.next()) {
                    bltFiles.add(fileEntryToBltFile(r.getFileEntry()));
                }
            } finally {
                r.close();
            }
            return bltFiles;
        } catch (Exception e) {
            log.error("Failed to select file entries", e);
            throw new DatabaseError(e);
        }
    }
    /**
     * Gets file entry with a given identity string.
     *
     * @param uuid File entry's identity string
     * @return File entry with a given ID
     * @throws DatabaseError In case when file catalog is not available
     */
    public BltFile load(String uuid) throws DatabaseError {
        try {
            ExpressionFactory xpr = new ExpressionFactory();
            FileQuery q = new FileQuery();
            // filter the query with a given file identity string
            q.setFilter(xpr.eq(xpr.attribute("_bdb/uuid"), xpr.literal(uuid)));
            FileResult r = fileCatalog.getDatabase().execute(q);
            BltFile bltFile = null;
            try {
                if (r.next()) {
                    bltFile = fileEntryToBltFile(r.getFileEntry());
                }
            } finally {
                r.close();
            }
            return bltFile;
        } catch (Exception e) {
            throw new DatabaseError(e);
        }
    }
    /**
     * Gets distinct radar stations. Stations are discovered based on the 
     * content of data files saved in the local storage.
     *
     * @return List containing distinct radar stations names.
     */
    public List<String> loadDistinctRadarStations() throws DatabaseError {
        try {
            List<String> result = new ArrayList<String>();
            AttributeQuery q = new AttributeQuery();
            ExpressionFactory xpr = new ExpressionFactory();
            q.fetch("plc", xpr.attribute("_bdb/source:PLC"));
            // Workaround for #56: bdb doesn't seem to handle diacritics properly,
            // so we fetch WMO identifier to be used with file queries
            q.fetch("wmo", xpr.attribute("_bdb/source:WMO"));
            q.setDistinct(true);
            AttributeResult r = fileCatalog.getDatabase().execute(q);
            //List<String> result = new ArrayList<String>();
            while (r.next()) {
                String identifier = r.getString("wmo") + " " + r.getString("plc");
                result.add(identifier);
            }
            Collections.sort(result);
            return result;
        } catch (Exception e) {
            throw new DatabaseError(e);
        }
    }
    
    /**
     * Fetches set of file entries matching criteria given by parameters.
     * @param param Query parameters
     * @return Set of file entries matching given criteria
     * @throws DatabaseError In case when file catalog is not available
     */
    public List<BltFile> load(BltQueryParameter param) 
                throws DatabaseError {
        try {
            ExpressionFactory xpr = new ExpressionFactory();
            FileQuery q = new FileQuery();    
            Expression ex = getExpression(param); 
            if (ex != null) {
                q.setFilter(ex);
            }
            if (validate(param.getOffset())) {
                q.setSkip(Integer.parseInt(param.getOffset()));
            }
            if (validate(param.getLimit())) {
                q.setLimit(Integer.parseInt(param.getLimit()));
            }
            if (param.getSortByDate().equals(BltQueryParameter.SORT_ASC)) {
                q.appendOrderClause(xpr.asc(
                        xpr.combinedDateTime("what/date", "what/time")));
            }
            if (param.getSortByDate().equals(BltQueryParameter.SORT_DESC)) {
                q.appendOrderClause(xpr.desc(
                        xpr.combinedDateTime("what/date", "what/time")));
            }
            if (param.getSortByTime().equals(BltQueryParameter.SORT_ASC)) {
                q.appendOrderClause(xpr.asc(
                        xpr.combinedDateTime("what/date", "what/time")));
            }
            if (param.getSortByTime().equals(BltQueryParameter.SORT_DESC)) {
                q.appendOrderClause(xpr.desc(
                        xpr.combinedDateTime("what/date", "what/time")));
            }
            if (param.getSortBySource().equals(BltQueryParameter.SORT_ASC)) {
                q.appendOrderClause(xpr.asc(xpr.attribute("_bdb/source:PLC")));
            }
            if (param.getSortBySource().equals(BltQueryParameter.SORT_DESC)) {
                q.appendOrderClause(xpr.desc(xpr.attribute("_bdb/source:PLC")));
            }
            if (param.getSortByObject().equals(BltQueryParameter.SORT_ASC)) {
                q.appendOrderClause(xpr.asc(xpr.attribute("what/object")));
            }
            if (param.getSortByObject().equals(BltQueryParameter.SORT_DESC)) {
                q.appendOrderClause(xpr.desc(xpr.attribute("what/object")));
            }
            FileResult r = fileCatalog.getDatabase().execute(q);
            List<BltFile> bltFiles = new ArrayList<BltFile>();
            try {
                while (r.next()) {
                    bltFiles.add(fileEntryToBltFile(r.getFileEntry()));
                }
            } finally {
                r.close();
            }
            return bltFiles;
        } catch (Exception e) {
            throw new DatabaseError(e);
        }
    }
    
    /**
     * Helper method constructs query expression.
     * @param param Query parameters
     * @return Query expression constructed based on given parameters
     */
    private Expression getExpression(BltQueryParameter param)  
    {
        if (validateParms(param.getRadar(), param.getFileObject(), 
                param.getStartDate(), param.getStartHour(),
                param.getStartMinute(), param.getStartSecond(),
                param.getEndDate(), param.getEndHour(), param.getEndMinute(),
                param.getEndSecond())) {
            ExpressionFactory xpr = new ExpressionFactory();
            List<Expression> xprs = new ArrayList<Expression>();
            
            int[] startTime = new int[3];
            int[] endTime = new int[3];
            
            if (validate(param.getRadar())) {
                String wmoNumber = param.getRadar()
                        .substring(0, param.getRadar().indexOf(" "));
                Expression e = xpr.eq(xpr.attribute("_bdb/source:WMO"), 
                                                        xpr.literal(wmoNumber));
                xprs.add(e);
            }
            if (validate(param.getFileObject())) {
                Expression e = xpr.eq(xpr.attribute("what/object"),
                        xpr.literal(param.getFileObject()));
                xprs.add(e);
            }
            if (validate(param.getStartDate())) {
                eu.baltrad.bdb.util.Date startDt = new eu.baltrad.bdb.util.Date(
                        Integer.parseInt(param.getStartDate().substring(0, 4)),
                        Integer.parseInt(param.getStartDate().substring(5, 7)),
                        Integer.parseInt(param.getStartDate().substring(8, 
                        param.getStartDate().length())));
                Expression e = xpr.ge(xpr.attribute("what/date"), 
                                                          xpr.literal(startDt));
                xprs.add(e);
            }
            if (validate(param.getEndDate())) {
                eu.baltrad.bdb.util.Date endDt = new eu.baltrad.bdb.util.Date(
                        Integer.parseInt(param.getEndDate().substring(0, 4)),
                        Integer.parseInt(param.getEndDate().substring(5, 7)),
                        Integer.parseInt(param.getEndDate().substring(8, 
                        param.getEndDate().length())));
                Expression e = xpr.le(xpr.attribute("what/date"), 
                                                            xpr.literal(endDt));
                xprs.add(e);
            }
            if (validate(param.getStartHour())) {
                startTime[0] = Integer.parseInt(param.getStartHour());   
            } else {
                startTime[0] = -1;
            }
            if (validate(param.getStartMinute())) {
                startTime[1] = Integer.parseInt(param.getStartMinute());
            } else {
                startTime[1] = -1;
            }
            if (validate(param.getStartSecond())) {
                startTime[2] = Integer.parseInt(param.getStartSecond());
            } else {
                startTime[2] = -1;
            }
            if (startTime[0] != -1 && startTime[1] != -1 && startTime[2] != -1)
            {
                eu.baltrad.bdb.util.Time startTm = new eu.baltrad.bdb.util.Time(
                        startTime[0], startTime[1], startTime[2]);
                Expression e = xpr.ge(xpr.attribute("what/time"), 
                                                          xpr.literal(startTm));
                xprs.add(e);
            }
            if (validate(param.getEndHour())) {
                endTime[0] = Integer.parseInt(param.getEndHour());   
            } else {
                endTime[0] = -1;
            }
            if (validate(param.getEndMinute())) {
                endTime[1] = Integer.parseInt(param.getEndMinute());
            } else {
                endTime[1] = -1;
            }
            if (validate(param.getEndSecond())) {
                endTime[2] = Integer.parseInt(param.getEndSecond());
            } else {
                endTime[2] = -1;
            }
            if (endTime[0] != -1 && endTime[1] != -1 && endTime[2] != -1)
            {
                eu.baltrad.bdb.util.Time endTm = new eu.baltrad.bdb.util.Time(
                        endTime[0], endTime[1], endTime[2]);
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
     * @param radarStation Radar station name 
     * @param fileObject File object type
     * @param startDate Lower date limit 
     * @param startHour Start hour
     * @param startMinute Start minute
     * @param startSecond Start second
     * @param endDate Upper date limit
     * @param endHour End hour
     * @param endMinute End minute
     * @param endSecond End second
     * @return True if at least one parameters is provided, false otherwise
     */
    private boolean validateParms(String radarStation, String fileObject,
            String startDate, String startHour, String startMinute, 
            String startSecond, String endDate, String endHour, 
            String endMinute, String endSecond) {
        boolean result = false;
        if( validate(radarStation) || validate(fileObject) 
                || validate(startDate)
            || (validate(startHour) && validate(startMinute) && 
                validate(startSecond)) 
                || validate(endDate) 
            || (validate(endHour) && validate(endMinute) && 
                validate(endSecond))) {
            result = true;
        }
        return result;
    }

    /**
     * @param fileCatalog the fileCatalog to set
     */
    @Autowired
    public void setFileCatalog(FileCatalog fileCatalog) {
        this.fileCatalog = fileCatalog;
    }

    /**
     * @param dataSourceManager the dataSourceManager to set
     */
    @Autowired
    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    /**
     * @param coreFilterManager the coreFilterManager to set
     */
    @Autowired
    public void setCoreFilterManager(CoreFilterManager coreFilterManager) {
        this.coreFilterManager = coreFilterManager;
    }
    
}

