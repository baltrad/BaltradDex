/* --------------------------------------------------------------------
Copyright (C) 2009-2014 Swedish Meteorological and Hydrological Institute, SMHI,

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

package eu.baltrad.dex.radar.controller;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.internal.matchers.ArrayEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import eu.baltrad.bdb.db.Database;
import eu.baltrad.bdb.db.DatabaseError;
import eu.baltrad.bdb.db.SourceManager;
import eu.baltrad.bdb.oh5.Source;
import eu.baltrad.dex.radar.manager.impl.RadarManager;
import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.util.MessageResourceUtil;

/**
 * @author Anders Henja
 */
public class SaveRadarControllerTest extends EasyMockSupport {
  interface MockMethods {
    Radar createRadar(String countryid, String cccc, int org, String plc, String rad, String wmo);
  };
  private SaveRadarController classUnderTest = null;
  private Database database = null;
  private MessageResourceUtil messages = null;
  private RadarManager radarManager = null;
  private MockMethods methods = null;

  @Before
  public void setUp() {
    database = createMock(Database.class);
    messages = createMock(MessageResourceUtil.class);
    methods = createMock(MockMethods.class);
    radarManager = createMock(RadarManager.class);
    classUnderTest = new SaveRadarController() {
      @Override
      protected Radar createRadar(String countryid, String cccc, int org, String plc, String rad, String wmo) {
        return methods.createRadar(countryid, cccc, org, plc, rad, wmo);
      }
    };
    classUnderTest.setRestfulDatabase(database);
    classUnderTest.setMessages(messages);
    classUnderTest.setRadarManager(radarManager);
  }
  
  @After
  public void tearDown() {
    database = null;
    messages = null;
    radarManager = null;
    classUnderTest = null;
  }
  
