/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model.register;

/**
 * Class implements data delivery register entry.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class DeliveryRegisterEntry {
//---------------------------------------------------------------------------------------- Variables
    private int id;
    private int userId;
    private long dataId;
//------------------------------------------------------------------------------------------ Methods    
    /**
     * Method returns register entry id.
     *
     * @return Register entry id
     */
    public int getId() { return id; }
    /**
     * Method sets register entry id.
     *
     * @param registerEntryID Register entry id
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Method returns user id.
     *
     * @return User id
     */
    public int getUserId() { return userId; }
    /**
     * Method sets user id.
     *
     * @param userId User id
     */
    public void setUserId( int userId ) { this.userId = userId; }
    /**
     * Method returns data id.
     *
     * @return Data id
     */
    public long getDataId() { return dataId; }
    /**
     * Method sets data id.
     *
     * @param dataId Data id
     */
    public void setDataId( long dataId ) { this.dataId = dataId; }
}
//--------------------------------------------------------------------------------------------------
