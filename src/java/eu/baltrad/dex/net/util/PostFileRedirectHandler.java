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

package eu.baltrad.dex.net.util;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.baltrad.dex.log.StickyLevel;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.ResponseParser;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.MessageResourceUtil;

/**
 * @author Anders Henja
 */
public class PostFileRedirectHandler {
  private final static String POST_REDIRECTED = "postfile.server.connection_server_update_redirected";
  
  private ProtocolManager protocolManager;
  private User peerUser;
  private User localUser;
  private Authenticator authenticator;
  private IUserManager userManager;
  protected Logger log;
  private MessageResourceUtil messages;
  private byte[] fileContent;
  
  public PostFileRedirectHandler() {
    this.log = Logger.getLogger("DEX");
  }
  
  /**
   * Returns if this redirect handler can handle the status code of the response 
   * @param response the response to be checked
   * @return true if this class can handle the response
   */
  public boolean canHandle(HttpResponse response) {
    return (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_MOVED_PERMANENTLY ||
            response.getStatusLine().getStatusCode() == HttpServletResponse.SC_MOVED_TEMPORARILY);
  }
  
  /**
   * Handles the response and atempts to create a new http request if the response indicates a redirect
   * @param client the http client to use
   * @param request the request resulting in the response
   * @param response the response
   * @return a new response from the redirect call if any
   * @throws Exception 
   */
  public HttpResponse handle(IHttpClientUtil client, HttpUriRequest request, HttpResponse response) throws Exception {
    ResponseParser parser = protocolManager.createParser(response);
    if (parser.isRedirected()) {
      String redirectURL = extractBaseUrlFromRedirect(peerUser.getNodeAddress(), request.getURI().toString(), parser.getRedirectURL());
      if (peerUser.getRedirectedAddress() == null) {
          peerUser.setRedirectedAddress(redirectURL);
          userManager.update(peerUser);
          log.log(StickyLevel.STICKY, messages.getMessage(POST_REDIRECTED, new String[] {peerUser.getName(), redirectURL}));
      }
      RequestFactory requestFactory = protocolManager.getFactory(redirectURL);
      HttpUriRequest redirectRequest = requestFactory.createPostFileRequest(localUser, fileContent);
      authenticator.addCredentials(redirectRequest, localUser.getName());
      HttpResponse redirectResponse = client.post(redirectRequest);
      return redirectResponse;
    }
    return null;
  }
  
  /**
   * Extracts redirect uri based on origin
   * @param baseURI
   * @param originURI
   * @param redirectURI
   * @return
   */
  public String extractBaseUrlFromRedirect(String baseURI, String originURI, String redirectURI) {
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
   * @return the protocol manager
   */
  public ProtocolManager getProtocolManager() {
    return protocolManager;
  }

  /**
   * @param protocolManager the protocol manager
   */
  @Autowired
  public void setProtocolManager(ProtocolManager protocolManager) {
    this.protocolManager = protocolManager;
  }

  /**
   * @return the peer user
   */
  public User getPeerUser() {
    return peerUser;
  }

  /**
   * @param peerUser the peer user
   */
  @Autowired  
  public void setPeerUser(User peerUser) {
    this.peerUser = peerUser;
  }

  /**
   * @return the local user
   */
  public User getLocalUser() {
    return localUser;
  }

  /**
   * @param localUser the local user
   */
  @Autowired
  public void setLocalUser(User localUser) {
    this.localUser = localUser;
  }

  /**
   * @return the authenticator
   */
  public Authenticator getAuthenticator() {
    return authenticator;
  }

  /**
   * @param authenticator the authenticator
   */
  @Autowired
  public void setAuthenticator(Authenticator authenticator) {
    this.authenticator = authenticator;
  }

  /**
   * @return the user manager
   */
  public IUserManager getUserManager() {
    return userManager;
  }

  /**
   * @param userManager the user manager
   */
  @Autowired
  public void setUserManager(IUserManager userManager) {
    this.userManager = userManager;
  }

  /**
   * @return utility for managing messages
   */
  public MessageResourceUtil getMessages() {
    return messages;
  }

  /**
   * @param messages utility for managing messages
   */
  @Autowired
  public void setMessages(MessageResourceUtil messages) {
    this.messages = messages;
  }

  /**
   * @return the file content
   */
  public byte[] getFileContent() {
    return fileContent;
  }

  /**
   * @param fileContent the file content
   */
  public void setFileContent(byte[] fileContent) {
    this.fileContent = fileContent;
  }
}
