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

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.springframework.ui.Model;

import eu.baltrad.beast.qc.AnomalyDetector;
import eu.baltrad.beast.qc.AnomalyException;
import eu.baltrad.beast.qc.IAnomalyDetectorManager;

/**
 * @author Anders Henja
 */
public class AnomalyDetectorControllerTest extends TestCase {
  interface MethodMock {
    public AnomalyDetector createDetector(String name, String description);
  }
  private AnomalyDetectorController classUnderTest = null;
  private MockControl managerControl = null;
  private IAnomalyDetectorManager manager = null;
  private MockControl modelControl = null;
  private Model model = null;
  private MockControl methodControl = null;
  private MethodMock method = null;
  
  public void setUp() throws Exception {
    super.setUp();
    managerControl = MockControl.createControl(IAnomalyDetectorManager.class);
    manager = (IAnomalyDetectorManager)managerControl.getMock();
    modelControl = MockControl.createControl(Model.class);
    model = (Model)modelControl.getMock();
    methodControl = MockControl.createControl(MethodMock.class);
    method = (MethodMock)methodControl.getMock();
    
    classUnderTest = new AnomalyDetectorController();
    classUnderTest.setManager(manager);
  }
  
  public void tearDown() throws Exception {
    super.tearDown();
    managerControl = null;
    manager = null;
    methodControl = null;
    method = null;
    classUnderTest = null;
  }
  
  protected void replay() throws Exception {
    managerControl.replay();
    modelControl.replay();
    methodControl.replay();
  }

  protected void verify() throws Exception {
    managerControl.verify();
    modelControl.verify();
    methodControl.verify();
  }

  public void testShowAnomalyDetectors() throws Exception {
    List<AnomalyDetector> detectors = new ArrayList<AnomalyDetector>();
    manager.list();
    managerControl.setReturnValue(detectors);
    model.addAttribute("emessage", null);
    modelControl.setReturnValue(null);
    model.addAttribute("anomaly_detectors", detectors);
    modelControl.setReturnValue(null);
    
    replay();
    
    String result = classUnderTest.showAnomalyDetectors(model, null);
    
    verify();
    assertEquals("anomaly_detectors", result);
  }
  
  public void testShowAnomalyDetectors_withEmessage() throws Exception {
    List<AnomalyDetector> detectors = new ArrayList<AnomalyDetector>();
    manager.list();
    managerControl.setReturnValue(detectors);
    model.addAttribute("emessage", "a message");
    modelControl.setReturnValue(null);
    model.addAttribute("anomaly_detectors", detectors);
    modelControl.setReturnValue(null);
    
    replay();
    
    String result = classUnderTest.showAnomalyDetectors(model, "a message");
    
    verify();
    assertEquals("anomaly_detectors", result);
  }
  
  public void testCreateAnomalyDetector_show() throws Exception {
    replay();
    
    String result = classUnderTest.createAnomalyDetector(model, null, null, null);
    
    verify();
    assertEquals("anomaly_detector_create", result);
  }

  public void testCreateAnomalyDetector_create() throws Exception {
    AnomalyDetector detector = new AnomalyDetector();
    
    method.createDetector("aname", "a description");
    methodControl.setReturnValue(detector);
    manager.add(detector);
    
    classUnderTest = new AnomalyDetectorController() {
      protected AnomalyDetector createDetector(String name, String description) {
        return method.createDetector(name, description);
      }
    };
    classUnderTest.setManager(manager);
    
    replay();
    
    String result = classUnderTest.createAnomalyDetector(model, "aname", "a description", "Add");
    
    verify();
    assertEquals("redirect:anomaly_detectors.htm", result);
  }

  public void testCreateAnomalyDetector_unknownOperation() throws Exception {
    replay();
    
    String result = classUnderTest.createAnomalyDetector(model, "aname", "a description", "Unknown");
    
    verify();
    assertEquals("anomaly_detector_create", result);
  }
  
  public void testCreateAnomalyDetector_failedAdd() throws Exception {
    AnomalyDetector detector = new AnomalyDetector();
    
    method.createDetector("aname", "a description");
    methodControl.setReturnValue(detector);
    manager.add(detector);
    managerControl.setThrowable(new AnomalyException("bummer"));
    model.addAttribute("name", "aname");
    modelControl.setReturnValue(null);
    model.addAttribute("description", "a description");
    modelControl.setReturnValue(null);
    model.addAttribute("emessage", "Failed to register anomaly detector: bummer");
    modelControl.setReturnValue(null);
    
    classUnderTest = new AnomalyDetectorController() {
      protected AnomalyDetector createDetector(String name, String description) {
        return method.createDetector(name, description);
      }
    };
    classUnderTest.setManager(manager);
    
    replay();
    
    String result = classUnderTest.createAnomalyDetector(model, "aname", "a description", "Add");
    
    verify();
    assertEquals("anomaly_detector_create", result);
  }

