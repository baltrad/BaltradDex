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

package eu.baltrad.dex.net.model.impl;

/**
 * Implements data upload object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.6.1
 * @since 1.6.1
 */
public class Upload extends Transfer {
    
    private long filesSent;
    private long failures;

    /**
     * Constructor.
     * @param dataSource Data source name
     * @param timeStamp Timestamp 
     * @param active Upload status
     */
    public Upload(String dataSource, long timeStamp, boolean active) {
        super(dataSource, timeStamp, active);
    }
    
    /**
     * @return the filesSent
     */
    public long getFilesSent() {
        return filesSent;
    }

    /**
     * @param filesSent the filesSent to set
     */
    public void setFilesSent(long filesSent) {
        this.filesSent = filesSent;
    }

    /**
     * @return the failures
     */
    public long getFailures() {
        return failures;
    }

    /**
     * @param failures the failures to set
     */
    public void setFailures(long failures) {
        this.failures = failures;
    }
    
}
