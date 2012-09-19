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

package eu.baltrad.dex.bltdata.model;

import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Class implements radar data processing methods.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class BltDataProcessor {
//---------------------------------------------------------------------------------------- Constants
    // HDF5 file extension
    public static final String H5_FILE_EXT = ".h5";
    // image file extension
    public static final String IMAGE_FILE_EXT = ".png";
    // data quantity
    public static final String H5_QUANTITY_ATTR = "quantity";
    // number of bis in the dataset
    public static final String H5_NBINS_ATTR = "nbins";
    // number of rays in the dataset
    public static final String H5_NRAYS_ATTR = "nrays";
    // bin resolution in meters
    public static final String H5_RSCALE_ATTR = "rscale";
    // index of the first ray in the dataset
    public static final String H5_A1GATE_ATTR = "a1gate";
    // antenna elevation angle
    public static final String H5_ELANGLE_ATTR = "elangle";
    // radar location latitude
    public static final String H5_LAT_0_ATTR = "lat";
     // radar location longitude
    public static final String H5_LON_0_ATTR = "lon";
    // color table resolution
    private static int COLOR_TABLE_DEPTH = 256;
    // HDF5 path separator
    public static final String H5_PATH_SEPARATOR = "/";
    // HDF5 WHERE group prefix
    public static final String H5_WHERE_GROUP_PREFIX = "where";
    // HDF5 WHAT group prefix
    public static final String H5_WHAT_GROUP_PREFIX = "what";
    // HDF5 DATASET prefix
    public static final String H5_DATASET_PREFIX = "dataset";
    // HDF5 data object types
    public static final String ODIMH5_PVOL_OBJ = "PVOL"; // Polar volume
    public static final String ODIMH5_CVOL_OBJ = "CVOL"; // Cartesian volume
    public static final String ODIMH5_SCAN_OBJ = "SCAN"; // Polar scan
    public static final String ODIMH5_RAY_OBJ = "RAY"; // Single polar ray
    public static final String ODIMH5_AZIM_OBJ = "AZIM"; // Azimuthal object
    public static final String ODIMH5_IMAGE_OBJ = "IMAGE"; // 2-D cartesian image
    public static final String ODIMH5_COMP_OBJ = "COMP"; // Cartesian composite image(s)
    public static final String ODIMH5_XSEC_OBJ = "XSEC"; // 2-D vertical cross section(s)
    public static final String ODIMH5_VP_OBJ = "VP"; // 1-D vertical profile
    public static final String ODIMH5_PIC_OBJ = "PIC"; // Embedded graphical image
//---------------------------------------------------------------------------------------- Variables
    /* Message logger */
    private Logger log;
    /* Used to store HDF5 dataset */
    private Dataset dataset;
    /* Used to store HDF5 attribute */
    private Attribute attribute;
    /* List of full dataset names */
    private List<String> datasetFullNames = new ArrayList<String>();
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public BltDataProcessor() {
        this.log = Logger.getLogger("DEX");
    }
    /**
     * Opens HDF5 file.
     *
     * @param name File name
     * @return H5File object if successful
     */
    public H5File openH5File( String fileName ) {
        FileFormat fileFormat = FileFormat.getFileFormat( FileFormat.FILE_TYPE_HDF5 );
        H5File h5File = null;
        try {
            h5File = ( H5File )fileFormat.createInstance( fileName, FileFormat.READ );
            h5File.open();
        } catch( Exception e ) {
            log.error( "Exception while opening HDF5 file", e );
            e.printStackTrace();
        }
        return h5File;
    }
    /**
     * Closes HDF5 file.
     *
     * @param h5File H5File object
     * @return 0 if successful, 1 otherwise
     */
    public int closeH5File( H5File h5File ) {
        int res;
        try {
            h5File.close();
            res = 0;
        } catch( HDF5Exception hdf5e ) {
            res = 1;
            log.error( "Error while closing HDF5 file", hdf5e );
        }
        return res;
    }
    /**
     * Gets reference to HDF5 file's root group.
     *
     * @param h5File HDF5 file object
     * @return HDF5 file's root group
     */
    public Group getH5Root( H5File h5File ) {
        Group root = null;
        try {
            root = ( Group )( ( DefaultMutableTreeNode )h5File.getRootNode() ).getUserObject();
        } catch( Exception e ) {
            log.error( "Error while accessing HDF5 file's root", e );
        }
        return root;
    }
    /**
     * Parses HDF5 file, recognized dataset elements and retrieves their full names.
     *
     * @param root HDF5 file's root group
     */
    public void getH5Datasets( Group root ) {
        List members = root.getMemberList();
        for( int i=0; i< members.size(); i++ ) {
            if( members.get( i ) instanceof Group ) {
                Group group = ( Group )members.get( i );
                if( group.getMemberList().size() > 0 ) {
                    getH5Datasets( group );
                }
            } else if( members.get( i ) instanceof Dataset ) {
                Dataset dset = ( Dataset )members.get( i );
                getDatasetFullNames().add( dset.getFullName() );
            } else {
                log.error( "Get H5 datasets: Unrecognized HDF5 object" );
            }
        }
    }
    /**
     * Parses HDF5 file, recognized group elements and retrieves full names of groups
     * matching a name given as parameter.
     * 
     * @param root HDF5 file's root group
     * @param groupName Name of the group to retrieve
     * @param prefix Name prefix / parent group name
     *
    public void getH5Groups( Group root, String groupName, String prefix ) {
        List members = root.getMemberList();
        for( int i = 0; i < members.size(); i++ ) {
            if( members.get( i ) instanceof Group ) {
                Group group = ( Group )members.get( i );
                String fullGroupName = group.getFullName();
                if( fullGroupName.startsWith( prefix )
                        && fullGroupName.endsWith( groupName ) ) {
                    getGroupFullNames().add( group.getFullName() );
                }
                if( group.getMemberList().size() > 0 ) {
                    getH5Groups( group, groupName, prefix );
                }
            } else {
                if( !( members.get( i ) instanceof Dataset ) ) {
                    System.err.println( "Get H5 groups: Unrecognized HDF5 object" );
                }
            }
        }
    }*/
    /**
     * Seeks for a given dataset in HDF5 file
     *
     * @param root HDF5 file's root group
     * @param datasetPath Path pointing to a given dataset
     */
    public void getH5Dataset( Group root, String datasetPath  ) {
        List members = root.getMemberList();
        for( int i=0; i< members.size(); i++ ) {
            if( members.get( i ) instanceof Group ) {
                Group group = ( Group )members.get( i );
                if( group.getMemberList().size() > 0 ) {
                    getH5Dataset( group, datasetPath );
                }
            } else if( members.get(i) instanceof Dataset ) {
                Dataset dset = ( Dataset )members.get( i );
                if( dset.getFullName().equals( datasetPath ) ) {
                    setDataset( dset );
                }
            } else {
                log.error( "Get H5 dataset: Unrecognized HDF5 object" );
            }
        }
    }
    /**
     * Gets HDF5 attribute identified by path and name.
     *
     * @param root HDF5 file's root group
     * @param groupPath Path pointing to a given group
     * @param attributeName Attribute's name
     */
    public void getH5Attribute( Group root, String groupPath, String attributeName ) {
        List members = root.getMemberList();
        for( int i=0; i< members.size(); i++ ) {
            if( members.get( i ) instanceof Group ) {
                Group group = ( Group )members.get( i );
                // group is identified by path and name
                String grpPath = group.getFullName();
                if( grpPath.equals( groupPath ) ) {
                    try {
                        List metadata = group.getMetadata();
                        for( int j = 0; j < metadata.size(); j++ ) {
                            Attribute attr = ( Attribute )metadata.get( j );
                            if( attr.getName().equals( attributeName ) ) {
                                setAttribute( attr );
                            }
                        }
                    } catch( Exception e ){
                        log.error( "Get HDF5 attribute failed to get metadata", e );
                    }
                }
                if( group.getMemberList().size() > 0 ) {
                    getH5Attribute( group, groupPath, attributeName );
                }
            }
        }
    }
    /**
     * Converts polar HDF5 dataset into cartesian image. The output image is scaled to the
     * desired size.
     * 
     * @param dataset Polar HDF5 dataset
     * @param nbins Number of bins in a ray
     * @param nrays Number of rays in the dataset
     * @param a1gate Index of the first ray in the dataset
     * @param size Size of the scaled image
     * @param colorPalette Color palette
     * @param rangeRingsDistance Number of bins between the rings, rings are drawn if range is > 0
     * @param rangeMaskStroke Range mask drawn with a given line stroke if > 0
     * @param rangeRingsColor Range rings color string
     * @param rangeMaskColor Range mask color String
     * @return Scaled image
     * @throws ArrayIndexOutOfBoundsException
     */
    public BufferedImage polarH5Dataset2Image( Dataset dataset, long nbins, long nrays, long a1gate,
            int size, Color[] colorPalette, short rangeRingsDistance, float rangeMaskStroke,
            String rangeRingsColor, String rangeMaskColor ) throws ArrayIndexOutOfBoundsException {
        byte[] buff = null;
        try {
            buff = ( byte[] )dataset.read();
        } catch( Exception e ) {
            log.error( "Failed to convert polar HDF5 dataset to image", e );
        }
        // radar range is determined based on the number of bins
        int range = ( int )nbins;
        BufferedImage bi = new BufferedImage( 2 * range, 2 * range, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2d = bi.createGraphics();
        int rule = AlphaComposite.SRC;
        for( int row = 0; row < 2 * range; row++ ) {
            for( int col = 0; col < 2 * range; col++ ) {
                //int x = 250 - col;
                //int y = row - 250;
                int x = range - col;
                int y = row - range;
                int r = Math.round( ( float )Math.sqrt( ( double )( x * x + y * y ) ) );
                if( r > nbins ) {
                    AlphaComposite ac = AlphaComposite.getInstance( rule, 0 );
                    g2d.setComposite( ac );
                    g2d.setColor( Color.WHITE );
                } else {
                    float beta = getBeta( x, y );
                    int index = getRayIndex( beta, a1gate );
                    int pixel = -999999;
                    if( buff != null ) {
                        long lpx = index * nbins + r;
                        int ipx = ( int )lpx;
                        if( ipx == buff.length ) {
                            ipx -= 1;
                        }
                        pixel = buff[ ipx ];
                    }
                    // we get out of color table bounds if we don't change pixel sign
                    if( pixel < 0 ) {
                        pixel = -pixel;
                    }
                    Color c = colorPalette[ ( COLOR_TABLE_DEPTH - 1 ) - pixel ];
                    if( pixel != 0 ) {
                        AlphaComposite ac = AlphaComposite.getInstance( rule, 1 );
                        g2d.setComposite( ac );
                        g2d.setColor( c );
                    } else {
                        AlphaComposite ac = AlphaComposite.getInstance( rule, 0 );
                        g2d.setComposite( ac );
                        g2d.setColor( Color.WHITE );
                    }
                }
                g2d.drawLine( row, col, row + 1, col );
            }
        }
        // draw range rings
        if( rangeRingsDistance > 0 ) {
            AlphaComposite ac = AlphaComposite.getInstance( rule, 1 );
            g2d.setComposite( ac );
            g2d.setColor( Color.decode( rangeRingsColor ) );
            for( int i = 1; i <= range; i++ ) {
                if( i % rangeRingsDistance == 0 ) {
                    g2d.drawOval( range - i, range - i, 2 * i, 2 * i );
                }
            }
        }
        // draw range mask with a given line stroke
        if( rangeMaskStroke > 0 ) {
            AlphaComposite ac = AlphaComposite.getInstance( rule, 1 );
            g2d.setComposite( ac );
            g2d.setColor( Color.decode( rangeMaskColor ) );
            BasicStroke stroke = new BasicStroke( rangeMaskStroke );
            g2d.setStroke( stroke );
            g2d.drawOval( 0, 0, ( 2 * range ), ( 2 * range ) );
        }
        Image img = bi.getScaledInstance( size, size, BufferedImage.SCALE_SMOOTH );
        // scaled image
        BufferedImage scaledImage = new BufferedImage( size, size, BufferedImage.TYPE_INT_ARGB );
        g2d = scaledImage.createGraphics();
        g2d.drawImage( img, 0, 0, null);
        g2d.dispose();
        return scaledImage;
    }
    /**
     * Gets alpha angle
     *
     * @param beta
     * @return Alpha angle
     */
    public float getAlpha( float beta ) {
        float alpha = 90 + beta;
        if( alpha >= 360 ) {
            alpha -= 360;
        }
        return alpha;
    }
    /**
     * Gets beta angle
     *
     * @param x
     * @param y
     * @return Beta angle
     */
    public float getBeta( float x, float y ) {
        float eps = 0.0001f;
        float beta = 90;
        if( Math.abs(x) > eps ) {
            beta = ( float )( 180.0 * Math.atan( y / x ) / Math.PI );
            if( y == 0 ) {
                beta = ( x >= 0 ) ? 0 : 180;
            } else {
                if( beta < 0 ) beta += 180;
                if( y < 0 ) beta += 180;
            }
        } else {
            if( Math.abs( y ) < eps ) return Float.NaN;
            if( y < 0 ) beta = 270;
        }
        return beta;
    }
    /**
     * Gets ray index.
     *
     * @param alpha Alpha angle
     * @param a1gate Index of the first ray
     * @return Ray index
     */
    int getRayIndex( float alpha, long a1gate ) {
        float a = alpha + a1gate;
        if( a >= 360 ) {
            a -= 360;
        }
        return ( int )a;
    }
    /**
     * Creates color palette from file.
     *
     * @param paletteFile Absolute path to palette file
     * @return palette Color palette
     */
    public Color[ ] createColorPalette( String paletteFile ) {
        File fin = new File( paletteFile );
        String[ ] rgbS = new String[ 3 ];
        int[ ] rgbV = new int[ 3 ];
        BufferedReader br;
        String s = new String();
        Color[ ] palette = new Color[ COLOR_TABLE_DEPTH ];
        try {
            br = new BufferedReader( new InputStreamReader( new FileInputStream( fin ) ) );
            for( int i = 0; i < 256; i++ ) {
                s = br.readLine();
                rgbS = s.split( " " );
                rgbV[ 0 ] = Integer.parseInt( rgbS[ 0 ] );
                rgbV[ 1 ] = Integer.parseInt( rgbS[ 1 ] );
                rgbV[ 2 ] = Integer.parseInt( rgbS[ 2 ] );
                palette[ 256 - ( i + 1 ) ] = new Color( rgbV[ 0 ], rgbV[ 1 ], rgbV[ 2 ] );
            }
            br.close();
        } catch( IOException e ) {
            log.error( "Failed to create color palette", e );
        }
	return palette;
    }
    /**
     * Saves product image as PNG file.
     *
     * @param image Source image
     * @param fileName Output file name
     */
    public int saveImageToFile( BufferedImage image, String fileName ) {
        int res;
        Graphics2D g = image.createGraphics();
        g.drawImage( image, 0, 0, null );
        g.dispose();
        try {
            File f = new File( fileName );
            ImageIO.write( image, "png", f );
            res = 0;
        } catch( IOException e ) {
            log.error( "Failed to save image to file", e );
            res = 1;
        }
        return res;
    }
    /**
     * Gets dataset retrieved from HDF5 file.
     *
     * @return Dataset retrieved from HDF5 file
     */
    public Dataset getDataset() { return dataset; }
    /**
     * Sets dataset retrieved from HDF5 file.
     * 
     * @param _dataset Dataset retrieved from HDF5 file
     */
    public void setDataset( Dataset dataset) { this.dataset = dataset; }
    /**
     * Gets current HDF5 attribute.
     * 
     * @return HDF5 attribute
     */
    public Attribute getAttribute() { return this.attribute; }
    /**
     * Sets current HDF5 attribute. 
     * 
     * @param attribute Current HDF5 attribute
     */
    public void setAttribute( Attribute attribute ) { this.attribute = attribute; }
    /**
     * Gets attribute value as object. Correct type has to be casted before usage.
     *
     * @return Attribute value as object
     */
    public Object getAttributeValue() {
        Object value = null;
        if( getAttribute().getType().getDatatypeClass() == Datatype.CLASS_INTEGER ) {
            try {
                int attrInt[] = ( int[] )getAttribute().getValue();
                Integer i = attrInt[ 0 ];
                long l = i.longValue();
                value = l;
            } catch( ClassCastException e ) {
                long attrLong[] = ( long[] )getAttribute().getValue();
                Long l = attrLong[ 0 ];
                value = l;
            }
        }
        if( getAttribute().getType().getDatatypeClass() == Datatype.CLASS_FLOAT ) {
            try {
                float attrFloat[] = ( float[] )getAttribute().getValue();
                Float f = attrFloat[ 0 ];
                double d = f.doubleValue();
                value = d;
            } catch( ClassCastException e ) {
                double attrDouble[] = ( double[] )getAttribute().getValue();
                Double d = attrDouble[ 0 ];
                value = d;
            }
        }
        if( getAttribute().getType().getDatatypeClass() == Datatype.CLASS_STRING ) {
            String attrString[] = ( String[] )getAttribute().getValue();
            value = attrString[ 0 ];
        }
        return value;
    }
    /**
     * Gets list of full dataset names.
     *
     * @return List of full dataset names
     */
    public List<String> getDatasetFullNames() { return datasetFullNames; }
    /**
     * Sets list of full dataset names.
     *
     * @param datasetFullNames List of full dataset names
     */
    public void setDatasetFullNames( List<String> datasetFullNames ) {
        this.datasetFullNames = datasetFullNames;
    }
}
//--------------------------------------------------------------------------------------------------
