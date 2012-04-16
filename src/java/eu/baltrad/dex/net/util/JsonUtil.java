/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.baltrad.dex.net.util;

import eu.baltrad.dex.datasource.model.DataSource;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.Set;
import java.util.HashSet;

import java.io.StringWriter;
import java.io.IOException;

/**
 * Json utility.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class JsonUtil {
    
    /** JSON object mapper */
    private ObjectMapper mapper;
    
    /**
     * Constructor.
     */
    public JsonUtil() {
        mapper = new ObjectMapper();
    }
    
    /**
     * Converts hash set containing data source objects into JSON string.
     * @param dataSources Hash set containing data source objects
     * @return JSON string
     */
    public String dataSourcesToJsonString(Set<DataSource> dataSources) {
        StringWriter writer = new StringWriter();
        String jsonString = null;
        try {
            mapper.writeValue(writer, dataSources);
            jsonString = writer.toString();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert data sources to JSON"
                    + " string", e);
        }
        return jsonString;
    }
    
    /**
     * Converts JSON string to hash set containing data source objects.
     * @param jsonString JSON string
     * @return Hash set containing data source objects
     */
    public Set<DataSource> jsonStringToDataSources(String jsonString) {
        HashSet<DataSource> dataSources = null;
        try {
            dataSources = mapper.readValue(jsonString, 
                                    new TypeReference<HashSet<DataSource>>(){});
        } catch(IOException e) {
            throw new RuntimeException("Failed to convert JSON string to data"
                    + " sources", e);
        }
        return dataSources;
    }
    
}
