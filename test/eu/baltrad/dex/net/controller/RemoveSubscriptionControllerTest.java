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

package eu.baltrad.dex.net.controller;

import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.controller.util.ModelMessageHelper;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;
import java.util.ArrayList;
import java.util.List;
import org.easymock.EasyMockSupport;
import static org.easymock.EasyMock.*;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Remove subscription controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.9.0
 * @since 1.9.0
 */
public class RemoveSubscriptionControllerTest extends EasyMockSupport {
  interface MethodMock {
    int cancelDownloads(String peerName, User local, List<Subscription> downloads);
    int removeDownloads(List<Subscription> downloads);
  }
  

    private RSController classUnderTest;
    private IUserManager userManagerMock;
    private ISubscriptionManager subscriptionManagerMock;
    private Authenticator authenticatorMock;
    private IHttpClientUtil httpClientMock;
    private PlatformTransactionManager txManagerMock;
    private IDataSourceManager dataSourceManagerMock;
    private INodeStatusManager nodeStatusManagerMock;
    
    private List<User> operators;
    private List<Subscription> downloads;
    private List<Subscription> activeDownloads;
    private List<Subscription> uploads;
    private Subscription s1, s2, s3, s4, s5;
    private MockHttpServletRequest request;

    private ModelMessageHelper messageHelper;
    
    private MethodMock methods = null;
    
    class RSController extends RemoveSubscriptionController {
        public RSController() {
            this.localNode = new User(1, "test", "user", "s3cret", "org", 
                    "unit", "locality", "state", "XX", "http://localhost:8084");
        }
        @Override
        public void initConfiguration() {}
        
        public void setPeerName(String peerName) {
            this.peerName = peerName;
        }
        public List<Subscription> getSelectedDownloads() {
            return this.selectedDownloads;
        }
        public void setSelectedDownloads(List<Subscription> selectedDownloads) {
            this.selectedDownloads = selectedDownloads;
        }
        public void setSelectedActiveDownloads(List<Subscription> selectedActiveDownloads) {
          this.selectedActiveDownloads = selectedActiveDownloads;
        }
        public List<Subscription> getSelectedActiveDownloads() {
            return this.selectedActiveDownloads;
        }
        public List<Subscription> getSelectedUploads() {
            return this.selectedUploads;
        }
        public void setSelectedUploads(List<Subscription> selectedUploads) {
            this.selectedUploads = selectedUploads;
        }
        @Override
        protected int cancelDownloads(String peerName, User local, List<Subscription> downloads) {
          return methods.cancelDownloads(peerName, local, downloads);
        }
        @Override
        protected int removeDownloads(List<Subscription> downloads) {
          return methods.removeDownloads(downloads);
        }
        
    }
    
    @Before
    public void setUp() {
        classUnderTest = new RSController();
        request = new MockHttpServletRequest();
        userManagerMock = createMock(IUserManager.class);
        subscriptionManagerMock = createMock(ISubscriptionManager.class);
        authenticatorMock = createMock(Authenticator.class);
        httpClientMock = createMock(IHttpClientUtil.class);
        txManagerMock = createMock(PlatformTransactionManager.class);
        dataSourceManagerMock = createMock(IDataSourceManager.class);
        methods = createMock(MethodMock.class);
        messageHelper = createMock(ModelMessageHelper.class);
        nodeStatusManagerMock = createMock(INodeStatusManager.class);
        
        classUnderTest.setMessageHelper(messageHelper);
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setTxManager(txManagerMock);
        classUnderTest.setPeerName("PeerUser");
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
        
        operators = new ArrayList<User>();
        operators.add(new User(1, "test1.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
                "http://test1.baltrad.eu"));
        operators.add(new User(2, "test2.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
                "http://test2.baltrad.eu"));
        operators.add(new User(3, "test3.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
                "http://test3.baltrad.eu"));
        
        downloads = new ArrayList<Subscription>();
        long time = 1340189763867L;
        downloads = new ArrayList<Subscription>();
        s1 = new Subscription(1, time, "download", "PeerUser", "DataSource1", 
                true, true);
        downloads.add(s1);
        s2 = new Subscription(2, time, "download", "PeerUser", "DataSource2", 
                true, false);
        downloads.add(s2);
        s3 = new Subscription(3, time, "download", "PeerUser", "DataSource3", 
                false, false);
        downloads.add(s3);
        
        activeDownloads = new ArrayList<Subscription>();
        activeDownloads.add(s1);
        activeDownloads.add(s2);
        
        uploads = new ArrayList<Subscription>();
        s4 = new Subscription(4, time, "upload", "PeerUser", "DataSource4", 
                true, true);
        uploads.add(s4);
        s5 = new Subscription(5, time, "upload", "PeerUser", "DataSource5", 
                true, false);
        uploads.add(s5);
        
        classUnderTest.setSelectedActiveDownloads(activeDownloads);
        classUnderTest.setSelectedDownloads(downloads);
        classUnderTest.setSelectedUploads(uploads);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        request = null;
        operators = null;
        downloads = null;
        activeDownloads = null;
        uploads = null;
        resetAll();
    }
    
