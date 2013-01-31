/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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
*******************************************************************************/

package eu.baltrad.dex.db.controller;

import eu.baltrad.dex.bltdata.util.DataProcessor;
import eu.baltrad.dex.db.manager.impl.BltFileManager;
import eu.baltrad.dex.db.model.BltFile;
import eu.baltrad.dex.db.model.BltDataset;
import eu.baltrad.dex.bltdata.util.DataProjector;

import eu.baltrad.bdb.FileCatalog;
import eu.baltrad.dex.config.manager.impl.ConfigurationManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.Group;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import java.awt.geom.Point2D;

import java.text.SimpleDateFormat;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.UUID;

/**
 * Provides access to detailed data file information.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.1.6
 */
@Controller
public class BltFileDetailsController {

    // View name
    private static final String SUCCESS_VIEW = "file_details";
    
    // Model keys
    private static final String FC_FILE_UUID = "uuid";
    private static final String DATASETS_KEY = "blt_datasets";
    private static final String FILE_KEY = "blt_file";
    private static final String FILE_NAME_KEY = "file_name";
    private static final String FILE_SOURCE_KEY = "source";
    private static final String FILE_TYPE_KEY = "type";
    private static final String FILE_DATE_KEY = "date_str";
    private static final String FILE_TIME_KEY = "time_str";
    private static final String FILE_STORAGE_KEY = "storage_time";
    // PROJ4 projection code
    private static final String PROJ4_PROJ_CODE = "aeqd";
    // PROJ4 ellipsoid code
    private static final String PROJ4_ELLPS_CODE = "WGS84";
    // Earth radius in meters
    private static final int EARTH_RADIUS = 6371000;
    // Image thumb size
    private static final int THUMB_IMAGE_SIZE = 64;
    // Range rings distance
    private static final short THUMB_RANGE_RINGS_DISTANCE = 0;
    // Range mask line stroke
    private static final float THUMB_RANGE_MASK_STROKE = 1.0f;
    // Range rings color string
    private static final String THUMB_RANGE_RINGS_COLOR = "#003098";
    // Range mask color string
    private static final String THUMB_RANGE_MASK_COLOR = "#003098";

    private DataProcessor bltDataProcessor;
    private BltFileManager bltFileManager;
    private FileCatalog fileCatalog;
    private ConfigurationManager configurationManager;
    private Logger log;
    
    private SimpleDateFormat dateTimeFormat;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    
    /**
     * Constructor.
     */
    public BltFileDetailsController() {
        this.log = Logger.getLogger("DEX");
        this.dateTimeFormat = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.timeFormat = new SimpleDateFormat("HH:mm:ss");
    }
    
