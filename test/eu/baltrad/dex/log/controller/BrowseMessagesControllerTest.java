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

package eu.baltrad.dex.log.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import eu.baltrad.dex.log.manager.impl.LogManager;
import eu.baltrad.dex.log.model.impl.LogEntry;
import eu.baltrad.dex.log.model.impl.LogParameter;
import static org.easymock.EasyMock.*;

/**
 * @author Anders Henja
 */
public class BrowseMessagesControllerTest extends EasyMockSupport {
  private interface MethodMock {
    LogParameter createLogParameter(String logger, String level,
        String startDate, String startHour, String startMinutes,
        String startSeconds, String endDate, String endHour, String endMinutes,
        String endSeconds, String phrase);
    int[] getPages(LogParameter param) throws Exception;
  };
  
  private BrowseMessagesController classUnderTest;
  private LogManager logManager = null;
  private MethodMock methods = null;
  
  @Before
  public void setUp() throws Exception {
    logManager = createMock(LogManager.class);
    methods = createMock(MethodMock.class);
    classUnderTest = new BrowseMessagesController() {
      protected LogParameter createLogParameter(String logger, String level,
          String startDate, String startHour, String startMinutes,
          String startSeconds, String endDate, String endHour, String endMinutes,
          String endSeconds, String phrase) {
        return methods.createLogParameter(logger, level, startDate, startHour, startMinutes, startSeconds, endDate, endHour, endMinutes, endSeconds, phrase);
      }
      protected int[] getPages(LogParameter param) throws Exception {
        return methods.getPages(param);
      }
    };
    classUnderTest.setLogManager(logManager);
  }
  
  @After
  public void tearDown() throws Exception {
    logManager = null;
    classUnderTest = null;
  }
   
  @Test
  public void processSubmit() throws Exception {
    ModelMap map = createMock(ModelMap.class);
    LogParameter logParameter = new LogParameter();
    int[] pages = new int[]{1,2,3};
    List<LogEntry> entries = new ArrayList<LogEntry>();
    expect(methods.createLogParameter(null, null, "2012-12-12", "10", "00", "00", "2012-12-13", "09", "59", "59", null)).andReturn(logParameter);
    expect(logManager.createQuery(logParameter, false)).andReturn("select something from something");
    expect(logManager.load("select something from something", 0, 20)).andReturn(entries);
    expect(methods.getPages(logParameter)).andReturn(pages);
    expect(map.addAttribute("first_page", pages[0])).andReturn(null);
    expect(map.addAttribute("last_page", pages[1])).andReturn(null);
    expect(map.addAttribute("current_page", pages[2])).andReturn(null);
    expect(map.addAttribute("messages", entries)).andReturn(null);
    expect(map.addAttribute("log_parameter", logParameter)).andReturn(null);
    replayAll();
    
    String result = classUnderTest.processSubmit(map, null, null, null, "2012-12-12", "10", "00", "00", "2012-12-13", "09", "59", "59", null);
    
    verifyAll();
    assertEquals("messages_browser", result);
  }
}
