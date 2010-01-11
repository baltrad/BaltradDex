/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

/**
 * Class implements data delivery register entry.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class DeliveryRegisterEntry {

    private int registerEntryID;
    private int userID;
    private int dataID;

    /**
     * Method returns register entry ID.
     *
     * @return Register entry ID
     */
    public int getRegisterEntryID() {
        return registerEntryID;
    }

    /**
     * Method sets register entry ID.
     *
     * @param registerEntryID Register entry ID
     */
    public void setRegisterEntryID( int registerEntryID ) {
        this.registerEntryID = registerEntryID;
    }

    /**
     * Method returns user ID.
     *
     * @return User ID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Method sets user ID.
     *
     * @param userID User ID
     */
    public void setUserID( int userID ) {
        this.userID = userID;
    }

    /**
     * Method returns data ID.
     *
     * @return Data ID
     */
    public int getDataID() {
        return dataID;
    }

    /**
     * Method sets data ID.
     *
     * @param dataID Data ID
     */
    public void setDataID( int dataID ) {
        this.dataID = dataID;
    }

}