  @Test
  public void processSubmit() throws Exception {
    ModelMap modelMap = createMock(ModelMap.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    SourceManager sourceManager = createMock(SourceManager.class);
    Radar radar = new Radar("SE", "ESWI", 82, "Ängelholm", "SE50", "02606");
    
    Source seSource = new Source("se", null);
    seSource.put("CCCC", "ESWI");
    seSource.put("ORG", "82");
    Source seangSource = new Source("seang", "se");
    seangSource.put("PLC", "Ängelholm");
    seangSource.put("RAD", "SE50");
    seangSource.put("WMO", "02606");
    
    expect(request.getParameter("center_id")).andReturn("se");
    expect(request.getParameter("radar_id")).andReturn("seang");
    expect(database.getSourceManager()).andReturn(sourceManager);
    expect(sourceManager.getSource("se")).andReturn(seSource);
    expect(sourceManager.getSource("seang")).andReturn(seangSource);
    expect(methods.createRadar("SE", "ESWI", 82, "Ängelholm", "SE50", "02606")).andReturn(radar);
    expect(radarManager.store(radar)).andReturn(1);
    expect(messages.getMessage(eq("saveradar.success"), aryEq(new Object[] {"Ängelholm","SE50", "02606"}))).andReturn("yupp");
    expect(modelMap.addAttribute("radar_save_success", "yupp")).andReturn(null);
    
    replayAll();
    String view = classUnderTest.processSubmit(request, modelMap);
    verifyAll();
    assertEquals("radars_save_status", view);
  }
  
  @Test
  public void loadCenters() throws Exception {
    ModelMap modelMap = createMock(ModelMap.class);
    SourceManager sourceManager = createMock(SourceManager.class);

    List<Source> parentSources = new ArrayList<Source>();
    Source bySource = new Source("by", null);
    bySource.put("CCCC", "UMMS");
    bySource.put("ORG", "226");
    Source seSource = new Source("se", null); 
    seSource.put("CCCC", "ESWI");
    seSource.put("ORG", "82");
    Source plSource = new Source("pl", null); 
    plSource.put("CCCC", "SOWR");
    plSource.put("ORG", "220");
    parentSources.add(bySource);
    parentSources.add(seSource);
    parentSources.add(plSource);
    
    expect(database.getSourceManager()).andReturn(sourceManager);
    expect(sourceManager.getParentSources()).andReturn(parentSources);

    replayAll();
    List<KeyValuePair> result = classUnderTest.loadCenters(modelMap);
    verifyAll();
    assertEquals(3, result.size());
    assertEquals("by", result.get(0).getKey());
    assertEquals("BY - UMMS - 226", result.get(0).getValue());
    assertEquals("se", result.get(1).getKey());
    assertEquals("SE - ESWI - 82", result.get(1).getValue());
    assertEquals("pl", result.get(2).getKey());
    assertEquals("PL - SOWR - 220", result.get(2).getValue());
  }
  
  @Test
  public void loadCenters_failedToLoad() throws Exception {
    ModelMap modelMap = createMock(ModelMap.class);
    SourceManager sourceManager = createMock(SourceManager.class);
    
    expect(database.getSourceManager()).andReturn(sourceManager);
    expect(sourceManager.getParentSources()).andThrow(new DatabaseError());
    expect(messages.getMessage("saveradar.odim_load_failure")).andReturn("an error");
    expect(modelMap.addAttribute("odim_load_error", "an error")).andReturn(null);
    
    replayAll();
    List<KeyValuePair> result = classUnderTest.loadCenters(modelMap);
    verifyAll();
    assertEquals(0, result.size());
  }
  
  @Test
  public void loadRadars() throws Exception {
    ModelMap modelMap = createMock(ModelMap.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    SourceManager sourceManager = createMock(SourceManager.class);
    List<Source> sources = new ArrayList<Source>();
    Source sekirSource = new Source("sekir", "SE");
    sekirSource.put("PLC", "Kiruna");
    sekirSource.put("RAD", "SE40");
    sekirSource.put("WMO", "02032");
    Source selulSource = new Source("selul", "SE");
    selulSource.put("PLC", "Lule\\xc3\\xa5");
    selulSource.put("RAD", "SE41");
    selulSource.put("WMO", "02092");
    Source searlSource = new Source("searl", "SE");
    searlSource.put("PLC", "Arlanda");
    searlSource.put("RAD", "SE46");
    searlSource.put("WMO", "02451");
    sources.add(sekirSource);
    sources.add(selulSource);
    sources.add(searlSource);
    
    expect(request.getParameter("center_id")).andReturn("se");
    expect(database.getSourceManager()).andReturn(sourceManager);
    expect(sourceManager.getSourcesWithParent("se")).andReturn(sources);
    
    replayAll();
    List<KeyValuePair> result = classUnderTest.loadRadars(request, modelMap);
    verifyAll();
    assertEquals(3, result.size());
    assertEquals("sekir", result.get(0).getKey());
    assertEquals("Kiruna - SE40 - 02032", result.get(0).getValue());
    assertEquals("selul", result.get(1).getKey());
    assertEquals("Lule\\xc3\\xa5 - SE41 - 02092", result.get(1).getValue());
    assertEquals("searl", result.get(2).getKey());
    assertEquals("Arlanda - SE46 - 02451", result.get(2).getValue());
  }
  
  @Test
  public void loadRadars_failedToLoad() throws Exception {
    ModelMap modelMap = createMock(ModelMap.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    SourceManager sourceManager = createMock(SourceManager.class);
    
    expect(request.getParameter("center_id")).andReturn("se");
    expect(database.getSourceManager()).andReturn(sourceManager);
    expect(sourceManager.getSourcesWithParent("se")).andThrow(new DatabaseError());
    expect(messages.getMessage("saveradar.odim_load_failure")).andReturn("an error");
    expect(modelMap.addAttribute("odim_load_error", "an error")).andReturn(null);
    
    replayAll();
    List<KeyValuePair> result = classUnderTest.loadRadars(request, modelMap);
    verifyAll();
    assertEquals(0, result.size());
  }  

  @Test
  public void loadRadars_emptyCenterId() throws Exception {
    ModelMap modelMap = createMock(ModelMap.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    
    expect(request.getParameter("center_id")).andReturn(null);
    
    replayAll();
    List<KeyValuePair> result = classUnderTest.loadRadars(request, modelMap);
    verifyAll();
    assertEquals(0, result.size());
  }  
  
}
