/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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

import eu.baltrad.dex.db.util.BltDataProcessor;

import eu.baltrad.bdb.FileCatalog;
import eu.baltrad.dex.config.manager.impl.ConfigurationManager;
import eu.baltrad.dex.util.ServletContextUtil;
import java.awt.Color;
import java.awt.image.BufferedImage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;

import java.io.File;

import java.util.UUID;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5File;

/**
 * Implements data visualization and preview functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.5.4
 */
@Controller
public class BltImagePreviewController {
 
    // View name
    private static final String SUCCESS_VIEW = "file_preview";
    
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
    // Color palette file
    private static final String PALETTE_FILE = "includes/color_palette.txt";
    
    private BltDataProcessor bltDataProcessor;
    private FileCatalog fileCatalog;
    private ConfigurationManager configurationManager;

    /**
     * Creates image based on parameters retrieved from request.
     * @param request HTTP request
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/file_preview.htm")
    public String showPreview(HttpServletRequest request, ModelMap model) 
        throws Exception {
        // get dataset metadata
        String fileUuid = request.getParameter( "file_uuid" );
        String fileObject = request.getParameter( "file_object" );
        String datasetPath = request.getParameter( "dataset_path" );
        String datasetWhere = request.getParameter( "dataset_where" );
        // get information necessary to display image
        String lat0 = request.getParameter( "lat0" );
        String lon0 = request.getParameter( "lon0" );
        String llLat = request.getParameter( "llLat" );
        String llLon = request.getParameter( "llLon" );
        String urLat = request.getParameter( "urLat" );
        String urLon = request.getParameter( "urLon" );
        // try to load image from disk before creating a new one
        String filePath = ServletContextUtil.getServletContextPath() + 
                configurationManager.getAppConf().getWorkDir() +
                File.separator + 
                configurationManager.getAppConf().getImagesDir() + 
                File.separator + fileUuid
            + datasetPath.replaceAll( BltDataProcessor.H5_PATH_SEPARATOR, "_" ) +
            BltDataProcessor.IMAGE_FILE_EXT;
        File imageFile = new File(filePath);
        if (!imageFile.exists()) {
            Color[] palette = bltDataProcessor.createColorPalette(
                ServletContextUtil.getServletContextPath() + PALETTE_FILE);
            H5File file = bltDataProcessor.openH5File(fileCatalog
                    .getLocalPathForUuid(UUID.fromString(fileUuid)).toString());
            Group root = bltDataProcessor.getH5Root(file);
            
            // Create image from SCAN or PVOL
            if (fileObject.equals(BltDataProcessor.ODIMH5_SCAN_OBJ) || 
                    fileObject.equals(BltDataProcessor.ODIMH5_PVOL_OBJ)) {
                bltDataProcessor.getH5Attribute(root, datasetWhere, 
                    BltDataProcessor.H5_NBINS_ATTR );
                long nbins = (Long) bltDataProcessor.getH5AttributeValue();
                bltDataProcessor.getH5Attribute(root, datasetWhere, 
                        BltDataProcessor.H5_RSCALE_ATTR );
                double rscale = (Double) bltDataProcessor.getH5AttributeValue();
                bltDataProcessor.getH5Dataset(root, datasetPath);
                Dataset dataset = bltDataProcessor.getH5Dataset();
                BufferedImage bi = bltDataProcessor.polar2Image(
                    nbins, rscale, dataset, palette, 0, true, false);
                bltDataProcessor.saveImageToFile(bi, filePath);
            }
            // Create image from Cartesian composite object
            if (fileObject.equals(BltDataProcessor.ODIMH5_COMP_OBJ)) {
                bltDataProcessor.getH5Attribute(root, datasetWhere, 
                    BltDataProcessor.H5_XSIZE_ATTR );
                long xsize = (Long) bltDataProcessor.getH5AttributeValue();
                bltDataProcessor.getH5Attribute(root, datasetWhere, 
                        BltDataProcessor.H5_YSIZE_ATTR );
                long ysize = (Long) bltDataProcessor.getH5AttributeValue();
                String dataSpecificWhat = datasetPath.substring(0, 
                        datasetPath.lastIndexOf(BltDataProcessor.H5_PATH_SEPARATOR) 
                            + 1) + BltDataProcessor.H5_WHAT_GROUP_PREFIX;
                bltDataProcessor.getH5Attribute(root, dataSpecificWhat, 
                        BltDataProcessor.H5_NODATA_ATTR);
                double noData = (Double) bltDataProcessor.getH5AttributeValue();
                bltDataProcessor.getH5Dataset(root, datasetPath);
                Dataset dataset = bltDataProcessor.getH5Dataset();
                BufferedImage bi = bltDataProcessor.cart2Image(xsize, ysize, 
                        noData, dataset, palette, 0, 0, false);
                bltDataProcessor.saveImageToFile(bi, filePath);
            }
            // Create image from Cartesian IMAGE object
            if (fileObject.equals(BltDataProcessor.ODIMH5_IMAGE_OBJ)) {
                
                // ... to be implemented ...
            
            }
            bltDataProcessor.closeH5File(file);
        }
        // reconstruct image URL
        StringBuffer requestURL = request.getRequestURL();
        String imageURL = requestURL.substring( 0, 
            requestURL.lastIndexOf( URL_PATH_SEPARATOR ) + 1 ) + 
                configurationManager.getAppConf().getWorkDir() + 
                URL_PATH_SEPARATOR + 
                configurationManager.getAppConf().getImagesDir() + 
                URL_PATH_SEPARATOR + 
                fileUuid + datasetPath.replaceAll( 
                BltDataProcessor.H5_PATH_SEPARATOR, "_" ) +
                BltDataProcessor.IMAGE_FILE_EXT;
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
    public void setBltDataProcessor(BltDataProcessor bltDataProcessor) {
        this.bltDataProcessor = bltDataProcessor;
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

