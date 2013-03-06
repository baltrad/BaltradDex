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

package eu.baltrad.dex.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Data compression utility class.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 1.7.0
 */
public class CompressDataUtil {

    protected String rootFolder;
    protected Map<String, String> fileListing;
    
    /**
     * Default constructor.
     */
    public CompressDataUtil() {}
    
    /**
     * Constructor.
     * @param rootFolder Root directory 
     */
    public CompressDataUtil(String rootFolder) {
        this.rootFolder = rootFolder;
        this.fileListing = new HashMap<String, String>();
    }
    
    /**
     * Create ZIP archive.
     * @return ZIP archive as byte array
     * @throws RuntimeException 
     */
    public byte[] zip() throws RuntimeException {
        listFiles(new File(rootFolder));
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(bos);
            try {
                Set<String> fileNames = fileListing.keySet();
                for (String name : fileNames) {
                    ZipEntry entry = new ZipEntry(name);
                    zos.putNextEntry(entry);
                    zos.write(fileToByteArray(fileListing.get(name)));
                }
            } finally {
                zos.closeEntry();
                zos.close();
                bos.close();
                return bos.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create ZIP archive", e);
        }
    }
    
    /**
     * Extract files from ZIP archive and save to file.
     * @param folder Root folder
     * @param is Compressed input stream 
     * @throws RuntimeException 
     */
    public void unzip(String folder, InputStream is) throws RuntimeException {
        try {
            if (!(new File(folder)).exists()) {
                (new File(folder)).mkdir();
            }
            ZipInputStream zis = null;
            byte[] buff = new byte[1024];
            try {
                zis = new ZipInputStream(is);
                ZipEntry entry = zis.getNextEntry();
                while (entry != null) {
                    String fileName = entry.getName();
                    File file = new File(folder + File.separator 
                            + fileName);
                    new File(file.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(file);
                    int len = 0;
                    while ((len = zis.read(buff)) > 0) {
                        fos.write(buff, 0, len);
                    }
                    fos.close();
                    entry = zis.getNextEntry();
                }
            } finally {
                zis.closeEntry();
                zis.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract ZIP archive to file", 
                    e);
        }
    }
    
    /**
     * Extract files from ZIP archive and write to output stream.
     * @param is Compressed input stream 
     * @return Uncompressed byte array  
     * @throws RuntimeException 
     */
    public byte[] unzip(InputStream is) throws RuntimeException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipInputStream zis = null;
            byte[] buff = new byte[1024];
            try {
                zis = new ZipInputStream(is);
                ZipEntry entry = zis.getNextEntry();
                while (entry != null) {
                    int len = 0;
                    while ((len = zis.read(buff)) > 0) {
                        bos.write(buff, 0, len);
                    }
                    entry = zis.getNextEntry();
                }
            } finally {
                zis.closeEntry();
                zis.close();
                bos.close();
                return bos.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract ZIP archive to " + 
                    "stream", e);
        }
    }
    
    /**
     * List files recursively.
     * @param node Directory tree node
     */
    protected void listFiles(File node) {
        if (node.isFile()) {
            fileListing.put(getZipEntry(node.getAbsoluteFile().toString()),
                    node.getAbsoluteFile().toString());
        }
        if (node.isDirectory()) {
            String[] subNodes = node.list();
            for (String subNode : subNodes) {
                listFiles(new File(node, subNode));
            }
        }
    }
    
    /**
     * Get file's path relative to root directory.
     * @param file Directory content
     * @return Relative file's path
     */
    protected String getZipEntry(String file) {
        return file.substring(new File(rootFolder)
                        .getAbsolutePath().length() + 1, file.length());
    }
    
    /**
     * Read file to byte array.
     * @param fileName Input file name
     * @return File content as byte array
     * @throws RuntimeException 
     */
    protected byte[] fileToByteArray(String fileName) throws RuntimeException {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        byte[] bytes = new byte[1024];
        try {
            try {
                fis = new FileInputStream(fileName);
                bos = new ByteArrayOutputStream();
                int len = 0;
                while ((len = fis.read(bytes)) > 0) {
                    bos.write(bytes, 0, len);
                }
            } finally {
                fis.close();
                bos.close();
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file to byte array", e);
        }
    }
    
}
