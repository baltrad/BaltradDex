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

package eu.baltrad.dex.net.request.factory.impl;

import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.impl.ProtocolVersionRequestFactory;
//import eu.baltrad.dex.net.util.UrlValidatorUtil;
//import eu.baltrad.dex.net.util.json.IJsonUtil;
//import eu.baltrad.dex.net.util.json.impl.JsonUtil;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.datasource.model.DataSource;

//import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.entity.ByteArrayEntity;

//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.codec.digest.DigestUtils;

import java.net.URI;
//import java.util.Date;
import java.util.Set;
import java.util.List;

/**
 * Implements default request factory.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class DefaultRequestFactory implements RequestFactory {
  ProtocolVersionRequestFactory versionFactory = null;
  
  public DefaultRequestFactory(URI serverURI) {
    versionFactory = new ProtocolVersionRequestFactory(serverURI, ProtocolVersionRequestFactory.PROTOCOL_VERSION_21);
  }
  
  @Override
  public HttpUriRequest createDataSourceListingRequest(User user) {
    return versionFactory.createDataSourceListingRequest(user);
  }

  @Override
  public HttpUriRequest createStartSubscriptionRequest(User user,
      Set<DataSource> dataSources) {
    return versionFactory.createStartSubscriptionRequest(user, dataSources);
  }

  @Override
  public HttpUriRequest createUpdateSubscriptionRequest(User user,
      List<Subscription> subscriptions) {
    return versionFactory.createUpdateSubscriptionRequest(user, subscriptions);
  }

  @Override
  public HttpUriRequest createPostFileRequest(User user, byte[] fileContent) {
    return versionFactory.createPostFileRequest(user, fileContent);
  }

  @Override
  public HttpUriRequest createPostMessageRequest(User user, String message) {
    return versionFactory.createPostMessageRequest(user, message);
  }

  @Override
  public HttpUriRequest createPostKeyRequest(User user, byte[] keyContent) {
    return versionFactory.createPostKeyRequest(user, keyContent);
  }
}
