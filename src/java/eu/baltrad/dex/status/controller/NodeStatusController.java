/*******************************************************************************
*
* Copyright (C) 2009-2014 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.status.controller;

import java.io.File;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.baltrad.dex.auth.manager.SecurityManager;
import eu.baltrad.dex.config.manager.impl.ConfigurationManager;
import eu.baltrad.dex.db.manager.IBltFileManager;
import eu.baltrad.dex.log.manager.ILogManager;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.registry.manager.IRegistryManager;
import eu.baltrad.dex.registry.model.impl.RegistryEntry;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.status.model.Status;
import eu.baltrad.dex.user.manager.IRoleManager;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.util.ServletContextUtil;
import eu.baltrad.dex.util.WebValidator;

/**
 * Node status controller.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 2.0.0
 * @since 1.6.1
 */
@Controller
@RequestMapping("/status.htm")
public class NodeStatusController {
    
    private static final String LOGIN_MSG = "login.controller.login_message";
    private static final long BYTES_PER_MB = 1024 * 1024;
    
    private INodeStatusManager statusManager;
    private IUserManager userManager; 
    private IRoleManager roleManager;
    private ConfigurationManager confManager;
    private ISubscriptionManager subscriptionManager;
    private IBltFileManager bltFileManager;
    private ILogManager logManager;
    private IRegistryManager registryManager;
    
    private MessageResourceUtil messages;
    private Logger log;
    
    private SimpleDateFormat format;
    
    // static arrays holding data transfer information
    protected static Map<String, Boolean> nodesStatus;
    protected static Map<String, List<Status>> downloads;
    protected static Map<String, List<Status>> uploads;
    
    /**
     * Default constructor.
     */
    public NodeStatusController() {
        this.log = Logger.getLogger("DEX");
        this.format = 
                new SimpleDateFormat("EEE, MMM d HH:mm:ss z yyyy", Locale.US);
        nodesStatus = new HashMap<String, Boolean>();
        downloads = new HashMap<String, List<Status>>();
        uploads = new HashMap<String, List<Status>>();
        log.info("BALTRAD system started");
    }
    
    /**
     * Sets session user and renders status page.
     * @param model Model 
     * @param principal Principal
     * @param session Http session
     * @return Welcome page name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String showStatus(Model model, Principal principal, 
                             HttpSession session) {
        return "status";
    }
    
    /**
     * Get peer node names.
     * @return List of peer node names 
     */
    @ModelAttribute("nodes")
    public List<String> getNodeNames() {
        return statusManager.loadNodeNames();
    }
    
    /**
     * Load transfer status for a given peer and transfer type.
     * @param peerName Peer node name
     * @param subscriptionType Transfer type - download / upload
     * @return Transfer status for a selected peer node
     */
    private List<Status> loadStatus(String peerName, String subscriptionType) {
        return statusManager.load(peerName, subscriptionType);
    }
    
    /**
     * Process submit request. Get transfer information for a selected peer.
     * @param model Model map
     * @param peerName Peer name for which transfer status is prepared 
     * @param request HTTP servlet request
     * @return View name
     */
    @RequestMapping(method = RequestMethod.POST)
    protected String processSubmit(Model model,
            @RequestParam(value="peer_name", required=false) String peerName,
            HttpServletRequest request) {
        // show transfers toggle
        Boolean showTransfers = true;
        if (nodesStatus.containsKey(peerName)) {
            showTransfers = nodesStatus.get(peerName);
            showTransfers = !showTransfers;   
            nodesStatus.clear();
            nodesStatus.put(peerName, showTransfers);
        } else {
            nodesStatus.clear();
            nodesStatus.put(peerName, true);
        }
        // don't load transfer information when tree node is collapsed
        if (showTransfers) {
            // load download status for selected peer
            if (downloads.containsKey(peerName)) {
                downloads.remove(peerName);
            }
            downloads.put(peerName, loadStatus(peerName, Subscription.LOCAL));
            // load upload status for selected peer
            if (uploads.containsKey(peerName)) {
                uploads.remove(peerName);
            }
            uploads.put(peerName, loadStatus(peerName, Subscription.PEER));
        }
        model.addAttribute("peers_status", nodesStatus);
        model.addAttribute("peers_downloads", downloads);
        model.addAttribute("peers_uploads", uploads);
        return "status";
    }
    
    /**
     * Set node name as model attribute.
     * @return Node name
     */
    @ModelAttribute("server_name")
    public String getNodeName() {
        return confManager.getAppConf().getNodeName();
    }
    
    /**
     * Set current time as model attribute.
     * @return Current time stamp
     */
    @ModelAttribute("current_time")
    public String getCurrentTime() {
        return format.format(new Date());
    }
    
    /**
     * Set software version as model attribute.
     * @return Software version
     */
    @ModelAttribute("software_version")
    public String getSoftwareVersion() {
        return confManager.getAppConf().getVersion();
    }
    
