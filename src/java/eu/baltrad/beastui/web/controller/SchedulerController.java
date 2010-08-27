/* --------------------------------------------------------------------
Copyright (C) 2009-2010 Swedish Meteorological and Hydrological Institute, SMHI,

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

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.scheduler.CronEntry;
import eu.baltrad.beast.scheduler.IBeastScheduler;

/**
 * Controller managing the scheduling
 * @author Anders Henja
 */
@Controller
public class SchedulerController {
  /**
   * The scheduler instance
   */
  private IBeastScheduler scheduler = null;
  
  /**
   * The router manager
   */
  private IRouterManager manager = null;
  
  /**
   * We need a logger here
   */
  private static Logger logger = LogManager.getLogger(SchedulerController.class);

  /**
   * Default constructor
   */
  public SchedulerController() {
  }

  /**
   * @param scheduler the scheduler to set
   */
  @Autowired
  public void setScheduler(IBeastScheduler scheduler) {
    this.scheduler = scheduler;
  }
  
  /**
   * @param scheduler the scheduler to set
   */
  @Autowired
  public void setManager(IRouterManager manager) {
    this.manager = manager;
  }
  
  /**
   * Shows the current scheduling information
   * @param model the mdoel
   * @param emessage
   * @return
   */
  @RequestMapping("/showschedule.htm")
  public String showSchedule(Model model,
      @RequestParam(value="emessage", required=false) String emessage) {
    logger.debug("showSchedule");
    model.addAttribute("schedule", scheduler.getSchedule());
    model.addAttribute("emessage", emessage);
    return "showschedule";
  }
  
  /**
   * Shows the create scheduled job page
   * @param model the model
   * @param expression the expression
   * @param jobname the job name
   * @return the redirect string
   */
  @RequestMapping("/createscheduledjob.htm")
  public String createScheduledJob(Model model,
      @RequestParam(value = "expression", required = false) String expression,
      @RequestParam(value = "jobname", required = false) String jobname) {
    String result = null;
    if (jobname != null && expression != null) {
      try {
        scheduler.register(expression, jobname);
        result = "redirect:showschedule.htm";
      } catch (Throwable t) {
        result = viewCreateScheduledJob(model, expression, jobname, "Could not register: "+t.getMessage());
      }
    } else {
      result = viewCreateScheduledJob(model, expression, jobname, null);
    }
    return result;
  }
  
  /**
   * Shows the scheduled job.
   * @param model
   * @return
   */
  @RequestMapping("/showscheduledjob.htm")
  public String showScheduledJob(Model model,
      @RequestParam(value = "id", required = true) Integer id,
      @RequestParam(value = "expression", required = false) String expression,
      @RequestParam(value = "jobname", required = false) String jobname,
      @RequestParam(value = "submitButton", required = false) String operation) {
    CronEntry entry = scheduler.getEntry(id);
    String result = "";
    if (entry != null) {
      if (expression == null || jobname == null) {
        result = viewShowScheduledJob(model, 
            entry.getId(), 
            entry.getExpression(), 
            entry.getName(),
            null);
      } else if (operation != null) {
        result = "redirect:showschedule.htm";
        try {
          if (operation.equals("Modify")) {
            scheduler.reregister(entry.getId(), expression, jobname);
          } else if (operation.equals("Delete")) {
            scheduler.unregister(entry.getId());
          } else {
            result = viewShowSchedule(model, "Unknown operation");
          }
        } catch (Throwable t) {
          result = viewShowSchedule(model, t.getMessage());
        }
      } else {
        result = viewShowSchedule(model, "Unknown operation");
      }
    } else {
      result = viewShowSchedule(model, "No such scheduled job");
    }

    return result;
  }
  
  /**
   * Sets the model to return the view for "Created Scheduled Job"
   * @param model the model
   * @param expression the cron expression
   * @param jobname the routing rule name
   * @param emessage a error message if any
   * @return createscheduledjob
   */
  protected String viewCreateScheduledJob(Model model,
      String expression,
      String jobname,
      String emessage) {
    List<String> jobs = manager.getNames();
    model.addAttribute("jobnames", jobs);
    model.addAttribute("expression", (expression == null)? "" : expression);
    model.addAttribute("jobname", (jobname == null)? "" : jobname);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "createscheduledjob";
  }
  
  /**
   * Sets the model to return the view for "Show Scheduled Job"
   * @param model the model
   * @param id the scheduled job id
   * @param expression the cron expression
   * @param jobname the routing rule name
   * @param emessage a error message if any
   * @return showscheduledjob
   */
  protected String viewShowScheduledJob(Model model,
      int id,
      String expression,
      String jobname,
      String emessage) {
    List<String> jobs = manager.getNames();
    model.addAttribute("jobnames", jobs);
    model.addAttribute("id", id);
    model.addAttribute("expression", expression);
    model.addAttribute("jobname", jobname);
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "showscheduledjob";
  }
  
  /**
   * Shows the schedule view
   * @param model the model
   * @param emessage the error message
   * @return the jsp page name
   */
  protected String viewShowSchedule(Model model,
      String emessage) {
    if (emessage != null) {
      model.addAttribute("emessage", emessage);
    }
    return "showschedule";
  }
}
