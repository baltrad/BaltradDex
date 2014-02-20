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

import javax.servlet.http.HttpServletResponse;

import org.keyczar.exceptions.KeyczarException;

import eu.baltrad.beast.message.IBltXmlMessage;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.model.User;

/**
 * @author Anders Henja
 */
public interface RequestParser {
  /**
   * @return Message to be signed
   */
  public String getMessage();
  
  /**
   * @return Signature string
   */
  public String getSignature();
  
  /**
   * @return User identity string
   */
  public String getUser();
  
  /**
   * @return Name of the requesting node
   */
  public String getNodeName();
  
  /**
   * @return the protocol version. If nothing found, empty string should be returned. null should never be returned.
   */
  public String getProtocolVersion();

  /**
   * @return a response writer that generates a response of same version as the request
   */
  public ResponseWriter getWriter(HttpServletResponse response);
  
  /**
   * @param authenticator the authenticator
   * @return if the message is authenticated properly or not
   */
  public boolean isAuthenticated(Authenticator authenticator) throws KeyczarException;
  
  /**
   * @return the user account
   * @throws RequestParserException upon error
   */
  public User getUserAccount();
  
  /**
   * @return the data sources
   * @throws RequestParserException upon error
   */
  public Set<DataSource> getDataSources();
  
  /**
   * @return the subscriptions
   * @throws RequestParserException upon error
   */
  public List<Subscription> getSubscriptions();
  
  /**
   * @return the baltrad xml message
   * @throws RequestParserException upon error
   */
  public IBltXmlMessage getBltXmlMessage();
}