    /**
     * Set node operator name as model attribute.
     * @return Operator name
     */
    @ModelAttribute("operator_name")
    public String getOperatorName() {
        return confManager.getAppConf().getOrgName();
    }
    
    /**
     * Set number of active downloads as model attribute.
     * @return Number of active downloads
     */
    @ModelAttribute("active_downloads")
    public long getActiveDownloads() {
        return subscriptionManager.count(Subscription.LOCAL);
    }
    
    /**
     * Set number of active uploads as model attribute.
     * @return Number of active uploads
     */
    @ModelAttribute("active_uploads")
    public long getActiveUploads() {
        return subscriptionManager.count(Subscription.PEER);
    }
    
    /**
     * Returns a boolean indicating whether or not the node address 
     * stored in the app configuration is 'localhost' or not.
     * @return isNodeAddressLocal
     */
    @ModelAttribute("is_node_address_local")
    public boolean isNodeAddressLocal() {
    	String nodeAddress = confManager.getAppConf().getNodeAddress();
    	return WebValidator.isUrlLocal(nodeAddress);
    }
    
    /**
     * Set number of file entries in the database as model attribute.
     * @return Number of file entries in the db
     */
    @ModelAttribute("db_file_entries")
    public long getFileEntries() {
        try {
            return bltFileManager.count();
        } catch (Exception e) {   
            return 0;
        }
    }
    
    /**
     * Set number of entries the system log as model attribute.
     * @return Number of file entries in the system log
     */
    @ModelAttribute("log_entries")
    public long getLogEntries() {
        return logManager.count();
    }
    
    /**
     * Set number of entries the delivery registry as model attribute.
     * @return Number of file entries in the delivery registry
     */
    @ModelAttribute("delivery_entries")
    public long getDeliveryEntries() {
        return registryManager.count(RegistryEntry.DOWNLOAD) + 
                registryManager.count(RegistryEntry.UPLOAD);
    }
    
    /**
     * Set available disk space as model attribute.
     * @return Available disk space in megabytes
     */
    @ModelAttribute("disk_space")
    public String getDiskSpace() {
        String path = ServletContextUtil.getServletContextPath() + 
                confManager.getAppConf().getWorkDir();
        File f = new File(path);
        long mbUsable = f.getUsableSpace() / BYTES_PER_MB;
        long mbTotal = f.getTotalSpace() / BYTES_PER_MB;
        long percentUsable = 0;
        if (mbTotal > 0) {
            percentUsable = mbUsable * 100 / mbTotal;
        }
        return Long.toString(mbUsable) + " MB (" + percentUsable +"%)";
    }
    
    /**
     * Sets last configuration storage date as model attribute.
     * @return Last configuration storage date
     */
    @ModelAttribute("last_config_saved")
    public String getLastConfigSaved() {
        return format.format(new Date(confManager.getLastModified()));
    }
    
    /**
     * Set user name as model attribute.
     * @param session HTTP session
     * @param principal Principal
     * @return User name
     */
    @ModelAttribute("current_user")
    public String getCurrentUser(HttpSession session, 
                HttpServletRequest request, Principal principal) {
        if (SecurityManager.getSessionUser(session) == null) {
            User user = userManager.load(principal.getName());
            SecurityManager.setSessionUser(session, user);
            SecurityManager.setSessionRole(session, 
                    roleManager.load(user.getRole()));
            String[] args = {user.getName(), request.getRemoteAddr()};
            log.info(messages.getMessage(LOGIN_MSG, args));
            return user.getName();
        } else {
            return SecurityManager.getSessionUser(session).getName();
        }
    }    

    /**
     * @param statusManager the statusManager to set
     */
    @Autowired
    public void setNodeStatusManager(INodeStatusManager statusManager) {
        this.statusManager = statusManager;
    }
    
    /**
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * @param roleManager the roleManager to set
     */
    @Autowired
    public void setRoleManager(IRoleManager roleManager) {
        this.roleManager = roleManager;
    }

    /**
     * @param confManager the confManager to set
     */
    @Autowired
    public void setConfManager(ConfigurationManager confManager) {
        this.confManager = confManager;
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
    public void setSubscriptionManager(ISubscriptionManager subscriptionManager) 
    {
        this.subscriptionManager = subscriptionManager;
    }

    /**
     * @param bltFileManager the bltFileManager to set
     */
    @Autowired
    public void setBltFileManager(IBltFileManager bltFileManager) {
        this.bltFileManager = bltFileManager;
    }

    /**
     * @param logManager the logManager to set
     */
    @Autowired
    public void setLogManager(ILogManager logManager) {
        this.logManager = logManager;
    }

    /**
     * @param registryManager the registryManager to set
     */
    @Autowired
    public void setRegistryManager(IRegistryManager registryManager) {
        this.registryManager = registryManager;
    }
    
}
