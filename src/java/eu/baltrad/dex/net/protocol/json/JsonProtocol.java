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
package eu.baltrad.dex.net.protocol.json;

import java.util.List;
import java.util.Set;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.model.User;

/**
 * Protocol handler for the json language
 * @author Anders Henja
 */
public interface JsonProtocol {
  /**
   * Creates a json string from a user object
   * @param user the user object
   * @return the json string
   */
  public String userAccountToJson(User user);
  
  /**
   * Parses a json string into a user object
   * @param json the json string
   * @return the user object
   */
  public User jsonToUserAccount(String json);
  
  /**
   * Creates a json string from a set of data sources
   * @param dataSources the data sources
   * @return a json string representation of this set
   */
  public String dataSourcesToJson(Set<DataSource> dataSources);
   
  /**
   * Parses a json string into a set of data sources
   * @param json the json string
   * @return a set of data sources
   * @throws RuntimeException
   */
  public Set<DataSource> jsonToDataSources(String json);
   
  /**
   * Creates a json string from a list of subscriptions
   * @param subscriptions the subscriptions
   * @return the json string
   */
  public String subscriptionsToJson(List<Subscription> subscriptions);
   
  /**
   * Parses a json string into a list of subscriptions
   * @param json
   * @return the list of subscriptions
   * @throws RuntimeException
   */
  public List<Subscription> jsonToSubscriptions(String json);
}
