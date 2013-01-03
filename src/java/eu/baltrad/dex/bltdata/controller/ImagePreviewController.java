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

package eu.baltrad.dex.bltdata.controller;

import eu.baltrad.dex.bltdata.util.DataProcessor;
import eu.baltrad.dex.util.InitAppUtil;

import eu.baltrad.bdb.FileCatalog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;

import java.io.File;

import java.util.UUID;

/**
 * Implements data visualization and preview functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.5.4
 */
@Controller
public class ImagePreviewController {
 
    // View name
    private static final String SUCCESS_VIEW = "data_preview";
    
    // the URL of image to be displayed
    private static final String IMAGE_URL_KEY = "image_url";
    // radar location latitude
    private static final String RADAR_LAT_0_KEY = "lat0";
    // radar location longitude
    private static final String RADAR_LON_0_KEY = "lon0";
    // data domain lower left corner latitude
    private static final String RADAR_LL_LAT_KEY = "llLat";
    // data domain lower left corner longitude
    private static final String RADAR_LL_LON_KEY = "llLon";
    // data domain upper right corner latitude
    private static final String RADAR_UR_LAT_KEY = "urLat";
    // data domain upper right corner longitude
    private static final String RADAR_UR_LON_KEY = "urLon";
    // URL path separator
    private static final String URL_PATH_SEPARATOR = "/";
    // range rings color string
    private static final String IMAGE_RANGE_RINGS_COLOR = "#A7A7A7";
    // range mask color string
    private static final String IMAGE_RANGE_MASK_COLOR = "#A7A7A7";
    
    private DataProcessor bltDataProcessor;
    private FileCatalog fileCatalog;

    /**
     * Creates image based on parameters retrieved from request.
     * @param request HTTP request
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/data_preview.htm")
    public String showPreview(HttpServletRequest request, ModelMap model) {
        // get dataset metadata
        String fileUuid = request.getParameter( "file_uuid" );
        String fileObject = request.getParameter( "file_object" );
        String datasetPath = request.getParameter( "dataset_path" );
        String datasetWhere = request.getParameter( "dataset_where" );
        String datasetWidth = request.getParameter( "dataset_width" );
        // get information necessary to display image
        String lat0 = request.getParameter( "lat0" );
        String lon0 = request.getParameter( "lon0" );
        String llLat = request.getParameter( "llLat" );
        String llLon = request.getParameter( "llLon" );
        String urLat = request.getParameter( "urLat" );
        String urLon = request.getParameter( "urLon" );

        // try to load image from disk before creating a new one
        String filePath = InitAppUtil.getImagesDir() + File.separator + fileUuid
            + datasetPath.replaceAll( DataProcessor.H5_PATH_SEPARATOR, "_" ) +
            DataProcessor.IMAGE_FILE_EXT;
        File imageFile = new File( filePath );
        if( !imageFile.exists() ) {
            // Create image from SCAN or PVOL
            if( fileObject.equals( DataProcessor.ODIMH5_SCAN_OBJ ) || 
                    fileObject.equals(DataProcessor.ODIMH5_PVOL_OBJ ) ) {
                bltDataProcessor.polarH5Dataset2Image(
                    fileCatalog.getLocalPathForUuid(
                        UUID.fromString(fileUuid)).toString(),
                    datasetPath, datasetWhere,
                    Integer.parseInt( datasetWidth ), ( short )0, 1.0f, 
                    IMAGE_RANGE_RINGS_COLOR,
                    IMAGE_RANGE_MASK_COLOR, filePath );
            }
            // Create image from Cartesian IMAGE object
            if( fileObject.equals( DataProcessor.ODIMH5_IMAGE_OBJ ) ) {
                // ... to be implemented ...
            }
            // ... other objects will come here ...
        }
        // reconstruct image URL
        StringBuffer requestURL = request.getRequestURL();
        String imageURL = requestURL.substring( 0, 
                requestURL.lastIndexOf( URL_PATH_SEPARATOR )
            + 1 ) + InitAppUtil.getConf().getWorkDir() + URL_PATH_SEPARATOR + 
            InitAppUtil.getConf().getImagesDir() + URL_PATH_SEPARATOR + 
                fileUuid + datasetPath.replaceAll( 
                DataProcessor.H5_PATH_SEPARATOR, "_" ) +
            DataProcessor.IMAGE_FILE_EXT;
        
        // create model
        model.addAttribute(RADAR_LAT_0_KEY, lat0);
        model.addAttribute(RADAR_LON_0_KEY, lon0);
        model.addAttribute(RADAR_LL_LAT_KEY, llLat);
        model.addAttribute(RADAR_LL_LON_KEY, llLon);
        model.addAttribute(RADAR_UR_LAT_KEY, urLat);
        model.addAttribute(RADAR_UR_LON_KEY, urLon);
        model.addAttribute(IMAGE_URL_KEY, imageURL);
        return SUCCESS_VIEW;
    }

    /**
     * @param bltDataProcessor the bltDataProcessor to set
     */
    @Autowired
    public void setBltDataProcessor(DataProcessor bltDataProcessor) {
        this.bltDataProcessor = bltDataProcessor;
    }

    /**
     * @param fileCatalog the fileCatalog to set
     */
    @Autowired
    public void setFileCatalog(FileCatalog fileCatalog) {
        this.fileCatalog = fileCatalog;
    }
    
}

