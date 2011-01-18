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

import java.util.ArrayList;
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
import eu.baltrad.beast.scheduler.CronEntryUtilities;
import eu.baltrad.beast.scheduler.IBeastScheduler;
import eu.baltrad.beastui.web.pojo.CronEntryMapping;

/**
 * Controller managing the scheduling
 * @author Anders Henja
 */
@Controller
public class SchedulerController {
  /**
   * Available second selections
   */
  protected static List<CronEntryMapping> SECONDS = createSecondsMapping(); 
  
  /**
   * Available minute selections
   */
  protected static List<CronEntryMapping> MINUTES = createMinutesMapping(); 

  /**
   * Available hour selections
   */
  protected static List<CronEntryMapping> HOURS = createHoursMapping(); 

  /**
   * Available day of month selections
   */
  protected static List<CronEntryMapping> DAYS_OF_MONTH = createDaysOfMonthMapping(); 

  /**
   * Available months selections
   */
  protected static List<CronEntryMapping> MONTHS = createMonthsMapping(); 

  /**
   * Available day of month selections
   */
  protected static List<CronEntryMapping> DAYS_OF_WEEK = createDaysOfWeekMapping(); 

  /**
   * The scheduler instance
   */
  private IBeastScheduler scheduler = null;
  
  /**
   * The router manager
   */
  private IRouterManager manager = null;
  
