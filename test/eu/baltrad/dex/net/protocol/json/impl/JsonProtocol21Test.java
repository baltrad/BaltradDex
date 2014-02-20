package eu.baltrad.dex.net.protocol.json.impl;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.protocol.json.impl.JsonProtocol20;
import eu.baltrad.dex.net.protocol.json.impl.JsonProtocol21;
import eu.baltrad.dex.user.model.User;

public class JsonProtocol21Test extends EasyMockSupport {
  JsonProtocol21 classUnderTest = null;
  
  @Before
  public void setUp() throws Exception {
    classUnderTest = new JsonProtocol21();
  }
  
  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
  }
  
  @Test
  public void userAccountToJson() throws Exception {
    JsonProtocol20 protocol20 = new JsonProtocol20();
    
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

    String expected = protocol20.userAccountToJson(user);
    String v21 = classUnderTest.userAccountToJson(user);
    
    assertEquals(expected, v21);
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
    
    assertEquals(true, result.indexOf("\"source\":") > 0);
    assertEquals(true, result.indexOf("\"fileObject\":") > 0);
    
    Set<DataSource> resultDS = new ObjectMapper().readValue(result, new TypeReference<HashSet<DataSource>>(){});
    DataSource ds = resultDS.iterator().next();
    assertEquals("NAME", ds.getName());
    assertEquals(DataSource.LOCAL, ds.getType());
    assertEquals("a description", ds.getDescription());
    assertEquals("A file object", ds.getFileObject());
    assertEquals("a source", ds.getSource());
  }
  
  @Test
  public void jsonToDataSource() throws Exception {
    String json = "[{\"name\":\"NAME\",\"type\":\"local\",\"description\":\"a description\", \"source\":\"a source\", \"fileObject\":\"a file object\"}]";
    Set<DataSource> resultDS = classUnderTest.jsonToDataSources(json);
    DataSource ds = resultDS.iterator().next();
    assertEquals("NAME", ds.getName());
    assertEquals(DataSource.LOCAL, ds.getType());
    assertEquals("a description", ds.getDescription());
    assertEquals("a file object", ds.getFileObject());
    assertEquals("a source", ds.getSource());
  }
}
