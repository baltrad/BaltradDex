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

package eu.baltrad.dex.datasource.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 * Class implements data source object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 2.0.0
 * @since 0.6.4
 */
public class DataSource implements Serializable, Comparable<DataSource> {

    /** Local data source */
    public static final String LOCAL = "local";
    /** Peer data source */
    public static final String PEER = "peer";
    
    /** Data source ID */
    @JsonIgnore
    private int id;
    /** Data source name */
    private String name;
    /** Data source type */
    private String type;
    /** Description */
    private String description;
    /** Data source code */
    private String source;
    /** File object code */
    private String fileObject;

    /**
     * Default constructor.
     */
    public DataSource() {}
    /**
     * Constructor.
     * @param name Data source name
     * @param type Data source type
     * @param description Description
     * @param source Data source code
     * @param fileObject File object code
     */
    public DataSource(String name, String type, String description, 
            String source, String fileObject) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.source = source;
        this.fileObject = fileObject;
    }
    
    /**
     * Constructor.
     * @param id Data source ID
     * @param name Data source name
     * @param type Data source type
     * @param description Description
     * @param source Data source code
     * @param fileObject File object code
     */
    public DataSource(int id, String name, String type, String description,
            String source, String fileObject) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.source = source;
        this.fileObject = fileObject;
    }
    
    /**
     * Compares data source with another object.
     *
     * @param o Object to compare with
     * @return True if objects are equal, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        boolean res = false;
        if( getClass() == o.getClass() ) {
            if( this.getName().equals( ( ( DataSource )o ).getName() ) ) {
                res = true;
            }
        }
        return res;
    }
    /**
     * Creates data source name hash code.
     *
     * @return Data source name code or 0 if product identifier is null
     */
    @Override
    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }
    /**
     * Gets data source ID.
     *
     * @return Data source ID
     */
    @JsonIgnore
    public int getId() { return id; }
    /**
     * Sets data source ID
     *
     * @param id Data source ID
     */
    @JsonIgnore
    public void setId( int id ) { this.id = id; }
    /**
     * Gets data source name.
     *
     * @return Data source name
     */
    public String getName() { return name; }
    /**
     * Sets data source name.
     *
     * @param name Data source name to set
     */
    public void setName(String name) { this.name = name; }
    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the fileObject
     */
    public String getFileObject() {
        return fileObject;
    }

    /**
     * @param fileObject the fileObject to set
     */
    public void setFileObject(String fileObject) {
        this.fileObject = fileObject;
    }
    
    /**
     * Gets data source description.
     *
     * @return Data source description
     */
    public String getDescription() { return description; }
    /**
     * Sets data source description.
     *
     * @param description Data source description to set
     */
    public void setDescription(String description) { 
        this.description = description; 
    }
    /**
     * Implements comparable interface. Allows to sort data source objects based 
     * on data source name.
     *
     * @param ds DataSource
     * @return 0 if objects are equal
     */
    public int compareTo( DataSource ds ) { 
        return getName().compareTo( ds.getName() ); 
    }
    
    public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("DataSource[").append(name).append("] = {id=").append(id).append(", type=").append(type).append(", source=").append(source)
            .append(", fileObject=").append(fileObject).append(", description=").append(description).append("}");
      return buffer.toString();
    }
    
}

