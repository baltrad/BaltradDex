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
    // Short node address, e.g. baltrad.imgw.pl (without port name and other stuff)
    private String shortAddress;
    // Full node address, e.g. http://baltrad.imgw.pl:8084/BaltradDex/dispatch.htm
    private String fullAddress;
    // Port number
    private String portNumber;
    private String factory;
    private String country;
    private String city;
    private String cityCode;
    private String street;
    private String number;
    private String phone;
    private String email;
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
     * Constructor creates user object with field values provided as parameters.
     *
     * @param id User ID
     * @param name User name
     * @param nameHash User name hash
     * @param roleName User role name
     * @param password Password
     * @param confirmPassword Password confirmed
     * @param shortAddress User node's short address
     * @param portNumber Port number
     * @param factory Name of the organization
     * @param country Country
     * @param city City
     * @param cityCode City code
     * @param street Street name
     * @param number Address number
     * @param phone Phone number
     * @param email Contact email
     */
    public User( int id, String name, String nameHash, String roleName, String password,
            String confirmPassword, String shortAddress, String portNumber, String factory,
            String country, String city, String cityCode, String street, String number,
            String phone, String email ) {
        this.id = id;
        this.name = name;
        this.nameHash = nameHash;
        this.roleName = roleName;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.shortAddress = shortAddress;
        this.portNumber = portNumber;
        this.factory = factory;
        this.country = country;
        this.city = city;
        this.cityCode = cityCode;
        this.street = street;
        this.number = number;
        this.phone = phone;
        this.email = email;
    }
    /**
     * Constructor creates user object with field values provided as parameters.
     *
     * @param id User ID
     * @param name User name
     * @param nameHash User name hash
     * @param roleName User role name
     * @param password Password
     * @param shortAddress User node's short address
     * @param portNumber Port number
     * @param factory Name of the organization
     * @param country Country
     * @param city City
     * @param cityCode City code
     * @param street Street name
     * @param number Address number
     * @param phone Phone number
     * @param email Contact email
     */
    public User( int id, String name, String nameHash, String roleName, String password, String shortAddress,
            String portNumber, String factory, String country, String city, String cityCode,
            String street, String number, String phone, String email ) {
        this.id = id;
        this.name = name;
        this.nameHash = nameHash;
        this.roleName = roleName;
        this.password = password;
        this.shortAddress = shortAddress;
        this.portNumber = portNumber;
        this.factory = factory;
        this.country = country;
        this.city = city;
        this.cityCode = cityCode;
        this.street = street;
        this.number = number;
        this.phone = phone;
        this.email = email;
    }
    /**
     * Method gets user id.
     *
     * @return User id
     */
    public int getId() { return id; }
    /**
     * Method sets user id.
     *
     * @param id User id
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Method gets role name.
     *
     * @return Role name
     */
    public String getRoleName() { return roleName; }
    /**
     * Method sets role name.
     *
     * @param roleName Role name
     */
    public void setRoleName( String roleName ) { this.roleName = roleName; }
    /**
     * Method gets user name.
     *
     * @return User name
     */
    public String getName() {
        return name;
    }
    /**
     * Method sets user name.
     *
     * @param name User name
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
   /**
     * Gets node's short address.
     *
     * @return Node's short address
     */
    public String getShortAddress() { return shortAddress; }
    /**
     * Sets node's short address.
     *
     * @param shortAddress Node's short address
     */
    public void setShortAddress( String shortAddress ) { this.shortAddress = shortAddress; }
    /**
     * Gets node's full address.
     *
     * @return Node's full address
     */
    public String getFullAddress() { return fullAddress; }
    /**
     * Sets node's full address.
     *
     * @param fullAddress Node's full address
     */
    public void setFullAddress( String fullAddress ) { this.fullAddress = fullAddress; }
    /**
     * Gets port number.
     *
     * @return Port number
     */
    public String getPortNumber() { return portNumber; }
    /**
     * Sets port number.
     *
     * @param portNumber Port number to set
     */
    public void setPortNumber( String portNumber ) { this.portNumber = portNumber; }
    /**
     * @return the factory
     */
    public String getFactory() {
        return factory;
    }

    /**
     * @param factory the factory to set
     */
    public void setFactory(String factory) {
        this.factory = factory;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the zipCode
     */
    public String getCityCode() {
        return cityCode;
    }

    /**
     * @param zipCode the zipCode to set
     */
    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    /**
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street the street to set
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the confirmPassword
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * @param confirmPassword the confirmPassword to set
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * @return the nameHash
     */
    public String getNameHash() {
        return nameHash;
    }

    /**
     * @param nameHash the nameHash to set
     */
    public void setNameHash(String nameHash) {
        this.nameHash = nameHash;
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
}
//--------------------------------------------------------------------------------------------------