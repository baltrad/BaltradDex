package eu.baltrad.dex.keystore.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.keyczar.exceptions.KeyczarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.baltrad.beast.exchange.IExchangeManager;
import eu.baltrad.beast.security.Authorization;
import eu.baltrad.beast.security.AuthorizationException;
import eu.baltrad.beast.security.AuthorizationManager;
import eu.baltrad.beast.security.AuthorizationRequest;
import eu.baltrad.beast.security.AuthorizationRequestManager;
import eu.baltrad.beast.security.IAuthorizationManager;
import eu.baltrad.beast.security.IAuthorizationRequestManager;
import eu.baltrad.beast.security.ISecurityManager;
import eu.baltrad.beast.security.SecurityStorageException;
import eu.baltrad.dex.log.StickyLevel;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.controller.util.ModelMessageHelper;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.ResponseParser;
import eu.baltrad.dex.net.protocol.ResponseParserException;
import eu.baltrad.dex.net.util.UrlValidatorUtil;
import eu.baltrad.dex.net.util.httpclient.impl.HttpClientUtil;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.beast.exchange.ExchangeStatusException;

@Controller
public class AuthorizationController {
  /**
   * The security manager
   */
  private ISecurityManager securityManager = null;
  
  /**
   * The exchange manager;
   */
  private IExchangeManager exchangeManager = null;
  
  /**
   * The authorization manager
   */
  private IAuthorizationManager authorizationManager;
  
  /**
   * The user manager
   */
  private IUserManager userManager;
  
  /**
   * THe authorization request manager
   */
  private IAuthorizationRequestManager requestManager = null;

  /**
   * The authenticator
   */
  private Authenticator authenticator;
  
  /**
   * Protocol manager
   */
  private ProtocolManager protocolManager = null;

  /**
   * The url validator
   */
  private UrlValidatorUtil urlValidator = new UrlValidatorUtil();

  /**
   * Helper for setting messages
   */
  private ModelMessageHelper messageHelper;

  private final static String DS_KEY_NOT_APPROVED = "datasource.controller.key_not_approved";
  private final static String DS_CONNECTION_UNAUTHORIZED = "datasource.controller.connection_unauthorized";
  private final static String DS_SERVER_ERROR_KEY = "datasource.controller.server_error";
  private final static String DS_MESSAGE_SIGNER_ERROR_KEY = "datasource.controller.message_signer_error";
  private final static String DS_INTERNAL_CONTROLLER_ERROR_KEY = "datasource.controller.internal_controller_error";
  private final static String DS_HTTP_CONN_ERROR_KEY = "datasource.controller.http_connection_error";
  private final static String DS_GENERIC_CONN_ERROR_KEY = "datasource.controller.generic_connection_error";

  /**
   * Logger
   */
  private static Logger logger = LogManager.getLogger(AuthorizationController.class);

  
  
  /**
   * Lists all unhandled requests as well as authorizations.
   * @param model the model
   * @param submitButton the button (Connect)
   * @return the jsp page to direct to
   */
  @RequestMapping("/authorization_list.htm")
  public String authorizationKeys(Model model,
		  @RequestParam(value = "submitButton", required = false) String submitButton) {
    logger.info("/authorization_keys.htm");
    if (submitButton != null && submitButton.equals("Connect")) {
    	return "redirect:connect_with_remote_host.htm";
    }
    List<AuthorizationRequest> requests = requestManager.list();
    List<AuthorizationRequest> incommingRequests = requestManager.findByOutgoing(false);
    
    for (AuthorizationRequest incomming : incommingRequests) {
      String uuid = incomming.getRequestUUID();
      int idx = 0;
      while (idx < requests.size()) {
        AuthorizationRequest other = requests.get(idx);
        if (other.getRequestUUID().equals(uuid) && other.isOutgoing()) { // If we have an incomming request with same name as outgoing, remove outgoing since it's superfluous but don't remove database
          requests.remove(idx);
        } else {
          idx++;
        }
      }
    }
    
    List<Authorization> authorizations = authorizationManager.list();
    
    logger.info("Adding requests with size: " + requests.size());
    model.addAttribute("localNodeName", securityManager.getLocalNodeName());
    model.addAttribute("requests", requests);
    model.addAttribute("authorizations", authorizations);
    return "authorization_list";
  }
  
