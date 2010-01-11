/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

/**
 * Class implements log message object.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class LogEntry {

//------------------------------------------------------------------------------------------- Fields
    private int id;
    private String rank;
    private String date;
    private String time;
    private String text;

//------------------------------------------------------------------------------------------ Methods

    /**
     * Default constructor.
     */
    public LogEntry() {}

    /**
     * Constructor.
     * 
     * @param rank Log entry rank
     * @param date Log entry date
     * @param time Log entry time
     * @param text Log entry text
     */
    public LogEntry( String rank, String date, String time, String text ) {
        this.rank = rank;
        this.date = date;
        this.time = time;
        this.text = text;
    }

    /**
     * Method gets log entry ID.
     *
     * @return Log entry ID
     */
    public int getId() { return id; }

    /**
     * Method sets log entry ID.
     *
     * @param id Log entry ID
     */
    public void setId( int id ) { this.id = id; }

    /**
     * Method gets log entry rank.
     *
     * @return Log entry rank
     */
    public String getRank() { return rank; }

    /**
     * Method sets log entry rank.
     *
     * @param rank Log entry rank
     */
    public void setRank( String rank ) { this.rank = rank; }

    /**
     * Method gets log entry date.
     *
     * @return Log entry date
     */
    public String getDate() { return date; }

    /**
     * Method sets log entry date.
     *
     * @param date Log entry date
     */
    public void setDate( String date ) { this.date = date; }

    /**
     * Method gets log entry time.
     *
     * @return Log entry time
     */
    public String getTime() { return time; }

    /**
     * Method sets log entry time.
     *
     * @param time Log entry time
     */
    public void setTime( String time ) { this.time = time; }

    /**
     * Method gets log entry text.
     *
     * @return Log entry text
     */
    public String getText() { return text; }

    /**
     * Method sets log entry text.
     *
     * @param text Log entry text
     */
    public void setText( String text ) { this.text = text; }
    
}
//--------------------------------------------------------------------------------------------------
