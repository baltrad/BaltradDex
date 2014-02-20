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

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.protocol.ResponseParser;
import eu.baltrad.dex.net.protocol.ResponseParserException;
import eu.baltrad.dex.net.protocol.json.JsonProtocol;
import eu.baltrad.dex.user.model.User;

/**
 * Supports parsing of responses and is able to manage different versions of the dex protocol
 * @author Anders Henja
 */
public class ProtocolVersionResponseParser implements ResponseParser {
  static final String PROTOCOL_VERSION_HDR = "DEX-Protocol-Version";
  
  static final String NODE_PROTOCOL_VERSION_HDR = "DEX-Node-Protocol-Version";

  /**
   * The http response that is parsed
   */
  protected HttpResponse httpResponse;
  
  /**
   * The json protocol to use (if any)
   */
  protected JsonProtocol jsonProtocol;
  
  /**
   * The status code.
   */
  private int statusCode;
  
  /**
   * the name of the one sending a response 
   */
  private String nodeName;
  
  /**
   * The protocol version the answering node is using
   */
  private String protocolVersion;
  
  /**
   * For debug purposes
   */
  private final static Logger logger = LogManager.getLogger(ProtocolVersionResponseParser.class);
  
  /**
   * Constructor. Initially parses statusCode, nodeName and protocolVersion since there is no meaning
   * to allow the constructor to be created without this information.
   * @param httpResponse the http response to parse
   * @throws ResponseParserException if above mentioned values not can be extracted 
   */
  public ProtocolVersionResponseParser(HttpResponse httpResponse) {
    init(httpResponse);
  }
  
  /**
   * Protected default constructor
   * Mostly for test purpose
   */
  protected ProtocolVersionResponseParser() {
  }
  
  /**
   * Initializes the object with the http response
   * @param httpResponse the http response
   */
  protected void init(HttpResponse httpResponse) {
    this.httpResponse = httpResponse;
    this.statusCode = parseStatusCode(httpResponse);
    this.nodeName = parseNodeName(httpResponse);
    this.protocolVersion = parseProtocolVersion(httpResponse);
    this.jsonProtocol = ProtocolVersionRequestFactory.getJsonProtocolForVersion(this.protocolVersion);
  }
  
  /**
   * @see ResponseParser#getStatusCode()
   */
  @Override
  public int getStatusCode() {
    return this.statusCode;
  }
  
  /**
   * @see ResponseParser#getNodeName()
   */
  @Override
  public String getNodeName() {
    return this.nodeName;
  }

  /**
   * @see ResponseParser#getProtocolVersion()
   */
  @Override
  public String getProtocolVersion() {
    return this.protocolVersion;
  }

  /**
   * @see ResponseParser#getConfiguredProtocolVersion()
   */
  @Override
  public String getConfiguredProtocolVersion() {
    try {
      Header header = httpResponse.getFirstHeader(NODE_PROTOCOL_VERSION_HDR);
      if (header != null) {
        return header.getValue();
      }
    } catch (Exception e) {
      // pass
    }
    return null;    
  }
  
  /**
   * @see ResponseParser#getReasonPhrase()
   */
  @Override
  public String getReasonPhrase() {
    try {
      return httpResponse.getStatusLine().getReasonPhrase();
    } catch (Exception e) {
      // pass
    }
    return "";
  }
  
  /**
   * @see ResponseParser#getDataSources()
   */
  @Override
  public Set<DataSource> getDataSources() {
    try {
      return this.jsonProtocol.jsonToDataSources(readResponse());
    } catch (Exception e) {
      throw new ResponseParserException(e);
    }
  }

  /**
   * @see ResponseParser#getUserAccount()
   */
  @Override
  public User getUserAccount() {
    try {
      return this.jsonProtocol.jsonToUserAccount(readResponse());
    } catch (Exception e) {
      throw new ResponseParserException(e);
    }
  }
  

  /**
   * @see eu.baltrad.dex.net.protocol.ResponseParser#getSubscriptions()
   */
  @Override
  public List<Subscription> getSubscriptions() {
    try {
      return this.jsonProtocol.jsonToSubscriptions(readResponse());
    } catch (Exception e) {
      throw new ResponseParserException(e);
    }
  }
  
  /**
   * Parses the status code from the response
   * @param response the response
   * @return the status code
   */
  protected int parseStatusCode(HttpResponse response) {
    try {
      return response.getStatusLine().getStatusCode();
    } catch (Exception e) {
      throw new ResponseParserException(e);
    }
  }
  
  /**
   * Extracts the node name from the http response
   * @param response the http response
   * @return the node name
   */
  protected String parseNodeName(HttpResponse response) {
    try {
      return response.getFirstHeader("Node-Name").getValue();
    } catch (Exception e) {
      return null;
    }
  }
  
  /**
   * Extracts the protocol version from the http response
   * @param response the http response
   * @return the protocol version. If nothing found, it defaults back to version 2.0
   */
  protected String parseProtocolVersion(HttpResponse response) {
    try {
      Header header = response.getFirstHeader(PROTOCOL_VERSION_HDR);
      if (header != null) {
        return header.getValue();
      }
    } catch (Exception e) {
      // pass
    }
    return ProtocolVersionRequestFactory.PROTOCOL_VERSION_20;
  }
  
  /**
   * Read http response body
   * @param response Http response
   * @return Response body
   * @throws ResponseParserException 
   */
  protected String readResponse() {
    try {
      InputStream is = null;
      try {
        is = httpResponse.getEntity().getContent();
        String result = IOUtils.toString(is, "UTF-8");
        logger.debug("Got response: '" + result + "'");
        return result;
      } finally {
        is.close();
      }
    } catch (Exception e) {
      throw new ResponseParserException(e);
    }
  }
}
