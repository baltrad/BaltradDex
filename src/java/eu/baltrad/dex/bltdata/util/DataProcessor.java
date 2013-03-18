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

package eu.baltrad.dex.bltdata.util;

import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.Attribute;

import org.apache.log4j.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;

import java.util.List;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Class implements radar data processing methods.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 0.1.6
 */
public class DataProcessor {
    
    public static final String H5_FILE_EXT = ".h5";
    public static final String IMAGE_FILE_EXT = ".png";
    public static final String H5_QUANTITY_ATTR = "quantity";
    public static final String H5_NBINS_ATTR = "nbins";
    public static final String H5_NRAYS_ATTR = "nrays";
    public static final String H5_RSCALE_ATTR = "rscale";
    public static final String H5_A1GATE_ATTR = "a1gate";
    public static final String H5_ELANGLE_ATTR = "elangle";
    public static final String H5_LAT_0_ATTR = "lat";
    public static final String H5_LON_0_ATTR = "lon";
    private static int COLOR_TABLE_DEPTH = 256;
    public static final String H5_PATH_SEPARATOR = "/";
    public static final String H5_WHERE_GROUP_PREFIX = "where";
    public static final String H5_WHAT_GROUP_PREFIX = "what";
    public static final String H5_DATASET_PREFIX = "dataset";
    
    public static final String DATASET_PATHS_KEY = "dataset_paths";
    public static final String GROUP_PATHS_KEY = "group_paths";
    
    // HDF5 data object types
    public static final String ODIMH5_PVOL_OBJ = "PVOL"; 
    public static final String ODIMH5_CVOL_OBJ = "CVOL"; 
    public static final String ODIMH5_SCAN_OBJ = "SCAN"; 
    public static final String ODIMH5_RAY_OBJ = "RAY"; 
    public static final String ODIMH5_AZIM_OBJ = "AZIM"; 
    public static final String ODIMH5_IMAGE_OBJ = "IMAGE"; 
    public static final String ODIMH5_COMP_OBJ = "COMP"; 
    public static final String ODIMH5_XSEC_OBJ = "XSEC"; 
    public static final String ODIMH5_VP_OBJ = "VP"; 
    public static final String ODIMH5_PIC_OBJ = "PIC";
    
    private Dataset h5Dataset;
    private Attribute h5Attribute;
    
    /**
     * Default constructor.
     */
    public DataProcessor() {}
    
    /**
     * Get HDF5 dataset object.
     * @return Dataset object
     */
    public Dataset getH5Dataset() {
        return h5Dataset;
    }
    
    /**
     * Get HDF5 attribute object.
     * @return Attribute object
     */
    public Attribute getH5Attribute() {
        return h5Attribute;
    }
    
    /**
     * Open HDF5 file.
     * @param name File name
     * @return H5File object if successful
     * @throws Runtime exception
     */
    public H5File openH5File(String fileName) throws RuntimeException {
        try {
            FileFormat fileFormat = FileFormat
                .getFileFormat(FileFormat.FILE_TYPE_HDF5);
            H5File h5File = (H5File) fileFormat.createInstance(fileName, 
                    FileFormat.READ);
            h5File.open();
            return h5File;
        } catch (Exception e) {
            throw new RuntimeException("Failed to open H5 file", e);
        }
    }
    
    /**
     * Close HDF5 file.
     * @param h5File H5File object
     * @throws Runtime exception 
     */
    public void closeH5File(H5File h5File) throws RuntimeException {
        try {
            h5File.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to close H5 file", e);
        }
    }
    
    /**
     * Gets reference to HDF5 file's root group.
     * @param h5File HDF5 file object
     * @return HDF5 file's root group
     * @throws Runtime exception
     */
    public Group getH5Root(H5File h5File) throws RuntimeException {
        try {
            Group root = (Group) ((DefaultMutableTreeNode) h5File.getRootNode())
                    .getUserObject();
            return root;
        } catch (Exception e) {
            throw new RuntimeException("Failed to access H5 file's root", e);
        }
    }
    