    /**
     * Get detailed information about file and prepare dataset image thumbs.
     * @param request Http servlet request
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/file_details.htm")
    public String processSubmit(HttpServletRequest request, ModelMap model) {
        
        String uuid = request.getParameter(FC_FILE_UUID);
        
        BltFile bltFile = bltFileManager.load(uuid);
        String fileName = bltFile.getPath().substring( 
                bltFile.getPath().lastIndexOf(File.separator)
            + 1, bltFile.getPath().length());
        model.addAttribute(FILE_NAME_KEY, fileName);
        model.addAttribute(FC_FILE_UUID, bltFile.getUuid());
        model.addAttribute(FILE_SOURCE_KEY, bltFile.getSource());
        model.addAttribute(FILE_TYPE_KEY, bltFile.getType());
        String storageTime = dateTimeFormat.format(bltFile.getStorageTime());
        model.addAttribute(FILE_STORAGE_KEY, storageTime);
        String dateStr = dateFormat.format(bltFile.getTimeStamp());
        model.addAttribute(FILE_DATE_KEY, dateStr);
        String timeStr = timeFormat.format(bltFile.getTimeStamp());
        model.addAttribute(FILE_TIME_KEY, timeStr);
        
        String filePath = fileCatalog.getLocalPathForUuid(
                UUID.fromString(uuid)).toString();
        H5File h5File = bltDataProcessor.openH5File(filePath);
        
        // Process file depending on file object / data type
        // SCANs and PVOLs
        if (bltFile.getType().equals(DataProcessor.ODIMH5_SCAN_OBJ) || 
                bltFile.getType().equals(DataProcessor.ODIMH5_PVOL_OBJ)) {
            processPolarData(bltFile, h5File, uuid, model);
        }
        // IMAGEs
        if (bltFile.getType().equals(DataProcessor.ODIMH5_IMAGE_OBJ)) {
            processCartesianData(bltFile, h5File, uuid, model);
        }

        // ... other file objects to be implemented here ...

        return SUCCESS_VIEW;
    }
    /**
     * Wrapper method processing polar data - SCANs and PVOLs
     * @param bltFile File entry from the File Catalog
     * @param h5File HDF5 file object
     * @param uuid Unique file entry 
     */
    public void processPolarData(BltFile bltFile, H5File h5File, 
            String uuid, ModelMap model) {
        // process the file
        Group root = bltDataProcessor.getH5Root( h5File );
        // get dataset full names
        bltDataProcessor.getH5Datasets( root );
        List<String> datasetFullNames = bltDataProcessor.getDatasetFullNames();
        // dataset objects holds file's metadata
        List<BltDataset> bltDatasets = new ArrayList<BltDataset>();

        // get radar location latitude
        bltDataProcessor.getH5Attribute( root, DataProcessor.H5_PATH_SEPARATOR +
                DataProcessor.H5_WHERE_GROUP_PREFIX, DataProcessor.H5_LAT_0_ATTR );
        double lat0_val = ( Double )bltDataProcessor.getAttributeValue();

        // get radar location longitude
        bltDataProcessor.getH5Attribute( root, DataProcessor.H5_PATH_SEPARATOR +
                DataProcessor.H5_WHERE_GROUP_PREFIX, DataProcessor.H5_LON_0_ATTR );
        double lon0_val = ( Double )bltDataProcessor.getAttributeValue();

        // initialize projection
        String[] projParms = new String[] { "+proj=" + PROJ4_PROJ_CODE, "+lat_0=" + lat0_val,
            "+lon_0=" + lon0_val, "+ellps=" + PROJ4_ELLPS_CODE, "+a=" + EARTH_RADIUS };
        int res = DataProjector.initializeProjection( projParms );
        if( res == 1 ) {
            log.error( "Failed to initialize projection" );
        }
        try {
            // iterate through datasets
            for( int i = 0; i < datasetFullNames.size(); i++ ) {
                 // find dataset specific WHERE group and data specific WHAT group
                String datasetFullName = datasetFullNames.get( i );
                String[] nameParts = datasetFullName.split( DataProcessor.H5_PATH_SEPARATOR );
                String whereGroup = "";
                String whatGroup = "";
                for( int j = 0; j < nameParts.length; j++ ) {
                    if( nameParts[ j ].startsWith( DataProcessor.H5_DATASET_PREFIX ) ) {
                        whereGroup = DataProcessor.H5_PATH_SEPARATOR + nameParts[ j ] +
                            DataProcessor.H5_PATH_SEPARATOR + DataProcessor.H5_WHERE_GROUP_PREFIX;
                        whatGroup = datasetFullName.substring( 0, datasetFullName.lastIndexOf(
                            DataProcessor.H5_PATH_SEPARATOR ) ) + DataProcessor.H5_PATH_SEPARATOR
                            + DataProcessor.H5_WHAT_GROUP_PREFIX;
                    }
                }
                // get data quantity
                bltDataProcessor.getH5Attribute( root, whatGroup,
                        DataProcessor.H5_QUANTITY_ATTR );
                String quantity_val = ( String )bltDataProcessor.getAttributeValue();

                // get elevation angles
                bltDataProcessor.getH5Attribute( root, whereGroup, DataProcessor.H5_ELANGLE_ATTR );
                double elangle_val = ( Double )bltDataProcessor.getAttributeValue();

                // get number of range bins
                bltDataProcessor.getH5Attribute( root, whereGroup, DataProcessor.H5_NBINS_ATTR );
                long nbins_val = ( Long )bltDataProcessor.getAttributeValue();

                // retrieve data domain coordinates
                bltDataProcessor.getH5Attribute( root, whereGroup, DataProcessor.H5_RSCALE_ATTR );
                double rscale_val = ( Double )bltDataProcessor.getAttributeValue();

                // scale - in meters
                double range = rscale_val * nbins_val;
                Point2D.Double llXY = new Point2D.Double( -range, -range );
                Point2D.Double llLatLon = DataProjector.pointXY2Geo( llXY );
                Point2D.Double urXY = new Point2D.Double( range, range );
                Point2D.Double urLatLon = DataProjector.pointXY2Geo( urXY );

                // create dataset objects
                BltDataset bltDataset = new BltDataset( datasetFullNames.get( i ),
                    whereGroup, quantity_val, nbins_val * 2, nbins_val * 2,
                    lat0_val, lon0_val, llLatLon.getY(), llLatLon.getX(), urLatLon.getY(),
                    urLatLon.getX(), elangle_val, configurationManager.getAppConf().getWorkDir() +
                    File.separator + configurationManager.getAppConf().getThumbsDir() + File.separator +
                    uuid + datasetFullNames.get( i ).replaceAll( DataProcessor.H5_PATH_SEPARATOR,
                    "_" ) + DataProcessor.IMAGE_FILE_EXT );

                // add dataset object to the list
                bltDatasets.add( bltDataset );

                // try to load thumb from disk before creating a new one
                String thumbPath = configurationManager.getAppConf().getThumbsDir() + File.separator
                    + uuid + datasetFullNames.get( i ).replaceAll(
                        DataProcessor.H5_PATH_SEPARATOR, "_" )  + DataProcessor.IMAGE_FILE_EXT;

                // generate image thumbs
                File thumb = new File( thumbPath );
                if( !thumb.exists() ) {
                    bltDataProcessor.polarH5Dataset2Image( h5File,
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
        Collections.sort(bltDatasets);
        model.addAttribute(DATASETS_KEY, bltDatasets);
        model.addAttribute(FILE_KEY, bltFile);
    }
    /**
     * Wrapper method processing Cartesian data - IMAGEs
     * @param bltFile File entry from the File Catalog
     * @param h5File HDF5 file object
     * @param uuid Unique file entry ID
     * @param model Model map
     */
    public void processCartesianData(BltFile bltFile, H5File h5File, 
            String uuid, ModelMap model) {
        // ... to be implemented ...

    }

    /**
     * @param bltDataProcessor the bltDataProcessor to set
     */
    @Autowired
    public void setBltDataProcessor(DataProcessor bltDataProcessor) {
        this.bltDataProcessor = bltDataProcessor;
    }

    /**
     * @param bltFileManager the bltFileManager to set
     */
    @Autowired
    public void setBltFileManager(BltFileManager bltFileManager) {
        this.bltFileManager = bltFileManager;
    }

    /**
     * @param fileCatalog the fileCatalog to set
     */
    @Autowired
    public void setFileCatalog(FileCatalog fileCatalog) {
        this.fileCatalog = fileCatalog;
    }
    
    /**
     * @param configurationManager 
     */
    @Autowired
    public void setConfigurationManager(
            ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }
    
}

