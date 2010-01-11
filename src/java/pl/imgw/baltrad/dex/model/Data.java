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
    private String nodeName;
    private String stationName;
    private String absolutePath;
    private String fileName;
    private String date;
    private String time;
    private int numberOfDatasets;
    
//------------------------------------------------------------------------------------------ Methods

    /**
     * Method gets data id.
     *
     * @return Data id
     */
    public int getId() {
        return id;
    }

    /**
     * Method sets data id.
     *
     * @param id Data id
     */
    public void setId( int id ) {
        this.id = id;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName the nodeName to set
     */
    public void setNodeName( String nodeName ) {
        this.nodeName = nodeName;
    }

    /**
     * @return the stationName
     */
    public String getStationName() {
        return stationName;
    }

    /**
     * @param stationName the stationName to set
     */
    public void setStationName( String stationName ) {
        this.stationName = stationName;
    }

    /**
     * @return the absolutePath
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * @param absolutePath the absolutePath to set
     */
    public void setAbsolutePath( String absolutePath ) {
        this.absolutePath = absolutePath;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate( String date ) {
        this.date = date;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime( String time ) {
        this.time = time;
    }

    /**
     * @return the numberOfDatasets
     */
    public int getNumberOfDatasets() {
        return numberOfDatasets;
    }

    /**
     * @param numberOfDatasets the numberOfDatasets to set
     */
    public void setNumberOfDatasets( int numberOfDatasets ) {
        this.numberOfDatasets = numberOfDatasets;
    }

}

//--------------------------------------------------------------------------------------------------