  /**
   * Handles authorization requests. Both denial and acceptance and deletion (when request should just be deleted without further processing).
   * @param model the model
   * @param requestUUID the request uuid
   * @param nodeEmail 
   * @param nodeAddress
   * @param submitButton
   * @return
   */
  @RequestMapping("/authorization_request.htm")
  public String authorizationRequest(Model model,
      @RequestParam(value = "uuid", required = false) String requestUUID,
      @RequestParam(value = "nodeEmail", required = false) String nodeEmail,
      @RequestParam(value = "nodeAddress", required = false) String nodeAddress,
      @RequestParam(value = "submitButton", required = false) String submitButton) {
    logger.info("/authorization_request.htm: " + requestUUID + " " + submitButton);
    AuthorizationRequest request = requestManager.get(requestUUID, false);
    if (request != null) {
      if (submitButton != null && submitButton.equals("Accept")) {
        if (!request.getNodeAddress().equals(nodeAddress) ||
            !request.getNodeEmail().equals(nodeEmail)) {
          request.setNodeAddress(nodeAddress);
          request.setNodeEmail(nodeEmail);
          try {
            requestManager.update(request);
          } catch (Exception e) {
            logger.info("Failed to update request before posting approval", e);
            model.addAttribute("emessage", "Could not update request during approval. Message was: " + e.getMessage());
            model.addAttribute("request", request);
            return "authorization_request";
          }
        }
        try {
          exchangeManager.approve(request);
          if (userManager.load(request.getNodeName()) == null) {
            User peer = new User();
            peer.setName(request.getNodeName());
            peer.setNodeAddress(request.getNodeAddress());
            peer.setRole(Role.PEER);
            try {
              userManager.store(peer);
            } catch (Exception e) {
              logger.error("Could not create peer account during approval");
            }
          }
          return "redirect:authorization_list.htm";
        } catch (AuthorizationException e) {
          logger.info("Failed to approve request", e);
          model.addAttribute("emessage", "Could not approve request, is node name same as local? Message was: " + e.getMessage());
        }
      } else if (submitButton != null && submitButton.equals("Deny")) {
        try {
          exchangeManager.deny(request);
          return "redirect:authorization_list.htm";
        } catch (AuthorizationException e) {
          model.addAttribute("emessage", "Could not deny request, is node name same as local?");
        }
      } else if (submitButton != null && submitButton.equals("Delete")) {
        exchangeManager.delete(request);
        return "redirect:authorization_list.htm";
      } else if (submitButton != null && submitButton.equals("Resend")) {
        return connectWithRemoteHost(model, request.getNodeAddress(), request.getMessage(), "Connect");
      }
    }
    
    if (request != null) {
      model.addAttribute("request", request);
    } else {
      model.addAttribute("emessage", "No request with specified uuid found");
      return "redirect:authorization_list.htm";
    }
    return "authorization_request";
  }
  