  /**
   * Utilities for working with cron expressions.
   */
  private CronEntryUtilities cronutilities = null;
  
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
   * @param cronutilities the cronutilities to set
   */
  @Autowired
  public void setCronEntryUtilities(CronEntryUtilities cronutilities) {
    this.cronutilities = cronutilities;
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
      @RequestParam(value = "seconds", required = false) List<String> seconds,
      @RequestParam(value = "minutes", required = false) List<String> minutes,
      @RequestParam(value = "hours", required = false) List<String> hours,
      @RequestParam(value = "daysOfMonth", required = false) List<String> daysOfMonth,
      @RequestParam(value = "months", required = false) List<String> months,
      @RequestParam(value = "daysOfWeek", required = false) List<String> daysOfWeek,
      @RequestParam(value = "jobname", required = false) String jobname) {
    String result = null;
    if (jobname != null) {
      try {
        String expression = cronutilities.createExpression(seconds, minutes, hours, daysOfMonth, months, daysOfWeek);
        logger.debug("Cron Expression = " + expression);
        scheduler.register(expression, jobname);
        result = "redirect:showschedule.htm";
      } catch (Throwable t) {
        result = viewCreateScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, jobname, "Could not register: "+t.getMessage());
      }
    } else {
      result = viewCreateScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, jobname, null);
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
      @RequestParam(value = "seconds", required = false) List<String> seconds,
      @RequestParam(value = "minutes", required = false) List<String> minutes,
      @RequestParam(value = "hours", required = false) List<String> hours,
      @RequestParam(value = "daysOfMonth", required = false) List<String> daysOfMonth,
      @RequestParam(value = "months", required = false) List<String> months,
      @RequestParam(value = "daysOfWeek", required = false) List<String> daysOfWeek,
      @RequestParam(value = "jobname", required = false) String jobname,
      @RequestParam(value = "submitButton", required = false) String operation) {
    CronEntry entry = scheduler.getEntry(id);
    String result = "";
    if (entry != null) {
      if (jobname == null) {
        List<String>[] entries = cronutilities.parseAllInExpression(entry.getExpression());
        result = viewShowScheduledJob(model, 
            entry.getId(),
            entries[CronEntryUtilities.SECONDS_INDEX],
            entries[CronEntryUtilities.MINUTES_INDEX],
            entries[CronEntryUtilities.HOURS_INDEX],
            entries[CronEntryUtilities.DAYSOFMONTH_INDEX],
            entries[CronEntryUtilities.MONTHS_INDEX],
            entries[CronEntryUtilities.DAYSOFWEEK_INDEX],
            entry.getName(),
            null);
      } else if (operation != null) {
        result = "redirect:showschedule.htm";
        try {
          if (operation.equals("Modify")) {
            String expression = cronutilities.createExpression(seconds, minutes, hours, daysOfMonth, months, daysOfWeek);
            logger.debug("Updating cron expression = " + expression);
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
      List<String> seconds,
      List<String> minutes,
      List<String> hours,
      List<String> daysOfMonth,
      List<String> months,
      List<String> daysOfWeek,
      String jobname,
      String emessage) {
    List<String> jobs = manager.getNames();
    model.addAttribute("seconds", (seconds == null)?createDefaultSeconds():seconds);
    model.addAttribute("minutes", (minutes == null)?createDefaultMinutes():minutes);
    model.addAttribute("hours", (hours == null)?createDefaultHours():hours);
    model.addAttribute("daysOfMonth", (daysOfMonth == null)?createDefaultDaysOfMonth():daysOfMonth);
    model.addAttribute("months", (months == null)?createDefaultMonths():months);
    model.addAttribute("daysOfWeek", (daysOfWeek == null)?createDefaultDaysOfWeek():daysOfWeek);
    model.addAttribute("jobname", (jobname == null)? "" : jobname);
    model.addAttribute("jobnames", jobs);
    model.addAttribute("selectableSeconds", SECONDS);
    model.addAttribute("selectableMinutes", MINUTES);
    model.addAttribute("selectableHours", HOURS);
    model.addAttribute("selectableDaysOfMonth", DAYS_OF_MONTH);
    model.addAttribute("selectableMonths", MONTHS);
    model.addAttribute("selectableDaysOfWeek", DAYS_OF_WEEK);
    
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
      List<String> seconds,
      List<String> minutes,
      List<String> hours,
      List<String> daysOfMonth,
      List<String> months,
      List<String> daysOfWeek,
      String jobname,
      String emessage) {
    List<String> jobs = manager.getNames();
    model.addAttribute("id", id);
    model.addAttribute("seconds", (seconds == null)?createDefaultSeconds():seconds);
    model.addAttribute("minutes", (minutes == null)?createDefaultMinutes():minutes);
    model.addAttribute("hours", (hours == null)?createDefaultHours():hours);
    model.addAttribute("daysOfMonth", (daysOfMonth == null)?createDefaultDaysOfMonth():daysOfMonth);
    model.addAttribute("months", (months == null)?createDefaultMonths():months);
    model.addAttribute("daysOfWeek", (daysOfWeek == null)?createDefaultDaysOfWeek():daysOfWeek);
    model.addAttribute("jobname", jobname);
    model.addAttribute("jobnames", jobs);
    model.addAttribute("selectableSeconds", SECONDS);
    model.addAttribute("selectableMinutes", MINUTES);
    model.addAttribute("selectableHours", HOURS);
    model.addAttribute("selectableDaysOfMonth", DAYS_OF_MONTH);
    model.addAttribute("selectableMonths", MONTHS);
    model.addAttribute("selectableDaysOfWeek", DAYS_OF_WEEK);
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
  
  /**
   * @return the default selection for seconds
   */
  protected List<String> createDefaultSeconds() {
    List<String> result = new ArrayList<String>();
    result.add("0");
    return result;
  }

  /**
   * @return the default selection for minutes
   */
  protected List<String> createDefaultMinutes() {
    List<String> result = new ArrayList<String>();
    result.add("*");
    return result;
  }

  /**
   * @return the default selection for hours
   */
  protected List<String> createDefaultHours() {
    List<String> result = new ArrayList<String>();
    result.add("*");
    return result;
  }  

  /**
   * @return the default selection for daysOfMonth
   */
  protected List<String> createDefaultDaysOfMonth() {
    List<String> result = new ArrayList<String>();
    result.add("*");
    return result;
  }  

  /**
   * @return the default selection for months
   */
  protected List<String> createDefaultMonths() {
    List<String> result = new ArrayList<String>();
    result.add("*");
    return result;
  }    

  /**
   * @return the default selection for months
   */
  protected List<String> createDefaultDaysOfWeek() {
    List<String> result = new ArrayList<String>();
    result.add("?");
    return result;
  }  
  
  /**
   * @return the selectable second choices
   */
  private static List<CronEntryMapping> createSecondsMapping() {
    List<CronEntryMapping> result = new ArrayList<CronEntryMapping>();
    result.add(new CronEntryMapping("*", "Every second"));
    result.add(new CronEntryMapping("*/2", "Every second second"));
    result.add(new CronEntryMapping("*/5", "Every fifth second"));
    result.add(new CronEntryMapping("*/10", "Every tenth second"));
    result.add(new CronEntryMapping("*/15", "Every fifteenth second"));
    result.add(new CronEntryMapping("*/20", "Every twentieth second"));
    result.add(new CronEntryMapping("*/30", "Every thirtieth second"));
    for (int i = 0; i < 60; i++) {
      result.add(new CronEntryMapping(""+i, null));
    }
    return result;
  }
  
  /**
   * @return the selectable minute choices
   */
  private static List<CronEntryMapping> createMinutesMapping() {
    List<CronEntryMapping> result = new ArrayList<CronEntryMapping>();
    result.add(new CronEntryMapping("*", "Every minute"));
    result.add(new CronEntryMapping("*/2", "Every second minute"));
    result.add(new CronEntryMapping("*/5", "Every fifth minute"));
    result.add(new CronEntryMapping("*/10", "Every tenth minute"));
    result.add(new CronEntryMapping("*/15", "Every fifteenth minute"));
    result.add(new CronEntryMapping("*/20", "Every twentieth minute"));
    result.add(new CronEntryMapping("*/30", "Every thirtieth minute"));
    for (int i = 0; i < 60; i++) {
      result.add(new CronEntryMapping(""+i, null));
    }
    return result;
  }
  
  /**
   * @return the selectable hour choices
   */
  private static List<CronEntryMapping> createHoursMapping() {
    List<CronEntryMapping> result = new ArrayList<CronEntryMapping>();
    result.add(new CronEntryMapping("*", "Every hour"));
    result.add(new CronEntryMapping("*/2", "Every second hour"));
    result.add(new CronEntryMapping("*/3", "Every third hour"));
    result.add(new CronEntryMapping("*/4", "Every fourth hour"));
    result.add(new CronEntryMapping("*/6", "Every sixth hour"));
    result.add(new CronEntryMapping("*/8", "Every eight hour"));
    result.add(new CronEntryMapping("*/12", "Every twelfth hour"));
    for (int i = 0; i < 24; i++) {
      result.add(new CronEntryMapping(""+i, null));
    }
    return result;
  }
  
  /**
   * @return the selectable days-of-month choices
   */
  private static List<CronEntryMapping> createDaysOfMonthMapping() {
    List<CronEntryMapping> result = new ArrayList<CronEntryMapping>();
    result.add(new CronEntryMapping("?", "Unused"));
    result.add(new CronEntryMapping("*", "Every day"));
    result.add(new CronEntryMapping("L", "Last day of month"));
    result.add(new CronEntryMapping("LW", "Last weekday of month"));
    for (int i = 1; i < 32; i++) {
      result.add(new CronEntryMapping(""+i, null));
    }
    return result;    
  }
  
  /**
   * @return the selectable months choices
   */
  private static List<CronEntryMapping> createMonthsMapping() {
    List<CronEntryMapping> result = new ArrayList<CronEntryMapping>();
    result.add(new CronEntryMapping("*", "Every month"));
    result.add(new CronEntryMapping("*/2", "Every second month"));
    result.add(new CronEntryMapping("*/3", "Every third month"));
    result.add(new CronEntryMapping("*/4", "Every fourth month"));
    result.add(new CronEntryMapping("*/6", "Every sixth month"));
    result.add(new CronEntryMapping("1", "January"));
    result.add(new CronEntryMapping("2", "February"));
    result.add(new CronEntryMapping("3", "March"));
    result.add(new CronEntryMapping("4", "April"));
    result.add(new CronEntryMapping("5", "May"));
    result.add(new CronEntryMapping("6", "June"));
    result.add(new CronEntryMapping("7", "July"));
    result.add(new CronEntryMapping("8", "August"));
    result.add(new CronEntryMapping("9", "September"));
    result.add(new CronEntryMapping("10", "October"));
    result.add(new CronEntryMapping("11", "November"));
    result.add(new CronEntryMapping("12", "December"));
    return result;    
  }

  /**
   * @return the selectable days-of-week choices
   */
  private static List<CronEntryMapping> createDaysOfWeekMapping() {
    List<CronEntryMapping> result = new ArrayList<CronEntryMapping>();
    result.add(new CronEntryMapping("?", "Unused"));
    result.add(new CronEntryMapping("1", "Sunday"));
    result.add(new CronEntryMapping("2", "Monday"));
    result.add(new CronEntryMapping("3", "Tuesday"));
    result.add(new CronEntryMapping("4", "Wednesday"));
    result.add(new CronEntryMapping("5", "Thursday"));
    result.add(new CronEntryMapping("6", "Friday"));
    result.add(new CronEntryMapping("7", "Saturday"));
    return result;    
  }

}
