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

import java.util.List;
import java.util.Set;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.model.User;

/**
 * Extracts information from a response
 * @author Anders Henja
 */
public interface ResponseParser {
  /**
   * @return the status code for the response
   * @throws ResponseParserException if status code not can be determined 
   */
  public int getStatusCode();
  
  /**
   * @return the name of the answering node
   * @throws ResponseParserException if node name not can be determined 
   */
  public String getNodeName();
  
  /**
   * @return the protocol version used by the answering node
   * @throws ResponseParserException if protocol version can be determined 
   */
  public String getProtocolVersion();
  
  /**
   * @return the protocol version that the answering node is configured to support
   * @throws ResponseParserException if protocol version can be determined 
   */
  public String getConfiguredProtocolVersion();
  
  /**
   * @return the reason phrase if any otherwise just the empty string
   */
  public String getReasonPhrase();
  
  /**
   * @return the data sources if any
   * @throws ResponseParserException if data sources not can be parsed 
   */
  public Set<DataSource> getDataSources();
  
  /**
   * @return the user account if any
   * @throws ResponseParserException if user account not can be parsed 
   */
  public User getUserAccount();
  
  /**
   * @return the subscriptions
   * @throws ResponseParserException if subscriptions not can be parsed
   */
  public List<Subscription> getSubscriptions();
  
}
