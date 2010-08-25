package eu.baltrad.beastui.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.easymock.MockControl;
import org.springframework.ui.Model;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.scheduler.CronEntry;
import eu.baltrad.beast.scheduler.IBeastScheduler;
import eu.baltrad.beast.scheduler.SchedulerException;
import junit.framework.TestCase;

public class SchedulerControllerTest extends TestCase {
  private static interface MockMethods {
    String viewCreateScheduledJob(Model model,
        String expression,
        String jobname,
        String emessage);
    String viewShowScheduledJob(Model model,
        int id,
        String expression,
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
    assertEquals("showschedule", result);
  }
 
  public void testCreateScheduledJob_default() throws Exception {
    MockControl methodsControl = MockControl.createControl(MockMethods.class);
    final MockMethods methods = (MockMethods)methodsControl.getMock();
    
    methods.viewCreateScheduledJob(model, null, null, null);
    methodsControl.setReturnValue("something");
    
    classUnderTest = new SchedulerController() {
      protected String viewCreateScheduledJob(Model model,
          String expression,
          String jobname,
          String emessage) {
        return methods.viewCreateScheduledJob(model, expression, jobname, emessage);
      }
    };
    classUnderTest.setManager(manager);
    classUnderTest.setScheduler(scheduler);    
    
    replay();
    methodsControl.replay();
    
    String result = classUnderTest.createScheduledJob(model, null, null);
    
    verify();
    methodsControl.verify();
    assertEquals("something", result);
  }
  
  public void testCreateScheduledJob() throws Exception {
    MockControl methodsControl = MockControl.createControl(MockMethods.class);
    final MockMethods methods = (MockMethods)methodsControl.getMock();
    
    scheduler.register("0 * * * * ?", "DEF");
    schedulerControl.setReturnValue(10);
    
    classUnderTest = new SchedulerController() {
      protected String viewCreateScheduledJob(Model model,
          String expression,
          String jobname,
          String emessage) {
        return methods.viewCreateScheduledJob(model, expression, jobname, emessage);
      }
    };
    classUnderTest.setManager(manager);
    classUnderTest.setScheduler(scheduler);    
    
    replay();
    methodsControl.replay();
    
    String result = classUnderTest.createScheduledJob(model, "0 * * * * ?", "DEF");
    
    verify();
    methodsControl.verify();
    assertEquals("redirect:showschedule.htm", result);
  }

  public void testCreateScheduledJob_badCron() throws Exception {
    MockControl methodsControl = MockControl.createControl(MockMethods.class);
    final MockMethods methods = (MockMethods)methodsControl.getMock();

    scheduler.register("0 * * * * ?", "DEF");
    schedulerControl.setThrowable(new SchedulerException("abc"));
    methods.viewCreateScheduledJob(model, "0 * * * * ?", "DEF", "Could not register: abc");
    methodsControl.setReturnValue("abc.htm");
    
    classUnderTest = new SchedulerController() {
      protected String viewCreateScheduledJob(Model model,
          String expression,
          String jobname,
          String emessage) {
        return methods.viewCreateScheduledJob(model, expression, jobname, emessage);
      }
    };
    classUnderTest.setManager(manager);
    classUnderTest.setScheduler(scheduler);    

    replay();
    methodsControl.replay();
    
    String result = classUnderTest.createScheduledJob(model, "0 * * * * ?", "DEF");
    
    verify();
    methodsControl.verify();
    assertEquals("abc.htm", result);
  }
  
