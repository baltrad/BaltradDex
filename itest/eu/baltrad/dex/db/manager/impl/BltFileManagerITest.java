/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import eu.baltrad.bdb.db.rest.RestfulDatabase;
import eu.baltrad.bdb.expr.Expression;
import eu.baltrad.bdb.expr.ExpressionFactory;
import eu.baltrad.bdb.db.AttributeQuery;
import eu.baltrad.bdb.db.AttributeResult;
import eu.baltrad.bdb.db.FileQuery;
import eu.baltrad.bdb.db.FileResult;
import eu.baltrad.bdb.db.FileEntry;

import eu.baltrad.beast.db.*;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.db.itest.DexJDBCITestHelper;
import eu.baltrad.dex.db.itest.DexDBITestHelper;

/**
 * Integration test for baltrad-db and Beast.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0.7
 * @since 1.0.7
 */
public class BltFileManagerITest extends TestCase {
    
    private AbstractApplicationContext context = null;
    private DexDBITestHelper bdbHelper = null;
    private DexJDBCITestHelper jdbcHelper = null;
    private RestfulDatabase bdb = null;
    private CoreFilterManager coreFilterManager = null;
    private IFilterManager attributeFilterManager = null;
    private IFilterManager combinedFilterManager = null;
    private IFilterManager alwaysMatchFilterManager = null;
    private SimpleJdbcOperations template = null;
    private int dsLegId = 0;
    private int dsBrzId = 0;
    private int fltrLegId = 0;
    private int fltrBrzId = 0;
    
    @Override
    public void setUp() {
        context = DexDBITestHelper.loadContext(this);
        jdbcHelper = (DexJDBCITestHelper) context.getBean("jdbcHelper");
        bdbHelper = (DexDBITestHelper) context.getBean("bdbHelper");
        bdb = (RestfulDatabase) context.getBean("bdb");
        attributeFilterManager = (AttributeFilterManager) 
                                      context.getBean("attributeFilterManager"); 
        combinedFilterManager = (CombinedFilterManager) 
                                       context.getBean("combinedFilterManager"); 
        alwaysMatchFilterManager = (AlwaysMatchFilterManager) 
                                    context.getBean("alwaysMatchFilterManager");
        template = (SimpleJdbcOperations)context.getBean("jdbcTemplate");
        Map<String, IFilterManager> subManagers = 
                                          new HashMap<String, IFilterManager>();
        subManagers.put("attr", attributeFilterManager);
        subManagers.put("combined", combinedFilterManager);
        subManagers.put("always", alwaysMatchFilterManager);
        coreFilterManager = new CoreFilterManager();
        coreFilterManager.setJdbcTemplate(template);
        coreFilterManager.setSubManagers(subManagers);
        
        jdbcHelper.deleteDataSources();
        jdbcHelper.deleteCombinedFilters();
        // Polar volumes - Legionowo
        DataSource dsLeg = new DataSource("LegionowoPVOLs", DataSource.LOCAL,
                "Polar volumes from Legionowo");
        
        assertNotNull(dsLeg);
        dsLegId = jdbcHelper.saveDataSource(dsLeg);
        assertTrue(dsLegId > 0);
        
        // create and save filter 
        CombinedFilter combinedFilter = new CombinedFilter();
        assertNotNull(combinedFilter);
        combinedFilter.setMatchType(CombinedFilter.MatchType.ALL);
        AttributeFilter sourceFilter = new AttributeFilter();
        sourceFilter.setAttribute("_bdb/source:WMO");
        sourceFilter.setValueType(AttributeFilter.ValueType.STRING);
        sourceFilter.setOperator(AttributeFilter.Operator.IN);
        sourceFilter.setValue("12374");
        combinedFilter.addChildFilter(sourceFilter);
        
        AttributeFilter fileObjectFilter = new AttributeFilter();
        fileObjectFilter.setAttribute("what/object");
        fileObjectFilter.setValueType(AttributeFilter.ValueType.STRING);
        fileObjectFilter.setOperator(AttributeFilter.Operator.IN);
        fileObjectFilter.setValue("PVOL");
        combinedFilter.addChildFilter(fileObjectFilter);
        coreFilterManager.store(combinedFilter);
        fltrLegId = combinedFilter.getId();
        assertTrue(fltrLegId > 0);
        jdbcHelper.saveCombinedFilter(dsLegId, combinedFilter.getId());
        
        // polar volumes - Gdańsk
        DataSource dsBrz = new DataSource("BrzuchaniaPVOLs", DataSource.LOCAL,
                "Polar volumes from Brzuchania");
        
        assertNotNull(dsBrz);
        dsBrzId = jdbcHelper.saveDataSource(dsBrz);
        assertTrue(dsBrzId > 0);
        
        // create and save filter 
        combinedFilter = new CombinedFilter();
        assertNotNull(combinedFilter);
        combinedFilter.setMatchType(CombinedFilter.MatchType.ALL);
        sourceFilter = new AttributeFilter();
        sourceFilter.setAttribute("_bdb/source:WMO");
        sourceFilter.setValueType(AttributeFilter.ValueType.STRING);
        sourceFilter.setOperator(AttributeFilter.Operator.IN);
        sourceFilter.setValue("12568");
        combinedFilter.addChildFilter(sourceFilter);
        
        fileObjectFilter = new AttributeFilter();
        fileObjectFilter.setAttribute("what/object");
        fileObjectFilter.setValueType(AttributeFilter.ValueType.STRING);
        fileObjectFilter.setOperator(AttributeFilter.Operator.IN);
        fileObjectFilter.setValue("PVOL");
        combinedFilter.addChildFilter(fileObjectFilter);
        coreFilterManager.store(combinedFilter);
        fltrBrzId = combinedFilter.getId();
        assertTrue(fltrBrzId > 0);
        jdbcHelper.saveCombinedFilter(dsBrzId, combinedFilter.getId());
    }
    
