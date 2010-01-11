/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

/**
 * Class implements data channel object.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class DataChannel {

    private int dataChannelID;
    private String name;
    private int wmoNumber;
    private String checked;

    /**
     * @return the dataChannelID
     */
    public int getDataChannelID() {
        return dataChannelID;
    }

    /**
     * @param dataChannelID the dataChannelID to set
     */
    public void setDataChannelID( int dataChannelID ) {
        this.dataChannelID = dataChannelID;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @return the wmoNumber
     */
    public int getWmoNumber() {
        return wmoNumber;
    }

    /**
     * @param wmoNumber the wmoNumber to set
     */
    public void setWmoNumber( int wmoNumber ) {
        this.wmoNumber = wmoNumber;
    }

    /**
     * @return the checked
     */
    public String getChecked() {
        return checked;
    }

    /**
     * @param checked the checked to set
     */
    public void setChecked(String checked) {
        this.checked = checked;
    }

}
