/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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
***************************************************************************************************/

package eu.baltrad.dex.datasource.model;

import java.io.Serializable;

/**
 * Class implements file object according to ODIM radar data information model.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class FileObject implements Serializable {
//---------------------------------------------------------------------------------------- Variables
    /** File object ID */
    private int id;
    /** File object identifier */
    private String fileObject;
    /** File object description */
    private String description;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public FileObject() {}
    /**
     * Constructor.
     *
     * @param id File object ID
     * @param fileObject File object identifier
     * @param description Description
     */
    public FileObject( int id, String fileObject, String description ) {
        this.id = id;
        this.fileObject = fileObject;
        this.description = description;
    }
    /**
     * Compares file object with another object.
     *
     * @param o Object to compare with
     * @return True if objects are equal, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        boolean res = false;
        if( getClass() == o.getClass() ) {
            if( this.getFileObject().equals( ( ( FileObject )o ).getFileObject() ) ) {
                res = true;
            }
        }
        return res;
    }
    /**
     * Creates file object identifier hash code.
     *
     * @return File object identifier hash code or 0 if identifier is null
     */
    @Override
    public int hashCode() {
        return( fileObject != null ? fileObject.hashCode() : 0 );
    }
    /**
     * Gets file object ID.
     *
     * @return File object ID
     */
    public int getId() { return id; }
    /**
     * Sets file object ID
     *
     * @param id File object ID
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Gets file object identifier.
     *
     * @return File object identifier
     */
    public String getFileObject() { return fileObject; }
    /**
     * Sets file object identifier.
     *
     * @param fileObject File object identifier to set
     */
    public void setFileObject( String fileObject ) { this.fileObject = fileObject; }
    /**
     * Gets file object description.
     *
     * @return File object description
     */
    public String getDescription() { return description; }
    /**
     * Sets file object description.
     *
     * @param description File object description to set
     */
    public void setDescription( String description ) { this.description = description; }
}
//--------------------------------------------------------------------------------------------------
