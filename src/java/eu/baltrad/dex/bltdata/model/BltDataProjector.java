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

package eu.baltrad.dex.bltdata.model;

import eu.baltrad.dex.log.model.*;

import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionFactory;
import com.jhlabs.map.proj.ProjectionException;

import java.awt.geom.Point2D;

/**
 * Encapsulates data projection functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.5
 * @since 0.1.5
 */
public class BltDataProjector {
//---------------------------------------------------------------------------------------- Variables
    private static Projection proj;
    private static LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Initializes projection object according to PROJ4 library specification.
     *
     * @param projParms Projection parameters as array of strings
     * @return 0 upon successfull initialization, 1 otherwise
     */
    public static int initializeProjection( String[] projParms ) {
        int res;
        try {
            proj = ProjectionFactory.fromPROJ4Specification( projParms );
            proj.initialize();
            res = 0;
        } catch( ProjectionException e ) {
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR,
                    "Failed to initialize projection: " + e.getMessage() ) );
            res = 1;
        }
        return res;
    }
    /**
     * Converts carthesian coordinates into latitude and longitude.
     *
     * @param xyPoint Carthesian point coordinates
     * @return Latitude and longitude of a given point
     */
    public static Point2D.Double pointXY2Geo( Point2D.Double xyPoint ) {
        Point2D.Double geoPoint = new Point2D.Double();
        proj.inverseTransform( xyPoint, geoPoint );
        return geoPoint;
    }
    /**
     * Converts latitude and longitude into carthesian coordinates.
     *
     * @param geoPoint Latitude and longitude of a given point
     * @return Carthesian coordinates of a given point
     */
    public static Point2D.Double pointGeo2XY( Point2D.Double geoPoint ) {
        Point2D.Double xyPoint = new Point2D.Double();
        proj.transform( geoPoint, xyPoint );
        return xyPoint;
    }
    /**
     * Gets reference to LogManager class instance.
     *
     * @return Reference to LogManager class instance
     */
    public LogManager getLogManager() { return logManager; }
    /**
     * Sets reference to LogManager class instance.
     *
     * @param logManager Reference to LogManager class instance
     */
    public void setLogManager( LogManager logManager ) { this.logManager = logManager; }
}
//--------------------------------------------------------------------------------------------------