  public void testShowAnomalyDetector() throws Exception {
    AnomalyDetector detector = new AnomalyDetector();
    detector.setName("aname");
    detector.setDescription("a description");
    
    manager.get("aname");
    managerControl.setReturnValue(detector);
    model.addAttribute("name", "aname");
    modelControl.setReturnValue(null);
    model.addAttribute("description", "a description");
    modelControl.setReturnValue(null);
    
    replay();
    
    String result = classUnderTest.showAnomalyDetector(model, "aname");
    
    verify();
    assertEquals("anomaly_detector_show", result);
  }

  public void testShowAnomalyDetector_noSuchDetector() throws Exception {
    List<AnomalyDetector> detectors = new ArrayList<AnomalyDetector>();
    manager.get("aname");
    managerControl.setThrowable(new AnomalyException("bad"));
    manager.list();
    managerControl.setReturnValue(detectors);
    model.addAttribute("anomaly_detectors", detectors);
    modelControl.setReturnValue(null);
    model.addAttribute("emessage", "Failed to locate anomaly detector 'aname': bad");
    modelControl.setReturnValue(null);
    
    replay();
    
    String result = classUnderTest.showAnomalyDetector(model, "aname");
    
    verify();
    assertEquals("anomaly_detectors", result);
  }

  public void testModifyAnomalyDetector_delete() throws Exception {
    manager.remove("aname");
    
    replay();
    
    String result = classUnderTest.modifyAnomalyDetector(model, "aname", "a description", "Delete");
    
    verify();
    assertEquals("redirect:anomaly_detectors.htm", result);
  }
  
  public void testModifyAnomalyDetector_delete_failure() throws Exception {
    List<AnomalyDetector> detectors = new ArrayList<AnomalyDetector>();
    manager.remove("aname");
    managerControl.setThrowable(new AnomalyException("bad"));
    manager.list();
    managerControl.setReturnValue(detectors);
    
    model.addAttribute("anomaly_detectors", detectors);
    modelControl.setReturnValue(null);
    model.addAttribute("emessage", "Failed to delete 'aname': bad");
    modelControl.setReturnValue(null);
    
    replay();
    
    String result = classUnderTest.modifyAnomalyDetector(model, "aname", "a description", "Delete");
    
    verify();
    assertEquals("anomaly_detectors", result);
  }

  public void testModifyAnomalyDetector_modify() throws Exception {
    AnomalyDetector detector = new AnomalyDetector();
    
    method.createDetector("aname", "a description");
    methodControl.setReturnValue(detector);
    manager.update(detector);
    
    classUnderTest = new AnomalyDetectorController() {
      protected AnomalyDetector createDetector(String name, String description) {
        return method.createDetector(name, description);
      }
    };
    classUnderTest.setManager(manager);
    
    replay();
    
    String result = classUnderTest.modifyAnomalyDetector(model, "aname", "a description", "Modify");
    
    verify();
    assertEquals("redirect:anomaly_detectors.htm", result);
  }

  public void testModifyAnomalyDetector_modify_failed() throws Exception {
    AnomalyDetector detector = new AnomalyDetector();
    method.createDetector("aname", "a description");
    methodControl.setReturnValue(detector);
    manager.update(detector);
    managerControl.setThrowable(new AnomalyException("bad"));
    
    model.addAttribute("name", "aname");
    modelControl.setReturnValue(null);
    model.addAttribute("description", "a description");
    modelControl.setReturnValue(null);
    model.addAttribute("emessage", "Failed to update anomaly detector 'aname': bad");
    modelControl.setReturnValue(null);
    
    classUnderTest = new AnomalyDetectorController() {
      protected AnomalyDetector createDetector(String name, String description) {
        return method.createDetector(name, description);
      }
    };
    classUnderTest.setManager(manager);
    
    replay();
    
    String result = classUnderTest.modifyAnomalyDetector(model, "aname", "a description", "Modify");
    
    verify();
    assertEquals("anomaly_detector_show", result);
  }

  
  public void testCreateDetector() throws Exception {
    AnomalyDetector result = classUnderTest.createDetector("aname", "a description");
    assertEquals("aname", result.getName());
    assertEquals("a description", result.getDescription());
  }
}
