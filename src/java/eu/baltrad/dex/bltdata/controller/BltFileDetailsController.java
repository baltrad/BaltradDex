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

package eu.baltrad.dex.bltdata.controller;

import eu.baltrad.dex.bltdata.model.BltDataProcessor;
import eu.baltrad.dex.bltdata.model.BltFileManager;
import eu.baltrad.dex.bltdata.model.BltFile;
import eu.baltrad.dex.bltdata.model.BltDataset;
import eu.baltrad.dex.bltdata.model.BltDataProjector;
import eu.baltrad.dex.util.FileCatalogConnector;
import eu.baltrad.dex.log.model.LogManager;
import eu.baltrad.dex.util.InitAppUtil;

import eu.baltrad.fc.FileCatalog;

import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.Group;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Date;
import java.awt.geom.Point2D;

/**
 * Implements functionality allowing for accessing detailed information about radar data file.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class BltFileDetailsController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    private static final String FC_FILE_UUID = "uuid";
    private static final String FILE_DETAILS_KEY = "file_details";
    private static final String BLT_DATASETS_KEY = "blt_datasets";
    private static final String BLT_FILE_KEY = "blt_file";
    // PROJ4 projection code
    private static final String PROJ4_PROJ_CODE = "aeqd";
    // PROJ4 allipsoid code
    private static final String PROJ4_ELLPS_CODE = "WGS84";
    // Earth radius in meters
    private static final int EARTH_RADIUS = 6371000;
    // image thumb size
    private static final int THUMB_IMAGE_SIZE = 64;
    // range rings distance
    private static final short THUMB_RANGE_RINGS_DISTANCE = 0;
    // range mask line stroke
    private static final float THUMB_RANGE_MASK_STROKE = 1.0f;
    // range rings color string
    private static final String THUMB_RANGE_RINGS_COLOR = "#003098";
    // range mask color string
    private static final String THUMB_RANGE_MASK_COLOR = "#003098";
//---------------------------------------------------------------------------------------- Variables
    private BltDataProcessor bltDataProcessor;
    private BltDataProjector bltDataProjector;
    private BltFileManager bltFileManager;
    private BltDataProcessorController bltDataProcessorController;
    private static FileCatalog fc;
    private String successView;
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Fetches detailed information about a file and prepares dataset image thumbs.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView object
     * @throws ServletException 
     * @throws IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        String uuid = request.getParameter( FC_FILE_UUID );
        if( fc == null ) {
            fc = FileCatalogConnector.connect();
        }
        // get file from the database
        BltFile bltFile = bltFileManager.getDataByID( fc, uuid );
        // open file from storage dir
        String filePath = FileCatalogConnector.getDataStorageDirectory() + File.separator + uuid 
                + BltDataProcessor.H5_FILE_EXT;
        H5File h5File = bltDataProcessor.openH5File( filePath );
        // process the file
        Group root = bltDataProcessor.getH5Root( h5File );
        // get dataset full names
        bltDataProcessor.getH5Datasets( root );
        List<String> datasetFullNames = bltDataProcessor.getDatasetFullNames();

        // dataset objects holds file's metadata
        List<BltDataset> bltDatasets = new ArrayList<BltDataset>();

        // get radar location latitude
        bltDataProcessor.getH5Attribute( root, BltDataProcessor.H5_PATH_SEPARATOR +
                BltDataProcessor.H5_WHERE_GROUP_PREFIX, BltDataProcessor.H5_LAT_0_ATTR,
                BltDataProcessor.H5_DOUBLE_ATTR );
        double[] lat0_val = ( double[] )bltDataProcessor.getDoubleAttribute().getValue();
        // get radar location longitude
        bltDataProcessor.getH5Attribute( root, BltDataProcessor.H5_PATH_SEPARATOR +
                BltDataProcessor.H5_WHERE_GROUP_PREFIX, BltDataProcessor.H5_LON_0_ATTR,
                BltDataProcessor.H5_DOUBLE_ATTR );
        double[] lon0_val = ( double[] )bltDataProcessor.getDoubleAttribute().getValue();
        // initialize projection
        String[] projParms = new String[] { "+proj=" + PROJ4_PROJ_CODE, "+lat_0=" + lat0_val[ 0 ],
            "+lon_0=" + lon0_val[ 0 ], "+ellps=" + PROJ4_ELLPS_CODE, "+a=" + EARTH_RADIUS };
        int res = BltDataProjector.initializeProjection( projParms );
        if( res == 1 ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Failed to initialize " +
                    "projection" );
        }
        try {
            // iterate through datasets
            for( int i = 0; i < datasetFullNames.size(); i++ ) {
                 // find dataset specific WHERE group and data specific WHAT group
                String datasetFullName = datasetFullNames.get( i );
                String[] nameParts = datasetFullName.split( BltDataProcessor.H5_PATH_SEPARATOR );
                String whereGroup = "";
                String whatGroup = "";
                for( int j = 0; j < nameParts.length; j++ ) {
                    if( nameParts[ j ].startsWith( BltDataProcessor.H5_DATASET_PREFIX ) ) {
                        whereGroup = BltDataProcessor.H5_PATH_SEPARATOR + nameParts[ j ] +
                            BltDataProcessor.H5_PATH_SEPARATOR + BltDataProcessor.H5_WHERE_GROUP_PREFIX;
                        whatGroup = datasetFullName.substring( 0, datasetFullName.lastIndexOf(
                            BltDataProcessor.H5_PATH_SEPARATOR ) ) + BltDataProcessor.H5_PATH_SEPARATOR
                            + BltDataProcessor.H5_WHAT_GROUP_PREFIX;
                    }
                }
                // get data quantity
                bltDataProcessor.getH5Attribute( root, whatGroup, BltDataProcessor.H5_QUANTITY_ATTR,
                        BltDataProcessor.H5_STR_ATTR );
                String[] quantity_val = ( String[] )bltDataProcessor.getStringAttribute().getValue();
                // get elevation angles
                bltDataProcessor.getH5Attribute( root, whereGroup, BltDataProcessor.H5_ELANGLE_ATTR,
                        BltDataProcessor.H5_DOUBLE_ATTR );
                double[] elangle_val = ( double[] )bltDataProcessor.getDoubleAttribute().getValue();
                // get number of range bins
                bltDataProcessor.getH5Attribute( root, whereGroup, BltDataProcessor.H5_NBINS_ATTR,
                        BltDataProcessor.H5_LONG_ATTR );
                long[] nbins_val = ( long[] )bltDataProcessor.getLongAttribute().getValue();
                // retrieve data domain coordinates
                bltDataProcessor.getH5Attribute( root, whereGroup, BltDataProcessor.H5_RSCALE_ATTR,
                        BltDataProcessor.H5_DOUBLE_ATTR );
                double[] rscale_val = ( double[] )bltDataProcessor.getDoubleAttribute().getValue();
                // scale - in meters
                double range = rscale_val[ 0 ] * nbins_val[ 0 ];
                Point2D.Double llXY = new Point2D.Double( -range, -range );
                Point2D.Double llLatLon = BltDataProjector.pointXY2Geo( llXY );
                Point2D.Double urXY = new Point2D.Double( range, range );
                Point2D.Double urLatLon = BltDataProjector.pointXY2Geo( urXY );

                // create dataset objects
                BltDataset bltDataset = new BltDataset( datasetFullNames.get( i ),
                    whereGroup, quantity_val[ 0 ], nbins_val[ 0 ] * 2, nbins_val[ 0 ] * 2,
                    lat0_val[ 0 ], lon0_val[ 0 ], llLatLon.getY(), llLatLon.getX(), urLatLon.getY(),
                    urLatLon.getX(), elangle_val[ 0 ], InitAppUtil.getThumbsStorageFolder() +
                    File.separator + uuid + datasetFullNames.get( i ).replaceAll(
                        BltDataProcessor.H5_PATH_SEPARATOR, "_" ) + BltDataProcessor.IMAGE_FILE_EXT );

                // add dataset object to the list
                bltDatasets.add( bltDataset );

                // try to load thumb from disk before creating a new one
                String thumbPath = InitAppUtil.getThumbsStorageDirectory() + File.separator
                    + uuid + datasetFullNames.get( i ).replaceAll(
                        BltDataProcessor.H5_PATH_SEPARATOR, "_" )  + BltDataProcessor.IMAGE_FILE_EXT;

                // generate image thumbs
                File thumb = new File( thumbPath );
                if( !thumb.exists() ) {
                    bltDataProcessorController.createImage( h5File,
                    datasetFullNames.get( i ).toString(), whereGroup, THUMB_IMAGE_SIZE,
                    THUMB_RANGE_RINGS_DISTANCE, THUMB_RANGE_MASK_STROKE, THUMB_RANGE_RINGS_COLOR,
                    THUMB_RANGE_MASK_COLOR, thumbPath );
                }
            }
        } finally {
            // important: image generating method does not close the file
            bltDataProcessor.closeH5File( h5File );
        }
        // reset dataset names list
        bltDataProcessor.setDatasetFullNames( new ArrayList<String>() );

        // sort dataset object list
        Collections.sort( bltDatasets );
        // create hash map to store file details and datasets
        HashMap model = new HashMap();
        model.put( BLT_DATASETS_KEY, bltDatasets );
        model.put( BLT_FILE_KEY, bltFile );
        return new ModelAndView( getSuccessView(), FILE_DETAILS_KEY, model );
    }
    /**
     * Gets reference to BltDataProcessor object.
     *
     * @return Reference to BltDataProcessor object
     */
    public BltDataProcessor getBltDataProcessor() { return bltDataProcessor; }
    /**
     * Sets reference to BltDataProcessor object.
     *
     * @param bltDataProcessor BltDataProcessor object reference to set
     */
    public void setBltDataProcessor( BltDataProcessor bltDataProcessor ) {
        this.bltDataProcessor = bltDataProcessor;
    }
    /**
     * Gets reference to BltDataProjector object.
     *
     * @return Reference to BltDataProctoror object
     */
    public BltDataProjector getBltDataProjector() { return bltDataProjector; }
    /**
     * Sets reference to BltDataProjector object.
     *
     * @param bltDataProjector BltDataProjector object reference to set
     */
    public void setBltDataProjector( BltDataProjector bltDataProjector ) {
        this.bltDataProjector = bltDataProjector;
    }
    /**
     * Gets reference to file manager object.
     *
     * @return Reference to file manager object
     */
    public BltFileManager getBltFileManager() { return bltFileManager; }
    /**
     * Sets reference to file manager object.
     *
     * @param Reference to file manager object
     */
    public void setBltFileManager( BltFileManager bltFileManager ) {
        this.bltFileManager = bltFileManager;
    }
    /**
     * Gets reference to BltDataProcessorController object.
     *
     * @return Reference to BltDataProcessorController object
     */
    public BltDataProcessorController getBltDataProcessorController() {
        return bltDataProcessorController;
    }
    /**
     * Sets reference to DataProcessorController object.
     *
     * @param dataProcessorController Reference to DataProcessorController object
     */
    public void setBltDataProcessorController(
            BltDataProcessorController bltDataProcessorController ) {
        this.bltDataProcessorController = bltDataProcessorController;
    }
    /**
     * Gets reference to success view name string.
     *
     * @return Reference to success view name string
     */
    public String getSuccessView() { return successView; }
    /**
     * Sets reference to success view name string.
     *
     * @param Reference to success view name string
     */
    public void setSuccessView( String successView ) { this.successView = successView; }
     /**
     * Gets reference to LogManager class instance.
     *
     * @return Reference to LogManager class instance
     */
    public LogManager getLogManager() { return logManager; }
    /**
     * Sets reference to LogManager class instance.
     *
     * @param logManager Reference to LogManager class instance
     */
    public void setLogManager( LogManager logManager ) { this.logManager = logManager; }
}
//--------------------------------------------------------------------------------------------------