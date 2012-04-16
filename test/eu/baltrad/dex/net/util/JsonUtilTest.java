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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.HashSet;
import java.io.StringWriter;

/**
 * Json utility test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class JsonUtilTest {
    
    private JsonUtil classUnderTest;
    private ObjectMapper mapper;
    private Set<DataSource> toJson;
    private String jsonString;
    
    @Before
    public void setUp() {
        jsonString = "[{\"name\":\"DS1\",\"id\":1,\"description\":\"A test "
            + "data source\"},{\"name\":\"DS2\",\"id\":2,\"description\":\"One "
            + "more test data source\"},{\"name\":\"DS3\",\"id\":3,\"" 
            + "description\":\"Yet another test data source\"}]";
        
        classUnderTest = new JsonUtil();
        mapper = new ObjectMapper();
        toJson = new HashSet<DataSource>();
        DataSource ds1 = new DataSource(1, "DS1", "A test data source");
        toJson.add(ds1);
        DataSource ds2 = new DataSource(2, "DS2", "One more test data source");
        toJson.add(ds2);
        DataSource ds3 = new DataSource(3, "DS3", 
                                                "Yet another test data source");
        toJson.add(ds3);
        assertEquals(3, toJson.size());
    }
    
    @Test
    public void hashSetToJsonString() throws Exception {
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, toJson);
        assertNotNull(writer.toString());
        assertEquals(jsonString, writer.toString());
    }
    
    @Test
    public void jsonStringToHashSet() throws Exception {
        HashSet<DataSource> fromJson = mapper.readValue(jsonString, 
                                    new TypeReference<HashSet<DataSource>>(){});
        assertNotNull(fromJson);
        assertEquals(3, fromJson.size());
        for (DataSource ds : fromJson) {
            assertNotNull(ds);
            assertNotNull(ds.getId());
            assertNotNull(ds.getName());
            assertNotNull(ds.getDescription());
        }
        
    }
    
    @Test
    public void dataSourcesToJsonString() {
        String s = classUnderTest.dataSourcesToJsonString(toJson);
        assertNotNull(s);
        assertEquals(jsonString, s);
    }
    
    @Test
    public void jsonStringToDataSources() {
        HashSet<DataSource> dataSources = (HashSet<DataSource>)
                             classUnderTest.jsonStringToDataSources(jsonString);
        assertNotNull(dataSources);
        assertEquals(3, dataSources.size());
        for (DataSource ds : dataSources) {
            assertNotNull(ds);
            assertNotNull(ds.getId());
            assertNotNull(ds.getName());
            assertNotNull(ds.getDescription());
        }
        
    }
}
