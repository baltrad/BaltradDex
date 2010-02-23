/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
    private String date;
    private String time;
    private String type;
    private String message;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor
     */
    public LogEntry() {}
    /**
     * Constructor.
     *
     * @param date Log entry date
     * @param time Log entry time
     * @param type Log entry type
     * @param message Log entry message
     */
    public LogEntry( String date, String time, String type, String message ) {
        this.date = date;
        this.time = time;
        this.type = type;
        this.message = message;
    }
    /**
     * Constructor.
     *
     * @param date Date object
     * @param type Log entry type
     * @param message Log entry message
     */
    public LogEntry( Date date, String type, String message ) {
        DateFormat dfDate = new SimpleDateFormat( "yyyy/MM/dd" );
        DateFormat dfTime = new SimpleDateFormat( "HH:mm:ss" );
        this.date = dfDate.format( date );
        this.time = dfTime.format( date );
        this.type = type;
        this.message = message;
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
     * Method gets the type of log entry.
     *
     * @return Log entry type
     */
    public String getType() { return type; }
    /**
     * Method sets the type of log entry.
     *
     * @param type Log entry type
     */
    public void setType( String type ) { this.type = type; }
    /**
     * Method gets log entry message.
     *
     * @return Log entry message
     */
    public String getMessage() { return message; }
    /**
     * Method sets log entry message.
     *
     * @param message Log entry message
     */
    public void setMessage( String message ) { this.message = message; }
}
//--------------------------------------------------------------------------------------------------
