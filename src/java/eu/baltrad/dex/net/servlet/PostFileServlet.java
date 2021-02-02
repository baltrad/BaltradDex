/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
*******************************************************************************/

package eu.baltrad.dex.net.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.keyczar.exceptions.KeyczarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import eu.baltrad.bdb.FileCatalog;
import eu.baltrad.bdb.db.DatabaseError;
import eu.baltrad.bdb.db.DuplicateEntry;
import eu.baltrad.bdb.db.FileEntry;
import eu.baltrad.bdb.oh5.MetadataMatcher;
import eu.baltrad.bdb.util.FileEntryNamer;
import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.exchange.ExchangeResponse;
import eu.baltrad.beast.exchange.IExchangeManager;
import eu.baltrad.beast.exchange.SendFileRequest;
import eu.baltrad.beast.exchange.SendFileRequestCallback;
import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.message.mo.BltDataMessage;
import eu.baltrad.beast.security.IAuthorizationManager;
import eu.baltrad.beast.security.ISecurityManager;
import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.db.manager.IBltFileManager;
import eu.baltrad.dex.keystore.manager.IKeystoreManager;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.request.INodeRequest;
import eu.baltrad.dex.net.request.factory.impl.DefaultRequestFactory;
import eu.baltrad.dex.net.request.impl.NodeRequest;
import eu.baltrad.dex.net.response.impl.NodeResponse;
import eu.baltrad.dex.net.util.FramePublisherManager;
import eu.baltrad.dex.net.util.PostFileRedirectHandler;
import eu.baltrad.dex.net.util.PostFileTask;
import eu.baltrad.dex.registry.manager.IRegistryManager;
import eu.baltrad.dex.registry.model.impl.RegistryEntry;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.status.model.Status;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.MessageResourceUtil;

