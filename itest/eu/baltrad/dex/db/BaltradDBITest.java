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
package eu.baltrad.dex.db;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;

import org.springframework.context.support.AbstractApplicationContext;

import eu.baltrad.bdb.db.FileEntry;
import eu.baltrad.bdb.db.rest.RestfulDatabase;
import eu.baltrad.bdb.db.AttributeQuery;
import eu.baltrad.bdb.db.AttributeResult;
import eu.baltrad.bdb.db.FileQuery;
import eu.baltrad.bdb.db.FileResult;
import eu.baltrad.bdb.expr.Expression;
import eu.baltrad.bdb.expr.ExpressionFactory;
import eu.baltrad.bdb.oh5.Metadata;

import eu.baltrad.dex.db.itest.DexDBITestHelper;


/**
 * Integration test for baltrad-db.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0.7
 * @since 1.0.7
 */
public class BaltradDBITest extends TestCase {
    
    private AbstractApplicationContext context = null;
    private DexDBITestHelper helper = null;
    private RestfulDatabase bdb = null;

    @Override
    public void setUp() throws Exception {
        context = DexDBITestHelper.loadContext(this);
        helper = (DexDBITestHelper) context.getBean("helper");
        bdb = new RestfulDatabase(helper.getBdbServerUri());
    }
    
    @Override
    public void tearDown() {
        bdb.close();
        helper = null;
        context.close();
    }
    
    public void testSelectLegionowo() throws Exception {
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        q.fetch("uuid", xpr.attribute("_bdb/uuid"));
        q.setFilter(xpr.eq(xpr.attribute("_bdb/source:PLC"), 
                                                    xpr.literal("Legionowo")));
        AttributeResult rs = bdb.execute(q);
        assertEquals(1, rs.size());
        rs.next();
        String legUuid = rs.getString("uuid");
        String fileName = helper.getUuidMap().get(legUuid);
        assertEquals("fixtures/Z_PVOL_20120315074000_plleg.h5", fileName);
        rs.close();
    }
    
    public void testSelectAll() throws Exception {
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        Set<String> result = new HashSet<String>();
        q.fetch("source", xpr.attribute("_bdb/source:PLC"));
        AttributeResult rs = bdb.execute(q);
        assertEquals(31, rs.size());
        while (rs.next()) {
            result.add(rs.getString("source"));
        }
        rs.close();
        assertTrue(result.contains("Legionowo"));
        assertTrue(result.contains("Poznań"));
        assertTrue(result.contains("Gdańsk"));
        assertTrue(result.contains("Świdwin"));
        assertTrue(result.contains("Brzuchania"));
        assertTrue(result.contains("Rzeszów"));
        assertTrue(result.contains("Ramza"));
        assertTrue(result.contains("Pastewnik"));
        assertTrue(result.contains("Ängelholm"));
        assertTrue(result.contains("Arlanda"));
        assertTrue(result.contains("Ase"));
        assertTrue(result.contains("Hudiksvall"));
        assertTrue(result.contains("Kiruna"));
        assertTrue(result.contains("Karlskrona"));
        assertTrue(result.contains("Leksand"));
        assertTrue(result.contains("Luleå"));
        assertTrue(result.contains("Östersund"));
        assertTrue(result.contains("Örnsköldsvik"));
        assertTrue(result.contains("Vara"));
        assertTrue(result.contains("Vilebo"));
    }
    
    public void testSelectByObject() {
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        Set<String> result = new HashSet<String>();
        q.fetch("source", xpr.attribute("_bdb/source:PLC"));
        Expression e1 = xpr.eq(xpr.attribute("what/object"), 
                                                           xpr.literal("PVOL"));
        q.setFilter(e1);
        AttributeResult rs = bdb.execute(q);
        assertEquals(19, rs.size());
        while (rs.next()) {
            result.add(rs.getString("source"));
        }
        rs.close();
        assertTrue(result.contains("Legionowo"));
        assertTrue(result.contains("Poznań"));
        assertTrue(result.contains("Gdańsk"));
        assertTrue(result.contains("Świdwin"));
        assertTrue(result.contains("Brzuchania"));
        assertTrue(result.contains("Rzeszów"));
        assertTrue(result.contains("Ramza"));
        assertTrue(result.contains("Pastewnik"));
        
        Expression e2 = xpr.eq(xpr.attribute("what/object"), 
                                                           xpr.literal("SCAN"));
        q.setFilter(e2);
        rs = bdb.execute(q);
        assertEquals(12, rs.size());
        result.clear();
        while (rs.next()) {
            result.add(rs.getString("source"));
        }
        assertTrue(result.contains("Ängelholm"));
        assertTrue(result.contains("Arlanda"));
        assertTrue(result.contains("Ase"));
        assertTrue(result.contains("Hudiksvall"));
        assertTrue(result.contains("Kiruna"));
        assertTrue(result.contains("Karlskrona"));
        assertTrue(result.contains("Leksand"));
        assertTrue(result.contains("Luleå"));
        assertTrue(result.contains("Östersund"));
        assertTrue(result.contains("Örnsköldsvik"));
        assertTrue(result.contains("Vara"));
        assertTrue(result.contains("Vilebo"));
    }
    