  @RequestMapping("/authorization_entry.htm")
  public String authorizationEntry(Model model,
      @RequestParam(value = "uuid", required = false) String connectionUUID,
      @RequestParam(value = "nodeEmail", required = false) String email,
      @RequestParam(value = "nodeAddress", required = false) String nodeAddress,
      @RequestParam(value = "authorized", required = false) Boolean authorized,
      @RequestParam(value = "injector", required = false) Boolean injector,
      @RequestParam(value = "submitButton", required = false) String submitButton) {
    logger.info("/authorization_entry.htm: " + connectionUUID + " " + submitButton);
    Authorization auth = authorizationManager.get(connectionUUID);
    if (auth != null) {
      model.addAttribute("authorization", auth);
      if (submitButton != null && submitButton.equals("Save")) {
        logger.info("Node: " + auth.getNodeName() + " auth " + authorized);
        auth.setNodeEmail(email);
        auth.setNodeAddress(nodeAddress);
        auth.setInjector(injector == null ? false : injector.booleanValue());
        auth.setAuthorized(authorized == null ? false : authorized.booleanValue());
        
        if (email == null || email.equals("")) {
          logger.info("Email address needs to be specified");
          model.addAttribute("emessage", "Email address needs to be specified");
        } else if (nodeAddress == null || nodeAddress.equals("")) {
          logger.info("Node address needs to be specified");
          model.addAttribute("emessage", "Node address needs to be specified");
        } else if (auth.isLocal() && authorized != null && authorized.booleanValue() == false) {
          logger.info("Trying to save local auth with disabled authorization");
          model.addAttribute("emessage", "Can not remove authorization for local node");
        } else if (auth.isLocal() && injector != null && injector.booleanValue() == false) {
          logger.info("Trying to save local auth with disabled injector");
          model.addAttribute("emessage", "Can not remove injector for local node");
        } else {
          authorizationManager.update(auth);
        }
        try {
          User user = userManager.load(auth.getNodeName());
          if (user != null) {
            user.setNodeAddress(nodeAddress);
            userManager.update(user);
          }
        } catch (Exception e) {
          model.addAttribute("emessage", "Could not update user node address");
          logger.error("Could not update user node address", e);
        }
        return "redirect:authorization_list.htm"; 
      } else if (submitButton != null && submitButton.equals("Delete")) {
        if (!auth.isLocal()) {
          authorizationManager.delete(auth.getConnectionUUID());
        }
        return "redirect:authorization_list.htm";
      } else if (submitButton != null && submitButton.equals("Connect")) {
        if (nodeAddress != null && urlValidator.validate(nodeAddress)) {
          String ret = connectWithRemotePeer(model, auth, nodeAddress);
          if (ret != null) {
            return ret;
          }
        }
      } else if (submitButton != null && submitButton.equals("Back")) {
        return "redirect:authorization_list.htm";
      }
    } else {
      model.addAttribute("emessage", "No authorization with specified uuid found");
      return "redirect:authorization_list.htm";
    }
    
    return "authorization_entry";
  }

  protected String extractBaseUrlFromRedirect(String baseURI, String originURI, String redirectURI) {
    String appended = originURI.substring(baseURI.length());
    if (redirectURI.endsWith(appended)) {
      String result = redirectURI.substring(0, redirectURI.length() - appended.length());
      if (result.endsWith("/")) {
        result = result.substring(0, result.length()-1);
      }
      return result;
    }
    return redirectURI;
  }
  
  protected String connectWithRemotePeer(Model model, Authorization auth, String url) {
    RequestFactory requestFactory = protocolManager.getFactory(url);
    User localUser = userManager.load(securityManager.getLocalNodeName());
    HttpClientUtil httpClient = null;
    
    if (localUser == null) {
      model.addAttribute("emessage", "No local user !?");
      return "authorization_entry";
    }
    HttpUriRequest req = requestFactory.createDataSourceListingRequest(localUser);
    try {
      httpClient = new HttpClientUtil(60000, 60000);
      authenticator.addCredentials(req, localUser.getName());
      HttpResponse response = httpClient.post(req);
      ResponseParser parser = protocolManager.createParser(response);
      logger.debug("Got a response message of version " + parser.getProtocolVersion());
      logger.debug("Answering node supports version " + parser.getConfiguredProtocolVersion());
      if (parser.isRedirected()) {
        String redirectedAddress = extractBaseUrlFromRedirect(url, req.getURI().toString(), parser.getRedirectURL());
        model.addAttribute("error_message", "Remote host indicates that address has been changed to " + redirectedAddress);
        auth.setRedirectedAddress(redirectedAddress);
        model.addAttribute("authorization", auth);
        return "authorization_entry";
      } else if (parser.getStatusCode() == HttpServletResponse.SC_OK) {
        model.addAttribute("url_input", url);
        model.addAttribute("connect", "connect");
        return "redirect:/node_connected.htm";
      } else if (parser.getStatusCode() == HttpServletResponse.SC_CREATED) {
        User peer = parser.getUserAccount();
        try {
          if (userManager.load(peer.getName()) == null) {
            peer.setRole(Role.PEER);
            userManager.store(peer);
            logger.warn("New peer account created: " + peer.getName());
          }
          model.addAttribute("url_input", url);
          model.addAttribute("connect", "connect");
          return "redirect:/node_connected.htm";
        } catch (Exception e) {
          logger.info("Could not create local peer " + peer.getName());
          model.addAttribute("error_message", "Could not create local peer " + peer.getName());
          model.addAttribute("authorization", auth);
          return "authorization_entry";
        }
      } else if (parser.getStatusCode() == HttpServletResponse.SC_NOT_FOUND) {
        messageHelper.setErrorDetailsMessage(model, DS_KEY_NOT_APPROVED, parser.getReasonPhrase());
      } else if (parser.getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
        messageHelper.setErrorDetailsMessage(model, DS_CONNECTION_UNAUTHORIZED, parser.getReasonPhrase());
      } else {
        messageHelper.setErrorDetailsMessage(model, DS_SERVER_ERROR_KEY, parser.getReasonPhrase());
      }
    } catch (KeyczarException e) {
      messageHelper.setErrorDetailsMessage(model, DS_MESSAGE_SIGNER_ERROR_KEY, e.getMessage());
    } catch (SecurityStorageException e) {
      messageHelper.setErrorDetailsMessage(model, DS_MESSAGE_SIGNER_ERROR_KEY, e.getMessage());
    } catch (ResponseParserException e) {
      messageHelper.setErrorDetailsMessage(model, DS_INTERNAL_CONTROLLER_ERROR_KEY, e.getMessage(), new Object[] {url});
    } catch (IOException e) {
      messageHelper.setErrorDetailsMessage(model, DS_HTTP_CONN_ERROR_KEY, e.getMessage(), new Object[] {url});
    } catch (Exception e) {
      messageHelper.setErrorDetailsMessage(model, DS_GENERIC_CONN_ERROR_KEY, e.getMessage(), new Object[] {url}); 
    } finally {
      if (httpClient != null) {
        try {
          httpClient.shutdown();
        } catch (Exception e) {
          //
        }
      }
    }
    return null;
  }

