/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model.user;

/**
 * Class implements role object.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class Role {
//---------------------------------------------------------------------------------------- Variables
    private int id;
    private String role;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public Role() {}
    /**
     * Creates new role object with given field values.
     *
     * @param id Role ID
     * @param role Role name
     */
    public Role( int id, String role ) {
        this.id = id;
        this.role = role;
    }
    /**
     * Gets role ID.
     *
     * @return Role ID
     */
    public int getId() { return id; }
    /**
     * Method sets role ID.
     *
     * @param id Role ID
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Gets role name.
     *
     * @return Role name
     */
    public String getRole() { return role; }
    /**
     * Sets role name.
     *
     * @param role Role name
     */
    public void setRole( String role ) { this.role = role; }
}
//--------------------------------------------------------------------------------------------------