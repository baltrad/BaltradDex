/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.net.util;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.Subscription;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * Json utility test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class JsonUtilTest {
    
    private static final String JSON_SOURCES = 
              "[{\"name\":\"DS1\",\"id\":1,\"description\":\"A test "
            + "data source\"},{\"name\":\"DS2\",\"id\":2,\"description\":\"One "
            + "more test data source\"},{\"name\":\"DS3\",\"id\":3,\"" 
            + "description\":\"Yet another test data source\"}]";
        
    private static final String JSON_SUBSCRIPTIONS = 
              "[{\"id\":1,\"type\":\"download\",\"timeStamp\""
            + ":1340189763867,\"userName\":\"User1\",\"nodeAddress\"" 
            + ":\"http://test.baltrad.eu\",\"active\":true,\"dataSourceName\":"
            + "\"DS1\",\"synkronized\":true,\"operatorName\":\"Operator1\"},"
            + "{\"id\":2,\"type\":\"download\",\"timeStamp\":1340189763867,"
            + "\"userName\":\"User2\",\"nodeAddress\":\"http://baltrad.eu\","
            + "\"active\":true,\"dataSourceName\":\"DS2\",\"synkronized\":"
            + "false,\"operatorName\":\"Operator2\"},{\"id\":3,\"type\":"
            + "\"upload\",\"timeStamp\":1340189763867,\"userName\":\"User3\","
            + "\"nodeAddress\":\"http://baltrad.imgw.pl\",\"active\":false,"
            + "\"dataSourceName\":\"DS3\",\"synkronized\":true,\"operatorName\""
            + ":\"Operator3\"}]";
    
    private JsonUtil classUnderTest;
    private Set<DataSource> dataSources;
    private List<Subscription> subscriptions;
    
    @Before
    public void setUp() {
        
        classUnderTest = new JsonUtil();
        dataSources = new HashSet<DataSource>();
        DataSource ds1 = new DataSource(1, "DS1", "A test data source");
        dataSources.add(ds1);
        DataSource ds2 = new DataSource(2, "DS2", "One more test data source");
        dataSources.add(ds2);
        DataSource ds3 = new DataSource(3, "DS3", 
                                                "Yet another test data source");
        dataSources.add(ds3);
        assertEquals(3, dataSources.size());
        
        long time = 1340189763867L;
        subscriptions = new ArrayList<Subscription>();
        Subscription s1 = new Subscription(1, time, "User1", "DS1", "Operator1",
                "download", true, true, "http://test.baltrad.eu");
        subscriptions.add(s1);
        Subscription s2 = new Subscription(2, time, "User2", "DS2", "Operator2",
                "download", true, false, "http://baltrad.eu");
        subscriptions.add(s2);
        Subscription s3 = new Subscription(3, time, "User3", "DS3", "Operator3",
                "upload", false, true, "http://baltrad.imgw.pl");
        subscriptions.add(s3);
        assertEquals(3, subscriptions.size());
    }
    
    @Test
    public void dataSourcesToJsonString() {
        String s = classUnderTest.dataSourcesToJson(dataSources);
        assertNotNull(s);
        assertEquals(JSON_SOURCES, s);
    }
    
    @Test
    public void jsonStringToDataSources() throws Exception  {
        HashSet<DataSource> sources = (HashSet<DataSource>)
                             classUnderTest.jsonToDataSources(JSON_SOURCES);
        assertNotNull(sources);
        assertEquals(3, sources.size());
        for (DataSource ds : sources) {
            assertNotNull(ds);
            assertNotNull(ds.getId());
            assertNotNull(ds.getName());
            assertNotNull(ds.getDescription());
        }
    }
    
    @Test
    public void subscriptionsToJsonString() {
        String s = classUnderTest.subscriptionsToJson(subscriptions);
        assertNotNull(s);
        assertEquals(JSON_SUBSCRIPTIONS, s);
    }
    
    @Test
    public void jsonStringToSubscriptions() throws Exception  {
        ArrayList<Subscription> subs = (ArrayList<Subscription>)
                         classUnderTest.jsonToSubscriptions(JSON_SUBSCRIPTIONS);
        assertNotNull(subs);
        assertEquals(3, subs.size());
        for (Subscription s : subs) {
            assertNotNull(s);
            assertNotNull(s.getId());
            assertNotNull(s.getTimeStamp());
            assertNotNull(s.getUserName());
            assertNotNull(s.getDataSourceName());
            assertNotNull(s.getOperatorName());
            assertNotNull(s.getType());
            assertNotNull(s.getActive());
            assertNotNull(s.getSynkronized());
            assertNotNull(s.getNodeAddress());
        }
    }
    
}
