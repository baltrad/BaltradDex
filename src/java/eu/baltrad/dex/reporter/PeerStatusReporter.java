/* --------------------------------------------------------------------
Copyright (C) 2009-2014 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the BaltradDex package.

The BaltradDex package is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The BaltradDex package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex package library.  If not, see <http://www.gnu.org/licenses/>.
------------------------------------------------------------------------*/

package eu.baltrad.dex.reporter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import eu.baltrad.beast.system.IMappableStatusReporter;
import eu.baltrad.beast.system.ISystemStatusReporter;
import eu.baltrad.beast.system.SystemStatus;
import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.ResponseParser;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.httpclient.impl.HttpClientUtil;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;

/**
 * @author Anders Henja
 */
public class PeerStatusReporter implements IMappableStatusReporter, InitializingBean {
  /**
   * User manager, for getting information about peer users.
   */
  private IUserManager userManager = null;
  
  /**
   * The logger
   */
  private final static Logger logger = LogManager.getLogger(PeerStatusReporter.class);
  
  /**
   * The configuration manager
   */
  private IConfigurationManager confManager;

  /**
   * The authenticator / signer.
   */
  private Authenticator authenticator;

  /**
   * The protocol manager
   */
  private ProtocolManager protocolManager = null;

  /**
   * The http client
   */
  private IHttpClientUtil httpClient;  
  
  /**
   * The node
   */
  protected User localNode;

  /**
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    authenticator = new KeyczarAuthenticator(confManager.getAppConf().getKeystoreDir());
    httpClient = new HttpClientUtil(
        Integer.parseInt(confManager.getAppConf().getConnTimeout()), 
        Integer.parseInt(confManager.getAppConf().getSoTimeout()));
    localNode = new User(confManager.getAppConf().getNodeName(),
        Role.NODE, null, confManager.getAppConf().getOrgName(),
        confManager.getAppConf().getOrgUnit(),
        confManager.getAppConf().getLocality(),
        confManager.getAppConf().getState(),
        confManager.getAppConf().getCountryCode(),
        confManager.getAppConf().getNodeAddress());
  }

  /**
   * @see eu.baltrad.beast.system.ISystemStatusReporter#getName()
   */
  @Override
  public String getName() {
    return "peer.status";
  }

  /**
   * Returns the status for the specified URL.
   * @param url the url to the supervisor on the peer node
   * @return the status
   */
  protected SystemStatus getPeerStatus(String url) {
    RequestFactory requestFactory = protocolManager.getFactory(url);
    HttpUriRequest req = requestFactory.createDataSourceListingRequest(localNode);
    SystemStatus status = SystemStatus.COMMUNICATION_PROBLEM;
    try {
      authenticator.addCredentials(req, localNode.getName());
      HttpResponse res = httpClient.post(req);
      ResponseParser parser = protocolManager.createParser(res);
      logger.debug("Got a response message of version " + parser.getProtocolVersion());
      logger.debug("Answering node supports version " + parser.getConfiguredProtocolVersion());
      if (parser.getStatusCode() != HttpStatus.SC_OK)  {
        if (parser.isRedirected()) {
          logger.info("The URL: " + url + " has been redirected but it isn't marked as redirected. Please verify connection");
        }
      } else {
        status = SystemStatus.OK;
      }
    } catch (Exception e) {
      logger.info("Failed to retrieve peer status for " + url);
    }
    
    return status;
  }
  
  /**
   * @see eu.baltrad.beast.system.IMappableStatusReporter#getMappedStatus(java.util.Map)
   */
  @Override
  public Map<String, Map<Object, SystemStatus>> getMappedStatus(Map<String, Object> arg) {
    Map<String, Map<Object, SystemStatus>> result = new HashMap<String, Map<Object,SystemStatus>>();
    Map<Object, SystemStatus> peers = new HashMap<Object, SystemStatus>();
    result.put(getName(), peers);
    List<User> users = userManager.loadPeers();
    for (User u: users) {
      logger.info("Name: " + u.getName() + ", adr:" + u.getNodeAddress() + ", red: " + u.getRedirectedAddress());
      String adr = u.getNodeAddress();
      if (u.getRedirectedAddress() != null) {
        adr = u.getRedirectedAddress();
      }
      SystemStatus s = getPeerStatus(adr);
      logger.info("Name: " + u.getName() + ", result: " + s);
      peers.put(u.getName(), s);
    }
    return result;
  }
  
  
  /**
   * @see eu.baltrad.beast.system.ISystemStatusReporter#getStatus(java.util.Map)
   */
  @Override
  public Set<SystemStatus> getStatus(Map<String, Object> values) {
    Set<SystemStatus> result = new HashSet<SystemStatus>();
    Collection<SystemStatus> c = getMappedStatus(values).get(getName()).values();
    result.addAll(c);
    return result;
  }


  /**
   * @see eu.baltrad.beast.system.ISystemStatusReporter#getSupportedAttributes()
   */
  @Override
  public Set<String> getSupportedAttributes() {
    return new HashSet<String>();
  }

  /**
   * @param manager the user manager
   */
  @Autowired
  public void setUserManager(IUserManager manager) {
    this.userManager = manager;
  }
  
  /**
   * @param configurationManager the config manager 
   */
  @Autowired
  public void setConfigurationManager(IConfigurationManager confManager) {
      this.confManager = confManager;
  }
  
  /**
   * @param authenticator the authenticator to set
   */
  @Autowired
  public void setAuthenticator(Authenticator authenticator) {
      this.authenticator = authenticator;
  }
  
  /**
   * @param protocolManager the protocol manager to use
   */
  @Autowired
  public void setProtocolManager(ProtocolManager protocolManager) {
    this.protocolManager = protocolManager;
  }
  
  /**
   * @param client the http client
   */
  protected void setHttpClient(IHttpClientUtil client) {
    httpClient = client;
  }
}
