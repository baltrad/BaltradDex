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

package eu.baltrad.dex.log;

import org.apache.log4j.Level;

/**
 * Custom log level.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7
 * @since 1.7
 */
public class StickyLevel extends Level {
    
    public static final StickyLevel STICKY = 
                                            new StickyLevel(60000, "STICKY", 0);
    
    /**
     * Constructor.
     * @param level  
     * @param levelStr 
     * @param syslogEquivalent 
     */
    public StickyLevel(int level, String levelStr, int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }
    
    /**
     * Convert an integer passed as argument to a level.
     * @param val Integer 
     * @param defaultLevel Default level
     * @return Level
     */
    public static StickyLevel toLevel(int val, Level defaultLevel) {
        return STICKY;
    }

    /**
     * Convert string the passed as argument to a level.
     * @param sArg String
     * @param defaultLevel Default level
     * @return Level
     */
    public static StickyLevel toLevel(String sArg, Level defaultLevel) {
        return STICKY;  
    }
    
}