  @RequestMapping("/connect_with_remote_host.htm")
  public String connectWithRemoteHost(Model model,
      @RequestParam(value = "connectionURL", required = false) String connectionURL,
      @RequestParam(value = "message", required = false) String message,
      @RequestParam(value = "submitButton", required = false) String submitButton) {
    logger.info("/connect_with_remote_host.htm");
    if (submitButton != null && submitButton.equals("Connect")) {
      logger.info("Sending request to: " + connectionURL);
      try {
        exchangeManager.requestAuthorization(connectionURL, message);
        return "redirect:authorization_list.htm";
      } catch (ExchangeStatusException e) {
        model.addAttribute("connectionURL", connectionURL);
        model.addAttribute("message", message);
        if (e.getStatus() == 409) { /** Conflict */
          model.addAttribute("emessage", "Exchange conflict error. Most likely, the remote host already have our key");
        } else if (e.getStatus() == 404) {
          model.addAttribute("emessage", "Exchange not found error. Most likely, the remote host doesn't run a node at specified address");
        } else if (e.getStatus() == 500) {
          model.addAttribute("emessage", "Exchange internal server error.Please contact remote administrator.");
        }
      }
    } else if (submitButton != null && submitButton.equals("Remove")) {
      try {
        AuthorizationRequest request = requestManager.getByRemoteAddress(connectionURL);
        if (request != null) {
          requestManager.remove(request.getRequestUUID());
        }
      } catch (Exception e) {
        // pass
      }
      return "redirect:authorization_list.htm";
    } else {
      model.addAttribute("connectionURL", connectionURL);
      model.addAttribute("message", message);
      if (connectionURL != null && !connectionURL.equals("") && requestManager.getByRemoteAddress(connectionURL) != null) {
        model.addAttribute("canRemove", true);
      }
    }
    return "connect_with_remote_host";
  }

  /**
   * @param requestManager the request manager
   */
  @Autowired
  public void setRequestManager(AuthorizationRequestManager requestManager) {
    this.requestManager = requestManager;
  }

  /**
   * @param securityManager the security manager
   */
  @Autowired
  public void setSecurityManager(ISecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  /**
   * @param authorizationManaager the authorization manager
   */
  @Autowired
  public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  /**
   * @param manager the exchange manager
   */
  @Autowired
  public void setExchangeManager(IExchangeManager manager) {
    this.exchangeManager = manager;
  }

  /**
   * @param userManager the user manager
   */
  @Autowired
  public void setUserManager(IUserManager userManager) {
    this.userManager = userManager;
  }

  @Autowired
  public void setAuthenticator(Authenticator authenticator) {
    this.authenticator = authenticator;
  }

  @Autowired
  public void setProtocolManager(ProtocolManager protocolManager) {
    this.protocolManager = protocolManager;
  }
  
  @Autowired
  public void setModelMessageHelper(ModelMessageHelper messageHelper) {
    this.messageHelper = messageHelper;
  }
}
