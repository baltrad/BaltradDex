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
package eu.baltrad.dex.net.protocol;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;

import eu.baltrad.dex.net.protocol.json.JsonProtocol;

/**
 * All access to the communication protocol can be aquired through this manager.
 * @author Anders Henja
 */
public interface ProtocolManager {
  /**
   * Returns or creates the factory to use for this address. The returned factory will be using
   * the default version.
   * @param nodeAddress the node address
   * @return the request factory
   */
  public RequestFactory getFactory(String nodeAddress);
  
  /**
   * Returns or creates the factory for the specified version to use for this address
   * @param nodeAddress the node address
   * @param version the version to use
   * @return the request factory
   */
  public RequestFactory getFactory(String nodeAddress, String version);
  
  /**
   * Creates a response parser that can handle the http response.
   * @param hresp the response that should be parsed.
   * @return the parser
   */
  public ResponseParser createParser(HttpResponse hresp);
  
  /**
   * Creates a request parser that can handle http servlet requests
   * @param request the http request
   * @return the request parser
   */
  public RequestParser createParser(HttpServletRequest request);
  
  /**
   * @return the protocol version that is set as default for the manager
   */
  public String getVersion();
  
  /**
   * Returns the json protocol handler that is used for the specified dex version. If null is returned, then
   * this protocol version does not support json.
   * @param version the dex protocol version
   * @return the json protocol to use or null if this version does not support json
   */
  public JsonProtocol getJsonProtocolForVersion(String version);
}
