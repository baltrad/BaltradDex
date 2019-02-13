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

import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import eu.baltrad.beast.system.IMappableStatusReporter;
import eu.baltrad.beast.system.SystemStatus;
import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.httpclient.impl.HttpClientUtil;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;

/**
 * @author Anders Henja
 */
public class PeerStatusReporter
    implements IMappableStatusReporter, InitializingBean {
  /**
   * User manager, for getting information about peer users.
   */
  private IUserManager userManager = null;

  /**
   * Keeps track on status of the different nodes
   */
  private INodeStatusManager statusManager = null;

  /**
   * The logger
   */
  private final static Logger logger = LogManager
      .getLogger(PeerStatusReporter.class);

  /**
   * The supported attributes
   */
  private static Set<String> SUPPORTED_ATTRIBUTES=new HashSet<String>();
  static {
    SUPPORTED_ATTRIBUTES.add("peers");
    SUPPORTED_ATTRIBUTES.add("minutes");
  }
  
  /**
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
  }

  /**
   * @see eu.baltrad.beast.system.ISystemStatusReporter#getName()
   */
  @Override
  public String getName() {
    return "peer.status";
  }

  /**
   * Tests if the specified date is within x minutes of now according to the
   * calendar
   * 
   * @param testedDate
   * @param minutes
   * @return true
   */
  protected boolean isWithinMinutes(Date testedDate, int minutes) {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.MINUTE, -minutes);
    if (testedDate.after(c.getTime()))
      return true;
    return false;
  }

  /**
   * Returns the status for the specified URL.
   * @param url the url to the supervisor on the peer node
   * @param minutesBackInTime number of minutes back in time we want to check
   * @return the status
   */
  protected SystemStatus getPeerStatus(String nodeName, int minutesBackInTime) {
    SystemStatus status = SystemStatus.COMMUNICATION_PROBLEM;
    try {
      logger.info("Node: " + nodeName + ", status: "
          + statusManager.getRuntimeNodeStatus(nodeName));
      Date statusDate = statusManager.getRuntimeNodeDate(nodeName);
      if (isWithinMinutes(statusDate, minutesBackInTime)) {
        int httpStatus = statusManager.getRuntimeNodeStatus(nodeName);
        if (httpStatus == HttpServletResponse.SC_OK) {
          status = SystemStatus.OK;
        }
      }
    } catch (Exception e) {
      logger.info("Failed to retrieve peer status for " + nodeName);
    }
    return status;
  }

  /**
   * @see eu.baltrad.beast.system.IMappableStatusReporter#getMappedStatus(java.util.Map)
   */
  @Override
  public Map<String, Map<Object, SystemStatus>> getMappedStatus(
      Map<String, Object> arg) {
    int minutesBackInTime = 5;
    
    Map<String, Map<Object, SystemStatus>> result = new HashMap<String, Map<Object, SystemStatus>>();
    Map<Object, SystemStatus> peers = new HashMap<Object, SystemStatus>();
    List<String> searchedPeers = new ArrayList<String>();
    ArrayList<String> requestedPeers = new ArrayList<String>(); // Used to keep
                                                                // track on any
                                                                // searched
                                                                // peers that
                                                                // doesn't have
                                                                // any peer
                                                                // entry

    result.put(getName(), peers);

    if (arg.containsKey("minutes")) {
      Object tlimit = arg.get("minutes");
      if (tlimit != null) {
        if (tlimit instanceof String) {
          try {
            minutesBackInTime = Integer.parseInt((String)tlimit);
          } catch (Exception e) {
          }
        } else if (tlimit instanceof Long) {
          // downcast ok
          minutesBackInTime = ((Long)tlimit).intValue();
        } else if (tlimit instanceof Integer) {
          minutesBackInTime = (Integer)tlimit;
        } // else use default value        
      }
    }
    
    
    if (arg.containsKey("peers")) {
      searchedPeers = Arrays.asList(((String) arg.get("peers")).split(","));
      for (String s : searchedPeers)
        requestedPeers.add(s);
    }

    List<String> names = userManager.loadPeerNames();
    logger.info("Number of peers " + names.size());
    for (String nodeName: names) {
      if (requestedPeers.contains(nodeName)) {
        requestedPeers.remove(nodeName);
      }
      if (searchedPeers.size() > 0 && !searchedPeers.contains(nodeName)) {
        continue;
      }
      SystemStatus s = getPeerStatus(nodeName, minutesBackInTime);
      logger.info("Name: " + nodeName + ", result: " + s);
      peers.put(nodeName, s);
    }

    if (requestedPeers.size() > 0) {
      for (String n : requestedPeers) {
        if (statusManager.getRuntimeNodeNames().contains(n)) {
          peers.put(n, getPeerStatus(n, minutesBackInTime));
        } else {
          peers.put(n, SystemStatus.UNDEFINED);
        }
      }
    }
    
    return result;
  }

  /**
   * @see eu.baltrad.beast.system.ISystemStatusReporter#getStatus(java.util.Map)
   */
  @Override
  public Set<SystemStatus> getStatus(Map<String, Object> values) {
    Set<SystemStatus> result = new HashSet<SystemStatus>();
    Collection<SystemStatus> c = getMappedStatus(values).get(getName())
        .values();
    result.addAll(c);
    return result;
  }

  /**
   * @see eu.baltrad.beast.system.ISystemStatusReporter#getSupportedAttributes()
   */
  @Override
  public Set<String> getSupportedAttributes() {
    return SUPPORTED_ATTRIBUTES;
  }

  /**
   * @param manager
   *          the user manager
   */
  @Autowired
  public void setUserManager(IUserManager manager) {
    this.userManager = manager;
  }
  
  @Autowired
  public void setNodeStatusManager(INodeStatusManager statusManager) {
    this.statusManager = statusManager;
  }
}
