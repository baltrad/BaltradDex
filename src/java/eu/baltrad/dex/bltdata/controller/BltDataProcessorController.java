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
import eu.baltrad.dex.util.ServletContextUtil;
import eu.baltrad.dex.log.model.MessageLogger;

import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.Dataset;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Data processor controller.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class BltDataProcessorController {
//---------------------------------------------------------------------------------------- Constants
    // path to color pallete file
    private static final String COLOR_PALETTE_FILE = "includes/color_palette.txt";
    // dataset paths key
    public static final String DATASET_PATHS_KEY = "dataset_paths";
    // group paths key
    public static final String GROUP_PATHS_KEY = "group_paths";
//---------------------------------------------------------------------------------------- Variables
    private BltDataProcessor bltDataProcessor;
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public BltDataProcessorController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Wrapper method creating image from a given file containing polar datasets.
     * 
     * @param h5FileName HDF5 data file's path
     * @param datasetPath Path pointing to a given dataset in HDF5 file
     * @param groupPath Path pointing to a given metadata group in HDF5 file
     * @param imageSize Output image size
     * @param rangeRingsDistance Distance between range rings
     * @param rangeMaskStroke Draw range mask with a given line stroke
     * @param rangeRingsColor Range rings color string
     * @param rangeMaskColor Range mask color String
     * @param imageFileName Output file name
     */
    public void polarH5Dataset2Image( String h5FileName, String datasetPath, String groupPath, int imageSize,
            short rangeRingsDistance, float rangeMaskStroke, String rangeRingsColor,
            String rangeMaskColor, String imageFileName ) {
        H5File h5File = bltDataProcessor.openH5File( h5FileName );
        try {
            Group root = bltDataProcessor.getH5Root( h5File );
            bltDataProcessor.getH5Dataset( root, datasetPath );
            Dataset dset = bltDataProcessor.getDataset();
            bltDataProcessor.getH5Attribute( root, groupPath, BltDataProcessor.H5_NBINS_ATTR );
            long nbins_val = ( Long )bltDataProcessor.getAttributeValue();
            bltDataProcessor.getH5Attribute( root, groupPath, BltDataProcessor.H5_NRAYS_ATTR );
            long nrays_val = ( Long )bltDataProcessor.getAttributeValue();
            bltDataProcessor.getH5Attribute( root, groupPath, BltDataProcessor.H5_A1GATE_ATTR );
            long a1gate_val = ( Long )bltDataProcessor.getAttributeValue();
            Color[] colorPalette = bltDataProcessor.createColorPalette(
                    ServletContextUtil.getServletContextPath() + COLOR_PALETTE_FILE );
            BufferedImage bi = bltDataProcessor.polarH5Dataset2Image( dset, nbins_val, nrays_val,
                    a1gate_val, imageSize, colorPalette, rangeRingsDistance, rangeMaskStroke,
                    rangeRingsColor, rangeMaskColor );
            bltDataProcessor.saveImageToFile( bi, imageFileName );
        } catch( ArrayIndexOutOfBoundsException e ) {
            log.error( "Failed to create image from polar dataset", e );
        } finally {
            bltDataProcessor.closeH5File( h5File );
        }
    }
    /**
     * Wrapper method creating image from a given radar data file containing polar datasets.
     * Warning: method does not close the file, so it has to be closed by explicit call
     * to the appropriate method from DataProcessor class.
     *
     * @param h5File Input HDF5 radar data file
     * @param datasetPath Path pointing to a given dataset in HDF5 file
     * @param groupPath Path pointing to a given metadata group in HDF5 file
     * @param imageSize Output image size
     * @param rangeRingsDistance Distance between range rings
     * @param rangeMaskStroke Draw range mask with a given line stroke
     * @param rangeRingsColor Range rings color string
     * @param rangeMaskColor Range mask color String
     * @param imageFileName Output file name
     */
    public void polarH5Dataset2Image( H5File h5File, String datasetPath, String groupPath, int imageSize,
            short rangeRingsDistance, float rangeMaskStroke, String rangeRingsColor,
            String rangeMaskColor, String imageFileName ) {
        try {
            Group root = bltDataProcessor.getH5Root( h5File );
            bltDataProcessor.getH5Dataset( root, datasetPath );
            Dataset dset = bltDataProcessor.getDataset();
            bltDataProcessor.getH5Attribute( root, groupPath, BltDataProcessor.H5_NBINS_ATTR );
            long nbins_val = ( Long )bltDataProcessor.getAttributeValue();
            bltDataProcessor.getH5Attribute( root, groupPath, BltDataProcessor.H5_NRAYS_ATTR );
            long nrays_val = ( Long )bltDataProcessor.getAttributeValue();
            bltDataProcessor.getH5Attribute( root, groupPath, BltDataProcessor.H5_A1GATE_ATTR );
            long a1gate_val = ( Long )bltDataProcessor.getAttributeValue();
            Color[] colorPalette = bltDataProcessor.createColorPalette(
                    ServletContextUtil.getServletContextPath() + COLOR_PALETTE_FILE );
            BufferedImage bi = bltDataProcessor.polarH5Dataset2Image( dset, nbins_val, nrays_val,
                    a1gate_val, imageSize, colorPalette, rangeRingsDistance, rangeMaskStroke,
                    rangeRingsColor, rangeMaskColor );
            bltDataProcessor.saveImageToFile( bi, imageFileName );
        } catch( ArrayIndexOutOfBoundsException e ) {
            log.error( "Failed to create image from polar dataset", e );
        }
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
     * @param dataProcessor BltDataProcessor object reference to set
     */
    public void setBltDataProcessor( BltDataProcessor bltDataProcessor ) {
        this.bltDataProcessor = bltDataProcessor;
    }
}
//--------------------------------------------------------------------------------------------------
