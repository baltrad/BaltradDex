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

import eu.baltrad.dex.db.util.BltAttribute;
import eu.baltrad.dex.db.util.BltDataProcessor;
import eu.baltrad.dex.db.manager.impl.BltFileManager;
import eu.baltrad.dex.db.model.BltFile;
import eu.baltrad.dex.db.model.BltDataset;
import eu.baltrad.dex.db.util.BltDataProjector;
import eu.baltrad.dex.config.manager.impl.ConfigurationManager;
import eu.baltrad.dex.util.ServletContextUtil;

import eu.baltrad.bdb.FileCatalog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.Dataset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.Color;

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
    private static final String H5_OBJECT_KEY = "h5_object";
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
    // color palette file
    private static final String PALETTE_FILE = "includes/color_palette.txt";

    private BltDataProcessor bltDataProcessor;
    private BltFileManager bltFileManager;
    private FileCatalog fileCatalog;
    private ConfigurationManager configurationManager;
    private Logger log;
    private Logger logger = LogManager.getLogger(BltFileDetailsController.class);
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
     * 
     * @param request
     *            Http servlet request
     * @param model
     *            Model map
     * @return View name
     */
    @RequestMapping("/file_details.htm")
    public String processSubmit(HttpServletRequest request, ModelMap model) {
        String uuid = request.getParameter(FC_FILE_UUID);
        BltFile bltFile = bltFileManager.load(uuid);
        String fileName = bltFile.getPath().substring(
                bltFile.getPath().lastIndexOf(File.separator) + 1,
                bltFile.getPath().length());
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

        String filePath = fileCatalog
                .getLocalPathForUuid(UUID.fromString(uuid)).toString();
        H5File h5File = bltDataProcessor.openH5File(filePath);

        // Process file depending on file object / data type
        // SCANs and PVOLs
        if (bltFile.getType().equals(BltDataProcessor.ODIMH5_SCAN_OBJ)
                || bltFile.getType().equals(BltDataProcessor.ODIMH5_PVOL_OBJ)) {
            processPolarData(bltFile, h5File, uuid, model);
        }
        // Composite maps
        if (bltFile.getType().equals(BltDataProcessor.ODIMH5_COMP_OBJ)) {
            processCompositeData(bltFile, h5File, uuid, model);
        }

        // ... other file objects to be implemented here ...

        return SUCCESS_VIEW;
    }

    /**
     * Wrapper method processing polar data - SCANs and PVOLs
     * 
     * @param bltFile
     *            File entry from the File Catalog
     * @param h5File
     *            HDF5 file object
     * @param uuid
     *            Unique file entry
     */
    public void processPolarData(BltFile bltFile, H5File h5File, String uuid,
            ModelMap model) {
        // process the file
        Group root = bltDataProcessor.getH5Root(h5File);
        // get dataset full names
        List<String> datasetPaths = new ArrayList<String>();
        bltDataProcessor.getH5DatasetPaths(root, datasetPaths);
        // dataset objects holds file's metadata
        List<BltDataset> bltDatasets = new ArrayList<BltDataset>();
        // get radar location latitude
        bltDataProcessor.getH5Attribute(root,
                BltDataProcessor.H5_PATH_SEPARATOR
                        + BltDataProcessor.H5_WHERE_GROUP_PREFIX,
                BltDataProcessor.H5_LAT_0_ATTR);
        double lat0_val = (Double) bltDataProcessor.getH5AttributeValue();

        // get radar location longitude
        bltDataProcessor.getH5Attribute(root,
                BltDataProcessor.H5_PATH_SEPARATOR
                        + BltDataProcessor.H5_WHERE_GROUP_PREFIX,
                BltDataProcessor.H5_LON_0_ATTR);
        double lon0_val = (Double) bltDataProcessor.getH5AttributeValue();

        // initialize projection
        String[] projParms = new String[] { "+proj=" + PROJ4_PROJ_CODE,
                "+lat_0=" + lat0_val, "+lon_0=" + lon0_val,
                "+ellps=" + PROJ4_ELLPS_CODE, "+a=" + EARTH_RADIUS };
        int res = BltDataProjector.initializeProjection(projParms);
        if (res == 1) {
            log.error("Failed to initialize projection");
        }
        Color[] palette = bltDataProcessor
                .createColorPalette(ServletContextUtil.getServletContextPath()
                        + PALETTE_FILE);

        // get object type
        bltDataProcessor.getH5Attribute(root,
                BltDataProcessor.H5_PATH_SEPARATOR
                        + BltDataProcessor.H5_WHAT_GROUP_PREFIX,
                BltDataProcessor.H5_OBJECT_ATTR);
        String object = bltDataProcessor.getH5AttributeValue().toString();

        try {
            // iterate through datasets
            for (int i = 0; i < datasetPaths.size(); i++) {
                // find dataset specific WHERE group and data specific WHAT
                // group
                String datasetFullName = datasetPaths.get(i);
                String[] nameParts = datasetFullName
                        .split(BltDataProcessor.H5_PATH_SEPARATOR);
                String whereGroup = "";
                String whatGroup = "";
                for (int j = 0; j < nameParts.length; j++) {
                    if (nameParts[j]
                            .startsWith(BltDataProcessor.H5_DATASET_PREFIX)) {
                        whereGroup = BltDataProcessor.H5_PATH_SEPARATOR
                                + nameParts[j]
                                + BltDataProcessor.H5_PATH_SEPARATOR
                                + BltDataProcessor.H5_WHERE_GROUP_PREFIX;
                        whatGroup = datasetFullName
                                .substring(
                                        0,
                                        datasetFullName
                                                .lastIndexOf(BltDataProcessor.H5_PATH_SEPARATOR))
                                + BltDataProcessor.H5_PATH_SEPARATOR
                                + BltDataProcessor.H5_WHAT_GROUP_PREFIX;
                    }
                }

                // get data quantity
                bltDataProcessor.getH5Attribute(root, whatGroup,
                        BltDataProcessor.H5_QUANTITY_ATTR);
                String quantity_val = bltDataProcessor
                        .getH5AttributeValue().toString();

                double nodata_val=255.0, undetect_val=0.0, offset_val=0.0, gain_val=1.0;
                try {
                  bltDataProcessor.getH5Attribute(root, whatGroup,
                      BltDataProcessor.H5_NODATA_ATTR);
                  nodata_val = (Double)bltDataProcessor.getH5AttributeValue();
                } catch (Exception e) {
                  logger.info("Dataset does not contain what/nodata attribute", e);
                }

                try {
                  bltDataProcessor.getH5Attribute(root, whatGroup,
                      BltDataProcessor.H5_UNDETECT_ATTR);
                  undetect_val = (Double)bltDataProcessor.getH5AttributeValue();
                } catch (Exception e) {
                  logger.info("Dataset does not contain what/undetect attribute", e);
                }

                try {
                  bltDataProcessor.getH5Attribute(root, whatGroup,
                      BltDataProcessor.H5_OFFSET_ATTR);
                  offset_val = (Double)bltDataProcessor.getH5AttributeValue();
                } catch (Exception e) {
                  logger.info("Dataset does not contain what/offset attribute", e);
                }

                try {
                  bltDataProcessor.getH5Attribute(root, whatGroup,
                      BltDataProcessor.H5_GAIN_ATTR);
                  gain_val = (Double)bltDataProcessor.getH5AttributeValue();
                } catch (Exception e) {
                  logger.info("Dataset does not contain what/gain attribute", e);
                }

                // get elevation angles
                bltDataProcessor.getH5Attribute(root, whereGroup,
                        BltDataProcessor.H5_ELANGLE_ATTR);
                double elangle_val = (Double) bltDataProcessor
                        .getH5AttributeValue();

                // get number of range bins
                bltDataProcessor.getH5Attribute(root, whereGroup,
                        BltDataProcessor.H5_NBINS_ATTR);
                long nbins_val = (Long) bltDataProcessor.getH5AttributeValue();

                // get bin resolution
                bltDataProcessor.getH5Attribute(root, whereGroup,
                        BltDataProcessor.H5_RSCALE_ATTR);
                double rscale_val = (Double) bltDataProcessor
                        .getH5AttributeValue();

                // scale - in meters
                double range = rscale_val * nbins_val;
                // retrieve data domain coordinates
                Point2D.Double llXY = new Point2D.Double(-range, -range);
                Point2D.Double llLatLon = BltDataProjector.pointXY2Geo(llXY);
                Point2D.Double urXY = new Point2D.Double(range, range);
                Point2D.Double urLatLon = BltDataProjector.pointXY2Geo(urXY);

                // create dataset objects
                BltDataset bltDataset = new BltDataset(
                        datasetPaths.get(i),
                        whereGroup,
                        quantity_val,
                        nbins_val * 2,
                        nbins_val * 2,
                        lat0_val,
                        lon0_val,
                        llLatLon.getY(),
                        llLatLon.getX(),
                        urLatLon.getY(),
                        urLatLon.getX(),
                        elangle_val,
                        configurationManager.getAppConf().getWorkDir()
                                + File.separator
                                + configurationManager.getAppConf()
                                        .getThumbsDir()
                                + File.separator
                                + uuid
                                + datasetPaths
                                        .get(i)
                                        .replaceAll(
                                                BltDataProcessor.H5_PATH_SEPARATOR,
                                                "_")
                                + BltDataProcessor.IMAGE_FILE_EXT);

                // add dataset object to the list
                bltDatasets.add(bltDataset);
                // try to load thumb from disk before creating a new one
                String thumbPath = ServletContextUtil.getServletContextPath()
                        + configurationManager.getAppConf().getWorkDir()
                        + File.separator
                        + configurationManager.getAppConf().getThumbsDir()
                        + File.separator
                        + uuid
                        + datasetPaths.get(i).replaceAll(
                                BltDataProcessor.H5_PATH_SEPARATOR, "_")
                        + BltDataProcessor.IMAGE_FILE_EXT;
                // generate image thumbs
                File thumb = new File(thumbPath);

                if (!thumb.exists()) {
                    bltDataProcessor.getH5Dataset(root, datasetPaths.get(i));
                    Dataset dataset = bltDataProcessor.getH5Dataset();
                    BufferedImage bi = bltDataProcessor.polar2Image(nbins_val,
                            rscale_val, nodata_val, undetect_val, offset_val, gain_val, dataset, palette, THUMB_IMAGE_SIZE,
                            false, true);
                    bltDataProcessor.saveImageToFile(bi, thumbPath);
                }
            }
        } finally {
            // important: image generating method does not close the file
            bltDataProcessor.closeH5File(h5File);
        }
        Collections.sort(bltDatasets);
        model.addAttribute(DATASETS_KEY, bltDatasets);
        model.addAttribute(H5_OBJECT_KEY, object);
        model.addAttribute(FILE_KEY, bltFile);
    }

    /**
     * Wrapper method processing Cartesian data - IMAGEs
     * 
     * @param bltFile
     *            File entry from the File Catalog
     * @param h5File
     *            HDF5 file object
     * @param uuid
     *            Unique file entry ID
     * @param model
     *            Model map
     */
    public void processCompositeData(BltFile bltFile, H5File h5File,
            String uuid, ModelMap model) {
        // process the file
        Group root = bltDataProcessor.getH5Root(h5File);
        // get dataset full names
        List<String> datasetPaths = new ArrayList<String>();
        bltDataProcessor.getH5DatasetPaths(root, datasetPaths);
        // dataset objects holds file's metadata
        List<BltDataset> bltDatasets = new ArrayList<BltDataset>();
        Color[] palette = bltDataProcessor
                .createColorPalette(ServletContextUtil.getServletContextPath()
                        + PALETTE_FILE);
        String whereGroup = BltDataProcessor.H5_PATH_SEPARATOR
                + BltDataProcessor.H5_WHERE_GROUP_PREFIX;

        // get object type
        bltDataProcessor.getH5Attribute(root,
                BltDataProcessor.H5_PATH_SEPARATOR
                        + BltDataProcessor.H5_WHAT_GROUP_PREFIX,
                BltDataProcessor.H5_OBJECT_ATTR);
        String object = bltDataProcessor.getH5AttributeValue().toString();

        try {
            // iterate through datasets
            for (int i = 0; i < datasetPaths.size(); i++) {
                // find dataset specific WHAT group
                String datasetFullName = datasetPaths.get(i);
                String[] nameParts = datasetFullName
                        .split(BltDataProcessor.H5_PATH_SEPARATOR);
                String datasetSpecificHow = "";
                String dataSpecificWhat = "";
                BltAttribute battr = null;
                for (int j = 0; j < nameParts.length; j++) {
                    if (nameParts[j]
                            .startsWith(BltDataProcessor.H5_DATASET_PREFIX)) {
                        datasetSpecificHow = datasetFullName.substring(0,
                                datasetFullName.indexOf(
                                        BltDataProcessor.H5_PATH_SEPARATOR, 1))
                                + BltDataProcessor.H5_PATH_SEPARATOR
                                + BltDataProcessor.H5_HOW_GROUP_PREFIX;
                        dataSpecificWhat = datasetFullName
                                .substring(
                                        0,
                                        datasetFullName
                                                .lastIndexOf(BltDataProcessor.H5_PATH_SEPARATOR))
                                + BltDataProcessor.H5_PATH_SEPARATOR
                                + BltDataProcessor.H5_WHAT_GROUP_PREFIX;
                    }
                }

                // get nodes
                bltDataProcessor.getH5Attribute(root, datasetSpecificHow,
                        BltDataProcessor.H5_NODES_ATTR);
                String nodes = bltDataProcessor.getH5AttributeValue().toString();
                String nodesList = nodes.replaceAll("'", "").replaceAll(",",
                        ", ");
                // get data quantity
                String quantity_val = null;
                battr = bltDataProcessor.findAttribute(root, dataSpecificWhat, BltDataProcessor.H5_QUANTITY_ATTR);
                if (battr != null && battr.isString()) {
                  quantity_val = battr.getString();
                }
                bltDataProcessor.getH5Attribute(root, whereGroup,
                        BltDataProcessor.H5_XSIZE_ATTR);
                long xsize = (Long) bltDataProcessor.getH5AttributeValue();
                bltDataProcessor.getH5Attribute(root, whereGroup,
                        BltDataProcessor.H5_YSIZE_ATTR);
                long ysize = (Long) bltDataProcessor.getH5AttributeValue();
                double noData = 0.0;
                battr = bltDataProcessor.findAttribute(root, dataSpecificWhat, BltDataProcessor.H5_NODATA_ATTR);
                if (battr != null && battr.isDouble()) {
                  noData = battr.getDouble();
                }
                bltDataProcessor.getH5Attribute(root, whereGroup,
                        BltDataProcessor.H5_LL_LON_ATTR);
                double llLon = (Double) bltDataProcessor.getH5AttributeValue();
                bltDataProcessor.getH5Attribute(root, whereGroup,
                        BltDataProcessor.H5_LL_LAT_ATTR);
                double llLat = (Double) bltDataProcessor.getH5AttributeValue();
                bltDataProcessor.getH5Attribute(root, whereGroup,
                        BltDataProcessor.H5_UR_LON_ATTR);
                double urLon = (Double) bltDataProcessor.getH5AttributeValue();
                bltDataProcessor.getH5Attribute(root, whereGroup,
                        BltDataProcessor.H5_UR_LAT_ATTR);
                double urLat = (Double) bltDataProcessor.getH5AttributeValue();

                // This will work only in the upper right (north-eastern)
                // quarter of geographic coordinates system.
                double lon0 = llLon + ((urLon - llLon) / 2);
                double lat0 = llLat + ((urLat - llLat) / 2);

                // create dataset objects
                BltDataset bltDataset = new BltDataset(
                        datasetPaths.get(i),
                        whereGroup,
                        quantity_val,
                        nodesList,
                        xsize,
                        ysize,
                        lat0,
                        lon0,
                        llLat,
                        llLon,
                        urLat,
                        urLon,
                        configurationManager.getAppConf().getWorkDir()
                                + File.separator
                                + configurationManager.getAppConf()
                                        .getThumbsDir()
                                + File.separator
                                + uuid
                                + datasetPaths
                                        .get(i)
                                        .replaceAll(
                                                BltDataProcessor.H5_PATH_SEPARATOR,
                                                "_")
                                + BltDataProcessor.IMAGE_FILE_EXT);

                // add dataset object to the list
                bltDatasets.add(bltDataset);

                // try to load thumb from disk before creating a new one
                String thumbPath = ServletContextUtil.getServletContextPath()
                        + configurationManager.getAppConf().getWorkDir()
                        + File.separator
                        + configurationManager.getAppConf().getThumbsDir()
                        + File.separator
                        + uuid
                        + datasetPaths.get(i).replaceAll(
                                BltDataProcessor.H5_PATH_SEPARATOR, "_")
                        + BltDataProcessor.IMAGE_FILE_EXT;
                // generate image thumbs
                File thumb = new File(thumbPath);
                if (!thumb.exists()) {
                    int thumbWidth = (int) Math.round((double) xsize / 10);
                    int thumbHeight = (int) Math.round((double) ysize / 10);
                    bltDataProcessor.getH5Dataset(root, datasetPaths.get(i));
                    Dataset dataset = bltDataProcessor.getH5Dataset();
                    BufferedImage bi = bltDataProcessor.cart2Image(xsize,
                            ysize, noData, dataset, palette, thumbWidth,
                            thumbHeight, true);
                    bltDataProcessor.saveImageToFile(bi, thumbPath);
                }
            }
        } finally {
            // important: image generating method does not close the file
            bltDataProcessor.closeH5File(h5File);
        }
        Collections.sort(bltDatasets);
        model.addAttribute(DATASETS_KEY, bltDatasets);
        model.addAttribute(H5_OBJECT_KEY, object);
        model.addAttribute(FILE_KEY, bltFile);
    }

    /**
     * @param bltDataProcessor
     *            the bltDataProcessor to set
     */
    @Autowired
    public void setBltDataProcessor(BltDataProcessor bltDataProcessor) {
        this.bltDataProcessor = bltDataProcessor;
    }

    /**
     * @param bltFileManager
     *            the bltFileManager to set
     */
    @Autowired
    public void setBltFileManager(BltFileManager bltFileManager) {
        this.bltFileManager = bltFileManager;
    }

    /**
     * @param fileCatalog
     *            the fileCatalog to set
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