    public void testSelectByDate() {
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        Set<String> result = new HashSet<String>();
        q.fetch("source", xpr.attribute("_bdb/source:PLC"));
        Expression e1 = xpr.eq(xpr.attribute("what/date"), 
                                                       xpr.literal("20120315"));
        q.setFilter(e1);
        AttributeResult rs = bdb.execute(q);
        assertEquals(8, rs.size());
        while (rs.next()) {
            result.add(rs.getString("source"));
        }
        rs.close();
        assertTrue(result.contains("Legionowo"));
        assertTrue(result.contains("Poznań"));
        assertTrue(result.contains("Gdańsk"));
        assertTrue(result.contains("Świdwin"));
        assertTrue(result.contains("Brzuchania"));
        assertTrue(result.contains("Rzeszów"));
        assertTrue(result.contains("Ramza"));
        assertTrue(result.contains("Pastewnik"));
    }
    
    public void testSelectByDateAndTime() {
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        Set<String> result = new HashSet<String>();
        q.fetch("source", xpr.attribute("_bdb/source:PLC"));
        Expression e1 = xpr.eq(xpr.attribute("what/date"), 
                                                       xpr.literal("20120315"));
        Expression e2 = xpr.ge(xpr.attribute("what/time"), 
                                                         xpr.literal("074010"));
        Expression e3 = xpr.and(e1, e2);
        Expression e4 = xpr.le(xpr.attribute("what/time"),
                                                         xpr.literal("074020"));
        Expression e5 = xpr.and(e3, e4);
        q.setFilter(e5);
        AttributeResult rs = bdb.execute(q);
        assertEquals(3, rs.size());
        while (rs.next()) {
            result.add(rs.getString("source"));
        } 
        rs.close();
        assertTrue(result.contains("Rzeszów"));
        assertTrue(result.contains("Ramza"));
        assertTrue(result.contains("Świdwin"));
    }
    
    public void testComplexSelect() {
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        Set<String> result = new HashSet<String>();
        q.fetch("source", xpr.attribute("_bdb/source:PLC"));
        List<Expression> xprs = new ArrayList<Expression>();
        Expression e1 = xpr.eq(xpr.attribute("what/object"), 
                                                           xpr.literal("PVOL"));
        Expression e2 = xpr.eq(xpr.attribute("what/date"), 
                                                       xpr.literal("20120315"));
        Expression e3 = xpr.ge(xpr.attribute("what/time"), 
                                                         xpr.literal("074010"));
        Expression e4 = xpr.le(xpr.attribute("what/time"),
                                                         xpr.literal("074020"));
        xprs.add(e1);
        xprs.add(e2);
        xprs.add(e3);
        xprs.add(e4);
        Expression ex = xpr.and(xprs);
        q.setFilter(ex);
        AttributeResult rs = bdb.execute(q);
        assertEquals(3, rs.size());
        while (rs.next()) {
            result.add(rs.getString("source"));
        } 
        rs.close();
        assertTrue(result.contains("Rzeszów"));
        assertTrue(result.contains("Ramza"));
        assertTrue(result.contains("Świdwin"));
    }
    
    public void testFetchFileByUuid() {
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        q.fetch("uuid", xpr.attribute("_bdb/uuid"));
        q.setFilter(xpr.eq(xpr.attribute("_bdb/source:PLC"), 
                                                    xpr.literal("Legionowo")));
        AttributeResult rs = bdb.execute(q);
        assertEquals(1, rs.size());
        rs.next();
        String legUuid = rs.getString("uuid");
        rs.close();
        
        FileQuery fq = new FileQuery();
        Expression e = xpr.eq(xpr.attribute("_bdb/uuid"), xpr.literal(legUuid));
        fq.setFilter(e);
        FileResult fr = bdb.execute(fq);
        assertEquals(1, fr.size());
        fr.next();
        FileEntry entry = fr.getFileEntry();
        fr.close();
        assertEquals(legUuid, entry.getUuid().toString());
        Metadata meta = entry.getMetadata();
        assertEquals("WMO:12374", meta.getWhatSource());
        assertEquals("PVOL", meta.getWhatObject());
        assertEquals("20120315", meta.getWhatDate().toString());
        assertEquals("074005", meta.getWhatTime().toString());
    }
    
    public void testFileDownload() throws Exception {
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        q.fetch("uuid", xpr.attribute("_bdb/uuid"));
        q.setFilter(xpr.eq(xpr.attribute("_bdb/source:PLC"), 
                                                    xpr.literal("Legionowo")));
        AttributeResult rs = bdb.execute(q);
        assertEquals(1, rs.size());
        rs.next();
        String legUuid = rs.getString("uuid");
        rs.close();
        
        FileQuery fq = new FileQuery();
        Expression e = xpr.eq(xpr.attribute("_bdb/uuid"), xpr.literal(legUuid));
        fq.setFilter(e);
        FileResult fr = bdb.execute(fq);
        assertEquals(1, fr.size());
        fr.next();
        FileEntry entry = fr.getFileEntry();
        fr.close();
        
        byte buff[] = new byte[entry.getContentSize()];
        entry.getContentStream().read(buff);
        File f = new File("_Z_PVOL_20120315074000_plleg.h5");
        OutputStream os = new FileOutputStream(f);
        os.write(buff);
        os.close();
        assertEquals(entry.getContentSize(), f.length());
        assertTrue(f.delete());
    }
    
}
