/**
 * 
 */
package eu.baltrad.dex.net.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import eu.baltrad.beast.admin.Administrator;
import eu.baltrad.beast.admin.Command;
import eu.baltrad.beast.admin.CommandResponse;
import eu.baltrad.beast.admin.JsonCommandParser;
import eu.baltrad.beast.admin.JsonGenerator;
import eu.baltrad.beast.admin.command.UserCommand;
import eu.baltrad.beast.admin.command_response.CommandResponseStatus;
import eu.baltrad.beast.admin.objects.User;
import eu.baltrad.beast.security.ISecurityManager;
import eu.baltrad.dex.net.request.impl.NodeRequest;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.manager.impl.UserManager;
import eu.baltrad.dex.user.validator.PasswordValidator;
import eu.baltrad.dex.util.MessageDigestUtil;

/**
 * @author anders
 */
@Controller
public class AdministratorServlet  extends HttpServlet {
  /**
   * Security manager keeping track on security
   */
  private ISecurityManager securityManager = null;

  /**
   * The command parser.
   */
  private JsonCommandParser jsonAdministratorCommandParser = null;
  
  /**
   * The response generator
   */
  private JsonGenerator jsonGenerator = null;
  
  /**
   * Administrator taking care of the beast part of the system
   */
  private Administrator administrator = null;

  /**
   * User manager when user commands are performed.
   */
  private IUserManager userManager;
  
  /**
   * Required to validate passwords affecting user.
   */
  private PasswordValidator validator;

  /**
   * If administrative capabilities should be enabled or not.
   */
  private boolean enabled = false;
  
  /**
   * The logger
   */
  private static Logger logger = LogManager.getLogger(AdministratorServlet.class);
  
