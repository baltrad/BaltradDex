package eu.baltrad.beastui.web.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.ui.Model;

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.router.IRouterManager;

public class CompositeRoutesControllerTest extends TestCase {
  private static interface MethodMocker {
    public String viewCreateRoute(Model model, String name, String author,
        Boolean active, String description, List<String> recipients, Boolean byscan,
        String areaid, Integer interval, Integer timeout, List<String> sources, String emessage);
    
    public List<String> getSources();
    
    public List<Integer> getIntervals();
  }
  
  private CompositeRoutesController classUnderTest = null;
  private MockControl managerControl = null;
  private IRouterManager manager = null;
  private MockControl adaptorControl = null;
  private IBltAdaptorManager adaptorManager = null;
  private MockControl modelControl = null;
  private Model model = null;
  private MockControl methodControl = null;
  private MethodMocker method = null;

  protected void setUp() throws Exception {
    managerControl = MockControl.createControl(IRouterManager.class);
    manager = (IRouterManager) managerControl.getMock();
    adaptorControl = MockControl.createControl(IBltAdaptorManager.class);
    adaptorManager = (IBltAdaptorManager) adaptorControl.getMock();
    modelControl = MockControl.createControl(Model.class);
    model = (Model) modelControl.getMock();
    methodControl = MockControl.createControl(MethodMocker.class);
    method = (MethodMocker)methodControl.getMock();
    
    classUnderTest = new CompositeRoutesController() {
      protected String viewCreateRoute(Model model, String name, String author,
          Boolean active, String description, List<String> recipients, Boolean byscan,
          String areaid, Integer interval, Integer timeout, List<String> sources, String emessage) {
        return method.viewCreateRoute(model, name, author, active, description, recipients, byscan, areaid,
            interval, timeout, sources, emessage);
      }
      protected List<String> getSources() {
        return method.getSources();
      }
      protected List<Integer> getIntervals() {
        return method.getIntervals();
      }
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
  }
  
  public void tearDown() throws Exception {
    classUnderTest = null;
    managerControl = null;
    manager = null;
    adaptorControl = null;
    adaptorManager = null;
    modelControl = null;
    model = null;
  }
  
  private void replay() {
    managerControl.replay();
    adaptorControl.replay();
    modelControl.replay();
    methodControl.replay();
  }

  private void verify() {
    managerControl.verify();
    adaptorControl.verify();
    modelControl.verify();
    methodControl.verify();
  }
  
  public void testCreateRoute_initial() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    method.viewCreateRoute(model, null, null, null, null, null, null, null, null, null, null, null);
    methodControl.setReturnValue("somestring");
    replay();
    
    String result = classUnderTest.createRoute(model, null, null, null, null,
        null, null, null, null, null, null);

    verify();
    assertEquals("somestring", result);
  }
  
