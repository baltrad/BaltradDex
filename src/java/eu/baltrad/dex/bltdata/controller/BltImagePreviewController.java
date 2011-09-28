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
import eu.baltrad.dex.util.InitAppUtil;

import eu.baltrad.fc.FileCatalog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.File;

import java.util.HashMap;

/**
 * Implements data visualization and preview functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.5.4
 * @since 0.5.4
 */
public class BltImagePreviewController implements Controller {
//---------------------------------------------------------------------------------------- Constants
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
    // model key
    private static final String MODEL_KEY = "model";
    // URL path separator
    private static final String URL_PATH_SEPARATOR = "/";
    // range rings color string
    private static final String IMAGE_RANGE_RINGS_COLOR = "#A7A7A7";
    // range mask color string
    private static final String IMAGE_RANGE_MASK_COLOR = "#A7A7A7";
//---------------------------------------------------------------------------------------- Variables
    private String successView;
    private BltDataProcessor bltDataProcessor;
    private BltDataProcessorController bltDataProcessorController;
    private InitAppUtil init;
    private FileCatalog fileCatalog;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public BltImagePreviewController() {
        init = InitAppUtil.getInstance();
    }
    /**
     * Creates image based on parameters retrieved from request.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView object holding model data
     * @throws ServletException 
     * @throws IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
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
        String filePath = init.getImagesDirPath() + File.separator + fileUuid
            + datasetPath.replaceAll( BltDataProcessor.H5_PATH_SEPARATOR, "_" ) +
            BltDataProcessor.IMAGE_FILE_EXT;
        File imageFile = new File( filePath );
        if( !imageFile.exists() ) {
            // Create image from SCAN or PVOL
            if( fileObject.equals( BltDataProcessor.ODIMH5_SCAN_OBJ ) || fileObject.equals(
                    BltDataProcessor.ODIMH5_PVOL_OBJ ) ) {
                bltDataProcessorController.polarH5Dataset2Image(
                    fileCatalog.local_path_for_uuid(fileUuid),
                    datasetPath, datasetWhere,
                    Integer.parseInt( datasetWidth ), ( short )0, 1.0f, IMAGE_RANGE_RINGS_COLOR,
                    IMAGE_RANGE_MASK_COLOR, filePath );
            }
            // Create image from Cartesian IMAGE object
            if( fileObject.equals( BltDataProcessor.ODIMH5_IMAGE_OBJ ) ) {
                // ... to be implemented ...
            }
            // ... other objects will come here ...
        }
        // reconstruct image URL
        StringBuffer requestURL = request.getRequestURL();
        String imageURL = requestURL.substring( 0, requestURL.lastIndexOf( URL_PATH_SEPARATOR )
            + 1 ) + init.getConfiguration().getWorkDir() + URL_PATH_SEPARATOR + 
            init.getConfiguration().getImagesDir() + URL_PATH_SEPARATOR + fileUuid +
            datasetPath.replaceAll( BltDataProcessor.H5_PATH_SEPARATOR, "_" ) +
            BltDataProcessor.IMAGE_FILE_EXT;
        
        // create model
        HashMap model = new HashMap();
        model.put( RADAR_LAT_0_KEY, lat0 );
        model.put( RADAR_LON_0_KEY, lon0 );
        model.put( RADAR_LL_LAT_KEY, llLat );
        model.put( RADAR_LL_LON_KEY, llLon );
        model.put( RADAR_UR_LAT_KEY, urLat );
        model.put( RADAR_UR_LON_KEY, urLon );
        model.put( IMAGE_URL_KEY, imageURL );
        return new ModelAndView( getSuccessView(), MODEL_KEY, model );
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
     * Gets reference to BltDataProcessor object.
     *
     * @return Reference to BltDataProcessor object
     */
    public BltDataProcessor getBltDataProcessor() { return bltDataProcessor; }
    /**
     * Sets reference to BltDataProcessor object.
     *
     * @param dataProcessor BltDataProcessor object reference to set
     */
    public void setBltDataProcessor( BltDataProcessor bltDataProcessor ) {
        this.bltDataProcessor = bltDataProcessor;
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

    public FileCatalog getFileCatalog() { return fileCatalog; }

    public void setFileCatalog(FileCatalog fileCatalog) {
        this.fileCatalog = fileCatalog;
    }
}
//--------------------------------------------------------------------------------------------------
