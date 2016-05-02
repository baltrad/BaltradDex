package eu.baltrad.dex.net.protocol.json.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.protocol.json.impl.JsonProtocol20;
import eu.baltrad.dex.user.model.User;

public class JsonProtocol20Test extends EasyMockSupport {
  JsonProtocol20 classUnderTest = null;
  
  @Before
  public void setUp() throws Exception {
    classUnderTest = new JsonProtocol20();
  }
  
  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
  }
  
  @Test
  public void userAccountToJson() throws Exception {
    User user = new User();
    user.setCountryCode("CC");
    user.setLocality("LOC");
    user.setName("MyName");
    user.setNodeAddress("http://localhost:8080");
    user.setOrgName("ORG");
    user.setOrgUnit("OU");
    user.setPassword("xxx");
    user.setRole("ADMIN");
    user.setState("STATE");
    
    String result = classUnderTest.userAccountToJson(user);
    
    User resultUser = new ObjectMapper().readValue(result, new TypeReference<User>(){});
    
    assertEquals("CC", resultUser.getCountryCode());
    assertEquals("LOC", resultUser.getLocality());
    assertEquals("MyName", resultUser.getName());
    assertEquals("http://localhost:8080", resultUser.getNodeAddress());
    assertEquals("ORG", resultUser.getOrgName());
    assertEquals("OU", resultUser.getOrgUnit());
    assertEquals("xxx", resultUser.getPassword());
    assertEquals("ADMIN", resultUser.getRole());
    assertEquals("STATE", resultUser.getState());
  }

  @Test
  public void userAccountToJson_noRedirect() throws Exception {
    User user = new User();
    user.setCountryCode("CC");
    user.setLocality("LOC");
    user.setName("MyName");
    user.setNodeAddress("http://localhost:8080");
    user.setOrgName("ORG");
    user.setOrgUnit("OU");
    user.setPassword("xxx");
    user.setRole("ADMIN");
    user.setState("STATE");
    user.setRedirectedAddress("http://slask.se");
    
    String result = classUnderTest.userAccountToJson(user);
    
    User resultUser = new ObjectMapper().readValue(result, new TypeReference<User>(){});
    
    assertEquals("CC", resultUser.getCountryCode());
    assertEquals("LOC", resultUser.getLocality());
    assertEquals("MyName", resultUser.getName());
    assertEquals("http://localhost:8080", resultUser.getNodeAddress());
    assertEquals("ORG", resultUser.getOrgName());
    assertEquals("OU", resultUser.getOrgUnit());
    assertEquals("xxx", resultUser.getPassword());
    assertEquals("ADMIN", resultUser.getRole());
    assertEquals("STATE", resultUser.getState());
    assertEquals(null, resultUser.getRedirectedAddress());
  }
  
  @Test
  public void jsonToUserAccount() throws Exception {
    String json = "{\"name\":\"MyName\",\"state\":\"STATE\",\"password\":\"xxx\",\"role\":\"ADMIN\",\"orgName\":\"ORG\",\"orgUnit\":\"OU\",\"locality\":\"LOC\",\"countryCode\":\"CC\",\"nodeAddress\":\"http://localhost:8080\"}";
    
    User resultUser = classUnderTest.jsonToUserAccount(json);
    
    assertEquals("CC", resultUser.getCountryCode());
    assertEquals("LOC", resultUser.getLocality());
    assertEquals("MyName", resultUser.getName());
    assertEquals("http://localhost:8080", resultUser.getNodeAddress());
    assertEquals("ORG", resultUser.getOrgName());
    assertEquals("OU", resultUser.getOrgUnit());
    assertEquals("xxx", resultUser.getPassword());
    assertEquals("ADMIN", resultUser.getRole());
    assertEquals("STATE", resultUser.getState());
  }
  
  @Test
  public void dataSourcesToJson() throws Exception {
    DataSource d1 = new DataSource();
    d1.setName("NAME");
    d1.setType(DataSource.LOCAL);
    d1.setDescription("a description");
    d1.setFileObject("A file object");   // This should not be added to the 2.0 protocol
    d1.setSource("a source");            // This should not be added to the 2.0 protocol  
    
    Set<DataSource> sets = new HashSet<DataSource>();
    sets.add(d1);
    
    String result = classUnderTest.dataSourcesToJson(sets);
    
    assertEquals(true, result.indexOf("\"source\":") < 0);
    assertEquals(true, result.indexOf("\"fileObject\":") < 0);
    
    Set<DataSource> resultDS = new ObjectMapper().readValue(result, new TypeReference<HashSet<DataSource>>(){});
    DataSource ds = resultDS.iterator().next();
    assertEquals("NAME", ds.getName());
    assertEquals(DataSource.LOCAL, ds.getType());
    assertEquals("a description", ds.getDescription());
    assertNull(ds.getFileObject());
    assertNull(ds.getSource());
  }
  
  @Test
  public void jsonToDataSources() throws Exception {
    String json = "[{\"name\":\"NAME\",\"type\":\"local\",\"description\":\"a description\"}]";
    Set<DataSource> resultDS = classUnderTest.jsonToDataSources(json);
    DataSource ds = resultDS.iterator().next();
    assertEquals("NAME", ds.getName());
    assertEquals(DataSource.LOCAL, ds.getType());
    assertEquals("a description", ds.getDescription());
    assertNull(ds.getFileObject());
    assertNull(ds.getSource());
  }
  
  @Test
  public void jsonToDataSources_21() throws Exception {
    // We can handle data sources from 2.1 releases as well, so just let em in...
    String json = "[{\"name\":\"NAME\",\"type\":\"local\",\"description\":\"a description\", \"source\":\"a source\", \"fileObject\":\"a file object\"}]";
    Set<DataSource> resultDS = classUnderTest.jsonToDataSources(json);
    DataSource ds = resultDS.iterator().next();
    assertEquals("NAME", ds.getName());
    assertEquals(DataSource.LOCAL, ds.getType());
    assertEquals("a description", ds.getDescription());
    assertEquals("a file object", ds.getFileObject());
    assertEquals("a source", ds.getSource());
  }
  
  @Test
  public void subscriptionsToJson() throws Exception {
    List<Subscription> subscriptions = new ArrayList<Subscription>();
    Subscription s1 = new Subscription();
    s1.setActive(true);
    s1.setDataSource("ds1");
    s1.setDate(new Date());
    s1.setSyncronized(true);
    s1.setType("t1");
    s1.setUser("u1");
    
    subscriptions.add(s1);
    
    String result = classUnderTest.subscriptionsToJson(subscriptions);

    List<Subscription> resultS = new ObjectMapper().readValue(result, new TypeReference<ArrayList<Subscription>>(){});
    assertEquals(1, resultS.size());
    assertEquals(s1.isActive(), resultS.get(0).isActive());
    assertEquals(s1.getDataSource(), resultS.get(0).getDataSource());
    assertEquals(s1.getDate(), resultS.get(0).getDate());
    assertEquals(s1.isSyncronized(), resultS.get(0).isSyncronized());
    assertEquals(s1.getType(), resultS.get(0).getType());
    assertEquals(s1.getUser(), resultS.get(0).getUser());
  }
  
  @Test
  public void jsonToSubscriptions() throws Exception {
    String json = "[{\"type\":\"t1\",\"date\":1391011732905,\"active\":true,\"dataSource\":\"ds1\",\"syncronized\":true,\"user\":\"u1\"}]";
    
    List<Subscription> result = classUnderTest.jsonToSubscriptions(json);

    assertEquals(1, result.size());
    assertEquals(true, result.get(0).isActive());
    assertEquals("ds1", result.get(0).getDataSource());
    assertEquals(true, result.get(0).isSyncronized());
    assertEquals("t1", result.get(0).getType());
    assertEquals("u1", result.get(0).getUser());
  }
}
