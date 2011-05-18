/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
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

/**
 * Class implements functionality allowing to store dataset specific metadata.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.5.1
 * @since 0.5.1
 */
public class BltDataset implements Comparable<BltDataset> {
//---------------------------------------------------------------------------------------- Variables
    // HDF5 DATASET name
    private String name;
    // HDF5 WHERE group name
    private String where;
    // data quantity
    private String quantity;
    // dataset width
    private long width;
    // dataset height
    private long height;
    // radar locatioon latitude
    private double lat0;
    // radar location longitude
    private double lon0;
    // radar image lower left corner latitude
    private double llLat;
    // radar image lower left corner longitude
    private double llLon;
    // radar image upper right corner latitude
    private double urLat;
    // radar image upper right corner longitude
    private double urLon;
    // antenna elevation angle
    private double elevationAngle;
    // image thumb path
    private String thumbPath;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     * 
     * @param name HDF5 dataset name
     * @param where HDF5 where group name
     * @param quantity Data quantity
     * @param width Dataset width
     * @param height Dataset height
     * @param lat0 Radar location latitude
     * @param lon0 Radar location longitude
     * @param llLat Radar image lower left corner latitude
     * @param llLon Radar image lower left corner longitude
     * @param urLat Radar image upper right corner latitude
     * @param urLon Radar image upper right corner longitude
     * @param elevationAngle Antenna elevation angle
     * @param thumbPath Thumb image path
     */
    public BltDataset( String name, String where, String quantity, long width, long height,
            double lat0, double lon0, double llLat, double llLon, double urLat, double urLon,
            double elevationAngle, String thumbPath ) {
        this.name = name;
        this.where = where;
        this.quantity = quantity;
        this.width = width;
        this.height = height;
        this.lat0 = lat0;
        this.lon0 = lon0;
        this.llLat = llLat;
        this.llLon = llLon;
        this.urLat = urLat;
        this.urLon = urLon;
        this.elevationAngle = elevationAngle;
        this.thumbPath = thumbPath;
    }
    /**
     * Implements comparable interface. Allows to sort datasets based on elevation angle.
     *
     * @param bltDataset Dataset to compare with the current dataset
     * @return 0 if objects are equal, 1 if current angle is greater than compared angle,
     * -1 otherwise
     */
    public int compareTo( BltDataset bltDataset ) {
        Double currentAngle = this.getElevationAngle();
        Double comparedAngle = bltDataset.getElevationAngle();
        return currentAngle.compareTo( comparedAngle );
    }
    /**
     * Gets HDF5 dataset name.
     *
     * @return HDF5 dataset name
     */
    public String getName() { return name; }
    /**
     * Sets HDF5 dataset name.
     *
     * @param name HDF5 dataset name to set
     */
    public void setName( String name ) { this.name = name; }
    /**
     * Gets HDF5 where group name.
     *
     * @return HDF5 where group name
     */
    public String getWhere() { return where; }
    /**
     * Sets HDF5 where group name.
     *
     * @param where HDF5 where group name to set
     */
    public void setWhere( String where ) { this.where = where; }
    /**
     * Gets data quantity.
     *
     * @return Data quantity
     */
    public String getQuantity() { return quantity; }
    /**
     * Sets data quantity.
     *
     * @param quantity Data quantuty to set
     */
    public void setQuantity( String quantity ) { this.quantity = quantity; }
    /**
     * Gets dataset width.
     *
     * @return Dataset width
     */
    public long getWidth() { return width; }
    /**
     * Sets dataset width.
     *
     * @param width Dataset width to set
     */
    public void setWidth( long width ) { this.width = width; }
    /**
     * Gets dataset height.
     *
     * @return Dataset height
     */
    public long getHeight() { return height; }
    /**
     * Sets dataset height.
     *
     * @param height Dataset height to set
     */
    public void setHeight( long height ) { this.height = height; }
    /**
     * Gets radar location latitude.
     *
     * @return Radar location latitude
     */
    public double getLat0() { return lat0; }
    /**
     * Sets radar location latitude.
     *
     * @param lat0 Radar location latitude to set
     */
    public void setLat0( double lat0 ) { this.lat0 = lat0; }
    /**
     * Gets radar location longitude.
     *
     * @return Radar location longitude
     */
    public double getLon0() { return lon0; }
    /**
     * Sets radar location longitude.
     *
     * @param lon0 Radar location longitude to set
     */
    public void setLon0( double lon0 ) { this.lon0 = lon0; }
    /**
     * Gets radar image lower left corner latitude.
     *
     * @return Radar image lower left corner latitude
     */
    public double getLlLat() { return llLat; }
    /**
     * Sets radar image lower left corner latitude.
     *
     * @param llLat Radar image lower left corner latitude to set
     */
    public void setLlLat( double llLat ) { this.llLat = llLat; }
    /**
     * Gets radar image lower left corner longitude.
     *
     * @return Radar image lower left corner longitude
     */
    public double getLlLon() { return llLon; }
    /**
     * Sets radar image lower left corner longitude.
     *
     * @param llLon Radar image lower left corner longitude to set
     */
    public void setLlLon( double llLon ) { this.llLon = llLon; }
    /**
     * Gets radar image upper right corner latitude.
     *
     * @return Radar image upper right corner latitude
     */
    public double getUrLat() { return urLat; }
    /**
     * Sets radar image upper right corner latitude.
     *
     * @param urLat Radar image upper right corner latitude to set
     */
    public void setUrLat(double urLat) { this.urLat = urLat; }
    /**
     * Gets radar image upper right corner longitude.
     *
     * @return Radar image upper right corner longitude
     */
    public double getUrLon() { return urLon; }
    /**
     * Sets radar image upper right corner longitude.
     *
     * @param urLon Radar image upper right corner longitude to set
     */
    public void setUrLon( double urLon ) { this.urLon = urLon; }
    /**
     * Gets antenna elevation angle.
     *
     * @return Antenna elevation angle
     */
    public double getElevationAngle() { return elevationAngle; }
    /**
     * Sets antenna elevation angle.
     *
     * @param elevationAngle Antenna elevation angle to set
     */
    public void setElevationAngle( double elevationAngle ) {
        this.elevationAngle = elevationAngle;
    }
    /**
     * Gets thumb image path.
     *
     * @return Thumb image path
     */
    public String getThumbPath() { return thumbPath; }
    /**
     * Sets thumb image path
     *
     * @param thumbPath Thumb image path to set
     */
    public void setThumbPath( String thumbPath ) { this.thumbPath = thumbPath; }
}
//--------------------------------------------------------------------------------------------------