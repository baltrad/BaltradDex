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

package eu.baltrad.dex.db.util;

import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.Attribute;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
import java.util.ArrayList;

/**
 * Class implements radar data processing methods.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 0.1.6
 */
public class BltDataProcessor {
    
    public static final String H5_FILE_EXT = ".h5";
    public static final String IMAGE_FILE_EXT = ".png";
    
    public static final String H5_OBJECT_ATTR = "object";
    public static final String H5_QUANTITY_ATTR = "quantity";
    public static final String H5_NODATA_ATTR = "nodata";
    public static final String H5_UNDETECT_ATTR = "undetect";
    public static final String H5_OFFSET_ATTR = "offset";
    public static final String H5_GAIN_ATTR = "gain";
    public static final String H5_NBINS_ATTR = "nbins";
    public static final String H5_NRAYS_ATTR = "nrays";
    public static final String H5_RSCALE_ATTR = "rscale";
    public static final String H5_A1GATE_ATTR = "a1gate";
    public static final String H5_ELANGLE_ATTR = "elangle";
    public static final String H5_NODES_ATTR = "nodes";
    public static final String H5_PROJDEF_ATTR = "projdef";
    public static final String H5_LAT_0_ATTR = "lat";
    public static final String H5_LON_0_ATTR = "lon";
    public static final String H5_UL_LAT_ATTR = "UL_lat";
    public static final String H5_UL_LON_ATTR = "UL_lon";
    public static final String H5_UR_LAT_ATTR = "UR_lat";
    public static final String H5_UR_LON_ATTR = "UR_lon";        
    public static final String H5_LR_LAT_ATTR = "LR_lat";
    public static final String H5_LR_LON_ATTR = "UR_lon";
    public static final String H5_LL_LAT_ATTR = "LL_lat";
    public static final String H5_LL_LON_ATTR = "LL_lon";
    public static final String H5_XSIZE_ATTR = "xsize";
    public static final String H5_YSIZE_ATTR = "ysize";
    
    private static int COLOR_TABLE_DEPTH = 256;
    public static final String H5_PATH_SEPARATOR = "/";
    public static final String H5_WHERE_GROUP_PREFIX = "where";
    public static final String H5_WHAT_GROUP_PREFIX = "what";
    public static final String H5_HOW_GROUP_PREFIX = "how";
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
    
    public static final float DELTA_ANGLE = 0.004f;
    public static final int RANGE_RINGS_DISTANCE = 50;
    
    private Dataset h5Dataset;
    private Attribute h5Attribute;
    
    private final static Logger logger = LogManager.getLogger(BltDataProcessor.class);
    
    /**
     * Default constructor.
     */
    public BltDataProcessor() {}
    
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
        
