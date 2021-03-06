/* --------------------------------------------------------------------
Copyright (C) 2009-2011 Swedish Meteorological and Hydrological Institute, SMHI,

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

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.baltrad.beast.pgf.IPgfClientHelper;
import eu.baltrad.beast.pgf.QualityControlInformation;
import eu.baltrad.beast.qc.AnomalyDetector;
import eu.baltrad.beast.qc.AnomalyException;
import eu.baltrad.beast.qc.IAnomalyDetectorManager;

/**
 * Controller for managing the anomaly detectors
 * 
 * @author Anders Henja
 */
@Controller
public class AnomalyDetectorController {
  /**
   * The anomaly detector manager
   */
  private IAnomalyDetectorManager manager = null;

  private IPgfClientHelper pgfClient = null;
  
  /**
   * We need a logger here
   */
  private static Logger logger = LogManager.getLogger(AnomalyDetectorController.class);

  /**
   * Default constructor
   */
  public AnomalyDetectorController() {
    
  }
  
  /**
   * @param manager the anomaly detector manager
   */
  @Autowired
  public void setManager(IAnomalyDetectorManager manager) {
    logger.debug("setManager(IAnomalyDetectorManager)");
    this.manager = manager;
  }

  @Autowired
  public void setPgfClientHelper(IPgfClientHelper pgfClient) {
    logger.debug("setPgfClientHelper(IPgfClientHelper)");
    this.pgfClient = pgfClient;
  }

  /**
   * Shows the list of anomaly detectors
   * @param model the model to fill
   * @param emessage a error message if any
   * @return the jsp page that handles the model
   */
  @RequestMapping("/anomaly_detectors.htm")
  public String showAnomalyDetectors(Model model,
      @RequestParam(value="emessage", required=false) String emessage) {
    logger.debug("showAnomalyDetectors(Model)");
    model.addAttribute("emessage", emessage);
    model.addAttribute("anomaly_detectors", manager.list());
    return "anomaly_detectors";
  }
  
  /**
   * Shows the page for creating anomaly detectors
   * @param model the model to fill
   * @param name the name of the anomaly detector
   * @param description the description of the anomaly detector
   * @param op the operation, Add is currently only supported
   * @return the jsp page that handles this model
   */
  @RequestMapping("/anomaly_detector_create.htm")
  public String createAnomalyDetector(Model model,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "submitButton", required = false) String op) {
    logger.debug("createAnomalyDetector(Model,..)");
    if (op != null && op.equals("Add")) {
      try {
        AnomalyDetector detector = createDetector(name, description);
        manager.add(detector);
        return "redirect:anomaly_detectors.htm";
      } catch (AnomalyException e) {
        logger.error("Failed to register anomaly detector", e);
        model.addAttribute("name", name);
        model.addAttribute("description", description);
        model.addAttribute("emessage", "Failed to register anomaly detector: " + e.getMessage());
      }
    } else if (op != null && op.equals("Import")) {
      try {
        List<QualityControlInformation> qcInfo = pgfClient.getQualityControls();
        model.addAttribute("qcs", qcInfo);
        return "anomaly_detector_import";
      } catch (Exception e) {
        logger.error("Failed to import quality control information from PGFs", e);
      }
    }
    return "anomaly_detector_create";
  }
  
  /**
   * Shows the page for displaying one anomaly detector
   * @param model the model for this page
   * @param name the name of the anomaly detector to show
   * @return the jsp page that handles this model
   */
  @RequestMapping("/anomaly_detector_show.htm")
  public String showAnomalyDetector(Model model, @RequestParam("name") String name) {
    try {
      AnomalyDetector detector = manager.get(name);
      model.addAttribute("name", detector.getName());
      model.addAttribute("description", detector.getDescription());
      return "anomaly_detector_show";
    } catch (AnomalyException e) {
      logger.debug("Failed to locate anomaly detector '" + name + "'");
      model.addAttribute("anomaly_detectors", manager.list());
      model.addAttribute("emessage", "Failed to locate anomaly detector '" + name +"': " + e.getMessage());
      return "anomaly_detectors";
    }
  }

  /**
   * Handles the modification request
   * @param model the model for this page
   * @param name the name of the anomaly detector to modify
   * @param description the description
   * @param operation the operation, either Modify or Delete
   * @return the jsp page or a redirection url
   */
  @RequestMapping("/anomaly_detector_edit.htm")
  public String modifyAnomalyDetector(Model model,
      @RequestParam("name") String name,
      @RequestParam("description") String description,
      @RequestParam(value = "submitButton") String operation) {
    String result = null;
    String emessage = null;
    if (operation.equals("Delete")) {
      try {
        manager.remove(name);
        result = "redirect:anomaly_detectors.htm";
      } catch (AnomalyException e) {
        logger.error("Failed to remove anomaly detector: " + name);
        model.addAttribute("anomaly_detectors", manager.list());
        model.addAttribute("emessage", "Failed to delete '"+name+"': " + e.getMessage());
        result = "anomaly_detectors";
        emessage = "Failed to anomaly detector";
      }
    } else {
      try {
        AnomalyDetector detector = createDetector(name, description);
        manager.update(detector);
        return "redirect:anomaly_detectors.htm";
      } catch (AnomalyException e) {
        emessage = "Failed to update anomaly detector '" + name + "': " + e.getMessage();
      }
    }
    if (result == null) {
      model.addAttribute("name", name);
      model.addAttribute("description", description);
      model.addAttribute("emessage", emessage);
      result = "anomaly_detector_show";
    }
    return result;
  }
  
  @RequestMapping("/anomaly_detector_import.htm")
  public String importAnomalyDetectors(Model model,
      @RequestParam(value = "imported", required=false) String[] imported) {
    if (imported != null) {
      List<QualityControlInformation> qcs = pgfClient.getQualityControls();
      List<AnomalyDetector> ads = manager.list();
      for (String x : imported) {
        QualityControlInformation qci = getQCI(qcs, x);
        if (qci != null && !hasDetector(ads, qci.getName())) {
          manager.add(createDetector(qci.getName(), qci.getDescription()));
        }
      }
    }
    return "redirect:anomaly_detectors.htm";
  }
  
  protected QualityControlInformation getQCI(List<QualityControlInformation> qcs, String name) {
    for (QualityControlInformation qci : qcs) {
      if (qci.getName().equals(name)) {
        return qci;
      }
    }
    return null;
  }
  
  protected boolean hasDetector(List<AnomalyDetector> ads, String name) {
    for (AnomalyDetector ad: ads) {
      if (ad.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Creates an anomaly detector
   * @param name the name of the detector
   * @param description the description
   * @return the anomaly detector
   */
  protected AnomalyDetector createDetector(String name, String description) {
    AnomalyDetector detector = new AnomalyDetector();
    detector.setName(name);
    detector.setDescription(description);
    return detector;
  }
}