  public void testCreateRoute_noAdaptors() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);

    model.addAttribute("emessage",
        "No adaptors defined, please add one before creating a route.");
    modelControl.setReturnValue(null);

    replay();

    String result = classUnderTest.createRoute(model, null, null, null, null,
        null, null, null, null, null, null);

    verify();
    assertEquals("redirect:adaptors.htm", result);
  }
  
  public void testCreateRoute_noName() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    String name = null;
    String author = "nisse";
    Boolean active = true;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Boolean byscan = new Boolean(false);
    String areaid = "xyz";
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    String emessage = "Name must be specified.";
    
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    
    method.viewCreateRoute(model, name, author, active, description, recipients, byscan, areaid, interval, timeout, sources, emessage);
    methodControl.setReturnValue("somestring");
    
    replay();
    String result = classUnderTest.createRoute(model, name, author, active, description,
        recipients, byscan, areaid, interval, timeout, sources);
    
    verify();
    assertEquals("somestring", result);
    
  }

  public void testCreateRoute_noAreaid() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    String name = "somename";
    String author = "nisse";
    Boolean active = true;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Boolean byscan = new Boolean(false);
    String areaid = null;
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    String emessage = "Areaid must be specified.";
    
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    
    method.viewCreateRoute(model, name, author, active, description, recipients, byscan, areaid, interval, timeout, sources, emessage);
    methodControl.setReturnValue("somestring");
    
    replay();
    String result = classUnderTest.createRoute(model, name, author, active, description,
        recipients, byscan, areaid, interval, timeout, sources);
    
    verify();
    assertEquals("somestring", result);
    
  }

  public void testCreateRoute_noSources() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    String name = "somename";
    String author = "nisse";
    Boolean active = true;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Boolean byscan = new Boolean(false);
    String areaid = "area";
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    String emessage = "Must specify at least one source.";
    
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    
    method.viewCreateRoute(model, name, author, active, description, recipients, byscan, areaid, interval, timeout, sources, emessage);
    methodControl.setReturnValue("somestring");
    
    replay();
    String result = classUnderTest.createRoute(model, name, author, active, description,
        recipients, byscan, areaid, interval, timeout, sources);
    
    verify();
    assertEquals("somestring", result);
  }
  
  public void testViewCreateRoute() throws Exception {
    List<String> adaptornames = new ArrayList<String>();
    List<String> sourceids = new ArrayList<String>();
    List<Integer> intervals = new ArrayList<Integer>();
    
    String name = "A";
    String author = "B";
    Boolean active = true;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Boolean byscan = new Boolean(false);
    String areaid = "blt_lambert";
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    String emessage = null;

    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptornames);
    method.getSources();
    methodControl.setReturnValue(sourceids);
    method.getIntervals();
    methodControl.setReturnValue(intervals);

    model.addAttribute("adaptors", adaptornames);
    modelControl.setReturnValue(null);
    model.addAttribute("sourceids", adaptornames);
    modelControl.setReturnValue(null);
    model.addAttribute("intervals", intervals);
    modelControl.setReturnValue(null);
    model.addAttribute("name", name);
    modelControl.setReturnValue(null);
    model.addAttribute("author", author);
    modelControl.setReturnValue(null);
    model.addAttribute("active", active);
    modelControl.setReturnValue(null);
    model.addAttribute("description", description);
    modelControl.setReturnValue(null);
    model.addAttribute("recipients", recipients);
    modelControl.setReturnValue(null);
    model.addAttribute("byscan", byscan);
    modelControl.setReturnValue(null);
    model.addAttribute("areaid", areaid);
    modelControl.setReturnValue(null);
    model.addAttribute("interval", interval);
    modelControl.setReturnValue(null);
    model.addAttribute("timeout", timeout);
    modelControl.setReturnValue(null);
    model.addAttribute("sources", sources);
    modelControl.setReturnValue(null);
    
    classUnderTest = new CompositeRoutesController() {
      protected List<String> getSources() {
        return method.getSources();
      }
      protected List<Integer> getIntervals() {
        return method.getIntervals();
      }      
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    
    replay();
    String result = classUnderTest.viewCreateRoute(model, name, author, active, description,
        recipients, byscan, areaid, interval, timeout, sources, emessage);
    verify();
    assertEquals("compositeroute_create", result);
  }

  public void testViewCreateRoute_emessage() throws Exception {
    List<String> adaptornames = new ArrayList<String>();
    List<String> sourceids = new ArrayList<String>();
    List<Integer> intervals = new ArrayList<Integer>();
    
    String name = "A";
    String author = "B";
    Boolean active = true;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Boolean byscan = new Boolean(false);
    String areaid = "blt_lambert";
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    String emessage = "Some message";

    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptornames);
    method.getSources();
    methodControl.setReturnValue(sourceids);
    method.getIntervals();
    methodControl.setReturnValue(intervals);

    model.addAttribute("adaptors", adaptornames);
    modelControl.setReturnValue(null);
    model.addAttribute("sourceids", adaptornames);
    modelControl.setReturnValue(null);
    model.addAttribute("intervals", intervals);
    modelControl.setReturnValue(null);
    model.addAttribute("name", name);
    modelControl.setReturnValue(null);
    model.addAttribute("author", author);
    modelControl.setReturnValue(null);
    model.addAttribute("active", active);
    modelControl.setReturnValue(null);
    model.addAttribute("description", description);
    modelControl.setReturnValue(null);
    model.addAttribute("recipients", recipients);
    modelControl.setReturnValue(null);
    model.addAttribute("byscan", byscan);
    modelControl.setReturnValue(null);
    model.addAttribute("areaid", areaid);
    modelControl.setReturnValue(null);
    model.addAttribute("interval", interval);
    modelControl.setReturnValue(null);
    model.addAttribute("timeout", timeout);
    modelControl.setReturnValue(null);
    model.addAttribute("sources", sources);
    modelControl.setReturnValue(null);
    model.addAttribute("emessage", emessage);
    modelControl.setReturnValue(null);
    
    classUnderTest = new CompositeRoutesController() {
      protected List<String> getSources() {
        return method.getSources();
      }
      protected List<Integer> getIntervals() {
        return method.getIntervals();
      }      
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    
    replay();
    String result = classUnderTest.viewCreateRoute(model, name, author, active, description,
        recipients, byscan, areaid, interval, timeout, sources, emessage);
    verify();
    assertEquals("compositeroute_create", result);
  }

  public void testGetSources() {
    final ParameterizedRowMapper<String> mapper = new ParameterizedRowMapper<String>() {
      public String mapRow(ResultSet arg0, int arg1) throws SQLException {return null;}
    };
    List<String> sources = new ArrayList<String>();
    
    MockControl jdbcControl = MockControl.createControl(SimpleJdbcOperations.class);
    SimpleJdbcOperations jdbc = (SimpleJdbcOperations)jdbcControl.getMock();
    
    jdbc.query("select bs.node_id from bdb_source_radars bsr, bdb_sources bs where bsr.id=bs.id",
        mapper);
    jdbcControl.setMatcher(MockControl.ARRAY_MATCHER);
    jdbcControl.setReturnValue(sources);

    classUnderTest = new CompositeRoutesController() {
      protected ParameterizedRowMapper<String> createSourceMapper() {
        return mapper;
      }
    };
    classUnderTest.setJdbcTemplate(jdbc);
    
    jdbcControl.replay();
    
    List<String> result = classUnderTest.getSources();
    
    jdbcControl.verify();
    assertSame(sources, result);
  }
}
