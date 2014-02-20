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

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;

import eu.baltrad.beast.parser.IXmlMessageParser;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.RequestParser;
import eu.baltrad.dex.net.protocol.ResponseParser;
import eu.baltrad.dex.net.protocol.json.JsonProtocol;

/**
 * Provides the user with appropriate communication handler.
 * @author Anders Henja
 * @date 2014-01-30
 */
public class ProtocolVersionManager implements ProtocolManager {
  private String defaultVersion = null;
  private IXmlMessageParser xmlMessageParser;
  
  /**
   * Default constructor
   */
  public ProtocolVersionManager() {
    this(ProtocolVersionRequestFactory.DEFAULT_PROTOCOL_VERSION);
  }

  /**
   * Default constructor
   */
  public ProtocolVersionManager(String defaultVersion) {
    this.defaultVersion = defaultVersion;
  }
  
  /**
   * @see ProtocolManager#getFactory(String)
   */
  @Override
  public synchronized RequestFactory getFactory(String nodeAddress) {
    return getFactory(nodeAddress, this.defaultVersion);
  }

  /**
   * @see ProtocolManager#getFactory(String, String)
   */
  @Override
  public synchronized RequestFactory getFactory(String nodeAddress, String version) {
    return new ProtocolVersionRequestFactory(URI.create(nodeAddress), version);
  }

  @Override
  public String getVersion() {
    return this.defaultVersion;
  }
  
  /**
   * @see ProtocolManager#getJsonProtocolForVersion(String)
   */
  @Override
  public JsonProtocol getJsonProtocolForVersion(String version) {
    return ProtocolVersionRequestFactory.getJsonProtocolForVersion(version);
  }

  @Override
  public ResponseParser createParser(HttpResponse hresp) {
    return new ProtocolVersionResponseParser(hresp);
  }
  
  @Override
  public RequestParser createParser(HttpServletRequest request) {
    return new ProtocolVersionRequestParser(request, defaultVersion, xmlMessageParser);
  }

  /**
   * @param xmlMessageParser the xml message parser to use
   */
  @Autowired
  public void setXmlMessageParser(IXmlMessageParser xmlMessageParser) {
    this.xmlMessageParser = xmlMessageParser;
  }
}
