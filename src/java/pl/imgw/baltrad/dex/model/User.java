/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

/**
 * Class implements user object.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class User {

//---------------------------------------------------------------------------------------- Constants
    public final static String ROLE_0 = "operator";
    public final static String ROLE_1 = "administrator";
    public final static String ROLE_2 = "user";
//---------------------------------------------------------------------------------------- Variables
    private int id;
    private String name;
    private String password;
    private String role;
    private String factory;
    private String country;
    private String city;
    private String zipCode;
    private String street;
    private String number;
    private String phone;
    private String email;
    private String nodeAddress;
    private String localDirectory;

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
     * Constructor creating new user object with given field values.
     *
     * @param name User name
     * @param password User password
     * @param role User role in the system
     * @param factory Name of user's organization
     * @param nodeAddress Node address
     * @param localDirectory User local directory
     */
    public User( String name, String password, String role, String factory, String country,
            String city, String zipCode, String street, String number, String phone, String email,
            String nodeAddress, String localDirectory ) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.factory = factory;
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
        this.street = street;
        this.number = number;
        this.phone = phone;
        this.email = email;
        this.nodeAddress = nodeAddress;
        this.localDirectory = localDirectory;
    }

    /**
     * Method gets user id.
     *
     * @return User id
     */
    public int getId() {
        return id;
    }

    /**
     * Method sets user id.
     *
     * @param id User id
     */
    public void setId( int id ) {
        this.id = id;
    }

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
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

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
    public String getZipCode() {
        return zipCode;
    }

    /**
     * @param zipCode the zipCode to set
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

    /**
     * @return the localDirectory
     */
    public String getLocalDirectory() {
        return localDirectory;
    }

    /**
     * @param localDirectory the localDirectory to set
     */
    public void setLocalDirectory(String localDirectory) {
        this.localDirectory = localDirectory;
    }   
}
//--------------------------------------------------------------------------------------------------