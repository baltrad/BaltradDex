package eu.baltrad.beastui.web.controller;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;
import org.springframework.ui.Model;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.scheduler.CronEntry;
import eu.baltrad.beast.scheduler.CronEntryUtilities;
import eu.baltrad.beast.scheduler.IBeastScheduler;
import eu.baltrad.beast.scheduler.SchedulerException;

public class SchedulerControllerTest extends TestCase {
  private static interface MockMethods {
    String viewCreateScheduledJob(Model model,
        List<String> seconds,
        List<String> minutes,
        List<String> hours,
        List<String> daysOfMonth,
        List<String> months,
        List<String> daysOfWeek,
        String jobname,
        String emessage);
    String viewShowScheduledJob(Model model,
        int id,
        List<String> seconds,
        List<String> minutes,
        List<String> hours,
        List<String> daysOfMonth,
        List<String> months,
        List<String> daysOfWeek,
        String jobname,
        String emessage);
    String viewShowSchedule(Model model,
        String emessage);
  };
  
  private MockControl managerControl = null;
  private IRouterManager manager = null;
  private MockControl schedulerControl = null;
  private IBeastScheduler scheduler = null;
  private MockControl modelControl = null;
  private Model model = null;
  private SchedulerController classUnderTest = null;
  
  public void setUp() throws Exception {
    managerControl = MockControl.createControl(IRouterManager.class);
    manager = (IRouterManager)managerControl.getMock();
    schedulerControl = MockControl.createControl(IBeastScheduler.class);
    scheduler = (IBeastScheduler)schedulerControl.getMock();
    modelControl = MockControl.createControl(Model.class);
    model = (Model)modelControl.getMock();
    
    classUnderTest = new SchedulerController();
    classUnderTest.setManager(manager);
    classUnderTest.setScheduler(scheduler);
  }
  
  public void tearDown() throws Exception {
    classUnderTest = null;
    managerControl = null;
    manager = null;
    schedulerControl = null;
    scheduler = null;
    modelControl = null;
    model = null;
  }
  
  protected void replay() throws Exception {
    managerControl.replay();
    schedulerControl.replay();
    modelControl.replay();
  }

  protected void verify() throws Exception {
    managerControl.verify();
    schedulerControl.verify();
    modelControl.verify();
  }
  
  
  public void testShowSchedule() throws Exception {
    List<CronEntry> schedule = new ArrayList<CronEntry>();
    scheduler.getSchedule();
    schedulerControl.setReturnValue(schedule);
    model.addAttribute("schedule", schedule);
    modelControl.setReturnValue(null);
    model.addAttribute("emessage", null);
    modelControl.setReturnValue(null);
    
    replay();
    String result = classUnderTest.showSchedule(model, null);
    
    verify();
    assertEquals("schedule", result);
  }
 
  public void testCreateScheduledJob_default() throws Exception {
    MockControl methodsControl = MockControl.createControl(MockMethods.class);
    final MockMethods methods = (MockMethods)methodsControl.getMock();
    
    methods.viewCreateScheduledJob(model, null, null, null, null, null, null, null, null);
    methodsControl.setReturnValue("something");
    
    classUnderTest = new SchedulerController() {
      protected String viewCreateScheduledJob(Model model,
          List<String> seconds,
          List<String> minutes,
          List<String> hours,
          List<String> daysOfMonth,
          List<String> months,
          List<String> daysOfWeek,
          String jobname,
          String emessage) {
        return methods.viewCreateScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, jobname, emessage); 
      }
    };
    classUnderTest.setManager(manager);
    classUnderTest.setScheduler(scheduler);    
    
    replay();
    methodsControl.replay();
    
    String result = classUnderTest.createScheduledJob(model, null, null, null, null, null, null, null);
    
