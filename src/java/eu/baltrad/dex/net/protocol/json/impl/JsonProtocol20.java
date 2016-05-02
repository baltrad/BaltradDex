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
package eu.baltrad.dex.net.protocol.json.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonFilter;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.protocol.json.JsonProtocol;
import eu.baltrad.dex.user.model.User;

/**
 * Json protocol 2.0
 * @author Anders Henja
 */
public class JsonProtocol20 implements JsonProtocol {
  /** JSON object mapper */
  private ObjectMapper mapper = new ObjectMapper();
  /** Data source filter */
  private FilterProvider dataSourcefilter = null;
  /** User filter */
  private FilterProvider userFilter = null;
  /** Subscription filter */
  private FilterProvider subscriptionFilter = null;
  
  // Need to have something to filter in
  @JsonFilter("filter properties by name")
  class PropertyFilterMixIn
  {
  }
  
  public JsonProtocol20() {
    mapper.getSerializationConfig().addMixInAnnotations(Object.class, PropertyFilterMixIn.class);
    String[] ignorableFieldNames = { "source", "fileObject" };
    dataSourcefilter = new SimpleFilterProvider().addFilter("filter properties by name",   
        SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames));
    userFilter = new SimpleFilterProvider().addFilter("filter properties by name",
        SimpleBeanPropertyFilter.serializeAllExcept(new String[]{"redirectedAddress"}));
    subscriptionFilter = new SimpleFilterProvider().addFilter("filter properties by name",
        SimpleBeanPropertyFilter.serializeAllExcept(new String[]{}));
  }
  
  @Override
  public String userAccountToJson(User user) {
    try {
      return mapper.writer().withFilters(userFilter).writeValueAsString(user);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create json string from user account", e);
    }
  }

  @Override
  public User jsonToUserAccount(String json) {
    try {
      return mapper.readValue(json, new TypeReference<User>(){});
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse json string into a user account", e);
    }
  }

  @Override
  public String dataSourcesToJson(Set<DataSource> dataSources) {
    try {
      return mapper.writer().withFilters(dataSourcefilter).writeValueAsString(dataSources);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create json string from data sources", e);
    }
  }

  @Override
  public Set<DataSource> jsonToDataSources(String json) {
    try {
      return mapper.readValue(json, new TypeReference<HashSet<DataSource>>() {});
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse json string into data sources", e);
    }
  }

  @Override
  public String subscriptionsToJson(List<Subscription> subscriptions) {
    try {
      return mapper.writer().withFilters(subscriptionFilter).writeValueAsString(subscriptions);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create json string from subscriptions", e);
    }
  }

  @Override
  public List<Subscription> jsonToSubscriptions(String json) {
    try {
      return mapper.readValue(json, new TypeReference<ArrayList<Subscription>>() {});
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse json string into subscriptions", e);
    }
  }
  public static void main(String[] args) {
    
  }
}
