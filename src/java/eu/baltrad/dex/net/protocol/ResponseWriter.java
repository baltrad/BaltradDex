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

import java.io.IOException;
import java.util.List;
import java.util.Set;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.model.User;

/**
 * Used for writing a response on a request.
 * @author Anders Henja
 */
public interface ResponseWriter {
  /**
   * Sends a user account response
   * @param localNodeName the local node name
   * @param user the user
   * @param status the http status
   * @throws IOException upon error
   */
  public void userAccountResponse(String localNodeName, User user, int status) throws IOException;
  
  /**
   * Sends a data source response
   * @param localNodeName the local node name
   * @param dataSources the data sources
   * @param status the status
   * @throws IOException upon error
   */
  public void dataSourcesResponse(String localNodeName, List<DataSource> dataSources, int status) throws IOException;

  /**
   * Sends a data source response
   * @param localNodeName the local node name
   * @param dataSources the data sources
   * @param status the status
   * @throws IOException upon error
   */
  public void dataSourcesResponse(String localNodeName, Set<DataSource> dataSources, int status) throws IOException;
  
  /**
   * Sends a subscription response
   * @param localNodeName the local node name
   * @param subscriptions the subscriptions
   * @param status the status
   * @throws IOException upon error
   */
  public void subscriptionResponse(String localNodeName, List<Subscription> subscriptions, int status) throws IOException;
  
  
  /**
   * Sends a response with a reason 
   * @param message the reason
   * @param status the error message
   */
  public void messageResponse(String message, int status);
  
  /**
   * Sends a status as a response
   * @param status the status
   */
  public void statusResponse(int status);
}
