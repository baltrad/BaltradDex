/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
***************************************************************************************************/

package eu.baltrad.dex.data.model;

import eu.baltrad.fc.FileCatalog;

import eu.baltrad.fc.expr.ExpressionFactory;
import eu.baltrad.fc.Query;
import eu.baltrad.fc.ResultSet;

import java.util.List;
import java.util.ArrayList;

/**
 * Data manager class implementing data handling functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class DataManager {
//---------------------------------------------------------------------------------------- Constants
    private static final String FC_ID_ATTR = "file:id";
    private static final String FC_PATH_ATTR = "file:path";
    private static final String FC_SRC_PLC_ATTR = "what/source:PLC";
    private static final String FC_DATE_ATTR = "what/date";
    private static final String FC_TIME_ATTR = "what/time";
    private static final String FC_DATE_STR = "yyyy/MM/dd";
    private static final String FC_TIME_STR = "HH:mm:ss";
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method queries file catalog for all files coming from a given data channel.
     *
     * @param fileCatalog File catalog instance
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
            Data data = new Data( r.string( 0 ), r.int64_( 1 ), channelName,
                    r.date( 2 ).to_string( FC_DATE_STR ), r.time( 3 ).to_string( FC_TIME_STR ) );
            dataList.add( data );
        }
        // delete the result set
        r.delete();
        // return the result set
        return dataList;
    }
    /**
     * Method fetches data record with a given ID.
     *
     * @param fileCatalog File catalog instance
     * @param dataId Data record ID
     * @return Data record with a given ID
     */
    public Data getDataById( FileCatalog fileCatalog, long dataId ) {
        // Query the file catalog
        Data data = null;
        ExpressionFactory xpr = new ExpressionFactory();
        Query q = fileCatalog.query();
        // file id
        q.fetch( xpr.attribute( FC_ID_ATTR ) );
        // channel name
        q.fetch( xpr.attribute( FC_SRC_PLC_ATTR ) );
        // filter the query with a given data id
        q.filter( xpr.attribute( FC_ID_ATTR ).eq( xpr.int64_( dataId ) ) );
        ResultSet r = q.execute();
        while( r.next() ) {
            data = new Data( r.int64_( 0 ), r.string( 1 ) );
        }
        // delete the result set
        r.delete();
        // return the result record
        return data;
    }
}
//--------------------------------------------------------------------------------------------------
