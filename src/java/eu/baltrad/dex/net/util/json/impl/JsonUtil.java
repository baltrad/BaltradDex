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

package eu.baltrad.dex.net.util.json.impl;

import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import java.io.StringWriter;
import java.io.IOException;

/**
 * Json utility.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class JsonUtil implements IJsonUtil {
    
    /** JSON object mapper */
    private ObjectMapper mapper;
    
    /**
     * Constructor.
     */
    public JsonUtil() {
        mapper = new ObjectMapper();
    }
    
    /**
     * Converts user account to JSON string.
     * @param account User account 
     * @return JSON string
     * @throws RuntimeException 
     */
    public String userAccountToJson(User user) throws RuntimeException {
        StringWriter writer = new StringWriter();
        String json = null;
        try {
            mapper.writeValue(writer, user);
            json = writer.toString();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to convert user account to JSON", e);
        }
        return json;
    }
    
    /**
     * Converts JSON string to user account object.
     * @param json JSON string
     * @return User account object
     * @throws RuntimeException 
     */
    public User jsonToUserAccount(String json) throws RuntimeException {
        User user = null;
        try {
            user = mapper.readValue(json, new TypeReference<User>(){});
        } catch(IOException e) {
            throw new RuntimeException(
                    "Failed to convert JSON to user account", e);
        }
        return user;
    }
    
    /**
     * Converts hash set containing data source objects into JSON string.
     * @param dataSources Hash set containing data source objects
     * @return JSON string
     * @throws RuntimeException
     */
    public String dataSourcesToJson(Set<DataSource> dataSources) 
            throws RuntimeException {
        StringWriter writer = new StringWriter();
        String json = null;
        try {
            mapper.writeValue(writer, dataSources);
            json = writer.toString();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to convert data sources to JSON" + e);
        }
        return json;
    }
    
    /**
     * Converts JSON string to hash set containing data source objects.
     * @param jsonString JSON string
     * @return Hash set containing data source objects
     * @throws RuntimeException
     */
    public Set<DataSource> jsonToDataSources(String json) 
            throws RuntimeException {
        HashSet<DataSource> dataSources = null;
        try {
            dataSources = mapper.readValue(json, 
                                    new TypeReference<HashSet<DataSource>>(){});
        } catch(IOException e) {
            throw new RuntimeException(
                    "Failed to convert JSON to data sources", e);
        }
        return dataSources;
    }
    
    /**
     * Converts list containing subscription objects into JSON string.
     * @param subscriptions List containing subscription objects
     * @return JSON string
     */
    public String subscriptionsToJson(List<Subscription> subscriptions) 
            throws RuntimeException {
        StringWriter writer = new StringWriter();
        String json = null;
        try {
            mapper.writeValue(writer, subscriptions);
            json = writer.toString();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to convert subscriptions to JSON", e);
        }
        return json;
    }
    
    /**
     * Converts JSON string to array list containing subscription objects.
     * @param jsonString JSON string
     * @return Array list containing subscription objects
     * @throws RuntimeException
     */
    public List<Subscription> jsonToSubscriptions(String jsonString) 
            throws RuntimeException {
        ArrayList<Subscription> subscriptions = null;
        try {
            subscriptions = mapper.readValue(jsonString, 
                                new TypeReference<ArrayList<Subscription>>(){});
        } catch(IOException e) {
            throw new RuntimeException(
                    "Failed to convert JSON to subscriptions", e);
        }
        return subscriptions;
    }
    
}
