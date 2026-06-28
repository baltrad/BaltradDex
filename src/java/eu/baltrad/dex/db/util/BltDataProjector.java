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

package eu.baltrad.dex.db.util;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import org.apache.log4j.Logger;

import java.awt.geom.Point2D;

/**
 * Encapsulates data projection functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.5
 * @since 0.1.5
 */
public class BltDataProjector {

    private static CoordinateReferenceSystem crs;
    private static CoordinateTransform transformToGeo;
    private static CoordinateTransform transformToXY;
    private static CRSFactory crsFactory = new CRSFactory();
    private static CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    private static CoordinateReferenceSystem wgs84 = crsFactory.createFromName("EPSG:4326");
    private static Logger log;

    /**
     * Constructor.
     */
    public BltDataProjector() {
        this.log = Logger.getLogger("DEX");
    }
    /**
     * Initializes projection object according to PROJ4 library specification.
     *
     * @param projParms Projection parameters as array of strings
     * @return 0 upon successfull initialization, 1 otherwise
     */
    public static int initializeProjection(String[] projParms) {
        int res;
        try {
            // Convert array of parameters to single PROJ4 string
            StringBuilder proj4String = new StringBuilder();
            for (String param : projParms) {
                if (proj4String.length() > 0) {
                    proj4String.append(" ");
                }
                proj4String.append(param);
            }
            
            crs = crsFactory.createFromParameters(null, proj4String.toString());
            transformToGeo = ctFactory.createTransform(crs, wgs84);
            transformToXY = ctFactory.createTransform(wgs84, crs);
            res = 0;
        } catch (Exception e) {
            log.error("Failed to initialize projection", e);
            res = 1;
        }
        return res;
    }
    /**
     * Converts carthesian coordinates into latitude and longitude.
     * @param xyPoint Carthesian point coordinates
     * @return Latitude and longitude of a given point
     */
    public static Point2D.Double pointXY2Geo(Point2D.Double xyPoint) {
        ProjCoordinate src = new ProjCoordinate(xyPoint.x, xyPoint.y);
        ProjCoordinate dest = new ProjCoordinate();
        transformToGeo.transform(src, dest);
        return new Point2D.Double(dest.x, dest.y);
    }
    /**
     * Converts latitude and longitude into carthesian coordinates.
     * @param geoPoint Latitude and longitude of a given point
     * @return Carthesian coordinates of a given point
     */
    public static Point2D.Double pointGeo2XY(Point2D.Double geoPoint) {
        ProjCoordinate src = new ProjCoordinate(geoPoint.x, geoPoint.y);
        ProjCoordinate dest = new ProjCoordinate();
        transformToXY.transform(src, dest);
        return new Point2D.Double(dest.x, dest.y);
    }
}

