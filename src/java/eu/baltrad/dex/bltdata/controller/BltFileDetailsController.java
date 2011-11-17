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
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.util.InitAppUtil;

import eu.baltrad.fc.FileCatalog;

import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.Group;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.awt.geom.Point2D;

/**
 * Implements functionality allowing for accessing detailed information about radar data file.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
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
    private FileCatalog fileCatalog;
    private String successView;
    private Logger log;
    private InitAppUtil init;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public BltFileDetailsController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
        init = new InitAppUtil();
    }
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
        // get file from the database
        BltFile bltFile = bltFileManager.getFileEntry( uuid );
        // Determine data type
        String fileObject = bltFile.getType();
        // open file from storage dir
        String filePath = fileCatalog.local_path_for_uuid(uuid);
        H5File h5File = bltDataProcessor.openH5File( filePath );
        HashMap model = new HashMap();
        // Process the file depending on file object / data type
        // SCANs and PVOLs
        if( fileObject.equals( BltDataProcessor.ODIMH5_SCAN_OBJ ) || fileObject.equals(
                BltDataProcessor.ODIMH5_PVOL_OBJ ) ) {
            model = processPolarData( bltFile, h5File, uuid );
        }
        // IMAGEs
        if( fileObject.equals( BltDataProcessor.ODIMH5_IMAGE_OBJ ) ) {
            model = processCartesianData( bltFile, h5File, uuid );
        }

        // ... other file objects to be implemented here ...

        return new ModelAndView( getSuccessView(), FILE_DETAILS_KEY, model );
    }
    /**
     * Wrapper method processing polar data - SCANs and PVOLs
     * 
     * @param bltFile File entry from the File Catalog
     * @param h5File HDF5 file object
     * @param uuid Unique file entry ID
     * @return HashMap holding file entry and extracted datasets
     */
    public HashMap processPolarData( BltFile bltFile, H5File h5File, String uuid ) {
        // process the file
        Group root = bltDataProcessor.getH5Root( h5File );
        // get dataset full names
        bltDataProcessor.getH5Datasets( root );
        List<String> datasetFullNames = bltDataProcessor.getDatasetFullNames();
        // dataset objects holds file's metadata
        List<BltDataset> bltDatasets = new ArrayList<BltDataset>();

        // get radar location latitude
        bltDataProcessor.getH5Attribute( root, BltDataProcessor.H5_PATH_SEPARATOR +
                BltDataProcessor.H5_WHERE_GROUP_PREFIX, BltDataProcessor.H5_LAT_0_ATTR );
        double lat0_val = ( Double )bltDataProcessor.getAttributeValue();

        // get radar location longitude
        bltDataProcessor.getH5Attribute( root, BltDataProcessor.H5_PATH_SEPARATOR +
                BltDataProcessor.H5_WHERE_GROUP_PREFIX, BltDataProcessor.H5_LON_0_ATTR );
        double lon0_val = ( Double )bltDataProcessor.getAttributeValue();

        // initialize projection
        String[] projParms = new String[] { "+proj=" + PROJ4_PROJ_CODE, "+lat_0=" + lat0_val,
            "+lon_0=" + lon0_val, "+ellps=" + PROJ4_ELLPS_CODE, "+a=" + EARTH_RADIUS };
        int res = BltDataProjector.initializeProjection( projParms );
        if( res == 1 ) {
            log.error( "Failed to initialize projection" );
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
                bltDataProcessor.getH5Attribute( root, whatGroup,
                        BltDataProcessor.H5_QUANTITY_ATTR );
                String quantity_val = ( String )bltDataProcessor.getAttributeValue();

                // get elevation angles
                bltDataProcessor.getH5Attribute( root, whereGroup, BltDataProcessor.H5_ELANGLE_ATTR );
                double elangle_val = ( Double )bltDataProcessor.getAttributeValue();

                // get number of range bins
                bltDataProcessor.getH5Attribute( root, whereGroup, BltDataProcessor.H5_NBINS_ATTR );
                long nbins_val = ( Long )bltDataProcessor.getAttributeValue();

                // retrieve data domain coordinates
                bltDataProcessor.getH5Attribute( root, whereGroup, BltDataProcessor.H5_RSCALE_ATTR );
                double rscale_val = ( Double )bltDataProcessor.getAttributeValue();

                // scale - in meters
                double range = rscale_val * nbins_val;
                Point2D.Double llXY = new Point2D.Double( -range, -range );
                Point2D.Double llLatLon = BltDataProjector.pointXY2Geo( llXY );
                Point2D.Double urXY = new Point2D.Double( range, range );
                Point2D.Double urLatLon = BltDataProjector.pointXY2Geo( urXY );

                // create dataset objects
                BltDataset bltDataset = new BltDataset( datasetFullNames.get( i ),
                    whereGroup, quantity_val, nbins_val * 2, nbins_val * 2,
                    lat0_val, lon0_val, llLatLon.getY(), llLatLon.getX(), urLatLon.getY(),
                    urLatLon.getX(), elangle_val, init.getConfiguration().getWorkDir() +
                    File.separator + init.getConfiguration().getThumbsDir() + File.separator +
                    uuid + datasetFullNames.get( i ).replaceAll( BltDataProcessor.H5_PATH_SEPARATOR,
                    "_" ) + BltDataProcessor.IMAGE_FILE_EXT );

                // add dataset object to the list
                bltDatasets.add( bltDataset );

                // try to load thumb from disk before creating a new one
                String thumbPath = init.getThumbsDirPath() + File.separator
                    + uuid + datasetFullNames.get( i ).replaceAll(
                        BltDataProcessor.H5_PATH_SEPARATOR, "_" )  + BltDataProcessor.IMAGE_FILE_EXT;

                // generate image thumbs
                File thumb = new File( thumbPath );
                if( !thumb.exists() ) {
                    bltDataProcessorController.polarH5Dataset2Image( h5File,
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
        return model;
    }
    /**
     * Wrapper method processing Cartesian data - IMAGEs
     *
     * @param bltFile File entry from the File Catalog
     * @param h5File HDF5 file object
     * @param uuid Unique file entry ID
     * @return HashMap holding file entry and extracted dataset
     */
    public HashMap processCartesianData( BltFile bltFile, H5File h5File, String uuid ) {
        // ... to be implemented ...
        return null;
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

    public FileCatalog getFileCatalog() { return fileCatalog; }

    public void setFileCatalog(FileCatalog fileCatalog) {
        this.fileCatalog = fileCatalog;
    }
}
//--------------------------------------------------------------------------------------------------
