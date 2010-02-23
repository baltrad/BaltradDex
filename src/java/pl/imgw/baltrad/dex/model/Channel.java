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
public class Channel {
//---------------------------------------------------------------------------------------- Variables
    private int id;
    private String name;
    private int wmoNumber;
    private boolean selected;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method gets data channel id.
     *
     * @return Data channel id
     */
    public int getId() { return id; }
    /**
     * Method sets data channel id.
     *
     * @param id Data channel id
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Method gets data channel name.
     *
     * @return Data channel name
     */
    public String getName() { return name; }
    /**
     * Method sets data channel name.
     *
     * @param name Data channel name
     */
    public void setName( String name ) { this.name = name; }
    /**
     * Method gets data channel's WMO number.
     *
     * @return Data channel's WMO number
     */
    public int getWmoNumber() { return wmoNumber; }
    /**
     * Method sets data channel's WMO number.
     *
     * @param wmoNumber Data channel's WMO number
     */
    public void setWmoNumber( int wmoNumber ) { this.wmoNumber = wmoNumber; }
    /**
     * Method gets channel selection toggle state.
     *
     * @return Channel selection toggle state
     */
    public boolean getSelected() { return selected; }
    /**
     * Method sets channel selection toggle state.
     *
     * @param selected Channel selection toggle state
     */
    public void setSelected( boolean selected ) { this.selected = selected; }
}
//--------------------------------------------------------------------------------------------------
