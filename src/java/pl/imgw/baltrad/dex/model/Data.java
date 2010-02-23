/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

/**
 * Class implements radar data object.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class Data {
//------------------------------------------------------------------------------------------- Fields
    private int id;
    private String channelName;
    private String path;
    private String date;
    private String time;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method gets data id.
     *
     * @return Data id
     */
    public int getId() { return id; }
    /**
     * Method sets data id.
     *
     * @param id Data id
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Method gets channel name.
     *
     * @return Channel name
     */
    public String getChannelName() { return channelName; }
    /**
     * Method sets channel name.
     *
     * @param channelName Channel name
     */
    public void setChannelName( String channelName ) { this.channelName = channelName; }
    /**
     * Method gets file's path.
     *
     * @return Path to the file
     */
    public String getPath() { return path; }
    /**
     * Method sets file's path.
     *
     * @param path Path to the file
     */
    public void setPath( String path ) { this.path = path; }
    /**
     * Method gets date.
     *
     * @return Date
     */
    public String getDate() { return date; }
    /**
     * Method sets date.
     *
     * @param date Date
     */
    public void setDate( String date ) { this.date = date; }
    /**
     * Method gets time.
     *
     * @return Time
     */
    public String getTime() { return time; }
    /**
     * Method sets time.
     *
     * @param time Time
     */
    public void setTime( String time ) { this.time = time; }
}
//--------------------------------------------------------------------------------------------------
