/* --------------------------------------------------------------------
Copyright (C) 2009-2016 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the BaltradDex package.

The BaltradDex package is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The BaltradDex package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex package library.  If not, see <http://www.gnu.org/licenses/>.
------------------------------------------------------------------------*/

package eu.baltrad.beastui.web.controller;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import eu.baltrad.bdb.db.Database;
import eu.baltrad.bdb.db.DatabaseError;
import eu.baltrad.bdb.expr.Expression;
import eu.baltrad.bdb.oh5.Metadata;
import eu.baltrad.bdb.oh5.MetadataMatcher;
import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.router.IRouterManager;

import static org.easymock.EasyMock.*;

/**
 * @author Anders Henja
 */
public class DistributionRoutesControllerTest extends EasyMockSupport {
  private DistributionRoutesController classUnderTest = null;
  private Database bdb = null;
  private IRouterManager routerManager = null;
  private MetadataMatcher matcher = null;
  private ObjectMapper jsonMapper = null;
  
  @Before
  public void setUp() throws Exception {
    bdb = createMock(Database.class);
    routerManager = createMock(IRouterManager.class);
    matcher = createMock(MetadataMatcher.class);
    jsonMapper = createMock(ObjectMapper.class);
    classUnderTest = new DistributionRoutesController();
    classUnderTest.setDatabase(bdb);
    classUnderTest.setManager(routerManager);
    classUnderTest.setMetadataMatcher(matcher);
    classUnderTest.setObjectMapper(jsonMapper);
  }
  
  @After
  public void tearDown() throws Exception {
    bdb = null;
    routerManager = null;
    matcher = null;
    jsonMapper = null;
    classUnderTest = null;
  }
  
  @Test
  public void testRoute() throws Exception {
    Model model = createMock(Model.class);
    MultipartFile multipartFile = createMock(MultipartFile.class);
    IFilter filter = createMock(IFilter.class);
    Expression xpr = createMock(Expression.class);
    InputStream inputStream = createMock(InputStream.class);
    Metadata metadata = createMock(Metadata.class);
    
    expect(jsonMapper.readValue("{}", IFilter.class)).andReturn(filter);
    expect(filter.getExpression()).andReturn(xpr);
    expect(multipartFile.getInputStream()).andReturn(inputStream);
    expect(bdb.queryFileMetadata(inputStream)).andReturn(metadata);
    expect(matcher.match(metadata, xpr)).andReturn(true);
    
    replayAll();
    
    String result = classUnderTest.testRoute(model, "{}", null, null, null, multipartFile);
    
    verifyAll();
    assertEquals("OK", result);
  }

  @Test
  public void testRoute_fail() throws Exception {
    Model model = createMock(Model.class);
    MultipartFile multipartFile = createMock(MultipartFile.class);
    IFilter filter = createMock(IFilter.class);
    Expression xpr = createMock(Expression.class);
    InputStream inputStream = createMock(InputStream.class);
    Metadata metadata = createMock(Metadata.class);
    
    expect(jsonMapper.readValue("{}", IFilter.class)).andReturn(filter);
    expect(filter.getExpression()).andReturn(xpr);
    expect(multipartFile.getInputStream()).andReturn(inputStream);
    expect(bdb.queryFileMetadata(inputStream)).andReturn(metadata);
    expect(matcher.match(metadata, xpr)).andReturn(false);
    
    replayAll();
    
    String result = classUnderTest.testRoute(model, "{}", null, null, null, multipartFile);
    
    verifyAll();
    assertEquals("FAIL", result);
  }

  @Test
  public void testRoute_bdbException() throws Exception {
    Model model = createMock(Model.class);
    MultipartFile multipartFile = createMock(MultipartFile.class);
    IFilter filter = createMock(IFilter.class);
    Expression xpr = createMock(Expression.class);
    InputStream inputStream = createMock(InputStream.class);
    
    expect(jsonMapper.readValue("{}", IFilter.class)).andReturn(filter);
    expect(filter.getExpression()).andReturn(xpr);
    expect(multipartFile.getInputStream()).andReturn(inputStream);
    expect(bdb.queryFileMetadata(inputStream)).andThrow(new DatabaseError("500"));
    
    replayAll();
    
    String result = classUnderTest.testRoute(model, "{}", null, null, null, multipartFile);
    
    verifyAll();
    assertEquals("FAIL", result);
  }
  
}