    @Override
    public void tearDown() {
        bdb.close();
        jdbcHelper = null;
        bdbHelper = null;
        context.close();
    }
    
    public void testGetFilter() throws Exception {
        int fltrId = jdbcHelper.getCombinedFilterId(dsLegId);
        IFilter comboFilter = coreFilterManager.load(fltrId);
        assertNotNull(comboFilter);
        assertTrue(fltrLegId == comboFilter.getId());
    }
    
    public void testCountDSFileEntries() {
        int fltrId = jdbcHelper.getCombinedFilterId(dsLegId);
        IFilter comboFilter = coreFilterManager.load(fltrId);
        assertNotNull(comboFilter);
        
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        q.setFilter(comboFilter.getExpression());
        q.fetch("fileCount", xpr.count(xpr.attribute("_bdb/uuid")));
        AttributeResult r = bdb.execute(q);
        r.next();
        long count = r.getLong("fileCount");
        assertTrue(count == 1);
        r.close();
        
        fltrId = jdbcHelper.getCombinedFilterId(dsBrzId);
        comboFilter = coreFilterManager.load(fltrId);
        assertNotNull(comboFilter);
        q = new AttributeQuery();
        xpr = new ExpressionFactory();
        q.setFilter(comboFilter.getExpression());
        q.fetch("fileCount", xpr.count(xpr.attribute("_bdb/uuid")));
        r = bdb.execute(q);
        r.next();
        count = r.getLong("fileCount");
        assertTrue(count == 6);
        r.close();
    }
    
