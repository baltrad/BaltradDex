/* --------------------------------------------------------------------
Copyright (C) 2009-2011 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the beast-ui package.

The beast-ui package is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The beast-ui package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the beast-ui package library.  If not, see <http://www.gnu.org/licenses/>.
------------------------------------------------------------------------*/
package eu.baltrad.beastui.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import eu.baltrad.beast.qc.AnomalyDetector;
import eu.baltrad.beast.qc.AnomalyException;
import eu.baltrad.beast.qc.IAnomalyDetectorManager;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 * @author Anders Henja
 */
public class AnomalyDetectorControllerTest extends EasyMockSupport {
  interface MethodMock {
    public AnomalyDetector createDetector(String name, String description);
  }
  private AnomalyDetectorController classUnderTest = null;
  private IAnomalyDetectorManager manager = null;
  private Model model = null;
  private MethodMock method = null;

  @Before
  public void setUp() throws Exception {
    manager = createMock(IAnomalyDetectorManager.class);
    model = createMock(Model.class);
    method = createMock(MethodMock.class);
    
    classUnderTest = new AnomalyDetectorController();
    classUnderTest.setManager(manager);
  }
  
  @After
  public void tearDown() throws Exception {
    manager = null;
    method = null;
    classUnderTest = null;
  }

  @Test
  public void testShowAnomalyDetectors() throws Exception {
    List<AnomalyDetector> detectors = new ArrayList<AnomalyDetector>();
    expect(manager.list()).andReturn(detectors);
    expect(model.addAttribute("emessage", null)).andReturn(null);
    expect(model.addAttribute("anomaly_detectors", detectors)).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.showAnomalyDetectors(model, null);
    
    verifyAll();
    assertEquals("anomaly_detectors", result);
  }
  
  @Test
  public void testShowAnomalyDetectors_withEmessage() throws Exception {
    List<AnomalyDetector> detectors = new ArrayList<AnomalyDetector>();
    expect(manager.list()).andReturn(detectors);
    expect(model.addAttribute("emessage", "a message")).andReturn(null);
    expect(model.addAttribute("anomaly_detectors", detectors)).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.showAnomalyDetectors(model, "a message");
    
    verifyAll();
    assertEquals("anomaly_detectors", result);
  }
  
  @Test
  public void testCreateAnomalyDetector_show() throws Exception {
    replayAll();
    
    String result = classUnderTest.createAnomalyDetector(model, null, null, null);
    
    verifyAll();
    assertEquals("anomaly_detector_create", result);
  }

  @Test
  public void testCreateAnomalyDetector_create() throws Exception {
    AnomalyDetector detector = new AnomalyDetector();
    
    expect(method.createDetector("aname", "a description")).andReturn(detector);
    manager.add(detector);
    
    classUnderTest = new AnomalyDetectorController() {
      protected AnomalyDetector createDetector(String name, String description) {
        return method.createDetector(name, description);
      }
    };
    classUnderTest.setManager(manager);
    
    replayAll();
    
    String result = classUnderTest.createAnomalyDetector(model, "aname", "a description", "Add");
    
    verifyAll();
    assertEquals("redirect:anomaly_detectors.htm", result);
  }

  @Test
  public void testCreateAnomalyDetector_unknownOperation() throws Exception {
    replayAll();
    
    String result = classUnderTest.createAnomalyDetector(model, "aname", "a description", "Unknown");
    
    verifyAll();
    assertEquals("anomaly_detector_create", result);
  }
  
  @Test
  public void testCreateAnomalyDetector_failedAdd() throws Exception {
    AnomalyDetector detector = new AnomalyDetector();
    
    expect(method.createDetector("aname", "a description")).andReturn(detector);
    manager.add(detector);
    expectLastCall().andThrow(new AnomalyException("bummer"));
    expect(model.addAttribute("name", "aname")).andReturn(null);
    expect(model.addAttribute("description", "a description")).andReturn(null);
    expect(model.addAttribute("emessage", "Failed to register anomaly detector: bummer")).andReturn(null);
    
    classUnderTest = new AnomalyDetectorController() {
      protected AnomalyDetector createDetector(String name, String description) {
        return method.createDetector(name, description);
      }
    };
    classUnderTest.setManager(manager);
    
    replayAll();
    
    String result = classUnderTest.createAnomalyDetector(model, "aname", "a description", "Add");
    
    verifyAll();
    assertEquals("anomaly_detector_create", result);
  }

