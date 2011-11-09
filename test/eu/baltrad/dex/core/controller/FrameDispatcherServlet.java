/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.core.controller;

import eu.baltrad.dex.util.MessageDigestUtil;
import static eu.baltrad.frame.model.BaltradFrameProtocol.*;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.util.Streams;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import java.security.*;
import java.security.spec.*;

/**
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.7
 * @since 0.7.7
 */
public class FrameDispatcherServlet extends HttpServlet {
//---------------------------------------------------------------------------------------- Constants    
    private static final String USER_NAME = "TestUser";
    private static final String PASSWORD = "s3cret";
    private static final int FILE_SIZE_EXPECTED = 350022;
//------------------------------------------------------------------------------------------ Methods
    /**
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    public void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        System.out.println( "_GET request received" );
        response.setContentType( "text/html" );
        PrintWriter pw = response.getWriter();
        pw.write( "<html><body>Greetings!</body></html>" );
        pw.close();
    }
    /**
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    public void doPost( HttpServletRequest request, HttpServletResponse response ) 
            throws ServletException, IOException {
        System.out.println( "_POST request received" );
        // verify post message request
        HashMap parms = parseMultipartRequest( request, "_data.h5", "_sigfile", "_pkfile" );
        String userName = ( String )parms.get( BF_USER_NAME );
        String passwd = ( String )parms.get( BF_PASSWORD );
        // authenticate request
        if( basicAuth( userName, passwd ) ) {
            System.out.println( "_Request successfully authenticated" );
            String requestType = ( String )parms.get( BF_REQUEST_TYPE );
            // message post
            if( requestType.equals( BF_POST_MESSAGE ) ) {
                String msg = ( String )parms.get( BF_MESSAGE_FIELD );
                System.out.println( "_New message received: " + msg );
                response.setStatus( HttpServletResponse.SC_OK );
            }
            // data source listing request
            if( requestType.equals( BF_GET_DS_LIST ) ) {
                System.out.println( "_Data source listing request received" );
                List<String> dsList = new ArrayList<String>();
                dsList.add( "TestDataSource" );
                dsList.add( "AnotherTestDataSource" );
                dsList.add( "YetOneMoreTestDataSource" );
                writeObjectToStream( response, dsList );
            }
            // file post
            if( requestType.equals( BF_POST_DATA_FILE ) ) {
                System.out.println( "_New data file received" );
                if( parms.containsKey( BF_DATA_FILE ) ) {
                    File f = ( File )parms.get( BF_DATA_FILE );
                    // check file size
                    if( f.length() == FILE_SIZE_EXPECTED ) {
                        response.setStatus( HttpServletResponse.SC_OK );
                    } else {
                        response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
                    }
                }
                response.setStatus( HttpServletResponse.SC_OK );
            }
            // PK authentication
            if( requestType.equals( BF_PK_AUTH ) ) {
                System.out.println( "_PK authentication request received" );
                if( pkAuth( ( File )parms.get( BF_SIG_FILE ), ( File )parms.get( BF_PK_FILE ), 
                         ( String )parms.get( BF_MESSAGE_FIELD ) ) ) {
                    System.out.println( "_PK authentication successful" );
                    response.setStatus( HttpServletResponse.SC_OK );
                } else {
                    System.out.println( "_PK authentication failed" );
                    response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
                }
            }
        } else {
            System.out.println( "_Basic authentication failed" );
            response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
        }
    }
    /**
     * 
     * @param request
     * @param dataFileName
     * @param sigFileName
     * @param pkFileName
     * @return 
     */
    private synchronized HashMap<String, Object> parseMultipartRequest( HttpServletRequest request,
            String dataFileName, String sigFileName, String pkFileName ) {
        HashMap<String, Object> parms = new HashMap<String, Object>();
        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator( request );
            FileItemStream fis = null;
            InputStream is = null;
            while( iterator.hasNext() ) {
                fis = iterator.next();
                String name = fis.getFieldName();
                if( fis.isFormField() ) {
                    // save parameter into hashmap
                    String value = Streams.asString( fis.openStream() );
                    parms.put( name, value );
                } else {
                    // save data to file 
                    is = fis.openStream();
                    if( fis.getFieldName().equals( BF_DATA_FILE_FIELD ) ) {
                        File f = readFileFromStream( is, dataFileName );
                        parms.put( BF_DATA_FILE, f );
                    }
                    // save signature to file
                    if( fis.getFieldName().equals( BF_SIG_FILE_FIELD ) ) {
                        File f = readFileFromStream( is, sigFileName );
                        parms.put( BF_SIG_FILE, f );
                    }
                    // save public key to file
                    if( fis.getFieldName().equals( BF_PK_FILE_FIELD ) ) {
                        File f = readFileFromStream( is, pkFileName );
                        parms.put( BF_PK_FILE, f );
                    }
                }
            }
        } catch( FileUploadException e ) {
            System.out.println( "failed to read field value " + e.getMessage() );
        } catch( IOException e ) {
            System.out.println( "failed to read field value " + e.getMessage() );
        }
        return parms;
    }
    /**
     * 
     * @param userName
     * @param password
     * @return 
     */
    private boolean basicAuth( String userName, String password ) {
        if( userName.equals( MessageDigestUtil.createHash( USER_NAME ) ) &&
                password.equals( MessageDigestUtil.createHash( PASSWORD ) ) ) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 
     * @param sigFile
     * @param pkFile
     * @return 
     */
    private boolean pkAuth( File sigFile, File pkFile, String message ) {
        boolean res = false;
        try {
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec( readBytesFromFile( pkFile ) );
            KeyFactory keyFactory = KeyFactory.getInstance( "DSA", "SUN" );
            PublicKey pubKey = keyFactory.generatePublic( pubKeySpec );
            Signature sig = Signature.getInstance( "SHA1withDSA", "SUN" );
            sig.initVerify( pubKey );
            signMessage( sig, message );
            byte[] sigToVerify = readBytesFromFile( sigFile );
            res = sig.verify( sigToVerify );
        } catch( Exception e ) {
            System.out.println( "failed to perform PK authentication " + e.getMessage() );
        }
        return res;
    }
    /**
     * 
     * @param resposne
     * @param message 
     */
    private void writeMessageToStream( HttpServletResponse response, String message ) {
        try {
            OutputStream os = null;
            try {
                os = response.getOutputStream();
                os.write( message.getBytes() );
            } finally {
                os.close();
            }
        } catch( IOException e ) {
            System.out.println( "failed to write message to the stream " + e.getMessage() );
        } 
    }
    /**
     * 
     * @param response
     * @param obj 
     */
    private void writeObjectToStream( HttpServletResponse response, Object obj ) {
        try {
            OutputStream os = null;
            ObjectOutputStream oos = null;
            try {
                os = response.getOutputStream();
                oos = new ObjectOutputStream( os );
                oos.writeObject( obj );
            } finally {
                oos.close();
                os.close();
            }
        } catch( IOException e ) {
            System.out.println( "failed to write object to the stream " + e.getMessage() );
        } 
    }
    /**
     * 
     * @param request
     * @return 
     */
    private String readMessageFromStream( HttpServletRequest request ) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        try {
            ServletInputStream sis = request.getInputStream();
            try {
                byte[] buff = new byte[4096];
                int bytesRead;
                while( ( bytesRead = sis.read( buff ) ) != -1 ) {
                    bos.write( buff, 0, bytesRead );
                }
            } finally {
                sis.close();
                bos.close();
            }
        } catch( IOException e ) {
            System.out.println( "failed to read message from the stream " + e.getMessage() );
        }
        return bos.toString();
    }
    /**
     * 
     * @param request
     * @param filePath
     * @return 
     */
    private File readFileFromStream( InputStream is, String filePath ) {
        File f = null;
        try {
            f = new File( filePath );
            FileOutputStream fos = new FileOutputStream( f );
            try {
                byte[] buff = new byte[4096];
                int bytesRead;
                while( ( bytesRead = is.read( buff ) ) != -1 ) {    
                    fos.write( buff, 0, bytesRead );
                }
            } finally {
                is.close();
                fos.close();
            }
        } catch( IOException e ) {
            System.out.println( "failed to read file from the stream " + e.getMessage() );
        }
        return f;
    }
    /**
     * 
     * @param sig
     * @param message
     */
    private void signMessage( Signature sig, String message ) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream( message.getBytes() );
            try {
                byte[] buff = new byte[1024];
                int bytesRead;
                while( ( bytesRead = bis.read( buff ) ) >= 0 ) {
                    sig.update( buff, 0, bytesRead );
                };
            } finally {
                bis.close();
            }
        } catch( Exception e ) {
            System.out.println( "failed to sign message " + e.getMessage() );
        }
    }
    /**
     * 
     * @param f
     * @return 
     */
    private byte[] readBytesFromFile( File f ) {
        byte[] bytes = null;
        try {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream( f );
                bytes = new byte[ fis.available() ];
                fis.read( bytes );
            } finally {
                fis.close();
            }
        } catch( FileNotFoundException e ) {
            System.out.println( "failed to read bytes from file " + e.getMessage() );
        } catch( IOException e ) {
            System.out.println( "failed to read bytes from file " + e.getMessage() );
        }
        return bytes;
    } 
}
//--------------------------------------------------------------------------------------------------