    public void testGetFileEntries() throws Exception {
        int fltrId = jdbcHelper.getCombinedFilterId(dsBrzId);
        IFilter comboFilter = coreFilterManager.load(fltrId);
        assertNotNull(comboFilter);
        
        ExpressionFactory xpr = new ExpressionFactory();
        FileQuery q = new FileQuery();
        q.setLimit(4);
        q.setSkip(2);
        q.appendOrderClause(xpr.desc(xpr.combinedDateTime("what/date", 
                                                                 "what/time")));
        q.setFilter(comboFilter.getExpression());
        FileResult r = bdb.execute(q);
        assertEquals(4, r.size());
        List<FileEntry> fileEntries = new ArrayList<FileEntry>();
        while (r.next()) {
            fileEntries.add(r.getFileEntry());
        }
        r.close();
        assertEquals(4, fileEntries.size());
        
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyyMMdd:HHmmss");
        for (FileEntry entry : fileEntries) {
            Date dt = df.parse(entry.getMetadata().getWhatDate().toString() +
                    ":" + entry.getMetadata().getWhatTime().toString());
            if (date != null) {
                assertTrue(date.after(dt));
            }
            date = dt;
        }    
    }
    
    public void testGetAllFileEntries() {
        FileQuery q = new FileQuery();
        FileResult r = bdb.execute(q);
        assertEquals(31, r.size());
        r.close();
    }
    
    public void testGetDistinctRadarStations() {
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        q.fetch("plc", xpr.attribute("_bdb/source:PLC"));
        // Workaround for #56: bdb doesn't seem to handle diacritics properly,
        // so we fetch WMO identifier to be used with file queries
        q.fetch("wmo", xpr.attribute("_bdb/source:WMO"));
        q.setDistinct(true);
        AttributeResult r = bdb.execute(q);
        assertEquals(20, r.size());
        List<String> result = new ArrayList<String>();
        while (r.next()) {
            String identifier = r.getString("wmo") + " " + r.getString("plc");
            result.add(identifier);
        }
        assertNotNull(result);
        assertEquals(20, result.size());
    }
    
    public void testQueryBdb() {
        Map<String, String> parms1 = new HashMap<String, String>();
        parms1.put("source", "Brzuchania");
        parms1.put("object", "PVOL");
        parms1.put("start_date", "20120322");
        parms1.put("stop_date", "20120322");
        parms1.put("start_time", "074000");
        parms1.put("stop_time", "082000");
        parms1.put("offset", "1");
        parms1.put("limit", "3");
        parms1.put("sort_by_time", "desc");
        
        ExpressionFactory xpr = new ExpressionFactory();
        List<Expression> xprs = new ArrayList<Expression>();
        if (!parms1.get("source").isEmpty()) {
            Expression e = xpr.eq(xpr.attribute("_bdb/source:PLC"), 
                                             xpr.literal(parms1.get("source")));
            xprs.add(e);
        }
        if (!parms1.get("object").isEmpty()) {
            Expression e = xpr.eq(xpr.attribute("what/object"), 
                                             xpr.literal(parms1.get("object")));
            xprs.add(e);
        }
        if (!parms1.get("start_date").isEmpty()) {
            String ds = parms1.get("start_date");
            eu.baltrad.bdb.util.Date startDate = 
                    new eu.baltrad.bdb.util.Date(
                        Integer.parseInt(ds.substring(0, 4)),
                        Integer.parseInt(ds.substring(4, 6)),
                        Integer.parseInt(ds.substring(6, ds.length())));
            Expression e = xpr.ge(xpr.attribute("what/date"), 
                                                        xpr.literal(startDate));
            xprs.add(e);
        }
        if (!parms1.get("stop_date").isEmpty()) {
            String ds = parms1.get("stop_date");
            eu.baltrad.bdb.util.Date startDate = 
                    new eu.baltrad.bdb.util.Date(
                        Integer.parseInt(ds.substring(0, 4)),
                        Integer.parseInt(ds.substring(4, 6)),
                        Integer.parseInt(ds.substring(6, ds.length())));
            Expression e = xpr.le(xpr.attribute("what/date"), 
                                                        xpr.literal(startDate));
            xprs.add(e);
        }
        if (!parms1.get("start_time").isEmpty()) {
            String ts = parms1.get("start_time");
            eu.baltrad.bdb.util.Time startTime = 
                    new eu.baltrad.bdb.util.Time(
                        Integer.parseInt(ts.substring(0, 2)),
                        Integer.parseInt(ts.substring(2, 4)),
                        Integer.parseInt(ts.substring(4, ts.length())));
            Expression e = xpr.ge(xpr.attribute("what/time"), 
                                                        xpr.literal(startTime));
            xprs.add(e);
        }
        if (!parms1.get("stop_time").isEmpty()) {
            String ts = parms1.get("stop_time");
            eu.baltrad.bdb.util.Time startTime = 
                    new eu.baltrad.bdb.util.Time(
                        Integer.parseInt(ts.substring(0, 2)),
                        Integer.parseInt(ts.substring(2, 4)),
                        Integer.parseInt(ts.substring(4, ts.length())));
            Expression e = xpr.le(xpr.attribute("what/time"), 
                                                        xpr.literal(startTime));
            xprs.add(e);
        }
        
        // count entries with AttributeQuery (you can't do this with FileQuery)
        Expression ex = xpr.and(xprs);
        AttributeQuery q = new AttributeQuery();
        q.setFilter(ex);
        q.fetch("entryCount", xpr.count(xpr.attribute("_bdb/uuid")));
        AttributeResult r = bdb.execute(q);
        r.next();
        long count = r.getLong("entryCount");
        assertEquals(4, count);
        r.close();
        
        // fetch entries with FileQuery
        FileQuery fq = new FileQuery();
        fq.setFilter(ex);
        if (!parms1.get("offset").isEmpty()) {
            fq.setSkip(Integer.parseInt(parms1.get("offset")));
        }
        if (!parms1.get("limit").isEmpty()) {
            fq.setLimit(Integer.parseInt(parms1.get("limit")));
        }
        if (!parms1.get("sort_by_time").isEmpty()) {
            if (parms1.get("sort_by_time").equals("desc")) {
                fq.appendOrderClause(xpr.desc(xpr.attribute("what/time")));
            } else {
                fq.appendOrderClause(xpr.asc(xpr.attribute("what/time")));
            }
        }
        FileResult fr = bdb.execute(fq);
        assertEquals(3, fr.size());
        // verify sorting
        int time = 0;
        while (fr.next()) {
            int t = fr.getFileEntry().getMetadata().getWhatTime()
                                                          .getCumulativeMsecs();
            if (time != 0) {
                assertTrue(time > t);
            }
            time = t;
        }
        fr.close();
    }
    