  @Test
  public void testShowAnomalyDetector() throws Exception {
    AnomalyDetector detector = new AnomalyDetector();
    detector.setName("aname");
    detector.setDescription("a description");
    
    expect(manager.get("aname")).andReturn(detector);
    expect(model.addAttribute("name", "aname")).andReturn(null);
    expect(model.addAttribute("description", "a description")).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.showAnomalyDetector(model, "aname");
    
    verifyAll();
    assertEquals("anomaly_detector_show", result);
  }

  @Test
  public void testShowAnomalyDetector_noSuchDetector() throws Exception {
    List<AnomalyDetector> detectors = new ArrayList<AnomalyDetector>();
    expect(manager.get("aname")).andThrow(new AnomalyException("bad"));
    expect(manager.list()).andReturn(detectors);
    expect(model.addAttribute("anomaly_detectors", detectors)).andReturn(null);
    expect(model.addAttribute("emessage", "Failed to locate anomaly detector 'aname': bad")).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.showAnomalyDetector(model, "aname");
    
    verifyAll();
    assertEquals("anomaly_detectors", result);
  }

  @Test
  public void testModifyAnomalyDetector_delete() throws Exception {
    manager.remove("aname");
    
    replayAll();
    
    String result = classUnderTest.modifyAnomalyDetector(model, "aname", "a description", "Delete");
    
    verifyAll();
    assertEquals("redirect:anomaly_detectors.htm", result);
  }
  
  @Test
  public void testModifyAnomalyDetector_delete_failure() throws Exception {
    List<AnomalyDetector> detectors = new ArrayList<AnomalyDetector>();
    manager.remove("aname");
    expectLastCall().andThrow(new AnomalyException("bad"));
    expect(manager.list()).andReturn(detectors);
    
    expect(model.addAttribute("anomaly_detectors", detectors)).andReturn(null);
    expect(model.addAttribute("emessage", "Failed to delete 'aname': bad")).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.modifyAnomalyDetector(model, "aname", "a description", "Delete");
    
    verifyAll();
    assertEquals("anomaly_detectors", result);
  }

  @Test
  public void testModifyAnomalyDetector_modify() throws Exception {
    AnomalyDetector detector = new AnomalyDetector();
    
    expect(method.createDetector("aname", "a description")).andReturn(detector);
    manager.update(detector);
    
    classUnderTest = new AnomalyDetectorController() {
      protected AnomalyDetector createDetector(String name, String description) {
        return method.createDetector(name, description);
      }
    };
    classUnderTest.setManager(manager);
    
    replayAll();
    
    String result = classUnderTest.modifyAnomalyDetector(model, "aname", "a description", "Modify");
    
    verifyAll();
    assertEquals("redirect:anomaly_detectors.htm", result);
  }

  @Test
  public void testModifyAnomalyDetector_modify_failed() throws Exception {
    AnomalyDetector detector = new AnomalyDetector();
    expect(method.createDetector("aname", "a description")).andReturn(detector);
    manager.update(detector);
    expectLastCall().andThrow(new AnomalyException("bad"));
    
    expect(model.addAttribute("name", "aname")).andReturn(null);
    expect(model.addAttribute("description", "a description")).andReturn(null);
    expect(model.addAttribute("emessage", "Failed to update anomaly detector 'aname': bad")).andReturn(null);
    
    classUnderTest = new AnomalyDetectorController() {
      protected AnomalyDetector createDetector(String name, String description) {
        return method.createDetector(name, description);
      }
    };
    classUnderTest.setManager(manager);
    
    replayAll();
    
    String result = classUnderTest.modifyAnomalyDetector(model, "aname", "a description", "Modify");
    
    verifyAll();
    assertEquals("anomaly_detector_show", result);
  }

  @Test
  public void testCreateDetector() throws Exception {
    AnomalyDetector result = classUnderTest.createDetector("aname", "a description");
    assertEquals("aname", result.getName());
    assertEquals("a description", result.getDescription());
  }
}
