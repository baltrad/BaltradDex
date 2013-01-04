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

package eu.baltrad.dex.user.model;

/**
 * Class implements user object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class User implements Comparable<User> {

    private int id;
    private String name;
    private String password;
    private String orgName;
    private String orgUnit;
    private String locality;
    private String state;
    private String countryCode;
    
    /**
     * Default constructor.
     */
    public User() {}
    
    /**
     * Constructor supporting login mechanism.
     * 
     * @param name User name
     * @param password User password
     */
    public User( String name, String password ) {
        this.name = name;
        this.password = password;
    }
    
    /**
     * Constructor.
     * @param id
     * @param name
     * @param password
     * @param orgName
     * @param orgUnit
     * @param locality
     * @param state
     * @param countryCode
     */
    public User(int id, String name, String password, String orgName, 
            String orgUnit, String locality, String state, String countryCode) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.orgName = orgName;
        this.orgUnit = orgUnit;
        this.locality = locality;
        this.state = state;
        this.countryCode = countryCode;
    }
    
    /**
     * Compare objects based on user name.
     * @param user User object to compare with
     * @return 0 if objects are equal
     */
    public int compareTo(User user) { 
        return getName().compareTo(user.getName()); 
    }
    
    /**
     * Compares this object with another.
     * @param obj Object to compare with
     * @return True is tested parameters are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        User user = (User) obj; 
        return this.getName() != null && 
               this.getName().equals(user.getName()) && 
               this.getPassword() != null &&
               this.getPassword().equals(user.getPassword()) &&
               this.getOrgName() != null && 
               this.getOrgName().equals(user.getOrgName()) &&
               this.getOrgUnit() != null && 
               this.getOrgUnit().equals(user.getOrgUnit()) &&
               this.getLocality() != null &&
               this.getLocality().equals(user.getLocality()) &&
               this.getState() != null &&
               this.getState().equals(user.getState()) &&
               this.getCountryCode() != null &&
               this.getCountryCode().equals(user.getCountryCode());
    }
    
    /**
     * Generate hash code.
     * @return Hash code
     */
    @Override
    public int hashCode() {
        int prime = 7;
        int result = 1;
        result = prime * result + ((this.getName() == null) ? 
                0 : this.getName().hashCode());
        result = prime * result + ((this.getPassword() == null) ? 
                0 : this.getPassword().hashCode());
        result = prime * result + ((this.getOrgName() == null) ? 
                0 : this.getOrgName().hashCode());
        result = prime * result + ((this.getOrgUnit() == null) ? 
                0 : this.getOrgUnit().hashCode());
        result = prime * result + ((this.getLocality() == null) ? 
                0 : this.getLocality().hashCode());
        result = prime * result + ((this.getState() == null) ? 
                0 : this.getState().hashCode());
        result = prime * result + ((this.getCountryCode() == null) ? 
                0 : this.getCountryCode().hashCode());
        return result;
    }
    
    
    /**
     * Gets user id.
     *
     * @return User id
     */
    public int getId() { return id; }
    
    /**
     * Sets user id.
     *
     * @param id User id
     */
    public void setId( int id ) { this.id = id; }
    
    /**
     * Method gets user name.
     *
     * @return User name
     */
    public String getName() { return name; }
    
    /**
     * Method sets user name.
     *
     * @param name User name
     */
    public void setName( String name ) { this.name = name; }
    
    /**
     * Gets password.
     * 
     * @return Password
     */
    public String getPassword() { return password; }
    
    /**
     * Sets password as hash.
     * 
     * @param password Password to set
     */
    public void setPassword( String password ) {
        this.password = password;
    }

    /**
     * @return the orgName
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * @param orgName the orgName to set
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * @return the orgUnit
     */
    public String getOrgUnit() {
        return orgUnit;
    }

    /**
     * @param orgUnit the orgUnit to set
     */
    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }

    /**
     * @return the locality
     */
    public String getLocality() {
        return locality;
    }

    /**
     * @param locality the locality to set
     */
    public void setLocality(String locality) {
        this.locality = locality;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
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
    
}
