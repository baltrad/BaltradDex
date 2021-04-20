/**
 * 
 */
package eu.baltrad.dex.net.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.codehaus.jackson.JsonNode;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.beast.admin.Administrator;
import eu.baltrad.beast.admin.Command;
import eu.baltrad.beast.admin.CommandResponse;
import eu.baltrad.beast.admin.JsonCommandParser;
import eu.baltrad.beast.admin.JsonCommandParserImpl;
import eu.baltrad.beast.admin.JsonGenerator;
import eu.baltrad.beast.security.ISecurityManager;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.validator.PasswordValidator;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * @author anders
 */
public class AdministratorServletTest extends EasyMockSupport {
  private AdministratorServlet classUnderTest = null;
  private ISecurityManager securityManager = null;
  private JsonCommandParser jsonCommandParser = null;
  private JsonGenerator jsonGenerator = null;
  private Administrator administrator = null;
  private IUserManager userManager = null;
  private PasswordValidator validator = null;;
  private HttpServletRequest servletRequest = null;
  private HttpServletResponse servletResponse = null;
  
  private static class TestCommand extends Command {
    private String raw = null;
    public TestCommand(String raw) {
      this.raw = raw;
    }
    @Override
    public String getOperation() {
      return "test_command";
    }
    
    @Override
    public String getRawMessage() {
      return this.raw;
    }
    
    @Override
    public boolean validate() {
      return true;
    }
  }
  
  @Before
  public void setUp() throws Exception {
    securityManager = createMock(ISecurityManager.class);
    jsonCommandParser = createMock(JsonCommandParser.class);
    jsonGenerator = createMock(JsonGenerator.class);
    administrator = createMock(Administrator.class);
    userManager = createMock(IUserManager.class);
    validator = createMock(PasswordValidator.class);
    
    servletRequest = createMock(HttpServletRequest.class);
    servletResponse = createMock(HttpServletResponse.class);
    
    classUnderTest = createMockBuilder(AdministratorServlet.class)
        .addMockedMethod("readInputStreamFromRequest", HttpServletRequest.class)
        .addMockedMethod("isAuthorizedIP", HttpServletRequest.class)
        .addMockedMethod("getNodeName", HttpServletRequest.class)
        .addMockedMethod("getMessageDate", HttpServletRequest.class)
        .addMockedMethod("getSignature", HttpServletRequest.class)
        .createMock();
    
    classUnderTest.setSecurityManager(securityManager);
    classUnderTest.setJsonAdministratorCommandParser(jsonCommandParser);
    classUnderTest.setJsonGenerator(jsonGenerator);
    classUnderTest.setAdministrator(administrator);
    classUnderTest.setUserManager(userManager);
    classUnderTest.setValidator(validator);
  }
  
  @After
  public void tearDown() throws Exception {
    securityManager = null;
    jsonCommandParser = null;
    jsonGenerator = null;
    administrator = null;
    userManager = null;
    validator = null;
    classUnderTest = null;

    servletRequest = null;
    servletResponse = null;
  }
  
  @Test
  public void doPost_basicFlow() throws Exception {
    Command command = new TestCommand("ABC");
    CommandResponse commandResponse = createMock(CommandResponse.class);
    PrintWriter writer = createMock(PrintWriter.class);

    expect(servletRequest.getContentType()).andReturn("application/json").anyTimes();
    expect(classUnderTest.readInputStreamFromRequest(servletRequest)).andReturn("ABC");
    expect(jsonCommandParser.parse("ABC")).andReturn(command);
    expect(classUnderTest.isAuthorizedIP(servletRequest)).andReturn(true);
    expect(classUnderTest.getNodeName(servletRequest)).andReturn("nodename");
    expect(classUnderTest.getMessageDate(servletRequest)).andReturn("MD");
    expect(classUnderTest.getSignature(servletRequest)).andReturn("SIGN");
    expect(securityManager.validate("nodename", "MD", "SIGN", command)).andReturn(true);
    expect(administrator.handle(command)).andReturn(commandResponse);
    expect(jsonGenerator.toJson(commandResponse)).andReturn("X");
    expect(servletResponse.getWriter()).andReturn(writer);
    servletResponse.setContentType("application/json");
    servletResponse.setCharacterEncoding("UTF-8");
    servletResponse.setStatus(HttpStatus.SC_OK);
    writer.print("X");
    writer.flush();
    
    replayAll();
    
    classUnderTest.doPost(servletRequest, servletResponse);
    
    verifyAll();
  }