/**
 * Receives and handles post file requests.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@Controller
public class PostFileServlet extends HttpServlet implements SendFileRequestCallback {

  private static final String PF_MESSAGE_VERIFIER_ERROR_KEY = "postfile.server.message_verifier_error";
  private static final String PF_UNAUTHORIZED_REQUEST_KEY = "postfile.server.unauthorized_request";
  private static final String PF_INVALID_SUBSCRIPTION_ERROR_KEY = "postfile.server.invalid_subscription_error";
  private static final String PF_INTERNAL_SERVER_ERROR_KEY = "postfile.server.internal_server_error";
  private static final String PF_DUPLICATE_ENTRY_ERROR_KEY = "postfile.server.duplicate_entry_error";
  private static final String PF_DATABASE_ERROR_KEY = "postfile.server.database_error";
  private static final String PF_GENERIC_POST_FILE_ERROR_KEY = "postfile.server.generic_post_file_error";

  private IConfigurationManager confManager;
  private INodeStatusManager nodeStatusManager;
  private FileCatalog catalog;
  private FileEntryNamer namer;
  private MetadataMatcher matcher;
  private IBltMessageManager messageManager;
  private IBltFileManager fileManager;
  private IRegistryManager registryManager;
  private IUserManager userManager;
  private FramePublisherManager framePublisherManager;
  private ISubscriptionManager subscriptionManager;
  private IDataSourceManager dataSourceManager;
  private MessageResourceUtil messages;
  private Logger log;
  private ProtocolManager protocolManager;
  private int connTimeout;
  private int soTimeout;

  protected User localNode;

  private final static Logger logger = LogManager.getLogger(PostFileServlet.class);

  private ISecurityManager securityManager = null;
  private IExchangeManager exchangeManager = null;

  /**
   * Default constructor.
   */
  public PostFileServlet() {
    this.log = Logger.getLogger("DEX");
  }

  /**
   * Initializes servlet with current configuration.
   */
  protected void initConfiguration() {
    this.connTimeout = Integer.parseInt(confManager.getAppConf().getConnTimeout());
    this.soTimeout = Integer.parseInt(confManager.getAppConf().getSoTimeout());
    this.localNode = new User(confManager.getAppConf().getNodeName(), Role.NODE, null,
        confManager.getAppConf().getOrgName(), confManager.getAppConf().getOrgUnit(),
        confManager.getAppConf().getLocality(), confManager.getAppConf().getState(),
        confManager.getAppConf().getCountryCode(), confManager.getAppConf().getNodeAddress());
  }

  /**
   * Store incoming file.
   * 
   * @param request Node request
   * @return File entry
   */
  private FileEntry storeFile(NodeRequest request) {
    InputStream is = null;
    try {
      try {
        is = request.getInputStream();
        return catalog.store(is);
      } finally {
        is.close();
      }
    } catch (IOException e) {
      logger.error("Caught IOException when storing file", e);
      return null;
    }
  }

  /**
   * Get file entry's content
   * 
   * @param entry File entry
   * @return Byte content
   */
  private byte[] getEntryContent(FileEntry entry) {
    InputStream eis = null;
    ByteArrayOutputStream baos = null;
    try {
      try {
        eis = entry.getContentStream();
        baos = new ByteArrayOutputStream();
        IOUtils.copy(eis, baos);
        return baos.toByteArray();
      } finally {
        eis.close();
        baos.close();
      }
    } catch (IOException e) {
      logger.error("Caught exception when getting entry content", e);
      return null;
    }
  }

  /**
   * Send message to the beast framework.
   * 
   * @param entry File entry
   */
  protected void sendMessage(FileEntry entry) {
    BltDataMessage msg = new BltDataMessage();
    msg.setFileEntry(entry);
    messageManager.manage(msg);
  }

  /**
   * Check if file comes from a valid subscription.
   * 
   * @param downloads List of subscribed data sources
   * @param entry     File entry
   * @return Subscription id if valid, otherwise 0
   */
  protected int validateSubscription(List<Subscription> downloads, FileEntry entry) {
    int subscriptionId = 0;
    for (Subscription s : downloads) {
      IFilter filter = fileManager.loadFilter(s.getDataSource(), DataSource.PEER);
      if (filter != null && matcher.match(entry.getMetadata(), filter.getExpression())) {
        subscriptionId = s.getId();
      }
    }
    return subscriptionId;
  }

  /**
   * Send file entry to subscribers.
   * 
   * @param uploads List of subscriptions
   * @param entry   File entry
   */
  protected void sendToSubscribers(List<Subscription> uploads, FileEntry entry) {
    if (uploads.size() > 0) {
      byte[] fileContent = getEntryContent(entry);
      long t = System.currentTimeMillis();
      for (Subscription s : uploads) {
        try {
          IFilter filter = fileManager.loadFilter(s.getDataSource(), DataSource.LOCAL);
          if (matcher.match(entry.getMetadata(), filter.getExpression())) {
            DataSource dataSource = dataSourceManager.load(s.getDataSource(), DataSource.LOCAL);
            User receiver = userManager.load(s.getUser());
            SendFileRequest request = new SendFileRequest();
            request.setAddress(receiver.getNodeAddress() + "/BaltradDex/post_file.htm");
            if (receiver.getRedirectedAddress() != null) {
              request.setAddress(receiver.getRedirectedAddress() + "/BaltradDex/post_file.htm");
            }
            request.setContentType("application/x-hdf5");
            request.setData(fileContent);
            request.setDate(new Date());
            request.setNodeName(receiver.getName());
            request.setMetadata(new Object[] {receiver, dataSource, s, entry.getUuid().toString()});
            exchangeManager.sendAsync(request, this);
          }
        } catch (Exception e) {
          log.error(messages.getMessage(PF_INVALID_SUBSCRIPTION_ERROR_KEY, new String[] { e.getMessage() }));
          logger.info("Failed to send message to subscriber", e);
        }
      }
      t = System.currentTimeMillis() - t;
      logger.info("Finished publishing to subscribers: took " + t + " ms");
    }
  }

  /**
   * Implements Controller interface.
   * 
   * @param request  Http servlet request
   * @param response Http servlet response
   * @return Model and view
   */
  @RequestMapping("/post_file.htm")
  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
    logger.debug("In handleRequest");
    initConfiguration();
    HttpSession session = request.getSession(true);
    doPost(request, response);
    return new ModelAndView();
  }

  private void logInfo(String message, String file) {
    if (file != null) {
      logger.info(message + ", thread: " + Thread.currentThread().getName() + ", file: " + file);
    } else {
      logger.info(message + ", thread: " + Thread.currentThread().getName());
    }
  }

  private void logDebug(String message, String file) {
    if (file != null) {
      logger.debug(message + ", thread: " + Thread.currentThread().getName() + ", file: " + file);
    } else {
      logger.debug(message + ", thread: " + Thread.currentThread().getName());
    }
  }
  
  /**
   * Actual mathod processing requests.
   * 
   * @param request  Http servlet request
   * @param response Http servlet response
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    NodeRequest req = new NodeRequest(request);
    NodeResponse res = new NodeResponse(response);
    logger.debug("Request arrived using protocol version '" + req.getProtocolVersion() + "'");
    long st = System.currentTimeMillis();
    long fileStored = 0, sentToSubscribers = 0;
    
    logInfo("doPost: ENTER Request arrived from: " + req.getNodeName(), null);
    
    try {
      if (securityManager.validate(req.getNodeName(), req.getSignature(), req.getMessage())) {
        log.info("New data file received from " + req.getNodeName());

        logInfo("New data file received from " + req.getNodeName() + ", reckognized after " + (System.currentTimeMillis() - st) + " ms.", null);
        
        nodeStatusManager.setRuntimeNodeStatus(req.getNodeName(), HttpServletResponse.SC_OK);

        logDebug("Runtime node status set after " + (System.currentTimeMillis() - st) + " ms.", null);

        // store entry
        FileEntry entry = storeFile(req);
        fileStored = System.currentTimeMillis();

        if (entry != null) {
          String name = namer.name(entry);

          logDebug("File " + name + " stored after " + (System.currentTimeMillis() - st) + " ms.", entry.getUuid().toString());
          
          if (securityManager.isInjector(req.getNodeName())) {
            // file sent by injector
            log.info("File " + name + " stored with UUID " + entry.getUuid().toString());
            sendMessage(entry);
            
            logDebug("- " + entry.getUuid().toString() + "- has been posted on beast queue after " + (System.currentTimeMillis() - st) + " ms.", entry.getUuid().toString());
            List<Subscription> uploads = subscriptionManager.load(Subscription.PEER);

            logDebug("- " + entry.getUuid().toString() + "- peer subsriptions loaded after " + (System.currentTimeMillis() - st) + " ms.", entry.getUuid().toString());
            sendToSubscribers(uploads, entry);

            logInfo("- " + entry.getUuid().toString() + "- file sent do subscribers after " + (System.currentTimeMillis() - st) + " ms.", entry.getUuid().toString());

            sentToSubscribers = System.currentTimeMillis();
            logger.info("PostFile from injector: File stored after " + (fileStored - st)
                + " ms, finished with subscribers after " + (sentToSubscribers - st) + " ms");
          } else {
            // file sent by peer
            List<Subscription> downloads = subscriptionManager.load(Subscription.LOCAL);
            logDebug("- " + entry.getUuid().toString() + "- local subscriptions loaded after " + (System.currentTimeMillis() - st) + " ms.", entry.getUuid().toString());
            int subscriptionId = validateSubscription(downloads, entry);
            logDebug("- " + entry.getUuid().toString() + "- subscriptions validated after " + (System.currentTimeMillis() - st) + " ms.", entry.getUuid().toString());
            if (subscriptionId > 0) {
              log.info("File " + name + " stored with UUID " + entry.getUuid().toString());
              sendMessage(entry);
              logDebug("- " + entry.getUuid().toString() + "- subscribed file has been posted on beast queue after " + (System.currentTimeMillis() - st) + " ms.", entry.getUuid().toString());
              // update status - increment downloads
              Status s = nodeStatusManager.load(subscriptionId);
              logDebug("- " + entry.getUuid().toString() + "- node status loaded after " + (System.currentTimeMillis() - st) + " ms.", entry.getUuid().toString());
              s.incrementDownloads();
              nodeStatusManager.update(s, subscriptionId);
              logDebug("- " + entry.getUuid().toString() + "- node status updated after " + (System.currentTimeMillis() - st) + " ms.", entry.getUuid().toString());
              logger.info("PostFile from peer: File stored after " + (fileStored - st)
                  + " ms. Total time to handle post: " + (System.currentTimeMillis() - st) + " ms");
            } else {
              log.warn("File " + name + " with UUID " + entry.getUuid().toString()
                  + " comes from unsubscribed data source. " + "Removing file.");
              catalog.remove(entry);
              res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
          }
        } else {
          logger.info("Could not store file for some reason");
          res.setStatus(HttpServletResponse.SC_NOT_FOUND, messages.getMessage(PF_GENERIC_POST_FILE_ERROR_KEY));
        }
      } else {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, messages.getMessage(PF_UNAUTHORIZED_REQUEST_KEY));
      }
    } catch (DuplicateEntry e) {
      res.setStatus(HttpServletResponse.SC_CONFLICT, messages.getMessage(PF_DUPLICATE_ENTRY_ERROR_KEY));
      logger.info("Duplicate entry for file from " + req.getNodeName());
    } catch (DatabaseError e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, messages.getMessage(PF_DATABASE_ERROR_KEY));
      logger.error("Database error", e);
    } catch (Exception e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, messages.getMessage(PF_INTERNAL_SERVER_ERROR_KEY));
      logger.error("Internal server error", e);
    }
    logInfo("doPost: EXIT Request from from: " + req.getNodeName(), null);
  }

  @Override
  public void filePublicationFailed(SendFileRequest request, ExchangeResponse response) {
    try {
      if (request.getMetadata() != null) {
        long st = System.currentTimeMillis();

        User user = (User)((Object[])request.getMetadata())[0];
        DataSource dataSource = (DataSource)((Object[])request.getMetadata())[1];
        Subscription subscription = (Subscription)((Object[])request.getMetadata())[2];
        String uuid = (String)((Object[])request.getMetadata())[3];

        nodeStatusManager.setRuntimeNodeStatus(user.getName(), response.statusCode());
        RegistryEntry entry = new RegistryEntry(
            user.getId(), dataSource.getId(),
            System.currentTimeMillis(),
            RegistryEntry.UPLOAD, uuid, user.getName(),
            false);
        log.error("Failed to send file " + uuid + " to user " + user.getName() + ": " +
            response.statusCode() + " - " + response.getMessage() +  ", thread: " + Thread.currentThread().getName() + ", file: " + uuid.toString());
        registryManager.store(entry);
        //  update status
        nodeStatusManager.setRuntimeNodeStatus(user.getName(), response.statusCode());
        Status status = nodeStatusManager.load(subscription.getId());
        status.incrementUploadFailures();
        nodeStatusManager.update(status, subscription.getId());
        logger.info("filePublished updated all db-entries in " + (System.currentTimeMillis() - st) + " ms " + ", thread: " + Thread.currentThread().getName() + ", file: " + uuid.toString());
      }
    } catch (Exception e) {
      logger.warn(e);
    }
  }

  @Override
  public void filePublished(SendFileRequest request) {
    try {
      if (request.getMetadata() != null) {
        long st = System.currentTimeMillis();
        User user = (User)((Object[])request.getMetadata())[0];
        DataSource dataSource = (DataSource)((Object[])request.getMetadata())[1];
        Subscription subscription = (Subscription)((Object[])request.getMetadata())[2];
        String uuid = (String)((Object[])request.getMetadata())[3];
        
        nodeStatusManager.setRuntimeNodeStatus(user.getName(), HttpServletResponse.SC_OK);

        RegistryEntry entry = new RegistryEntry(user.getId(), dataSource.getId(),
            System.currentTimeMillis(), RegistryEntry.UPLOAD,
            uuid, user.getName(), true);
        log.info("File " + uuid + " sent to user " +user.getName() + ", thread: " + Thread.currentThread().getName());
        registryManager.store(entry);
        Status status = nodeStatusManager.load(subscription.getId());
        status.incrementUploads();
        nodeStatusManager.update(status, subscription.getId());
        logger.info("filePublished updated all db-entries in " + (System.currentTimeMillis() - st) + " ms " + ", thread: " + Thread.currentThread().getName() + ", file: " + uuid.toString());
      }
    } catch (Exception e) {
      logger.info(e);
    }
  }
  

  @Override
  public void filePublished(SendFileRequest request, String redirectAddress, int statusCode) {
    try {
      if (request.getMetadata() != null) {
        long st = System.currentTimeMillis();

        User user = (User)((Object[])request.getMetadata())[0];
        DataSource dataSource = (DataSource)((Object[])request.getMetadata())[1];
        Subscription subscription = (Subscription)((Object[])request.getMetadata())[2];
        String uuid = (String)((Object[])request.getMetadata())[3];

        nodeStatusManager.setRuntimeNodeStatus(user.getName(), HttpServletResponse.SC_OK);

        RegistryEntry entry = new RegistryEntry(user.getId(), dataSource.getId(),
            System.currentTimeMillis(), RegistryEntry.UPLOAD,
            uuid, user.getName(), true);
        log.info("File " + uuid + " sent to user " +user.getName() +  ", thread: " + Thread.currentThread().getName() + ", file: " + uuid.toString());
        registryManager.store(entry);
        Status status = nodeStatusManager.load(subscription.getId());
        status.incrementUploads();
        nodeStatusManager.update(status, subscription.getId());
        logger.info("filePublished redirect updated all db-entries in " + (System.currentTimeMillis() - st) + " ms " + ", thread: " + Thread.currentThread().getName() + ", file: " + uuid.toString());
      }
    } catch (Exception e) {
      logger.info(e);
    }
    
    try {
      if (request.getMetadata() != null && redirectAddress != null) {
        User user = (User)((Object[])request.getMetadata())[0];
        String redirectBase = extractBaseUrlFromRedirect(user.getNodeAddress(), request.getAddress(), redirectAddress);
        logger.info("request uri: " + request.getAddress() + " redirected to " + redirectAddress);
        if (redirectBase != null) {
          user.setRedirectedAddress(redirectBase);
          userManager.update(user);
        }
      }
    } catch (Exception e) {
      logger.info(e);
    }
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
  
  /**
   * @param configurationManager
   */
  @Autowired
  public void setConfigurationManager(IConfigurationManager confManager) {
    this.confManager = confManager;
  }

  /**
   * @param nodeStatusManager the nodeStatusManager to set
   */
  @Autowired
  public void setNodeStatusManager(INodeStatusManager nodeStatusManager) {
    this.nodeStatusManager = nodeStatusManager;
  }

  /**
   * @param catalog the catalog to set
   */
  @Autowired
  public void setCatalog(FileCatalog catalog) {
    this.catalog = catalog;
  }

  /**
   * @param messages the messages to set
   */
  @Autowired
  public void setMessages(MessageResourceUtil messages) {
    this.messages = messages;
  }

  /**
   * @param subscriptionManager the subscriptionManager to set
   */
  @Autowired
  public void setSubscriptionManager(ISubscriptionManager subscriptionManager) {
    this.subscriptionManager = subscriptionManager;
  }

  /**
   * @param log the log to set
   */
  public void setLog(Logger log) {
    this.log = log;
  }

  /**
   * @param namer the namer to set
   */
  @Autowired
  public void setNamer(FileEntryNamer namer) {
    this.namer = namer;
  }

  /**
   * @param messageManager the messageManager to set
   */
  @Autowired
  public void setMessageManager(IBltMessageManager messageManager) {
    this.messageManager = messageManager;
  }

  /**
   * @param matcher the matcher to set
   */
  @Autowired
  public void setMatcher(MetadataMatcher matcher) {
    this.matcher = matcher;
  }

  /**
   * @param fileManager the fileManager to set
   */
  @Autowired
  public void setFileManager(IBltFileManager fileManager) {
    this.fileManager = fileManager;
  }

  /**
   * @param registryManager the registryManager to set
   */
  @Autowired
  public void setRegistryManager(IRegistryManager registryManager) {
    this.registryManager = registryManager;
  }

  /**
   * @param userManager the userManager to set
   */
  @Autowired
  public void setUserManager(IUserManager userManager) {
    this.userManager = userManager;
  }

  /**
   * @param framePublisherManager the framePublisherManager to set
   */
  @Autowired
  public void setFramePublisherManager(FramePublisherManager framePublisherManager) {
    this.framePublisherManager = framePublisherManager;
  }

  /**
   * @param dataSourceManager the dataSourceManager to set
   */
  @Autowired
  public void setDataSourceManager(IDataSourceManager dataSourceManager) {
    this.dataSourceManager = dataSourceManager;
  }

  /**
   * @return the protocol managear
   */
  public ProtocolManager getProtocolManager() {
    return protocolManager;
  }

  /**
   * @param protocolManager the protocol manager
   */
  public void setProtocolManager(ProtocolManager protocolManager) {
    this.protocolManager = protocolManager;
  }

  /**
   * @param securityManager the security manager
   */
  @Autowired
  public void setSecurityManager(ISecurityManager securityManager) {
    this.securityManager = securityManager;
  }
  
  /**
   * @param exchangeManager the exchange manager
   */
  @Autowired
  public void setExchangeManager(IExchangeManager exchangeManager) {
    this.exchangeManager = exchangeManager;
  }

}
