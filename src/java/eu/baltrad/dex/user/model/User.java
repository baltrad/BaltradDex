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

package eu.baltrad.dex.user.model;

import eu.baltrad.dex.util.MessageDigestUtil;

/**
 * Class implements user object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class User {
//---------------------------------------------------------------------------------------- Constants
    // Role key values
    public final static String ROLE_ADMIN = "admin";
    public final static String ROLE_OPERATOR = "operator";
    public final static String ROLE_PEER = "peer";
    public final static String ROLE_USER = "user";
//---------------------------------------------------------------------------------------- Variables
    private int id;
    private String name;
    private String nameHash;
    private String roleName;
    private String password;
    private String confirmPassword;
    private String organizationName;
    private String organizationUnit;
    private String localityName;
    private String stateName;
    private String countryCode;
    private String nodeAddress;
    private boolean checked;
//------------------------------------------------------------------------------------------ Methods
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
     * 
     * @param name User name
     * @param nodeAddress Node address
     * @param roleName Role
     */
    public User(String name, String roleName, String nodeAddress) {
        this.name = name;
        this.roleName = roleName;
        this.nodeAddress = nodeAddress;
    }
    /**
     * 
     * @param id
     * @param name
     * @param nameHash
     * @param roleName
     * @param password
     * @param confirmPassword
     * @param organizationName
     * @param organizationUnit
     * @param localityName
     * @param stateName
     * @param countryCode
     * @param nodeAddress 
     */
    public User(int id, String name, String nameHash, String roleName, String password,
            String confirmPassword, String organizationName, String organizationUnit, 
            String localityName, String stateName, String countryCode, String nodeAddress) {
        this.id = id;
        this.name = name;
        this.nameHash = nameHash;
        this.roleName = roleName;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.organizationName = organizationName;
        this.organizationUnit = organizationUnit;
        this.localityName = localityName;
        this.stateName = stateName;
        this.countryCode = countryCode;
        this.nodeAddress = nodeAddress;
    }
    /**
     * 
     * @param id
     * @param name
     * @param nameHash
     * @param roleName
     * @param password
     * @param organizationName
     * @param organizationUnit
     * @param localityName
     * @param stateName
     * @param countryCode
     * @param nodeAddress 
     */
    public User(int id, String name, String nameHash, String roleName, String password, 
            String organizationName, String organizationUnit, String localityName, String stateName, 
            String countryCode, String nodeAddress ) {
        this.id = id;
        this.name = name;
        this.nameHash = nameHash;
        this.roleName = roleName;
        this.password = password;
        this.organizationName = organizationName;
        this.organizationUnit = organizationUnit;
        this.localityName = localityName;
        this.stateName = stateName;
        this.countryCode = countryCode;
        this.nodeAddress = nodeAddress;
    }
    /**
     * 
     * 
     * @param name
     * @param nameHash
     * @param roleName
     * @param organizationName
     * @param organizationUnit
     * @param localityName
     * @param stateName
     * @param countryCode
     * @param nodeAddress 
     */
    public User(String name, String nameHash, String roleName, String organizationName, 
            String organizationUnit, String localityName, String stateName, String countryCode, 
            String nodeAddress ) {
        this.name = name;
        this.nameHash = nameHash;
        this.roleName = roleName;
        this.organizationName = organizationName;
        this.organizationUnit = organizationUnit;
        this.localityName = localityName;
        this.stateName = stateName;
        this.countryCode = countryCode;
        this.nodeAddress = nodeAddress;
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
     * Gets role name.
     *
     * @return Role name
     */
    public String getRoleName() { return roleName; }
    /**
     * Sets role name.
     *
     * @param roleName Role name
     */
    public void setRoleName( String roleName ) { this.roleName = roleName; }
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
     * Gets user name as hash.
     * 
     * @return nameHash User name as hash
     */
    public String getNameHash() { return nameHash; }
    /**
     * Sets user name as hash.
     * 
     * @param name User name
     */
    public void setNameHash( String name ) {
        this.nameHash = MessageDigestUtil.createHash( name );
    }
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
        this.password = MessageDigestUtil.createHash( password );
    }
    /**
     * Gets confirmed password.
     * 
     * @return confirmPassword Confirmed password
     */
    public String getConfirmPassword() { return confirmPassword; }
    /**
     * Sets confirmed password as hash.
     * 
     * @param confirmPassword Confirmed password to set
     */
    public void setConfirmPassword( String confirmPassword ) {
        this.confirmPassword = MessageDigestUtil.createHash( confirmPassword );
    }
    /**
     * @return the checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * @param checked the checked to set
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * @return the organizationName
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * @param organizationName the organizationName to set
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * @return the organizationUnit
     */
    public String getOrganizationUnit() {
        return organizationUnit;
    }

    /**
     * @param organizationUnit the organizationUnit to set
     */
    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    /**
     * @return the localityName
     */
    public String getLocalityName() {
        return localityName;
    }

    /**
     * @param localityName the localityName to set
     */
    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    /**
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
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
     * @return the nodeAddress
     */
    public String getNodeAddress() {
        return nodeAddress;
    }

    /**
     * @param nodeAddress the nodeAddress to set
     */
    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }
}
//--------------------------------------------------------------------------------------------------