        if (getH5Attribute().getType().getDatatypeClass() == Datatype.CLASS_INTEGER) {
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
        if (getH5Attribute().getType().getDatatypeClass() == Datatype.CLASS_FLOAT) {
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
    
    public BltAttribute findAttribute(Group root, String groupPath, String attributeName)  throws RuntimeException {
      BltAttribute attrResult = null;
      try {
        List members = root.getMemberList();
        for (int i = 0; i < members.size() && attrResult == null; i++) {
          if (members.get(i) instanceof Group) {
            Group group = (Group) members.get(i);
            String grpPath = group.getFullName();
            if (grpPath.equals(groupPath)) {   
              List metadata = group.getMetadata();
              for (int j = 0; j < metadata.size(); j++) {
                Attribute attr = (Attribute) metadata.get(j);
                if(attr.getName().equals(attributeName)) {
                  attrResult = new BltAttribute(attr);
                }
              }
            }
            if(group.getMemberList().size() > 0) {
              attrResult = findAttribute(group, groupPath, attributeName);
            }
          }
        }
      } catch (Exception e) {
        throw new RuntimeException("Failed to access attribute", e);
      }
      return attrResult;
    }
    
    protected byte[] convertShortToByte(short[] polarData, long nbins, double nodata, double undetect, double offset, double gain) {
      int i = 0;
      byte[] result = new byte[polarData.length];
      for (i = 0; i < result.length; i++) {
        if ((double)(polarData[i]) == nodata) {
          result[i] = (byte)255;
        } else if ((double)(polarData[i]) == undetect)  {
          result[i] = (byte)0;
        } else {
          double v = offset + ((double)polarData[i])*gain;
          double x = (v - (-30.0))/0.4;
          result[i] = (byte)x;
        }
      }
      return result;
    }

    protected byte[] convertIntToByte(int[] polarData, long nbins, double nodata, double undetect, double offset, double gain) {
      int i = 0;
      byte[] result = new byte[polarData.length];
      for (i = 0; i < result.length; i++) {
        if ((double)(polarData[i]) == nodata) {
          result[i] = (byte)255;
        } else if ((double)(polarData[i]) == undetect)  {
          result[i] = (byte)0;
        } else {
          double v = offset + polarData[i]*gain;
          double x = (v - (-30.0))/0.4;
          result[i] = (byte)x;
        }
      }
      return result;
    }

    
    /**
     * Transform polar dataset into Cartesian image.
     * @param nbins Number of range samples per ray
     * @param rscale Range sample size in meters
     * @param dataset Polar dataset
     * @param palette Color palette
     * @param outputImageSize Output image size
     * @param rangeRings Range rings toggle
     * @param rangeMask Range mask toggle
     * @return Cartesian image
     * @throws RuntimeException 
     */
    public BufferedImage polar2Image(long nbins, double rscale, double nodata, double undetect, double offset, double gain,
            Dataset dataset, Color[] palette, int outputImageSize, 
            boolean rangeRings, boolean rangeMask) throws RuntimeException {
        try {
            Object polarData = dataset.read();
            byte[] polar = null;
            if (polarData instanceof byte[]) {
              logger.info("polarData is byte[]");
              polar = (byte[])polarData;
            } else if (polarData instanceof short[]) {
              logger.info("polarData is short[]");
              polar = convertShortToByte((short[])polarData, nbins, nodata, undetect, offset, gain);
            } else if (polarData instanceof int[]) {
              logger.info("polarData is int[]");
              polar = convertIntToByte((int[])polarData, nbins, nodata, undetect, offset, gain);
            }
            //byte[] polar = (byte[]) dataset.read();
            int radius = (int) nbins;
            int ray = 0;
            
            BufferedImage bi = new BufferedImage(radius * 2, radius * 2,
                    BufferedImage.TYPE_INT_ARGB);
            int rule = AlphaComposite.SRC;
            Graphics2D g2d = bi.createGraphics();
            
            for (int i = 0; i < polar.length; i++) {
                if (i % radius == 0) {
                    ray++;
                    
                    double alpha = ray * (Math.PI / 180);
                    // rotate 90 deg counter clockwise
                    alpha -= Math.PI / 2;
                    double a1 = alpha - 2 * DELTA_ANGLE;
                    double a2 = alpha - DELTA_ANGLE;
                    double a3 = alpha + DELTA_ANGLE;
                    double a4 = alpha + 2 * DELTA_ANGLE;
                    
                    double[] alphas = {alpha, a1, a2, a3, a4};
                    
                    for (int angle = 0; angle < alphas.length; angle++) {
                        for (int j = 0; j < radius; j++) {
                            double x = j * Math.cos(alphas[angle]) + radius;
                            double y = j * Math.sin(alphas[angle]) + radius;
                            int xc = (int) Math.round(x);
                            int yc = (int) Math.round(y);

                            int byteVal = (0xFF & ((int) polar[i + j]));
                            short val = (short) byteVal;
                            
                            if (val > 0) {
                                AlphaComposite ac = 
                                        AlphaComposite.getInstance(rule, 1);
                                g2d.setComposite(ac);
                                Color color = 
                                        palette[(COLOR_TABLE_DEPTH - 1) - val];
                                g2d.setColor(color);
                            } else {
                                AlphaComposite ac = 
                                        AlphaComposite.getInstance(rule, 0);
                                g2d.setComposite(ac);
                                g2d.setColor(Color.WHITE);
                            }
                            g2d.drawRect(xc, yc, 1, 1);
                        }       
                    }
                }   
            }
            
            // range bins per kilometer
            double binsPerKm = 1000 / rscale;
            int dist = (int) Math.round(RANGE_RINGS_DISTANCE * binsPerKm);
            
            // draw range rings
            if (rangeRings) {
                AlphaComposite ac = AlphaComposite.getInstance(rule, 1);
                g2d.setComposite(ac);
                g2d.setColor(Color.decode("#A7A7A7"));
                for (int i = 1; i <= radius; i++) {
                    if (i % dist == 0) {
                        g2d.drawOval(radius - i, radius - i, 2 * i, 2 * i);
                    }
                }
            }    
            
            // draw range mask
            if (rangeMask) {
                AlphaComposite ac = AlphaComposite.getInstance(rule, 0.1f);
                g2d.setComposite(ac);
                g2d.setColor(Color.decode("#000000"));
                for (int x = 0; x < bi.getWidth(); x++) {
                    for (int y = 0; y < bi.getHeight(); y++) {
                        int dx = Math.abs(radius - x);
                        int dy = Math.abs(radius - y);
                        if (Math.sqrt(dx * dx + dy * dy) > radius) {
                            g2d.drawRect(x, y, 1, 1);
                        }   
                    }
                }
            }
            
            // range in kilometers
            int range = (int) Math.round(nbins * rscale / 1000);
            if (outputImageSize == 0) {
                outputImageSize = 2 * range;
            }
            
            // get image scaled according to range
            Image img = bi.getScaledInstance(outputImageSize, outputImageSize, 
                    BufferedImage.SCALE_SMOOTH);
            BufferedImage scaledImage = new BufferedImage(outputImageSize, 
                    outputImageSize, 
                    BufferedImage.TYPE_INT_ARGB);
            g2d = scaledImage.createGraphics();
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();
            return scaledImage;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate image " + 
                    "from polar dataset", e);
        }
    }
    
    /**
     * Transform Cartesian dataset into image.
     * @param width Dataset width
     * @param height Dataset height
     * @param noData Out of range areas
     * @param dataset Dataset
     * @param palette Color palette
     * @param outputImageWidth Output image width 
     * @param outputImageHeight Output image height 
     * @return Cartesian image
     * @throws RuntimeException 
     */
    public BufferedImage cart2Image(long width, long height, double noData, 
            Dataset dataset, Color[] palette, int outputImageWidth, 
            int outputImageHeight, boolean rangeMask) throws RuntimeException {
        try {
            int imageWidth = (int) width;
            int imageHeight = (int) height;
            byte[] comp = (byte[]) dataset.read();

            BufferedImage bi = new BufferedImage(imageWidth, imageHeight,
                    BufferedImage.TYPE_INT_ARGB);
            int rule = AlphaComposite.SRC;
            Graphics2D g2d = bi.createGraphics();
            int x = 0;
            int y = 0;
            for (int i = 0; i < comp.length; i++) {
                if (i > 0 && i % imageWidth == 0) {
                    x++;
                }
                y = i - x * imageWidth;
                int byteVal = (0xFF & ((int) comp[i]));
                short val = (short) byteVal;
                if (val > 0) {
                    AlphaComposite ac = 
                            AlphaComposite.getInstance(rule, 1);
                    g2d.setComposite(ac);
                    Color color = 
                            palette[(COLOR_TABLE_DEPTH - 1) - val];
                    g2d.setColor(color);
                } else {
                    AlphaComposite ac = 
                            AlphaComposite.getInstance(rule, 0);
                    g2d.setComposite(ac);
                    g2d.setColor(Color.WHITE);
                }
                
                if (val == noData && rangeMask) {
                    AlphaComposite ac = AlphaComposite.getInstance(rule, 0.1f);
                    g2d.setComposite(ac);
                    g2d.setColor(Color.decode("#000000"));
                }
                if (val == noData && !rangeMask) {
                    AlphaComposite ac = AlphaComposite.getInstance(rule, 0.0f);
                    g2d.setComposite(ac);    
                }
                g2d.drawRect(y, x, 1, 1);   
            }
            
            if (outputImageWidth == 0) {
                outputImageWidth = imageWidth;
            }
            if (outputImageHeight == 0) {
                outputImageHeight = imageHeight;
            }
            
            // get scaled image
            Image img = bi.getScaledInstance(outputImageWidth, 
                    outputImageHeight, BufferedImage.SCALE_SMOOTH);
            BufferedImage scaledImage = new BufferedImage(outputImageWidth, 
                    outputImageHeight, BufferedImage.TYPE_INT_ARGB);
            g2d = scaledImage.createGraphics();
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();
            return scaledImage;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate image " + 
                    "from cartesian composite", e);
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
    
    /**
     * Interpolate image using arithmetic average calculated in 5x5 filter 
     * @param image Input image
     *
    public void interpolate(BufferedImage image) {
        for (int x = 2; x < image.getWidth() - 2; x++) {
            for (int y = 2; y < image.getHeight() - 2; y++) {
                int[] argb = extractRGB(image.getRGB(x, y));
                // interpolate with 5x5 filter for no-data pixels only
                if (argb[0] == 0) {
                    List<Integer> filter = new ArrayList<Integer>();
                    for (int wx = x - 2; wx <= x + 2; wx++) {
                        for (int wy = y - 2; wy <= y + 2; wy++) {
                            argb = extractRGB(image.getRGB(wx, wy));
                            if (argb[0] != 0) {
                                filter.add(image.getRGB(wx, wy));
                            }
                        }
                    }
                    // calculate mean for each color component separately
                    if (filter.size() > 12) {
                        int r = 0;
                        int g = 0;
                        int b = 0;
                        for (int i = 0; i < filter.size(); i++) {
                            argb = extractRGB(filter.get(i));
                            r += argb[1];
                            g += argb[2];
                            b += argb[3];
                        }
                        r /= filter.size();
                        g /= filter.size();
                        b /= filter.size();
                        int rgb = new Color(r, g, b).getRGB();
                        image.setRGB(x, y, rgb);
                    }
                }   
            }
        }
    }*/
    
    
    /**
     * Extract color component information from integer ARGB value.
     * @param px ARGB pixel value
     * @return ARGB color component array
     *
    private int[] extractRGB(int px) {
        int argb[] = new int[4];
        argb[0] = (px >> 24) & 0xFF;
        argb[1] = (px >> 16) & 0xFF;
        argb[2] = (px >> 8) & 0xFF;
        argb[3] = px & 0xFF;
        return argb;
    }*/
    
}