    @Test
    public void subscribedPeers() {
        expect(userManagerMock.loadOperators()).andReturn(operators).once();
        
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.subscribedPeers(model);
        
        assertEquals("subscription_remove_downloads_peers", viewName);
        assertTrue(model.containsAttribute("downloads_peers"));
        assertEquals(operators, model.asMap().get("downloads_peers"));
        
        verifyAll();
    }
    
    @Test
    public void downloadsByPeer() {
        expect(subscriptionManagerMock.load(Subscription.LOCAL, "PeerUser"))
                .andReturn(downloads).once();
        
        replayAll();
        
        Model model = new ExtendedModelMap();
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        String viewName = classUnderTest.downloadsByPeer(model, "PeerUser");
        
        assertEquals("subscription_remove_downloads", viewName);
        assertTrue(model.containsAttribute("downloads"));
        assertEquals(downloads, model.asMap().get("downloads"));
        assertTrue(model.containsAttribute("peer_name"));
        assertEquals("PeerUser", model.asMap().get("peer_name"));
        
        verifyAll();
    }
    
    @Test
    public void removeSelectedDownloads_Unchecked() {
        expect(subscriptionManagerMock.load(Subscription.LOCAL, "PeerUser"))
                .andReturn(downloads).once();
        
        replayAll();
        
        classUnderTest.setPeerName("PeerUser");
        Model model = new ExtendedModelMap();
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        String viewName = classUnderTest
                .removeSelectedDownloads(request, model);
        
        assertEquals("subscription_remove_downloads", viewName);
        assertTrue(model.containsAttribute("downloads"));
        assertEquals(downloads, model.asMap().get("downloads"));
        assertTrue(model.containsAttribute("peer_name"));
        assertEquals("PeerUser", model.asMap().get("peer_name"));
        
        verifyAll();
    }
    