  public void testShowScheduledJob_initial() throws Exception {
    MockControl methodsControl = MockControl.createControl(MockMethods.class);
    final MockMethods methods = (MockMethods)methodsControl.getMock();
    
    CronEntry entry = new CronEntry(10, "* * *", "abc");
    
    scheduler.getEntry(10);
    schedulerControl.setReturnValue(entry);
    methods.viewShowScheduledJob(model, 10, "* * *", "abc", null);
    methodsControl.setReturnValue("dumdi");
    
    classUnderTest = new SchedulerController() {
      protected String viewShowScheduledJob(Model model,
          int id,
          String expression,
          String jobname,
          String emessage) {
        return methods.viewShowScheduledJob(model, id, expression, jobname, emessage);
      }
    };
    classUnderTest.setScheduler(scheduler);     

    replay();
    methodsControl.replay();
    
    String result = classUnderTest.showScheduledJob(model, 10, null, null, null);
    
    verify();
    methodsControl.verify();
    
    assertEquals("dumdi", result);
  }
  
  public void testShowScheduledJob_modify() throws Exception {
    CronEntry entry = new CronEntry(10, "* * *", "abc");
    scheduler.getEntry(10);
    schedulerControl.setReturnValue(entry);
    scheduler.reregister(10, "* * *", "BAC");
    
    replay();
    
    String result = classUnderTest.showScheduledJob(model, 10, "* * *", "BAC", "Modify");
    
    verify();
    assertEquals("redirect:showschedule.htm", result);
  }

  public void testShowScheduledJob_delete() throws Exception {
    CronEntry entry = new CronEntry(10, "* * *", "abc");
    scheduler.getEntry(10);
    schedulerControl.setReturnValue(entry);
    scheduler.unregister(10);
    
    replay();
    
    String result = classUnderTest.showScheduledJob(model, 10, "* * *", "BAC", "Delete");
    
    verify();
    assertEquals("redirect:showschedule.htm", result);
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
    
    String result = classUnderTest.showScheduledJob(model, 10, null, null, "Modify");
    
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
    
    String result = classUnderTest.showScheduledJob(model, 10, "abc", "abc", "DoIt");
    
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
    
    String result = classUnderTest.showScheduledJob(model, 10, "abc", "abc", null);
    
    verify();
    methodsControl.verify();
    assertEquals("alfons", result);
  }
  
  public void testViewCreateScheduledJob() throws Exception {
    List<String> jobnames = new ArrayList<String>();

    manager.getNames();
    managerControl.setReturnValue(jobnames);
    model.addAttribute("jobnames", jobnames);
    modelControl.setReturnValue(null);
    model.addAttribute("expression", "0 * * * * ?");
    modelControl.setReturnValue(null);
    model.addAttribute("jobname", "nisse");
    modelControl.setReturnValue(null);
    
    replay();
    
    String result = classUnderTest.viewCreateScheduledJob(model, "0 * * * * ?", "nisse", null);
    
    verify();
    assertEquals("createscheduledjob", result);
  }

  public void testViewShowScheduledJob() throws Exception {
    List<String> jobnames = new ArrayList<String>();

    manager.getNames();
    managerControl.setReturnValue(jobnames);
    model.addAttribute("jobnames", jobnames);
    modelControl.setReturnValue(null);
    model.addAttribute("id", 10);
    modelControl.setReturnValue(null);
    model.addAttribute("expression", "0 1 0 1 0 ?");
    modelControl.setReturnValue(null);
    model.addAttribute("jobname", "nisse");
    modelControl.setReturnValue(null);
    
    replay();

    String result = classUnderTest.viewShowScheduledJob(model, 10, "0 1 0 1 0 ?", "nisse", null);
    
    verify();
    assertEquals("showscheduledjob", result);
  }
  
  public void testViewShowSchedule() throws Exception {
    
    replay();
    
    String result = classUnderTest.viewShowSchedule(model, null);
    
    verify();
    assertEquals("showschedule", result);
  }
  
  public void testViewShowSchedule_emessage() throws Exception {
    model.addAttribute("emessage", "an error occured");
    modelControl.setReturnValue(null);
    
    replay();
    
    String result = classUnderTest.viewShowSchedule(model, "an error occured");
    
    verify();
    assertEquals("showschedule", result);
  }
}
