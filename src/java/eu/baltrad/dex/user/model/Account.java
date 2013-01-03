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
 * User account object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class Account extends User {
    
    private String repeatPassword;
    private String roleName;
    private String nodeAddress;
    
    /**
     * Default constructor.
     */
    public Account() {}
    
    /**
     * Constructor.
     * @param id Record id
     * @param name User name
     * @param password Password
     * @param orgName Organization name
     * @param orgUnit Organization unit
     * @param locality Locality
     * @param state State
     * @param countryCode Country code 
     * @param roleName User's role name
     * @param nodeAddress Node address
     */
    public Account(int id, String name, String password, String orgName, 
                   String orgUnit, String locality, String state, 
                   String countryCode, String roleName, String nodeAddress) {
        setId(id);
        setName(name);
        setPassword(password);
        setOrgName(orgName);
        setOrgUnit(orgUnit);
        setLocality(locality);
        setState(state);
        setCountryCode(countryCode);
        setRoleName(roleName);
        setNodeAddress(nodeAddress);
    }

    /**
     * Constructor.
     * @param name User name
     * @param nodeAddress Node address
     * @param orgName Organization name
     * @param orgUnit Organization unit
     * @param locality Locality
     * @param state State
     * @param countryCode Country code 
     */
    public Account(String name, String nodeAddress, String orgName, 
                   String orgUnit, String locality, String state, 
                   String countryCode) {
        setName(name);
        setNodeAddress(nodeAddress);
        setOrgName(orgName);
        setOrgUnit(orgUnit);
        setLocality(locality);
        setState(state);
        setCountryCode(countryCode);
    }
    
    /**
     * @return the repeatPassword
     */
    public String getRepeatPassword() {
        return repeatPassword;
    }

    /**
     * @param repeatPassword the repeatPassword to set
     */
    public final void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    /**
     * @return the roleName
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName the roleName to set
     */
    public final void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * @return the nodeAddress
     */
    public String getNodeAddress() {
        return nodeAddress;
    }

    /**
     * @param nodeAddress the nodeAddress to set
     */
    public final void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }
    
}