    @Test
    public void removeSelectedDownloads_Checked() {
        expect(subscriptionManagerMock.load(1)).andReturn(s1).once();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).once();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).once();
        
        replayAll();
        
        request.addParameter("downloadIds", new String[] {"1", "2", "3"});
        
        classUnderTest.setPeerName("PeerUser");
        Model model = new ExtendedModelMap();
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        String viewName = classUnderTest
                .removeSelectedDownloads(request, model);
        
        assertEquals("subscription_remove_selected_downloads", viewName);
        assertEquals(downloads, classUnderTest.getSelectedDownloads());
        assertEquals(activeDownloads, 
                classUnderTest.getSelectedActiveDownloads());
        
        verifyAll();
    }
    
    @Test
    public void removeDownloadsStatus_OK() throws Exception {
      Model model = createMock(Model.class);
      
      expect(methods.cancelDownloads("PeerUser", classUnderTest.localNode, activeDownloads)).andReturn(200);
      expect(methods.removeDownloads(downloads)).andReturn(0);
      messageHelper.setKeyMessage(model, "subscription_remove_success", "removesubscription.cancel_ok_remove_ok");
      
      replayAll();

      String viewName = classUnderTest.removeDownloadsStatus(model);
      
      verifyAll();
      assertEquals("subscription_remove_downloads_status", viewName);
    }
    
    @Test
    public void removeDownloadsStatus_CancelFailRemoveOK() throws Exception {
      Model model = createMock(Model.class);
      
      expect(methods.cancelDownloads("PeerUser", classUnderTest.localNode, activeDownloads)).andReturn(500);
      expect(methods.removeDownloads(downloads)).andReturn(0);
      messageHelper.setKeyMessage(model, "subscription_remove_error", "removesubscription.cancel_fail_remove_ok");
      
      replayAll();

      String viewName = classUnderTest.removeDownloadsStatus(model);
      
      verifyAll();
      assertEquals("subscription_remove_downloads_status", viewName);
    }
    
    @Test
    public void removeDownloadsStatus_CancelOKRemoveFail() throws Exception {
      Model model = createMock(Model.class);
      
      expect(methods.cancelDownloads("PeerUser", classUnderTest.localNode, activeDownloads)).andReturn(200);
      expect(methods.removeDownloads(downloads)).andReturn(1);
      messageHelper.setKeyMessage(model, "subscription_remove_error", "removesubscription.cancel_ok_remove_fail");
      
      replayAll();

      String viewName = classUnderTest.removeDownloadsStatus(model);
      
      verifyAll();
      assertEquals("subscription_remove_downloads_status", viewName);
    }
    
    @Test
    public void removeDownloadsStatus_Fail() throws Exception {
      Model model = createMock(Model.class);
      
      expect(methods.cancelDownloads("PeerUser", classUnderTest.localNode, activeDownloads)).andReturn(500);
      expect(methods.removeDownloads(downloads)).andReturn(1);
      messageHelper.setKeyMessage(model, "subscription_remove_error", "removesubscription.cancel_fail_remove_fail");
      
      replayAll();

      String viewName = classUnderTest.removeDownloadsStatus(model);
      
      verifyAll();
      assertEquals("subscription_remove_downloads_status", viewName);
    }
    
    @Test
    public void removeDownloads() throws Exception {
      
      List<Subscription> downloads = new ArrayList<Subscription>();
      downloads.add(new Subscription(1, 1340189763867L, "downloads", "PeerUser", "DS1", true, true));
      downloads.add(new Subscription(2, 1340189763867L, "downloads", "PeerUser", "DS2", true, true));
      
      TransactionStatus status = new SimpleTransactionStatus();
      DataSource ds1 = createMock(DataSource.class);
      DataSource ds2 = createMock(DataSource.class);
      
      expect(txManagerMock.getTransaction(isA(TransactionDefinition.class))).andReturn(status);
      expect(dataSourceManagerMock.load("DS1", "peer")).andReturn(ds1);
      expect(ds1.getId()).andReturn(3);
      expect(dataSourceManagerMock.delete(3)).andReturn(1);
      subscriptionManagerMock.delete(1);
      expect(nodeStatusManagerMock.delete(1)).andReturn(1);
      expect(messageHelper.getMessage("removesubscription.success", "PeerUser", "DS1")).andReturn("Removed 1");
      
      expect(dataSourceManagerMock.load("DS2", "peer")).andReturn(ds2);
      expect(ds2.getId()).andReturn(4);
      expect(dataSourceManagerMock.delete(4)).andReturn(1);
      subscriptionManagerMock.delete(2);
      expect(nodeStatusManagerMock.delete(2)).andReturn(1);
      expect(messageHelper.getMessage("removesubscription.success", "PeerUser", "DS2")).andReturn("Removed 2");
      
      txManagerMock.commit(status);
      
      replayAll();

      RemoveSubscriptionController classUnderTest = new RemoveSubscriptionController();
      classUnderTest.setTxManager(txManagerMock);
      classUnderTest.setSubscriptionManager(subscriptionManagerMock);
      classUnderTest.setMessageHelper(messageHelper);
      classUnderTest.setDataSourceManager(dataSourceManagerMock);
      classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
      
      int result = classUnderTest.removeDownloads(downloads);
      
      verifyAll();
      assertEquals(0, result);
    }

    @Test
    public void removeDownloads_exception() throws Exception {
      
      List<Subscription> downloads = new ArrayList<Subscription>();
      downloads.add(new Subscription(1, 1340189763867L, "downloads", "PeerUser", "DS1", true, true));
      downloads.add(new Subscription(2, 1340189763867L, "downloads", "PeerUser", "DS2", true, true));
      
      TransactionStatus status = new SimpleTransactionStatus();
      DataSource ds1 = createMock(DataSource.class);
      DataSource ds2 = createMock(DataSource.class);
      
      expect(txManagerMock.getTransaction(isA(TransactionDefinition.class))).andReturn(status);
      expect(dataSourceManagerMock.load("DS1", "peer")).andReturn(ds1);
      expect(ds1.getId()).andReturn(3);
      expect(dataSourceManagerMock.delete(3)).andReturn(1);
      subscriptionManagerMock.delete(1);
      expect(nodeStatusManagerMock.delete(1)).andReturn(1);
      expect(messageHelper.getMessage("removesubscription.success", "PeerUser", "DS1")).andReturn("Removed 1");
      
      expect(dataSourceManagerMock.load("DS2", "peer")).andReturn(ds2);
      expect(ds2.getId()).andReturn(4);
      expect(dataSourceManagerMock.delete(4)).andReturn(1);
      subscriptionManagerMock.delete(2);
      expectLastCall().andThrow(new Exception("Failed to delete subscription 2"));
      
      txManagerMock.rollback(status);
      
      replayAll();

      RemoveSubscriptionController classUnderTest = new RemoveSubscriptionController();
      classUnderTest.setTxManager(txManagerMock);
      classUnderTest.setSubscriptionManager(subscriptionManagerMock);
      classUnderTest.setMessageHelper(messageHelper);
      classUnderTest.setDataSourceManager(dataSourceManagerMock);
      classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
      
      int result = classUnderTest.removeDownloads(downloads);
      
      verifyAll();
      assertEquals(1, result);
    }

    
    @Test
    public void removeUploads() {
      Model model = createMock(Model.class);
      
      expect(subscriptionManagerMock.load(Subscription.PEER)).andReturn(uploads).once();
      expect(model.addAttribute("uploads", uploads)).andReturn(null); 

      replayAll();
      
      String viewName = classUnderTest.removeUploads(model);
      
      verifyAll();
      assertEquals("subscription_remove_uploads", viewName);
    }
    
    @Test 
    public void removeSelectedUploads_Unchecked() {
        expect(subscriptionManagerMock.load(Subscription.PEER))
                .andReturn(uploads).once();
        
        replayAll();
        
        Model model = new ExtendedModelMap();
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        String viewName = classUnderTest
                .removeSelectedUploads(request, model);
        
        assertEquals("subscription_remove_uploads", viewName);
        assertTrue(model.containsAttribute("uploads"));
        assertEquals(uploads, model.asMap().get("uploads"));
        
        verifyAll();
    }
    
    @Test 
    public void removeSelectedUploads_Checked() {
        expect(subscriptionManagerMock.load(4)).andReturn(s4).once();
        expect(subscriptionManagerMock.load(5)).andReturn(s5).once();
        
        replayAll();
        
        request.addParameter("uploadIds", new String[] {"4", "5"});
        
        Model model = new ExtendedModelMap();
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        String viewName = classUnderTest
                .removeSelectedUploads(request, model);
        
        assertEquals("subscription_remove_selected_uploads", viewName);
        assertEquals(uploads, classUnderTest.getSelectedUploads());
        
        verifyAll();
    }
    
    @Test
    public void removeUploadsStatus_OK() throws Exception {
      Model model = createMock(Model.class);
      
      subscriptionManagerMock.delete(4);
      expect(nodeStatusManagerMock.delete(4)).andReturn(1);
      expect(messageHelper.getMessage("removesubscription.success", "DataSource4", "PeerUser")).andReturn("Deleted upload 4");
      subscriptionManagerMock.delete(5);
      expect(nodeStatusManagerMock.delete(5)).andReturn(1);
      expect(messageHelper.getMessage("removesubscription.success", "DataSource5", "PeerUser")).andReturn("Deleted upload 5");
      messageHelper.setKeyMessage(model, "subscription_remove_success", "removesubscription.completed_success");
      
      replayAll();
      
      String viewName = classUnderTest.removeUploadsStatus(model);
      
      verifyAll();
      assertEquals("subscription_remove_uploads_status", viewName);
    }
    
    @Test
    public void removeUploadsStatus_Fail() throws Exception {
      Model model = createMock(Model.class);
      subscriptionManagerMock.delete(4);
      expectLastCall().andThrow(new Exception("Failed to remove subscription"));
      messageHelper.setKeyMessage(model, "subscription_remove_error", "removesubscription.completed_failure");

      replayAll();
      
      String viewName = classUnderTest.removeUploadsStatus(model);
      
      verifyAll();
      assertEquals("subscription_remove_uploads_status", viewName);
    }
}