  /**
   * Mapping function for supporting the administrator.htm JSON API.
   * @param request the http request
   * @param response the http response
   * @return the model and view
   */
  @RequestMapping("/administrator.htm")
  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
    logger.info("/administrator.htm ContentType: " + request.getContentType());
    request.getSession(true);
    doPost(request, response);
    return new ModelAndView();
  }
  
  /**
   * Translates a dex user into a beast user
   * @param dUser the dex user
   * @return the beast user
   */
  protected User beastUserFromDexUser(eu.baltrad.dex.user.model.User dUser) {
    User result = new User();
    result.setName(dUser.getName());
    result.setPassword("<secret>");
    result.setRole(dUser.getRole());
    return result;
  }

  /**
   * Gets the node name (Node-Name) from the servlet request header
   * @param request the request
   * @return the node name
   */
  protected String getNodeName(HttpServletRequest request) {
    String nodeName = (String)request.getAttribute("Node-Name");
    if (nodeName == null) {
      nodeName = request.getHeader("Node-Name");
    }
    return nodeName;
  }

  /**
   * Gets the message date (Date) from the servlet request header
   * @param request the request
   * @return the date string
   */
  protected String getMessageDate(HttpServletRequest request) {
    String messageDate = (String)request.getAttribute("Date");
    if (messageDate == null) {
      messageDate = request.getHeader("Date");
    }
    return messageDate;
  }

  /**
   * Gets the signature (Signature) from the servlet request header
   * @param request the request
   * @return the signature
   */
  protected String getSignature(HttpServletRequest request) {
    String signature = (String)request.getAttribute("Signature");
    if (signature == null) {
      signature = request.getHeader("Signature");
    }
    return signature;
  }
  
  
  /**
   * Reads data from the input stream
   * @param is the input stream
   * @return the data
   */
  protected String readInputStreamFromRequest(HttpServletRequest request)  throws IOException {
    return IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8.name());
  }
  
  /**
   * Handles the actual request and produces the response.
   * @param request the http request
   * @param response the http response
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    if (!enabled) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
    
    try {
      if (request.getContentType().startsWith("application/json")) {
        String data = readInputStreamFromRequest(request);
        Command command = getJsonAdministratorCommandParser().parse(data);
        if (command != null) {
          logger.info("Command parsed: " + command.getOperation() + ", " + command.getClass());
        }

        if (!isAuthorizedIP(request) ||
            !securityManager.validate(getNodeName(request), getMessageDate(request), getSignature(request), command)) {
          logger.warn("Admin command rejected: " + command.getOperation() + ", from " + request.getRemoteAddr());
          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
          return;
        }
        
        logger.info("Command approved: " + command.getOperation() + ", " + command.getClass());

        String jsonResponse = null;
        if (command instanceof UserCommand) {
          UserCommand userCommand = (UserCommand)command;
          User user = userCommand.getUser();
          
          logger.info("UserCommand: operation = " + userCommand.getOperation());
          if (userCommand.getOperation().equals(UserCommand.CHANGE_PASSWORD)) {
            eu.baltrad.dex.user.model.User dexUser = userManager.load(user.getName());
            CommandResponseStatus status = new CommandResponseStatus(false);
            if (dexUser != null) {
              if (userManager.updatePassword(dexUser.getId(), MessageDigestUtil.createHash("MD5", user.getNewpassword())) > 0) {
                status =  new CommandResponseStatus(true);
              }
              jsonResponse = getJsonGenerator().toJson(status);
            }
          } else if (userCommand.getOperation().equals(UserCommand.LIST)) {
            logger.info("Returning list of users");
            List<eu.baltrad.dex.user.model.User> users = userManager.load();
            List<User> foundUsers = new ArrayList<User>();
            logger.info("Dex users: " + users.size());
            for (eu.baltrad.dex.user.model.User dUser : users) {
              if (user.getRole() == null || user.getRole().equals(dUser.getRole())) {
                if (dUser.getRole().equals(User.ROLE_ADMIN) ||
                    dUser.getRole().equals(User.ROLE_OPERATOR) ||
                    dUser.getRole().equals(User.ROLE_USER)) {
                  foundUsers.add(beastUserFromDexUser(dUser));
                }
              }
            }
            jsonResponse = getJsonGenerator().toJsonFromUsers(foundUsers);
          }
        } else {
          CommandResponse commandResponse = administrator.handle(command);
          logger.info("Response: " + commandResponse);
          jsonResponse = getJsonGenerator().toJson(commandResponse);
        }
        
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.SC_OK);
        out.print(jsonResponse);
        out.flush();
      }
    } catch (Exception e) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      logger.error("Failed to process request", e);
    }
  }
  
  /**
   * Tests if the request comes from a known source
   * @param request the servlet request
   * @return true if request should be allowed
   */
  protected boolean isAuthorizedIP(HttpServletRequest request) {
    String remote = request.getRemoteAddr();
    logger.info("Got request from = " + remote + ", local = " + request.getLocalAddr());

    if (remote != null
        && (remote.equals("127.0.0.1") || 
            remote.equals("::1") || 
            remote.equals("0:0:0:0:0:0:0:1") || 
            remote.equals(request.getLocalAddr()))) {
      return true;
    }
    return false;
  }
  
  /**
   * @return the security manager
   */
  public ISecurityManager getSecurityManager() {
    return securityManager;
  }

  /**
   * @param securityManager the security manager
   */
  @Autowired
  public void setSecurityManager(ISecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  /**
   * @return the json parser used for parsing beast admin commands
   */
  public JsonCommandParser getJsonAdministratorCommandParser() {
    return jsonAdministratorCommandParser;
  }

  /**
   * @param commandParser the json parser
   */
  @Autowired
  public void setJsonAdministratorCommandParser(JsonCommandParser commandParser) {
    this.jsonAdministratorCommandParser = commandParser;
  }

  /**
   * @return the administrator
   */
  public Administrator getAdministrator() {
    return administrator;
  }

  /**
   * @param administrator the administrator to set
   */
  @Autowired
  public void setAdministrator(Administrator administrator) {
    this.administrator = administrator;
  }

  /**
   * @return the jsonCommandResponseGenerator
   */
  public JsonGenerator getJsonGenerator() {
    return jsonGenerator;
  }

  /**
   * @param jsonGenerator the jsonGenerator to set
   */
  @Autowired
  public void setJsonGenerator(JsonGenerator jsonGenerator) {
    this.jsonGenerator = jsonGenerator;
  }

  /**
   * @return the userManager
   */
  public IUserManager getUserManager() {
    return userManager;
  }

  /**
   * @param userManager the userManager to set
   */
  @Autowired
  public void setUserManager(IUserManager userManager) {
    this.userManager = userManager;
  }

  /**
   * @return the validator
   */
  public PasswordValidator getValidator() {
    return validator;
  }

  /**
   * @param validator the validator to set
   */
  @Autowired
  public void setValidator(PasswordValidator validator) {
    this.validator = validator;
  }

  /**
   * @return the enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}
