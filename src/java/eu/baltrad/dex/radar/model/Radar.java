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

package eu.baltrad.dex.radar.model;

import java.io.Serializable;

/**
 * Implements radar object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.1.6
 */
public class Radar implements Serializable {

    private int id;
    private String countryCode;
    private String centerCode;
    private int centerNumber;
    private String radarPlace;
    private String radarCode;
    private String radarWmo;

    /**
     * Default constructor
     */
    public Radar() {}
    
    /**
     * Constructor.
     * @param id Id
     * @param countryCode Country code
     * @param centerCode Center code
     * @param centerNumber Center number
     * @param radarPlace Radar place name
     * @param radarCode Radar code
     * @param radarWmo  Radar WMO number
     */
    public Radar(int id, String countryCode, String centerCode, 
            int centerNumber, String radarPlace, String radarCode, 
            String radarWmo) {
        this.id = id;
        this.countryCode = countryCode;
        this.centerCode = centerCode;
        this.centerNumber = centerNumber;
        this.radarPlace = radarPlace;
        this.radarCode = radarCode;
        this.radarWmo = radarWmo;
    }

    /**
     * Constructor.
     * @param countryCode Country code
     * @param centerCode Center code
     * @param centerNumber Center number
     * @param radarPlace Radar place name
     * @param radarCode Radar code
     * @param radarWmo  Radar WMO number
     */
    public Radar(String countryCode, String centerCode, int centerNumber, 
            String radarPlace, String radarCode, String radarWmo) {
        this.countryCode = countryCode;
        this.centerCode = centerCode;
        this.centerNumber = centerNumber;
        this.radarPlace = radarPlace;
        this.radarCode = radarCode;
        this.radarWmo = radarWmo;
    }
    
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    /**
     * @return the centerCode
     */
    public String getCenterCode() {
        return centerCode;
    }

    /**
     * @param centerCode the centerCode to set
     */
    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

    /**
     * @return the centerNumber
     */
    public int getCenterNumber() {
        return centerNumber;
    }

    /**
     * @param centerNumber the centerNumber to set
     */
    public void setCenterNumber(int centerNumber) {
        this.centerNumber = centerNumber;
    }

    /**
     * @return the radarPlace
     */
    public String getRadarPlace() {
        return radarPlace;
    }

    /**
     * @param radarPlace the radarPlace to set
     */
    public void setRadarPlace(String radarPlace) {
        this.radarPlace = radarPlace;
    }

    /**
     * @return the radarCode
     */
    public String getRadarCode() {
        return radarCode;
    }

    /**
     * @param radarCode the radarCode to set
     */
    public void setRadarCode(String radarCode) {
        this.radarCode = radarCode;
    }

    /**
     * @return the radarWmo
     */
    public String getRadarWmo() {
        return radarWmo;
    }

    /**
     * @param radarWmo the radarWmo to set
     */
    public void setRadarWmo(String radarWmo) {
        this.radarWmo = radarWmo;
    }
    
}

