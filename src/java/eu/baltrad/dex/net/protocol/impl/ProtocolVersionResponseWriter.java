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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.protocol.ResponseWriter;
import eu.baltrad.dex.net.protocol.json.JsonProtocol;
import eu.baltrad.dex.user.model.User;

/**
 * @author Anders Henja
 */
public class ProtocolVersionResponseWriter implements ResponseWriter {
  private final static String NODE_NAME_HEADER = "Node-Name";

  private final static String PROTOCOL_VERSION_HEADER = "DEX-Protocol-Version";
  
  private final static String NODE_PROTOCOL_VERSION_HEADER = "DEX-Node-Protocol-Version";
  
  /**
   * The json protocol to use if any
   */
  private JsonProtocol jsonProtocol;

  /**
   * Wrapper around the http response
   */
  private HttpServletResponseWrapper responseWrapper = null;
  
  /**
   * The response version
   */
  private String responseVersion = null;
  
  /**
   * The highest version we are able to support. Used in the response so that the requestor knows what we are capable of
   */
  private String supportedVersion = null;
  
  private static Logger logger = LogManager.getLogger(ProtocolVersionResponseWriter.class);
  
  /**
   * Constructor
   * @param response the http response
   * @param responseVersion the version to be used in the response
   * @param supportedVersion what version we want to use
   */
  public ProtocolVersionResponseWriter(HttpServletResponse response, String responseVersion, String supportedVersion) {
    this.responseWrapper = createResponseWrapper(response);
    jsonProtocol = getJsonProtocol(responseVersion);
    this.responseVersion = responseVersion;
    this.supportedVersion = supportedVersion;
  }
  
  /**
   * @see eu.baltrad.dex.net.protocol.ResponseWriter#userAccountResponse(java.lang.String, eu.baltrad.dex.user.model.User, int)
   */
  @Override
  public void userAccountResponse(String localNodeName, User user, int status) throws IOException {
    PrintWriter writer = createPrintWriter(responseWrapper);
    try {
      writer.print(jsonProtocol.userAccountToJson(user));
      responseWrapper.setStatus(status);
      responseWrapper.addHeader(NODE_NAME_HEADER, localNodeName);
      responseWrapper.addHeader(PROTOCOL_VERSION_HEADER, this.responseVersion);
      responseWrapper.addHeader(NODE_PROTOCOL_VERSION_HEADER, this.supportedVersion);
    } finally {
      writer.close();
    }
  }

  /**
   * @see eu.baltrad.dex.net.protocol.ResponseWriter#dataSourcesResponse(java.lang.String, java.util.Set, int)
   */
  @Override
  public void dataSourcesResponse(String localNodeName,
      Set<DataSource> dataSources, int status) throws IOException {
    PrintWriter writer = createPrintWriter(responseWrapper);
    try {
      writer.print(jsonProtocol.dataSourcesToJson(dataSources));
      responseWrapper.setStatus(status);
      responseWrapper.addHeader(NODE_NAME_HEADER, localNodeName);
      responseWrapper.addHeader(PROTOCOL_VERSION_HEADER, this.responseVersion);
      responseWrapper.addHeader(NODE_PROTOCOL_VERSION_HEADER, this.supportedVersion);
    } finally {
      writer.close();
    }
  }

  /**
   * @see eu.baltrad.dex.net.protocol.ResponseWriter#dataSourcesResponse(java.lang.String, java.util.List, int)
   */
  @Override
  public void dataSourcesResponse(String localNodeName,
      List<DataSource> dataSources, int status) throws IOException {
    dataSourcesResponse(localNodeName, dsListToSet(dataSources), status);
  }


  /**
   * @see eu.baltrad.dex.net.protocol.ResponseWriter#subscriptionResponse(java.lang.String, java.util.List, int)
   */
  @Override
  public void subscriptionResponse(String localNodeName,
      List<Subscription> subscriptions, int status) throws IOException {
    PrintWriter writer = createPrintWriter(responseWrapper);
    
    try {
      writer.print(jsonProtocol.subscriptionsToJson(subscriptions));
      responseWrapper.setStatus(status);
      responseWrapper.addHeader(NODE_NAME_HEADER, localNodeName);
      responseWrapper.addHeader(PROTOCOL_VERSION_HEADER, this.responseVersion);
      responseWrapper.addHeader(NODE_PROTOCOL_VERSION_HEADER, this.supportedVersion);
    } finally {
      writer.close();
    }
  }
  
  /**
   * @see eu.baltrad.dex.net.protocol.ResponseWriter#messageResponse(java.lang.String, int)
   */
  @Override
  public void messageResponse(String message, int status) {
    responseWrapper.setStatus(status, message);
  }

  /**
   * @see eu.baltrad.dex.net.protocol.ResponseWriter#statusResponse(int)
   */
  @Override
  public void statusResponse(int status) {
    responseWrapper.setStatus(status);
  }
  
  /**
   * Creates a http response wrapper. 
   * @param response the response to wrap
   * @return the wrapped response
   */
  protected HttpServletResponseWrapper createResponseWrapper(HttpServletResponse response) {
    return new HttpServletResponseWrapper(response);
  }

  /**
   * Creates a set from a list
   * @param dataSources the list to convert
   * @return the converted set
   */
  protected Set<DataSource> dsListToSet(List<DataSource> dataSources) {
    return new HashSet<DataSource>(dataSources);
  }
  
  /**
   * Creates the print wrapper
   * @param wrapper the wrapper around the servlet response
   * @return the print writer
   * @throws IOException upon error
   */
  protected PrintWriter createPrintWriter(HttpServletResponseWrapper wrapper) throws IOException {
    return new PrintWriter(new OutputStreamWriter(responseWrapper.getOutputStream(), "UTF-8"));
  }
  
  /**
   * Returns a json protocol suitable for provided version
   * @param version the version
   * @return the json protocol
   */
  protected JsonProtocol getJsonProtocol(String version) {
    return ProtocolVersionRequestFactory.getJsonProtocolForVersion(version);
  }
}
