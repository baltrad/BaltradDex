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

import eu.baltrad.dex.net.util.json.impl.JsonUtil;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.model.User;
import java.util.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Json utility test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 1.1.0
 */
public class JsonUtilTest {
    
    private static final String JSON_ACCOUNT = 
            "{\"name\":\"test\",\"id\":1,\"state\":\"state\",\"nodeAddress\"" +
            ":\"http://localhost:8084\",\"orgName\":\"org\",\"orgUnit\":" +
            "\"unit\",\"locality\":\"locality\",\"countryCode\":\"XX\"," +
            "\"role\":\"user\",\"password\":\"s3cret\"}";
    
    private static final String JSON_SOURCES = 
            "[{\"name\":\"DS1\",\"type\":\"local\",\"id\":1,\"description\"" +
            ":\"A test data source\"},{\"name\":\"DS2\",\"type\":\"local\"," +
            "\"id\":2,\"description\":\"One more test data source\"},{\"" + 
            "name\":\"DS3\",\"type\":\"local\",\"id\":3,\"description\":\"" + 
            "Yet another test data source\"}]";
        
    private static final String JSON_SUBSCRIPTIONS = 
            "[{\"id\":1,\"type\":\"local\",\"date\":1340189763867,\"active\":" + 
            "true,\"dataSource\":\"DataSource1\",\"user\":\"User1\"," +
            "\"syncronized\":true},{\"id\":2,\"type\":\"local\",\"date\":" +
            "1340189763867,\"active\":true,\"dataSource\":\"DataSource2\"," +
            "\"user\":\"User2\",\"syncronized\":false},{\"id\":3,\"type\":" +
            "\"peer\",\"date\":1340189763867,\"active\":false,\"dataSource\":" +
            "\"DataSource3\",\"user\":\"User3\",\"syncronized\":true}]"; 
            
    private JsonUtil classUnderTest;
    private User user;
    private Set<DataSource> dataSources;
    private List<Subscription> subscriptions;
    
    @Before
    public void setUp() {
        classUnderTest = new JsonUtil();
        
        user = new User(1, "test", "user", "s3cret", "org", "unit", "locality", 
                "state", "XX", "http://localhost:8084");
        
        dataSources = new HashSet<DataSource>();
        DataSource ds1 = new DataSource(1, "DS1", "local", 
                "A test data source");
        dataSources.add(ds1);
        DataSource ds2 = new DataSource(2, "DS2", "local", 
                "One more test data source");
        dataSources.add(ds2);
        DataSource ds3 = new DataSource(3, "DS3", "local",
                "Yet another test data source");
        dataSources.add(ds3);
        assertEquals(3, dataSources.size());
        
        long time = 1340189763867L;
        subscriptions = new ArrayList<Subscription>();
        
        Subscription s1 = new Subscription(1, time, "local", "User1", 
                "DataSource1", true, true);
        subscriptions.add(s1);
        Subscription s2 = new Subscription(2, time, "local", "User2", 
                "DataSource2", true, false);
        subscriptions.add(s2);
        Subscription s3 = new Subscription(3, time, "peer", "User3", 
                "DataSource3", false, true);
        subscriptions.add(s3);
        
        assertEquals(3, subscriptions.size());
    }
    
    @Test
    public void userAccountToJson() {
        String s = classUnderTest.userAccountToJson(user);
        assertNotNull(s);
        assertEquals(JSON_ACCOUNT.length(), s.length());
    }
    
    @Test
    public void jsonToUserAccount() {
        User _user = classUnderTest.jsonToUserAccount(JSON_ACCOUNT);
        assertNotNull(_user);
        assertEquals(user.getId(), _user.getId());
        assertEquals(user.getName(), _user.getName());
        assertEquals(user.getPassword(), _user.getPassword());
        assertEquals(user.getOrgName(), _user.getOrgName());
        assertEquals(user.getOrgUnit(), _user.getOrgUnit());
        assertEquals(user.getLocality(), _user.getLocality());
        assertEquals(user.getState(), _user.getState());
        assertEquals(user.getCountryCode(), _user.getCountryCode());
        assertEquals(user.getRole(), _user.getRole());
        assertEquals(user.getNodeAddress(), _user.getNodeAddress());
    }
    
    @Test
    public void dataSourcesToJson() {
        String s = classUnderTest.dataSourcesToJson(dataSources);
        assertEquals(JSON_SOURCES.length(), s.length());
    }
    
    @Test
    public void jsonToDataSources() throws Exception  {
        HashSet<DataSource> sources = (HashSet<DataSource>)
                             classUnderTest.jsonToDataSources(JSON_SOURCES);
        assertNotNull(sources);
        assertEquals(3, sources.size());
        for (DataSource ds : sources) {
            assertNotNull(ds);
            assertNotNull(ds.getId());
            assertNotNull(ds.getName());
            assertNotNull(ds.getType());
            assertNotNull(ds.getDescription());
        }
    }
    
    @Test
    public void subscriptionsToJson() {
        String s = classUnderTest.subscriptionsToJson(subscriptions);
        assertNotNull(s);
        assertEquals(JSON_SUBSCRIPTIONS.length(), s.length());
    }
    
    @Test
    public void jsonToSubscriptions() throws Exception  {
        ArrayList<Subscription> subs = (ArrayList<Subscription>)
                         classUnderTest.jsonToSubscriptions(JSON_SUBSCRIPTIONS);
        assertNotNull(subs);
        assertEquals(subscriptions.size(), subs.size());
        
        for (int i = 0; i < subs.size(); i++) {
            assertEquals(subscriptions.get(i).getId(), subs.get(i).getId());
            assertEquals(subscriptions.get(i).getDate(), 
                    subs.get(i).getDate());
            assertEquals(subscriptions.get(i).getType(), 
                    subs.get(i).getType());
            assertEquals(subscriptions.get(i).getUser(), subs.get(i).getUser());
            assertEquals(subscriptions.get(i).getDataSource(), 
                    subs.get(i).getDataSource());
            assertEquals(subscriptions.get(i).isActive(), 
                    subs.get(i).isActive());
            assertEquals(subscriptions.get(i).isSyncronized(), 
                    subs.get(i).isSyncronized());
        }
    }
    
}
