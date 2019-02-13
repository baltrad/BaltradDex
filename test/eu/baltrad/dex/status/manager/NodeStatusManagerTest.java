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

package eu.baltrad.dex.status.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.dex.status.manager.impl.NodeStatusManager;

/**
 * @author Anders Henja
 */
public class NodeStatusManagerTest {
  private NodeStatusManager classUnderTest;
  
  @Before
  public void setUp() throws Exception {
    classUnderTest = new NodeStatusManager();
  }
  
  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
  }
  
  @Test
  public void getRuntimeNodeNames() {
    classUnderTest.setRuntimeNodeStatus("a", 1);
    classUnderTest.setRuntimeNodeStatus("b", 2);
    
    Set<String> result = classUnderTest.getRuntimeNodeNames();
    assertTrue(result.contains("a"));
    assertTrue(result.contains("b"));
    assertEquals(2, result.size());
  }
  
  @Test
  public void getRuntimeNodeStatus() {
    classUnderTest.setRuntimeNodeStatus("a", 1);
    classUnderTest.setRuntimeNodeStatus("b", 2);
    
    assertEquals(1, classUnderTest.getRuntimeNodeStatus("a"));
    assertEquals(2, classUnderTest.getRuntimeNodeStatus("b"));
  }

  @Test(expected=RuntimeException.class)
  public void getRuntimeNodeStatus_exceptionOnFail() {
    classUnderTest.setRuntimeNodeStatus("a", 1);
    classUnderTest.setRuntimeNodeStatus("b", 2);
    
    classUnderTest.getRuntimeNodeStatus("c");
  }

  @Test
  public void getRuntimeNodeDate() {
    Date prev = Calendar.getInstance().getTime();
    classUnderTest.setRuntimeNodeStatus("a", 1);
    try { Thread.sleep(10); } catch (Exception e) {}
    Date mid = Calendar.getInstance().getTime();
    try { Thread.sleep(10); } catch (Exception e) {}
    classUnderTest.setRuntimeNodeStatus("b", 2);
    Date now = Calendar.getInstance().getTime();
    
    Date aDate = classUnderTest.getRuntimeNodeDate("a");
    Date bDate = classUnderTest.getRuntimeNodeDate("b");
    assertTrue(aDate.compareTo(prev) >= 0);
    assertTrue(aDate.compareTo(mid) < 0);
    assertTrue(aDate.compareTo(bDate) < 0);
    assertTrue(bDate.compareTo(mid) > 0);
    assertTrue(bDate.compareTo(now) <= 0);
  }

  @Test(expected=RuntimeException.class)
  public void getRuntimeNodeDate_exceptionOnFail() {
    classUnderTest.setRuntimeNodeStatus("a", 1);
    classUnderTest.setRuntimeNodeStatus("b", 2);
    
    classUnderTest.getRuntimeNodeDate("c");
  }

}