    verify();
    methodsControl.verify();
    assertEquals("something", result);
  }
  
  public void testCreateScheduledJob() throws Exception {
    MockControl methodsControl = MockControl.createControl(MockMethods.class);
    final MockMethods methods = (MockMethods)methodsControl.getMock();
    MockControl cronutilControl = MockClassControl.createControl(CronEntryUtilities.class);
    CronEntryUtilities cronutil = (CronEntryUtilities)cronutilControl.getMock();
    
    List<String> seconds = new ArrayList<String>();
    List<String> minutes = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();
    List<String> daysOfMonth = new ArrayList<String>();
    List<String> months = new ArrayList<String>();
    List<String> daysOfWeek = new ArrayList<String>();
    
    cronutil.createExpression(seconds, minutes, hours, daysOfMonth, months, daysOfWeek);
    cronutilControl.setReturnValue("0 * * * * ?");
    scheduler.register("0 * * * * ?", "DEF");
    schedulerControl.setReturnValue(10);
    
    classUnderTest = new SchedulerController() {
      protected String viewCreateScheduledJob(Model model,
          List<String> seconds,
          List<String> minutes,
          List<String> hours,
          List<String> daysOfMonth,
          List<String> months,
          List<String> daysOfWeek,
          String jobname,
          String emessage) {
        return methods.viewCreateScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, jobname, emessage); 
      }
    };
    classUnderTest.setManager(manager);
    classUnderTest.setScheduler(scheduler);    
    classUnderTest.setCronEntryUtilities(cronutil);
    
    replay();
    methodsControl.replay();
    cronutilControl.replay();
    
    String result = classUnderTest.createScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "DEF");
    
    verify();
    methodsControl.verify();
    cronutilControl.verify();
    assertEquals("redirect:schedule.htm", result);
  }

  public void testCreateScheduledJob_badCron() throws Exception {
    MockControl methodsControl = MockControl.createControl(MockMethods.class);
    final MockMethods methods = (MockMethods)methodsControl.getMock();
    MockControl cronutilControl = MockClassControl.createControl(CronEntryUtilities.class);
    CronEntryUtilities cronutil = (CronEntryUtilities)cronutilControl.getMock();
    
    List<String> seconds = new ArrayList<String>();
    List<String> minutes = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();
    List<String> daysOfMonth = new ArrayList<String>();
    List<String> months = new ArrayList<String>();
    List<String> daysOfWeek = new ArrayList<String>();

    cronutil.createExpression(seconds, minutes, hours, daysOfMonth, months, daysOfWeek);
    cronutilControl.setReturnValue("0 * * * * ?");
    scheduler.register("0 * * * * ?", "DEF");
    schedulerControl.setThrowable(new SchedulerException("abc"));
    methods.viewCreateScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "DEF", "Could not register: abc");
    methodsControl.setReturnValue("abc.htm");
    
    classUnderTest = new SchedulerController() {
      protected String viewCreateScheduledJob(Model model,
          List<String> seconds,
          List<String> minutes,
          List<String> hours,
          List<String> daysOfMonth,
          List<String> months,
          List<String> daysOfWeek,
          String jobname,
          String emessage) {
        return methods.viewCreateScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, jobname, emessage); 
      }
    };
    classUnderTest.setManager(manager);
    classUnderTest.setScheduler(scheduler);    
    classUnderTest.setCronEntryUtilities(cronutil);
    
    replay();
    methodsControl.replay();
    cronutilControl.replay();
    
    String result = classUnderTest.createScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "DEF");
    
    verify();
    methodsControl.verify();
    cronutilControl.verify();
    assertEquals("abc.htm", result);
  }
  
  @SuppressWarnings("unchecked")
  public void testShowScheduledJob_initial() throws Exception {
    MockControl methodsControl = MockControl.createControl(MockMethods.class);
    final MockMethods methods = (MockMethods)methodsControl.getMock();
    
    MockControl cronutilControl = MockClassControl.createControl(CronEntryUtilities.class);
    CronEntryUtilities cronutil = (CronEntryUtilities)cronutilControl.getMock();

    List<String>[] entries = new List[6];
    entries[0] = new ArrayList<String>();
    entries[1] = new ArrayList<String>();
    entries[2] = new ArrayList<String>();
    entries[3] = new ArrayList<String>();
    entries[4] = new ArrayList<String>();
    entries[5] = new ArrayList<String>();
    
    cronutil.parseAllInExpression("* * * * * *");
    cronutilControl.setReturnValue(entries);

    CronEntry entry = new CronEntry(10, "* * * * * *", "abc");
    
    scheduler.getEntry(10);
    schedulerControl.setReturnValue(entry);
    methods.viewShowScheduledJob(model, 10, entries[0], entries[1], entries[2], entries[3], entries[4], entries[5], "abc", null);
    methodsControl.setReturnValue("dumdi");
    
    classUnderTest = new SchedulerController() {
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
        return methods.viewShowScheduledJob(model, id, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, jobname, emessage);
      }
    };
    classUnderTest.setScheduler(scheduler);
    classUnderTest.setCronEntryUtilities(cronutil);

    replay();
    methodsControl.replay();
    cronutilControl.replay();
    
    String result = classUnderTest.showScheduledJob(model, 10, null, null, null, null, null, null, null, null);
    
    verify();
    methodsControl.verify();
    cronutilControl.verify();
    
    assertEquals("dumdi", result);
  }
  
  public void testShowScheduledJob_modify() throws Exception {
    MockControl cronutilControl = MockClassControl.createControl(CronEntryUtilities.class);
    CronEntryUtilities cronutil = (CronEntryUtilities)cronutilControl.getMock();
    
    List<String> seconds = new ArrayList<String>();
    List<String> minutes = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();
    List<String> daysOfMonth = new ArrayList<String>();
    List<String> months = new ArrayList<String>();
    List<String> daysOfWeek = new ArrayList<String>();
    
    CronEntry entry = new CronEntry(10, "* * *", "abc");
    scheduler.getEntry(10);
    schedulerControl.setReturnValue(entry);
    cronutil.createExpression(seconds, minutes, hours, daysOfMonth, months, daysOfWeek);
    cronutilControl.setReturnValue("* 1 * 2 * 3");
    scheduler.reregister(10, "* 1 * 2 * 3", "BAC");
    
    replay();
    cronutilControl.replay();
    
    classUnderTest.setCronEntryUtilities(cronutil);
    String result = classUnderTest.showScheduledJob(model, 10, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "BAC", "Save");
    
    verify();
    cronutilControl.verify();
    assertEquals("redirect:schedule.htm", result);
  }

  public void testShowScheduledJob_delete() throws Exception {
    List<String> seconds = new ArrayList<String>();
    List<String> minutes = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();
    List<String> daysOfMonth = new ArrayList<String>();
    List<String> months = new ArrayList<String>();
    List<String> daysOfWeek = new ArrayList<String>();

    CronEntry entry = new CronEntry(10, "* * *", "abc");
    scheduler.getEntry(10);
    schedulerControl.setReturnValue(entry);
    scheduler.unregister(10);
    
    replay();
    
    String result = classUnderTest.showScheduledJob(model, 10, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "BAC", "Delete");
    
    verify();
    assertEquals("redirect:schedule.htm", result);
  }

  
  public void testShowScheduledJob_noSuchId() throws Exception {
    MockControl methodsControl = MockControl.createControl(MockMethods.class);
    final MockMethods methods = (MockMethods)methodsControl.getMock();
    scheduler.getEntry(10);
    schedulerControl.setReturnValue(null);
    methods.viewShowSchedule(model, "No such scheduled job");
    methodsControl.setReturnValue("alfons");
    
    classUnderTest = new SchedulerController() {
      protected String viewShowSchedule(Model model,
          String emessage) {
        return methods.viewShowSchedule(model, emessage);
      }
    };
    classUnderTest.setScheduler(scheduler);
    
    replay();
    methodsControl.replay();
    
    String result = classUnderTest.showScheduledJob(model, 10, null, null, null, null, null, null, null, "Modify");
    
    verify();
    methodsControl.verify();
    assertEquals("alfons", result);
  }

  public void testShowScheduledJob_unknownOperation() throws Exception {
    CronEntry entry = new CronEntry(10, "* * *", "abc");
    MockControl methodsControl = MockControl.createControl(MockMethods.class);
    final MockMethods methods = (MockMethods)methodsControl.getMock();
    scheduler.getEntry(10);
    schedulerControl.setReturnValue(entry);
    methods.viewShowSchedule(model, "Unknown operation");
    methodsControl.setReturnValue("alfons");
    
    classUnderTest = new SchedulerController() {
      protected String viewShowSchedule(Model model,
          String emessage) {
        return methods.viewShowSchedule(model, emessage);
      }
    };
    classUnderTest.setScheduler(scheduler);
    
    replay();
    methodsControl.replay();
    
    String result = classUnderTest.showScheduledJob(model, 10, null, null, null, null, null, null, "Data", "DoIt");
    
    verify();
    methodsControl.verify();
    assertEquals("alfons", result);
  }

  public void testShowScheduledJob_nullOperation() throws Exception {
    CronEntry entry = new CronEntry(10, "* * *", "abc");
    MockControl methodsControl = MockControl.createControl(MockMethods.class);
    final MockMethods methods = (MockMethods)methodsControl.getMock();
    scheduler.getEntry(10);
    schedulerControl.setReturnValue(entry);
    methods.viewShowSchedule(model, "Unknown operation");
    methodsControl.setReturnValue("alfons");
    
    classUnderTest = new SchedulerController() {
      protected String viewShowSchedule(Model model,
          String emessage) {
        return methods.viewShowSchedule(model, emessage);
      }
    };
    classUnderTest.setScheduler(scheduler);
    
    replay();
    methodsControl.replay();
    
    String result = classUnderTest.showScheduledJob(model, 10, null, null, null, null, null, null, "Data", null);
    
    verify();
    methodsControl.verify();
    assertEquals("alfons", result);
  }
  
  public void testViewCreateScheduledJob() throws Exception {
    List<String> seconds = new ArrayList<String>();
    List<String> minutes = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();
    List<String> daysOfMonth = new ArrayList<String>();
    List<String> months = new ArrayList<String>();
    List<String> daysOfWeek = new ArrayList<String>();
    
    List<String> jobnames = new ArrayList<String>();

    manager.getNames();
    managerControl.setReturnValue(jobnames);
    model.addAttribute("seconds", seconds);
    modelControl.setReturnValue(null);
    model.addAttribute("minutes", minutes);
    modelControl.setReturnValue(null);
    model.addAttribute("hours", hours);
    modelControl.setReturnValue(null);
    model.addAttribute("daysOfMonth", daysOfMonth);
    modelControl.setReturnValue(null);
    model.addAttribute("months", months);
    modelControl.setReturnValue(null);
    model.addAttribute("daysOfWeek", daysOfWeek);
    modelControl.setReturnValue(null);

    model.addAttribute("selectableSeconds", SchedulerController.SECONDS);
    modelControl.setReturnValue(null);
    model.addAttribute("selectableMinutes", SchedulerController.MINUTES);
    modelControl.setReturnValue(null);
    model.addAttribute("selectableHours", SchedulerController.HOURS);
    modelControl.setReturnValue(null);
    model.addAttribute("selectableDaysOfMonth", SchedulerController.DAYS_OF_MONTH);
    modelControl.setReturnValue(null);
    model.addAttribute("selectableMonths", SchedulerController.MONTHS);
    modelControl.setReturnValue(null);
    model.addAttribute("selectableDaysOfWeek", SchedulerController.DAYS_OF_WEEK);
    modelControl.setReturnValue(null);
    
    model.addAttribute("jobnames", jobnames);
    modelControl.setReturnValue(null);
    model.addAttribute("jobname", "nisse");
    modelControl.setReturnValue(null);
    
    replay();
    
    String result = classUnderTest.viewCreateScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "nisse", null);
    
    verify();
    assertEquals("schedule_create_job", result);
  }

  public void testViewShowScheduledJob() throws Exception {
    List<String> seconds = new ArrayList<String>();
    List<String> minutes = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();
    List<String> daysOfMonth = new ArrayList<String>();
    List<String> months = new ArrayList<String>();
    List<String> daysOfWeek = new ArrayList<String>();

    List<String> jobnames = new ArrayList<String>();

    manager.getNames();
    managerControl.setReturnValue(jobnames);
    model.addAttribute("jobnames", jobnames);
    modelControl.setReturnValue(null);
    model.addAttribute("id", 10);
    modelControl.setReturnValue(null);
    model.addAttribute("seconds", seconds);
    modelControl.setReturnValue(null);
    model.addAttribute("minutes", minutes);
    modelControl.setReturnValue(null);
    model.addAttribute("hours", hours);
    modelControl.setReturnValue(null);
    model.addAttribute("daysOfMonth", daysOfMonth);
    modelControl.setReturnValue(null);
    model.addAttribute("months", months);
    modelControl.setReturnValue(null);
    model.addAttribute("daysOfWeek", daysOfWeek);
    modelControl.setReturnValue(null);

    model.addAttribute("selectableSeconds", SchedulerController.SECONDS);
    modelControl.setReturnValue(null);
    model.addAttribute("selectableMinutes", SchedulerController.MINUTES);
    modelControl.setReturnValue(null);
    model.addAttribute("selectableHours", SchedulerController.HOURS);
    modelControl.setReturnValue(null);
    model.addAttribute("selectableDaysOfMonth", SchedulerController.DAYS_OF_MONTH);
    modelControl.setReturnValue(null);
    model.addAttribute("selectableMonths", SchedulerController.MONTHS);
    modelControl.setReturnValue(null);
    model.addAttribute("selectableDaysOfWeek", SchedulerController.DAYS_OF_WEEK);
    modelControl.setReturnValue(null);
    
    model.addAttribute("jobname", "nisse");
    modelControl.setReturnValue(null);
    
    replay();

    String result = classUnderTest.viewShowScheduledJob(model, 10, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "nisse", null);
    
    verify();
    assertEquals("schedule_show_job", result);
  }
  
  public void testViewShowSchedule() throws Exception {
    
    replay();
    
    String result = classUnderTest.viewShowSchedule(model, null);
    
    verify();
    assertEquals("schedule", result);
  }
  
  public void testViewShowSchedule_emessage() throws Exception {
    model.addAttribute("emessage", "an error occured");
    modelControl.setReturnValue(null);
    
    replay();
    
    String result = classUnderTest.viewShowSchedule(model, "an error occured");
    
    verify();
    assertEquals("schedule", result);
  }
}