    /**
     * Parses HDF5 file.
     * @param root HDF5 file's root group
     * @param paths Dataset paths
     * @throws Runtime exception
     */
    public void getH5DatasetPaths(Group root, List<String> paths) 
                                                    throws RuntimeException {
        try {
            List members = root.getMemberList();
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i) instanceof Group) {
                    Group group = (Group) members.get(i);
                    if (group.getMemberList().size() > 0) {
                        getH5DatasetPaths(group, paths);
                    }
                } else if (members.get(i) instanceof Dataset) {
                    Dataset dset = (Dataset) members.get(i);
                    paths.add(dset.getFullName());
                } 
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get datasource paths", e);
        }
    }
    
    /**
     * Seeks for a given dataset in HDF5 file
     * @param root HDF5 file's root group
     * @param path Dataset path
     * @throws Runtime exception 
     */
    public void getH5Dataset(Group root, String path) throws RuntimeException {
        try {
            List members = root. getMemberList();
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i) instanceof Group) {
                    Group group = (Group) members.get(i);
                    if (group.getMemberList().size() > 0) {
                        getH5Dataset(group, path);
                    }
                } else if (members.get(i) instanceof Dataset) {
                    Dataset dset = (Dataset) members.get(i);
                    if (dset.getFullName().equals(path)) {
                        h5Dataset = dset;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to access dataset", e);
        }
    }
    
    /**
     * Gets HDF5 attribute identified by path and name.
     * @param root HDF5 file's root group
     * @param groupPath Path pointing to a given group
     * @param attributeName Attribute's name
     * @throws Runtime exception
     */
    public void getH5Attribute(Group root, String groupPath, 
                                String attributeName) throws RuntimeException {
        try {
            List members = root.getMemberList();
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i) instanceof Group) {
                    Group group = (Group) members.get(i);
                    String grpPath = group.getFullName();
                    if (grpPath.equals(groupPath)) {   
                        List metadata = group.getMetadata();
                        for (int j = 0; j < metadata.size(); j++) {
                            Attribute attr = (Attribute) metadata.get(j);
                            if(attr.getName().equals(attributeName)) {
                                h5Attribute = attr;
                            }
                        }
                    }
                    if(group.getMemberList().size() > 0) {
                        getH5Attribute(group, groupPath, attributeName);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to access attribute", e);
        }
    }
    
    /**
     * Gets attribute value.
     * @return Attribute value
     */
    public Object getH5AttributeValue() {
        Object value = null;
        if (getH5Attribute().getType().getDatatypeClass() == 
                Datatype.CLASS_INTEGER) {
            try {
                int attrInt[] = (int[]) getH5Attribute().getValue();
                Integer i = attrInt[0];
                long l = i.longValue();
                value = l;
            } catch (ClassCastException e) {
                long attrLong[] = (long[]) getH5Attribute().getValue();
                Long l = attrLong[0];
                value = l;
            }
        }
        if (getH5Attribute().getType().getDatatypeClass() == 
                Datatype.CLASS_FLOAT) {
            try {
                float attrFloat[] = (float[]) getH5Attribute().getValue();
                Float f = attrFloat[0];
                double d = f.doubleValue();
                value = d;
            } catch (ClassCastException e) {
                double attrDouble[] = (double[]) getH5Attribute().getValue();
                Double d = attrDouble[0];
                value = d;
            }
        }
        if (getH5Attribute().getType().getDatatypeClass() == 
                Datatype.CLASS_STRING ) {
            String attrString[] = (String[]) getH5Attribute().getValue();
            value = attrString[0];
        }
        return value;
    }
    
    /**
     * Convert polar dataset into cartesian image.
     * @param nbins Number of range bins
     * @param dataset Polar dataset to convert
     * @param palette Color palette
     * @param imageSize Output image size
     * @param rangeRingsDistance Range rings distance 
     * @param rangeMaskStroke Range mask stroke
     * @param rangeRingsColor Range rings color
     * @param rangeMaskColor Range mask color
     * @return Buffered image
     * @throws RuntimeException 
     */
    public BufferedImage polarDataset2CartImage(long nbins, Dataset dataset,
            Color[] palette, int imageSize, int rangeRingsDistance, 
            float rangeMaskStroke, String rangeRingsColor, 
            String rangeMaskColor) throws RuntimeException {
        try {
            int radius = (int) nbins;
            byte[] polar = (byte[]) dataset.read();

            BufferedImage bi = new BufferedImage(radius * 2, radius * 2, 
                    BufferedImage.TYPE_INT_ARGB);
            int rule = AlphaComposite.SRC;
            Graphics2D g2d = bi.createGraphics();

            // draw radar image
            for (int i = 0; i < polar.length; i++) {
                int ray;
                int bin;
                ray = (int) Math.floor(i / radius);
                bin = i - (radius * ray);
                double alpha = ray * (Math.PI / 180);
                // rotate 90 deg counter-clockwise
                alpha -= Math.PI / 2;
                double x = bin * Math.cos(alpha) + radius;
                double y = bin * Math.sin(alpha) + radius;
                int xc = (int) Math.floor(x);
                int yc = (int) Math.floor(y);
                int val =  (int) polar[i];
                // in case value is less than zero
                if (val < 0) {
                    val = -val;
                }
                Color color = palette[(COLOR_TABLE_DEPTH - 1) - val];
                if (val > 0) {
                    AlphaComposite ac = AlphaComposite.getInstance(rule, 1);
                    g2d.setComposite(ac);
                    g2d.setColor(color);
                } else {
                    AlphaComposite ac = AlphaComposite.getInstance(rule, 0);
                    g2d.setComposite(ac);
                    g2d.setColor(Color.WHITE);
                }
                g2d.drawRect(xc, yc, 1, 1);
                // fill empty spaces by increasing ray angle a bit
                alpha += 0.006f;
                x = bin * Math.cos(alpha) + radius;
                y = bin * Math.sin(alpha) + radius;
                xc = (int) Math.floor(x);
                yc = (int) Math.floor(y);
                g2d.drawRect(xc, yc, 1, 1);
            }
            // draw range rings
            if (rangeRingsDistance > 0) {
                AlphaComposite ac = AlphaComposite.getInstance(rule, 1);
                g2d.setComposite(ac);
                g2d.setColor(Color.decode(rangeRingsColor));
                for (int i = 1; i <= radius; i++) {
                    if (i % rangeRingsDistance == 0) {
                        g2d.drawOval(radius - i, radius - i, 2 * i, 2 * i);
                    }
                }
            }
            // draw range mask
            if (rangeMaskStroke > 0) {
                AlphaComposite ac = AlphaComposite.getInstance(rule, 1);
                g2d.setComposite(ac);
                g2d.setColor(Color.decode(rangeMaskColor));
                BasicStroke stroke = new BasicStroke(rangeMaskStroke);
                g2d.setStroke(stroke);
                g2d.drawOval(0, 0, radius * 2, radius * 2);
            }
            // get scaled image
            Image img = bi.getScaledInstance(imageSize, imageSize, 
                    BufferedImage.SCALE_SMOOTH);
            BufferedImage scaledImage = new BufferedImage(imageSize, imageSize, 
                    BufferedImage.TYPE_INT_ARGB);
            g2d = scaledImage.createGraphics();
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();
            return scaledImage;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert polar dataset to " +
                    "image", e);
        }
    }
    
    /**
     * Creates color palette from file.
     * @param fileName Absolute path to palette file
     * @return palette Color palette
     * @throws Runtime exception
     */
    public Color[] createColorPalette(String fileName) throws RuntimeException {
        try {
            File fin = new File(fileName);
            int[] rgbV = new int[3];
            BufferedReader br;
            Color[] palette = new Color[COLOR_TABLE_DEPTH];
            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fin)));
            for( int i = 0; i < 256; i++ ) {
                String s = br.readLine();
                String[] rgbS = s.split(" ");
                rgbV[0] = Integer.parseInt(rgbS[0]);
                rgbV[1] = Integer.parseInt(rgbS[1]);
                rgbV[2] = Integer.parseInt(rgbS[2]);
                palette[COLOR_TABLE_DEPTH - (i + 1)] = 
                        new Color(rgbV[0], rgbV[1], rgbV[2]);
            }
            br.close();
            return palette;
        } catch(Exception e) {
            throw new RuntimeException("Failed to create color palette", e);
        }
    }
    
    /**
     * Saves product image to PNG file.
     * @param image Source image
     * @param fileName Output file name
     * @return True upon success
     * @throws Runtime exception
     */
    public boolean saveImageToFile(BufferedImage image, String fileName) 
            throws RuntimeException {
        try {
            Graphics2D g = image.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            File f = new File(fileName);
            return ImageIO.write(image, "png", f);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image to file", e);
        }
    } 
    
}

