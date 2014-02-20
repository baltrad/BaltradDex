/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
*******************************************************************************/

package eu.baltrad.dex.net.protocol;

import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;

import org.apache.http.client.methods.HttpUriRequest;

import java.util.Set;
import java.util.List;

/**
 * Request factory.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public interface RequestFactory {
  /**
   * Creates a request for data sources 
   * @param user the user information
   * @return the request
   */
  public HttpUriRequest createDataSourceListingRequest(User user);
  
  /**
   * Creates a request to start a subscription on data sources
   * @param user the user
   * @param dataSources the data sources that is wanted
   * @return the request
   */
  public HttpUriRequest createStartSubscriptionRequest(User user, Set<DataSource> dataSources);
  
  /**
   * Creates a request to update the subscriptions
   * @param user the user
   * @param subscriptions the subscriptions
   * @return the request
   */
  public HttpUriRequest createUpdateSubscriptionRequest(User user, List<Subscription> subscriptions);
    
  /**
   * Creates a file request (sends a file)
   * @param user the user
   * @param fileContent the content of the file
   * @return the request
   */
  public HttpUriRequest createPostFileRequest(User user, byte[] fileContent);
    
  /**
   * Creates a message request 
   * @param user the user
   * @param message the message to send
   * @return the request
   */
  public HttpUriRequest createPostMessageRequest(User user, String message);
    
  /**
   * Creates a key request (sends the key information) 
   * @param user the user
   * @param keyContent the content of the key
   * @return the request
   */
  public HttpUriRequest createPostKeyRequest(User user, byte[] keyContent);
}
