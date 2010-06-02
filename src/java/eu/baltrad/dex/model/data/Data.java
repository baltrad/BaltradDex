/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model.data;

/**
 * Class implements radar data object.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class Data {
//---------------------------------------------------------------------------------------- Variables
    private long id;
    private String channelName;
    private String path;
    private String date;
    private String time;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor
     */
    public Data(){}
    /**
     * Constructor sets field values.
     *
     * @param path Absolute data file path
     * @param id File index in the database
     * @param channelName Channel name
     * @param date Date
     * @param time Time
     */
    public Data( String path, long id, String channelName, String date, String time ) {
        this.path = path;
        this.id = id;
        this.channelName = channelName;
        this.date = date;
        this.time = time;
    }
    /**
     * Constructor setting file id and channel name.
     *
     * @param id File index in the database
     * @param channelName Channel name
     */
    public Data( long id, String channelName ) {
        this.id = id;
        this.channelName = channelName;
    }
    /**
     * Method gets data id.
     *
     * @return Data id
     */
    public long getId() { return id; }
    /**
     * Method sets data id.
     *
     * @param id Data id
     */
    public void setId( long id ) { this.id = id; }
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