  @Test
  public void doPost_badRemoteAddr() throws Exception {
    Command command = new TestCommand("ABC");

    expect(servletRequest.getContentType()).andReturn("application/json").anyTimes();
    expect(classUnderTest.readInputStreamFromRequest(servletRequest)).andReturn("ABC");
    expect(jsonCommandParser.parse("ABC")).andReturn(command);
    expect(classUnderTest.isAuthorizedIP(servletRequest)).andReturn(false);
    expect(servletRequest.getRemoteAddr()).andReturn("1.2.3.4");

    servletResponse.setStatus(HttpStatus.SC_FORBIDDEN);

    replayAll();
    
    classUnderTest.doPost(servletRequest, servletResponse);
    
    verifyAll();
  }

  
  @Test
  public void doPost_badSignature() throws Exception {
    Command command = new TestCommand("ABC");

    expect(servletRequest.getContentType()).andReturn("application/json").anyTimes();
    expect(classUnderTest.readInputStreamFromRequest(servletRequest)).andReturn("ABC");
    expect(jsonCommandParser.parse("ABC")).andReturn(command);
    expect(classUnderTest.isAuthorizedIP(servletRequest)).andReturn(true);
    
    expect(servletRequest.getRemoteAddr()).andReturn("1.2.3.4");
    expect(classUnderTest.getNodeName(servletRequest)).andReturn("nodename");
    expect(classUnderTest.getMessageDate(servletRequest)).andReturn("MD");
    expect(classUnderTest.getSignature(servletRequest)).andReturn("SIGN");
    expect(securityManager.validate("nodename", "MD", "SIGN", command)).andReturn(false);

    servletResponse.setStatus(HttpStatus.SC_FORBIDDEN);

    replayAll();
    
    classUnderTest.doPost(servletRequest, servletResponse);
    
    verifyAll();
  }
  
  @Test
  public void doPost_missingNodeName() throws Exception {
    Command command = new TestCommand("ABC");

    expect(servletRequest.getContentType()).andReturn("application/json").anyTimes();
    expect(classUnderTest.readInputStreamFromRequest(servletRequest)).andReturn("ABC");
    expect(jsonCommandParser.parse("ABC")).andReturn(command);
    expect(classUnderTest.isAuthorizedIP(servletRequest)).andReturn(true);
    
    expect(servletRequest.getRemoteAddr()).andReturn("1.2.3.4");
    expect(classUnderTest.getNodeName(servletRequest)).andReturn(null);
    expect(classUnderTest.getMessageDate(servletRequest)).andReturn("MD");
    expect(classUnderTest.getSignature(servletRequest)).andReturn("SIGN");
    expect(securityManager.validate(null, "MD", "SIGN", command)).andReturn(false);

    servletResponse.setStatus(HttpStatus.SC_FORBIDDEN);

    replayAll();
    
    classUnderTest.doPost(servletRequest, servletResponse);
    
    verifyAll();
  }
  
  @Test
  public void isAuthorizedIP_ipv4_true() throws Exception {
    expect(servletRequest.getRemoteAddr()).andReturn("127.0.0.1");
    
    classUnderTest = new AdministratorServlet();
    
    replayAll();
    
    boolean result = classUnderTest.isAuthorizedIP(servletRequest);
    
    verifyAll();
    assertEquals(true, result);
  }

  @Test
  public void isAuthorizedIP_ipv6_true() throws Exception {
    expect(servletRequest.getRemoteAddr()).andReturn("::1");
    
    classUnderTest = new AdministratorServlet();
    
    replayAll();
    
    boolean result = classUnderTest.isAuthorizedIP(servletRequest);
    
    verifyAll();
    assertEquals(true, result);
  }

  @Test
  public void isAuthorizedIP_denied_null() throws Exception {
    expect(servletRequest.getRemoteAddr()).andReturn(null);
    
    classUnderTest = new AdministratorServlet();
    
    replayAll();
    
    boolean result = classUnderTest.isAuthorizedIP(servletRequest);
    
    verifyAll();
    assertEquals(false, result);
  }

  
  @Test
  public void isAuthorizedIP_denied_ipv4() throws Exception {
    expect(servletRequest.getRemoteAddr()).andReturn("1.2.3.4");
    
    classUnderTest = new AdministratorServlet();
    
    replayAll();
    
    boolean result = classUnderTest.isAuthorizedIP(servletRequest);
    
    verifyAll();
    assertEquals(false, result);
  }

  @Test
  public void isAuthorizedIP_denied_ipv6() throws Exception {
    expect(servletRequest.getRemoteAddr()).andReturn("0:1:2:3.4:5:6");
    
    classUnderTest = new AdministratorServlet();
    
    replayAll();
    
    boolean result = classUnderTest.isAuthorizedIP(servletRequest);
    
    verifyAll();
    assertEquals(false, result);
  }
}
