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

package eu.baltrad.dex.net.protocol.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.keyczar.exceptions.KeyczarException;
import eu.baltrad.beast.message.IBltXmlMessage;
import eu.baltrad.beast.parser.IXmlMessageParser;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.protocol.RequestParser;
import eu.baltrad.dex.net.protocol.RequestParserException;
import eu.baltrad.dex.net.protocol.ResponseWriter;
import eu.baltrad.dex.net.protocol.json.JsonProtocol;
import eu.baltrad.dex.net.request.impl.NodeRequest;
import eu.baltrad.dex.user.model.User;

/**
 * @author Anders Henja
 */
public class ProtocolVersionRequestParser implements RequestParser {
  private NodeRequest request;
  private String defaultVersion;
  private IXmlMessageParser xmlMessageParser;
  
  /**
   * Constructor
   * @param request the http request
   * @param defaultVersion the default version that we want to use for the communication.
   * @param xmlMessageParser an xml message parser instance
   */
  public ProtocolVersionRequestParser(HttpServletRequest request, String defaultVersion, IXmlMessageParser xmlMessageParser) {
    this.request = createNodeRequest(request);
    this.defaultVersion = defaultVersion;
    this.xmlMessageParser = xmlMessageParser;
  }
  
  /**
   * @see eu.baltrad.dex.net.protocol.RequestParser#getMessage()
   */
  @Override
  public String getMessage() {
    return request.getMessage();
  }

  /**
   * @see eu.baltrad.dex.net.protocol.RequestParser#getSignature()
   */
  @Override
  public String getSignature() {
    return request.getSignature();
  }

  /**
   * @see eu.baltrad.dex.net.protocol.RequestParser#getUser()
   */
  @Override
  public String getUser() {
    return request.getUser();
  }

  /**
   * @see eu.baltrad.dex.net.protocol.RequestParser#getNodeName()
   */
  @Override
  public String getNodeName() {
    return request.getNodeName();
  }

  /**
   * @see eu.baltrad.dex.net.protocol.RequestParser#getProtocolVersion()
   */
  @Override
  public String getProtocolVersion() {
    return request.getProtocolVersion();
  }

  /**
   * @see eu.baltrad.dex.net.protocol.RequestParser#getWriter(javax.servlet.http.HttpServletResponse)
   */
  @Override
  public ResponseWriter getWriter(HttpServletResponse response) {
    return new ProtocolVersionResponseWriter(response, getProtocolVersion(), defaultVersion);
  }

  /**
   * @see eu.baltrad.dex.net.protocol.RequestParser#isAuthenticated(eu.baltrad.dex.net.auth.Authenticator)
   */
  @Override
  public boolean isAuthenticated(Authenticator authenticator) throws KeyczarException {
    return authenticator.authenticate(getMessage(), getSignature(), getNodeName());
  }

  /**
   * @see eu.baltrad.dex.net.protocol.RequestParser#getUserAccount()
   */
  @Override
  public User getUserAccount() {
    try {
      return getJsonProtocol(getProtocolVersion()).jsonToUserAccount(readInputStream());
    } catch (Exception e) {
      throw new RequestParserException(e);
    }
  }


  /**
   * @see eu.baltrad.dex.net.protocol.RequestParser#getDataSources()
   */
  @Override
  public Set<DataSource> getDataSources() {
    try {
      return getJsonProtocol(getProtocolVersion()).jsonToDataSources(readInputStream());
    } catch (Exception e) {
      throw new RequestParserException(e);
    }
  }

  /**
   * @see eu.baltrad.dex.net.protocol.RequestParser#getSubscriptions()
   */
  @Override
  public List<Subscription> getSubscriptions() {
    try {
      return getJsonProtocol(getProtocolVersion()).jsonToSubscriptions(readInputStream());
    } catch (Exception e) {
      throw new RequestParserException(e);
    }
  }

  /**
   * @see eu.baltrad.dex.net.protocol.RequestParser#getBltXmlMessage()
   */
  @Override
  public IBltXmlMessage getBltXmlMessage() {
    try {
      return xmlMessageParser.parse(readInputStream());
    } catch (Exception e) {
      throw new RequestParserException(e);
    }
  }
  
  /**
   * @return the input stream as UTF-8
   * @throws IOException upon failure
   */
  protected String readInputStream() throws IOException {
    ServletInputStream sis = request.getInputStream();
    StringWriter writer = new StringWriter();
    String json = "";
    try {
        IOUtils.copy(sis, writer, "UTF-8");
        json = writer.toString();
    } finally {
        writer.close();
        sis.close();
    }
    return json;    
  }

  /**
   * Creates a node request from the servlet request
   * @param req the servlet request
   * @return the node request
   */
  protected NodeRequest createNodeRequest(HttpServletRequest req) {
    return new NodeRequest(req);
  }
  
  /**
   * @param version the protocol version we are using
   * @return the json protocol to use for this version
   */
  protected JsonProtocol getJsonProtocol(String version) {
    return ProtocolVersionRequestFactory.getJsonProtocolForVersion(version);
  }
}