    public void testBdbOperators() {
        ExpressionFactory xpr = new ExpressionFactory();
        List<Expression> xprs = new ArrayList<Expression>();
        Expression e1 = xpr.eq(xpr.attribute("_bdb/source:PLC"), 
                                             xpr.literal("Brzuchania"));
        xprs.add(e1);
        eu.baltrad.bdb.util.Date startDate = 
                    new eu.baltrad.bdb.util.Date(2012, 3, 22);
        Expression e2 = xpr.ge(xpr.attribute("what/date"), 
                                                        xpr.literal(startDate));
        xprs.add(e2);
        
        eu.baltrad.bdb.util.Date stopDate = 
                    new eu.baltrad.bdb.util.Date(2012, 3, 22);
        Expression e3 = xpr.le(xpr.attribute("what/date"), 
                                                        xpr.literal(stopDate));
        xprs.add(e3);
        
        eu.baltrad.bdb.util.Time startTime = 
                    new eu.baltrad.bdb.util.Time(7, 40, 0); 
        Expression e4 = xpr.ge(xpr.attribute("what/time"), 
                                                        xpr.literal(startTime));
        xprs.add(e4);
        
        eu.baltrad.bdb.util.Time stopTime = 
                    new eu.baltrad.bdb.util.Time(8, 10, 59); 
        Expression e5 = xpr.le(xpr.attribute("what/time"), 
                                                        xpr.literal(stopTime));
        xprs.add(e5);
        
        Expression ex = xpr.and(xprs);
        AttributeQuery q = new AttributeQuery();
        q.setFilter(ex);
        q.fetch("entryCount", xpr.count(xpr.attribute("_bdb/uuid")));
        AttributeResult r = bdb.execute(q);
        r.next();
        long count = r.getLong("entryCount");
        assertEquals(4, count);
        r.close();
    }
    
}
