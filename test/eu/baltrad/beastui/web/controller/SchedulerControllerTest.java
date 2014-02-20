package eu.baltrad.beastui.web.controller;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.scheduler.CronEntry;
import eu.baltrad.beast.scheduler.CronEntryUtilities;
import eu.baltrad.beast.scheduler.IBeastScheduler;
import eu.baltrad.beast.scheduler.SchedulerException;

public class SchedulerControllerTest extends EasyMockSupport {
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
  
  private IRouterManager manager = null;
  private IBeastScheduler scheduler = null;
  private Model model = null;
  private SchedulerController classUnderTest = null;
  
  @Before
  public void setUp() throws Exception {
    manager = createMock(IRouterManager.class);
    scheduler = createMock(IBeastScheduler.class);
    model = createMock(Model.class);
    
    classUnderTest = new SchedulerController();
    classUnderTest.setManager(manager);
    classUnderTest.setScheduler(scheduler);
  }
  
  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
    manager = null;
    scheduler = null;
    model = null;
  }

  @Test
  public void testShowSchedule() throws Exception {
    List<CronEntry> schedule = new ArrayList<CronEntry>();
    expect(scheduler.getSchedule()).andReturn(schedule);
    expect(model.addAttribute("schedule", schedule)).andReturn(null);
    expect(model.addAttribute("emessage", null)).andReturn(null);
    
    replayAll();
    String result = classUnderTest.showSchedule(model, null);
    
    verifyAll();
    assertEquals("schedule", result);
  }
 
  @Test
  public void testCreateScheduledJob_default() throws Exception {
    final MockMethods methods = createMock(MockMethods.class);
    
    expect(methods.viewCreateScheduledJob(model, null, null, null, null, null, null, null, null)).andReturn("something");
    
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
    
    replayAll();
    
    String result = classUnderTest.createScheduledJob(model, null, null, null, null, null, null, null);
    
    verifyAll();

    assertEquals("something", result);
  }

  @Test
  public void testCreateScheduledJob() throws Exception {
    final MockMethods methods = createMock(MockMethods.class);
    CronEntryUtilities cronutil = createMock(CronEntryUtilities.class);
    
    List<String> seconds = new ArrayList<String>();
    List<String> minutes = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();
    List<String> daysOfMonth = new ArrayList<String>();
    List<String> months = new ArrayList<String>();
    List<String> daysOfWeek = new ArrayList<String>();
    
    expect(cronutil.createExpression(seconds, minutes, hours, daysOfMonth, months, daysOfWeek)).andReturn("0 * * * * ?");
    expect(scheduler.register("0 * * * * ?", "DEF")).andReturn(10);
    
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
    
    replayAll();
    
    String result = classUnderTest.createScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "DEF");
    
    verifyAll();

    assertEquals("redirect:schedule.htm", result);
  }

  @Test
  public void testCreateScheduledJob_badCron() throws Exception {
    final MockMethods methods = createMock(MockMethods.class);
    CronEntryUtilities cronutil = createMock(CronEntryUtilities.class);
    
    List<String> seconds = new ArrayList<String>();
    List<String> minutes = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();
    List<String> daysOfMonth = new ArrayList<String>();
    List<String> months = new ArrayList<String>();
    List<String> daysOfWeek = new ArrayList<String>();

    expect(cronutil.createExpression(seconds, minutes, hours, daysOfMonth, months, daysOfWeek)).andReturn("0 * * * * ?");
    expect(scheduler.register("0 * * * * ?", "DEF")).andThrow(new SchedulerException("abc"));
    expect(methods.viewCreateScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "DEF", "Could not register: abc")).andReturn("abc.htm");
    
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
    
    replayAll();
    
    String result = classUnderTest.createScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "DEF");
    
    verifyAll();
    
    assertEquals("abc.htm", result);
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testShowScheduledJob_initial() throws Exception {
    final MockMethods methods = createMock(MockMethods.class);
    CronEntryUtilities cronutil = createMock(CronEntryUtilities.class);

    List<String>[] entries = new List[6];
    entries[0] = new ArrayList<String>();
    entries[1] = new ArrayList<String>();
    entries[2] = new ArrayList<String>();
    entries[3] = new ArrayList<String>();
    entries[4] = new ArrayList<String>();
    entries[5] = new ArrayList<String>();
    
    expect(cronutil.parseAllInExpression("* * * * * *")).andReturn(entries);

    CronEntry entry = new CronEntry(10, "* * * * * *", "abc");
    
    expect(scheduler.getEntry(10)).andReturn(entry);
    expect(methods.viewShowScheduledJob(model, 10, entries[0], entries[1], entries[2], entries[3], entries[4], entries[5], "abc", null)).andReturn("dumdi");
    
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

    replayAll();
    
    String result = classUnderTest.showScheduledJob(model, 10, null, null, null, null, null, null, null, null);
    
    verifyAll();
    
    assertEquals("dumdi", result);
  }

  @Test
  public void testShowScheduledJob_modify() throws Exception {
    CronEntryUtilities cronutil = createMock(CronEntryUtilities.class);
    
    List<String> seconds = new ArrayList<String>();
    List<String> minutes = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();
    List<String> daysOfMonth = new ArrayList<String>();
    List<String> months = new ArrayList<String>();
    List<String> daysOfWeek = new ArrayList<String>();
    
    CronEntry entry = new CronEntry(10, "* * *", "abc");
    expect(scheduler.getEntry(10)).andReturn(entry);
    expect(cronutil.createExpression(seconds, minutes, hours, daysOfMonth, months, daysOfWeek)).andReturn("* 1 * 2 * 3");
    scheduler.reregister(10, "* 1 * 2 * 3", "BAC");
    
    replayAll();
    
    classUnderTest.setCronEntryUtilities(cronutil);
    String result = classUnderTest.showScheduledJob(model, 10, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "BAC", "Save");
    
    verifyAll();

    assertEquals("redirect:schedule.htm", result);
  }

  @Test
  public void testShowScheduledJob_delete() throws Exception {
    List<String> seconds = new ArrayList<String>();
    List<String> minutes = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();
    List<String> daysOfMonth = new ArrayList<String>();
    List<String> months = new ArrayList<String>();
    List<String> daysOfWeek = new ArrayList<String>();

    CronEntry entry = new CronEntry(10, "* * *", "abc");
    expect(scheduler.getEntry(10)).andReturn(entry);
    scheduler.unregister(10);
    
    replayAll();
    
    String result = classUnderTest.showScheduledJob(model, 10, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "BAC", "Delete");
    
    verifyAll();
    assertEquals("redirect:schedule.htm", result);
  }

  @Test
  public void testShowScheduledJob_noSuchId() throws Exception {
    final MockMethods methods = createMock(MockMethods.class);
    expect(scheduler.getEntry(10)).andReturn(null);
    expect(methods.viewShowSchedule(model, "No such scheduled job")).andReturn("alfons");
    
    classUnderTest = new SchedulerController() {
      protected String viewShowSchedule(Model model,
          String emessage) {
        return methods.viewShowSchedule(model, emessage);
      }
    };
    classUnderTest.setScheduler(scheduler);
    
    replayAll();
    
    String result = classUnderTest.showScheduledJob(model, 10, null, null, null, null, null, null, null, "Modify");
    
    verifyAll();

    assertEquals("alfons", result);
  }

  @Test
  public void testShowScheduledJob_unknownOperation() throws Exception {
    CronEntry entry = new CronEntry(10, "* * *", "abc");
    final MockMethods methods = createMock(MockMethods.class);
    expect(scheduler.getEntry(10)).andReturn(entry);
    expect(methods.viewShowSchedule(model, "Unknown operation")).andReturn("alfons");
    
    classUnderTest = new SchedulerController() {
      protected String viewShowSchedule(Model model,
          String emessage) {
        return methods.viewShowSchedule(model, emessage);
      }
    };
    classUnderTest.setScheduler(scheduler);
    
    replayAll();
    
    String result = classUnderTest.showScheduledJob(model, 10, null, null, null, null, null, null, "Data", "DoIt");
    
    verifyAll();

    assertEquals("alfons", result);
  }

  @Test
  public void testShowScheduledJob_nullOperation() throws Exception {
    CronEntry entry = new CronEntry(10, "* * *", "abc");
    final MockMethods methods = createMock(MockMethods.class);
    expect(scheduler.getEntry(10)).andReturn(entry);
    expect(methods.viewShowSchedule(model, "Unknown operation")).andReturn("alfons");
    
    classUnderTest = new SchedulerController() {
      protected String viewShowSchedule(Model model,
          String emessage) {
        return methods.viewShowSchedule(model, emessage);
      }
    };
    classUnderTest.setScheduler(scheduler);
    
    replayAll();
    
    String result = classUnderTest.showScheduledJob(model, 10, null, null, null, null, null, null, "Data", null);
    
    verifyAll();

    assertEquals("alfons", result);
  }

  @Test
  public void testViewCreateScheduledJob() throws Exception {
    List<String> seconds = new ArrayList<String>();
    List<String> minutes = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();
    List<String> daysOfMonth = new ArrayList<String>();
    List<String> months = new ArrayList<String>();
    List<String> daysOfWeek = new ArrayList<String>();
    
    List<String> jobnames = new ArrayList<String>();

    expect(manager.getNames()).andReturn(jobnames);
    expect(model.addAttribute("seconds", seconds)).andReturn(null);
    expect(model.addAttribute("minutes", minutes)).andReturn(null);
    expect(model.addAttribute("hours", hours)).andReturn(null);
    expect(model.addAttribute("daysOfMonth", daysOfMonth)).andReturn(null);
    expect(model.addAttribute("months", months)).andReturn(null);
    expect(model.addAttribute("daysOfWeek", daysOfWeek)).andReturn(null);

    expect(model.addAttribute("selectableSeconds", SchedulerController.SECONDS)).andReturn(null);
    expect(model.addAttribute("selectableMinutes", SchedulerController.MINUTES)).andReturn(null);
    expect(model.addAttribute("selectableHours", SchedulerController.HOURS)).andReturn(null);
    expect(model.addAttribute("selectableDaysOfMonth", SchedulerController.DAYS_OF_MONTH)).andReturn(null);
    expect(model.addAttribute("selectableMonths", SchedulerController.MONTHS)).andReturn(null);
    expect(model.addAttribute("selectableDaysOfWeek", SchedulerController.DAYS_OF_WEEK)).andReturn(null);
    
    expect(model.addAttribute("jobnames", jobnames)).andReturn(null);
    expect(model.addAttribute("jobname", "nisse")).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.viewCreateScheduledJob(model, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "nisse", null);
    
    verifyAll();
    assertEquals("schedule_create_job", result);
  }

  @Test
  public void testViewShowScheduledJob() throws Exception {
    List<String> seconds = new ArrayList<String>();
    List<String> minutes = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();
    List<String> daysOfMonth = new ArrayList<String>();
    List<String> months = new ArrayList<String>();
    List<String> daysOfWeek = new ArrayList<String>();

    List<String> jobnames = new ArrayList<String>();

    expect(manager.getNames()).andReturn(jobnames);
    expect(model.addAttribute("jobnames", jobnames)).andReturn(null);
    expect(model.addAttribute("id", 10)).andReturn(null);
    expect(model.addAttribute("seconds", seconds)).andReturn(null);
    expect(model.addAttribute("minutes", minutes)).andReturn(null);
    expect(model.addAttribute("hours", hours)).andReturn(null);
    expect(model.addAttribute("daysOfMonth", daysOfMonth)).andReturn(null);
    expect(model.addAttribute("months", months)).andReturn(null);
    expect(model.addAttribute("daysOfWeek", daysOfWeek)).andReturn(null);

    expect(model.addAttribute("selectableSeconds", SchedulerController.SECONDS)).andReturn(null);
    expect(model.addAttribute("selectableMinutes", SchedulerController.MINUTES)).andReturn(null);
    expect(model.addAttribute("selectableHours", SchedulerController.HOURS)).andReturn(null);
    expect(model.addAttribute("selectableDaysOfMonth", SchedulerController.DAYS_OF_MONTH)).andReturn(null);
    expect(model.addAttribute("selectableMonths", SchedulerController.MONTHS)).andReturn(null);
    expect(model.addAttribute("selectableDaysOfWeek", SchedulerController.DAYS_OF_WEEK)).andReturn(null);
    
    expect(model.addAttribute("jobname", "nisse")).andReturn(null);
    
    replayAll();

    String result = classUnderTest.viewShowScheduledJob(model, 10, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, "nisse", null);
    
    verifyAll();
    assertEquals("schedule_show_job", result);
  }
  
  public void testViewShowSchedule() throws Exception {
    
    replayAll();
    
    String result = classUnderTest.viewShowSchedule(model, null);
    
    verifyAll();
    assertEquals("schedule", result);
  }
  
  public void testViewShowSchedule_emessage() throws Exception {
    expect(model.addAttribute("emessage", "an error occured")).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.viewShowSchedule(model, "an error occured");
    
    verifyAll();
    assertEquals("schedule", result);
  }
}
