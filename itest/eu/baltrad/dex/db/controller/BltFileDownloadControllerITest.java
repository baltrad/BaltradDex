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

package eu.baltrad.dex.db.controller;

import java.util.UUID;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;

import org.springframework.context.support.AbstractApplicationContext;

import eu.baltrad.bdb.FileCatalog;
import eu.baltrad.bdb.BasicFileCatalog;
import eu.baltrad.bdb.db.AttributeQuery;
import eu.baltrad.bdb.db.AttributeResult;
import eu.baltrad.bdb.expr.ExpressionFactory;

import eu.baltrad.dex.db.itest.DexDBITestHelper;

/**
 * Integration test for baltrad-db.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0.7
 * @since 1.0.7
 */
public class BltFileDownloadControllerITest extends TestCase {
    
    private AbstractApplicationContext context = null;
    private FileCatalog catalog;
    
    @Override
    public void setUp() {
        context = DexDBITestHelper.loadContext(this);
        catalog = (BasicFileCatalog) context.getBean("catalog");
        assertNotNull(catalog);
    }
    
    @Override
    public void tearDown() {
        catalog = null;
        context.close();
    } 
    
    public void testFileDownload() throws Exception {
        AttributeQuery q = new AttributeQuery();
        ExpressionFactory xpr = new ExpressionFactory();
        q.fetch("uuid", xpr.attribute("_bdb/uuid"));
        q.setFilter(xpr.eq(xpr.attribute("_bdb/source:PLC"), 
                                                    xpr.literal("Legionowo")));
        AttributeResult r = catalog.getDatabase().execute(q);
        assertEquals(1, r.size());
        r.next();
        UUID uuid = UUID.fromString(r.getString("uuid"));
        r.close();
        File fi = catalog.getLocalPathForUuid(uuid);
        assertNotNull(fi);
        File fo = new File("_Z_PVOL_20120315074000_plleg.h5");
        assertNotNull(fo);
        byte[] buff = new byte[1024];
        FileInputStream fis = new FileInputStream(fi);
        FileOutputStream fos = new FileOutputStream(fo);
        int len;
        while ((len = fis.read(buff)) > 0) {
            fos.write(buff, 0, len);
        }
        fis.close();
        fos.close();
        assertEquals(fi.length(), fo.length());
        assertTrue(fo.delete());
    }
}

