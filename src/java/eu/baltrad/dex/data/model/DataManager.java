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
import eu.baltrad.fc.db.FileQuery;
import eu.baltrad.fc.db.FileResult;
import eu.baltrad.fc.db.FileEntry;

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
    private static final String FC_SRC_PLC_ATTR = "what/source:PLC";
    private static final String FC_FILE_UUID = "file:uuid";
    private static final String FC_DATE_STR = "yyyy/MM/dd";
    private static final String FC_TIME_STR = "HH:mm:ss";
    private static final String FC_DATE_TIME_STR = "yyyy/MM/dd, HH:mm:ss";
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method queries file catalog for all files coming from a given radar station.
     *
     * @param fc File catalog instance
     * @param radarName Radar station name
     * @return List containing data from a given radar station
     */
    public List< Data > getDataByRadar( FileCatalog fc, String radarName ) {
        ExpressionFactory xpr = new ExpressionFactory();
        FileQuery q = fc.query_file();
        // filter the query with a given channel name
        q.filter( xpr.attribute( FC_SRC_PLC_ATTR ).eq( xpr.string( radarName ) ) );
        FileResult r = q.execute();
        List< Data > dataList = new ArrayList<Data>();
        while( r.next() ) {
            FileEntry fileEntry = r.entry();
            Data data = new Data(
                fileEntry.uuid(),
                fc.storage().store( fc.database().entry_by_uuid( fileEntry.uuid() ) ),
                fileEntry.stored_at().to_string( FC_DATE_TIME_STR ), radarName,
                fileEntry.what_date().to_string( FC_DATE_STR ),
                fileEntry.what_time().to_string( FC_TIME_STR ),
                fileEntry.what_object() );
            dataList.add( data );
        }
        // delete the result set
        r.delete();
        return dataList;
    }
    /**
     * Method queries file catalog for a file entry with a given identity string.
     *
     * @param fc File catalog instance
     * @param uuid File entry's identity string
     * @return File entry with a given ID
     */
    public Data getDataByID( FileCatalog fc, String uuid ) {
        ExpressionFactory xpr = new ExpressionFactory();
        FileQuery q = fc.query_file();
        // filter the query with a given file identity string
        q.filter( xpr.attribute( FC_FILE_UUID ).eq( xpr.string( uuid ) ) );
        FileResult r = q.execute();
        Data data = null;
        while( r.next() ) {
            FileEntry fileEntry = r.entry();
            data = new Data(
                fileEntry.uuid(),
                fc.storage().store( fc.database().entry_by_uuid( fileEntry.uuid() ) ),
                fileEntry.stored_at().to_string( FC_DATE_TIME_STR ),
                fileEntry.what_source(),
                fileEntry.what_date().to_string( FC_DATE_STR ),
                fileEntry.what_time().to_string( FC_TIME_STR ),
                fileEntry.what_object() );
        }
        // delete the result set
        r.delete();
        return data;
    }
}
//--------------------------------------------------------------------------------------------------
