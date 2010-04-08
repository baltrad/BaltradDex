/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model;

import eu.baltrad.fc.FileCatalog;
import eu.baltrad.fc.expr.ExpressionFactory;
import eu.baltrad.fc.Query;
import eu.baltrad.fc.ResultSet;

import java.util.List;
import java.util.ArrayList;

/**
 * Data manager class implementing data handling functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class DataManager {
//---------------------------------------------------------------------------------------- Constants
    private static final String FC_ID_ATTR = "file_id";
    private static final String FC_PATH_ATTR = "path";
    private static final String FC_SRC_PLC_ATTR = "src_PLC";
    private static final String FC_DATE_ATTR = "what/date";
    private static final String FC_TIME_ATTR = "what/time";
    private static final String FC_DATE_STR = "yyyy/MM/dd";
    private static final String FC_TIME_STR = "HH:mm:ss";
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method queries file catalog for all files coming from a given data channel.
     *
     * @param channelName Data channel name
     * @return List containing data from a given data channel
     */
    public List getDataFromChannel( FileCatalog fileCatalog, String channelName ) {
        // Query the file catalog
        ExpressionFactory xpr = new ExpressionFactory();
        Query q = fileCatalog.query();
        // path
        q.fetch( xpr.attribute( FC_PATH_ATTR ) );
        // file id
        q.fetch( xpr.attribute( FC_ID_ATTR ) );
        // date
        q.fetch( xpr.attribute( FC_DATE_ATTR ) );
        // time
        q.fetch( xpr.attribute( FC_TIME_ATTR ) );
        // filter the query with a given channel name
        q.filter( xpr.attribute( FC_SRC_PLC_ATTR ).eq( xpr.string( channelName ) ) );
        ResultSet r = q.execute();
        List dataList = new ArrayList();
        while( r.next() ) {
            Data data = new Data( r.string( 0 ), r.integer( 1 ), channelName,
                    r.date( 2 ).to_string( FC_DATE_STR ), r.time( 3 ).to_string( FC_TIME_STR ) );
            dataList.add( data );
        }
        // delete the result set
        r.delete();
        // return the result set
        return dataList;
    }
}
//--------------------------------------------------------------------------------------------------
