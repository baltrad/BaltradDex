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
import eu.baltrad.dex.util.EasyX509TrustManager;

import junit.framework.TestCase;

import org.mortbay.jetty.testing.ServletTester;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.HttpParams;
import org.apache.http.HttpVersion;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.content.FileBody;

import javax.servlet.http.HttpServletResponse;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.cert.Certificate;

/**
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.7
 * @since 0.7.7
 */
public class FrameDispatcherControllerTest extends TestCase {
//---------------------------------------------------------------------------------------- Constants    
    private static final String BF_USER_NAME = "BF_UserName";
    private static final String BF_PASSWORD = "BF_Password";
    private static final String BF_REQUEST_TYPE = "BF_RequestType";
    private static final String BF_POST_MESSAGE = "BF_PostMessage";
    private static final String BF_MESSAGE_FIELD = "BF_MessageField";
    private static final String BF_POST_DATA_FILE = "BF_PostDataFile";
    private static final String BF_DATA_FILE_FIELD = "BF_DataFileField";
    private static final String BF_GET_DS_LIST = "BF_GetDSList";
    private static final String BF_PK_AUTH = "BF_PKAuth";
    private static final String BF_SIG_FILE_FIELD = "BF_SignatureFileField";
    private static final String BF_PK_FILE_FIELD = "BF_PKFileField";
    private static final String BF_POST_CERT = "BF_PostCertificate";
    private static final String BF_CERT_FILE_FIELD = "BF_CertFileField";
    private static final String BF_CERT_AUTH = "BF_CertAuth";
    private static final String SERVLET_PATH = "/FrameDispatcherServlet";
    private static final String TEST_DATA_FILE = "test_1.h5";
    private static final String SIG_FILE = "sigfile";
    private static final String PK_FILE = "pkfile";
    private static final String PK_FILE_STUB = "pkfilestub";
    private static final String MSG_TO_SIGN = "Sample message body to be signed";
//---------------------------------------------------------------------------------------- Variables    
    private static ServletTester tester;
    private static HttpClient client;
    private static String context;
    private static KeyPair pair;
    private static Signature dsa;
    private static byte[] signature;
    private static byte[] publicKey;
    private static byte[] publicKeyStub;
    private static Certificate cert;
    private static PrivateKey privateKey;
//------------------------------------------------------------------------------------------ Methods    
    @Override
    public void setUp() throws Exception {
        tester = new ServletTester();
        
        assertNotNull( tester );
        
        tester.setContextPath( "/" );
        tester.addServlet( FrameDispatcherServlet.class, SERVLET_PATH );
        context = tester.createSocketConnector( true );
        tester.start();
        
        SchemeRegistry schemeReg = new SchemeRegistry();
        registerHttpScheme( schemeReg );
        registerHttpsScheme( schemeReg );
        ClientConnectionManager connMgr = new ThreadSafeClientConnManager( schemeReg );
        client = new DefaultHttpClient( connMgr );
        
        assertNotNull( client );
        
        HttpParams httpParams = client.getParams();
        
        assertNotNull( httpParams );
        
        HttpConnectionParams.setConnectionTimeout( httpParams, 60000 );
        HttpConnectionParams.setSoTimeout( httpParams, 60000 );
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
    }
    
    public void testHttpGetMethod() throws IOException {
        HttpGet httpGet = new HttpGet( context + SERVLET_PATH );
        HttpResponse response = client.execute( httpGet );
        
        assertEquals( "Server failed to respond to GET request", HttpServletResponse.SC_OK, 
                response.getStatusLine().getStatusCode() );
        
        HttpEntity entity = response.getEntity();
        if( entity != null ) {
            entity.writeTo( System.out );
        }
    }
    
    public void testBasicAuthFail() throws IOException {
        HttpPost post = new HttpPost( context + SERVLET_PATH );
        MultipartEntity me = new MultipartEntity();
        me.addPart( BF_USER_NAME, new StringBody( MessageDigestUtil.createHash( "InvalidUser" ) ) );
        me.addPart( BF_PASSWORD, new StringBody( MessageDigestUtil.createHash( "bullshit" ) ) );
        me.addPart( BF_REQUEST_TYPE, new StringBody( BF_POST_MESSAGE ) );
        me.addPart( BF_MESSAGE_FIELD, new StringBody( "How is it going?" ) );
        post.setEntity( me );
        HttpResponse response = client.execute( post );
        
        assertEquals( HttpServletResponse.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode() );
    }
    
    public void testBasicAuthSuccess() throws IOException {
        HttpPost post = new HttpPost( context + SERVLET_PATH );
        MultipartEntity me = new MultipartEntity();
        me.addPart( BF_USER_NAME, new StringBody( MessageDigestUtil.createHash( "TestUser" ) ) );
        me.addPart( BF_PASSWORD, new StringBody( MessageDigestUtil.createHash( "s3cret" ) ) );
        me.addPart( BF_REQUEST_TYPE, new StringBody( BF_POST_MESSAGE ) );
        me.addPart( BF_MESSAGE_FIELD, new StringBody( "How is it going?" ) );
        post.setEntity( me );
        HttpResponse response = client.execute( post );
        
        assertEquals( HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode() );
    }
    
    public void testPostMessage() throws IOException {
        HttpPost post = new HttpPost( context + SERVLET_PATH );
        MultipartEntity me = new MultipartEntity();
        me.addPart( BF_USER_NAME, new StringBody( MessageDigestUtil.createHash( "TestUser" ) ) );
        me.addPart( BF_PASSWORD, new StringBody( MessageDigestUtil.createHash( "s3cret" ) ) );
        me.addPart( BF_REQUEST_TYPE, new StringBody( BF_POST_MESSAGE ) );
        me.addPart( BF_MESSAGE_FIELD, new StringBody( "How is it going?" ) );
        post.setEntity( me );
        HttpResponse response = client.execute( post );
        
        assertEquals( HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode() );
    }
    
    public void testPostDataFile() throws IOException {
        HttpPost post = new HttpPost( context + SERVLET_PATH );
        MultipartEntity me = new MultipartEntity();
        me.addPart( BF_USER_NAME, new StringBody( MessageDigestUtil.createHash( "TestUser" ) ) );
        me.addPart( BF_PASSWORD, new StringBody( MessageDigestUtil.createHash( "s3cret" ) ) );
        me.addPart( BF_REQUEST_TYPE, new StringBody( BF_POST_DATA_FILE ) );
        me.addPart( BF_DATA_FILE_FIELD, new FileBody( new File( TEST_DATA_FILE ) ) );
        post.setEntity( me );
        HttpResponse response = client.execute( post );
        
        assertEquals( HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode() );
    }
    
    public void testPostDSListRequest() throws IOException {    
        HttpPost post = new HttpPost( context + SERVLET_PATH );
        MultipartEntity me = new MultipartEntity();
        me.addPart( BF_USER_NAME, new StringBody( MessageDigestUtil.createHash( "TestUser" ) ) );
        me.addPart( BF_PASSWORD, new StringBody( MessageDigestUtil.createHash( "s3cret" ) ) );
        me.addPart( BF_REQUEST_TYPE, new StringBody( BF_GET_DS_LIST ) );
        post.setEntity( me );
        HttpResponse response = client.execute( post );
        
        assertEquals( HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode() );
        
        List<String> dsList = ( List<String> )readObjectFromStream( response );
        
        assertNotNull( dsList );
        
        Collections.sort( dsList );
        
        assertEquals( "AnotherTestDataSource", dsList.get( 0 ) );
        assertEquals( "TestDataSource", dsList.get( 1 ) );
        assertEquals( "YetOneMoreTestDataSource", dsList.get( 2 ) );
    }
    
    public void testInitSignature() throws Exception {
        pair = generateKeys();
        PrivateKey priv = pair.getPrivate();
        
        assertNotNull( priv );
        
        dsa = Signature.getInstance( "SHA1withDSA", "SUN" );
        
        assertNotNull( dsa );
        
        dsa.initSign( priv );
    }
    
    public void testGetSignature() {
        signature = signMessage( dsa, MSG_TO_SIGN );
        
        assertNotNull( signature );
        
        writeByteArrayToFile( signature, SIG_FILE );
    }
    
    public void testGetPublicKey() {
        publicKey = pair.getPublic().getEncoded();
        
        assertNotNull( publicKey );
        
        writeByteArrayToFile( publicKey, PK_FILE );
    }
    
    public void testGetFakePublicKey() {
        SecureRandom sr = new SecureRandom();
        publicKeyStub = new byte[ 444 ];
        sr.nextBytes( publicKeyStub );
        
        assertNotNull( publicKeyStub );
        
        writeByteArrayToFile( publicKeyStub, PK_FILE_STUB );
    }
    
    public void testPKAuthFail() throws IOException {
        HttpPost post = new HttpPost( context + SERVLET_PATH );
        MultipartEntity me = new MultipartEntity();
        me.addPart( BF_USER_NAME, new StringBody( MessageDigestUtil.createHash( "TestUser" ) ) );
        me.addPart( BF_PASSWORD, new StringBody( MessageDigestUtil.createHash( "s3cret" ) ) );
        me.addPart( BF_REQUEST_TYPE, new StringBody( BF_PK_AUTH ) );
        me.addPart( BF_MESSAGE_FIELD, new StringBody( MSG_TO_SIGN ) );
        me.addPart( BF_SIG_FILE_FIELD, new FileBody( new File( SIG_FILE ) ) );
        me.addPart( BF_PK_FILE_FIELD, new FileBody( new File( PK_FILE_STUB ) ) );
        post.setEntity( me );
        HttpResponse response = client.execute( post );
        
        assertEquals( HttpServletResponse.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode() );
    }
    
    public void testPKAuthSuccess() throws IOException {
        HttpPost post = new HttpPost( context + SERVLET_PATH );
        MultipartEntity me = new MultipartEntity();
        me.addPart( BF_USER_NAME, new StringBody( MessageDigestUtil.createHash( "TestUser" ) ) );
        me.addPart( BF_PASSWORD, new StringBody( MessageDigestUtil.createHash( "s3cret" ) ) );
        me.addPart( BF_REQUEST_TYPE, new StringBody( BF_PK_AUTH ) );
        me.addPart( BF_MESSAGE_FIELD, new StringBody( MSG_TO_SIGN ) );
        me.addPart( BF_SIG_FILE_FIELD, new FileBody( new File( SIG_FILE ) ) );
        me.addPart( BF_PK_FILE_FIELD, new FileBody( new File( PK_FILE ) ) );
        post.setEntity( me );
        HttpResponse response = client.execute( post );
        
        assertEquals( HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode() );
    }
    
    public void testLoadCertFromKS() {
        cert = loadCertFromKS( ".keystore", "szewczenko", 
                new char[]{ 's', '3', 'c', 'r', 'e', 't' } );
        
        assertNotNull( cert );
    }
    
    public void testSaveCertToFile() {
        saveCertToFile( cert, "szewczenko.cer" );
        File f = new File( "szewczenko.cer" );
        
        assertNotNull( f );
        assertTrue( f.length() > 0 );
    }
    
    public void testExtractPublicKey() {
        PublicKey pk = cert.getPublicKey();
        
        assertNotNull( pk );
    }
    
    public void testLoadPrivateKeyFromKS() {
        privateKey = loadPrivateKeyFromKS( ".keystore", "szewczenko", 
                new char[]{ 's', '3', 'c', 'r', 'e', 't' } );
        
        assertNotNull( privateKey );
    }
    
    /**
     * Certificate authentication request. Certificate is attached to the message body. 
     * Server will verify the certificate (e.g. by comparing its fingerprints) and store 
     * it in the trusted keystore.  
     */
    public void testPostCertRequest() throws IOException {
        HttpPost post = new HttpPost( context + SERVLET_PATH );
        MultipartEntity me = new MultipartEntity();
        me.addPart( BF_USER_NAME, new StringBody( MessageDigestUtil.createHash( "TestUser" ) ) );
        me.addPart( BF_PASSWORD, new StringBody( MessageDigestUtil.createHash( "s3cret" ) ) );
        me.addPart( BF_REQUEST_TYPE, new StringBody( BF_POST_CERT ) );
        me.addPart( BF_MESSAGE_FIELD, new StringBody( "Sending certificate so you can verify it" ) );
        Certificate cert = loadCertFromKS( ".keystore", "testcert", 
                new char[]{ 's', '3', 'c', 'r', 'e', 't' } );
        
        assertNotNull( cert );
        
        saveCertToFile( cert, "testcert.cer" );
        me.addPart( BF_CERT_FILE_FIELD, new FileBody( new File( "testcert.cer" ) ) );
        post.setEntity( me );
        HttpResponse response = client.execute( post );
        
        assertEquals( HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode() );
    }
    
    /**
     * Authenticate using certificate existing in server's trusted keystore. We send only message
     * and a signature. The certificate and its public key is extracted from the keystore 
     * based on alias (in production code user name will be used as alias).
     */
    public void testCertAuthFail() throws IOException, Exception {
        HttpPost post = new HttpPost( context + SERVLET_PATH );
        MultipartEntity me = new MultipartEntity();
        me.addPart( BF_USER_NAME, new StringBody( MessageDigestUtil.createHash( "TestUser" ) ) );
        me.addPart( BF_PASSWORD, new StringBody( MessageDigestUtil.createHash( "s3cret" ) ) );
        me.addPart( BF_REQUEST_TYPE, new StringBody( BF_CERT_AUTH ) );
        me.addPart( BF_MESSAGE_FIELD, new StringBody( MSG_TO_SIGN ) );
        // try to authenticate with an invalid certificate
        PrivateKey priv = loadPrivateKeyFromKS( ".keystore", "szewczenko", 
                new char[]{ 's', '3', 'c', 'r', 'e', 't' } );
        
        assertNotNull( priv );
        
        Signature sig = Signature.getInstance( "SHA1withDSA", "SUN" );
        
        assertNotNull( sig );
        
        sig.initSign( priv );
        byte[] sigBytes = signMessage( sig, MSG_TO_SIGN );
        
        assertNotNull( sigBytes );
        
        writeByteArrayToFile( sigBytes, SIG_FILE );
        me.addPart( BF_SIG_FILE_FIELD, new FileBody( new File( SIG_FILE ) ) );
        post.setEntity( me );
        HttpResponse response = client.execute( post );
        
        assertEquals( HttpServletResponse.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode() );
    }
    
    /**
     * Authenticate using certificate existing in server's trusted keystore. We send only message
     * and a signature. The certificate and its public key is extracted from the keystore 
     * based on alias (in production code user name will be used as alias).
     */
    public void testCertAuthSuccess() throws IOException, Exception {
        HttpPost post = new HttpPost( context + SERVLET_PATH );
        MultipartEntity me = new MultipartEntity();
        me.addPart( BF_USER_NAME, new StringBody( MessageDigestUtil.createHash( "TestUser" ) ) );
        me.addPart( BF_PASSWORD, new StringBody( MessageDigestUtil.createHash( "s3cret" ) ) );
        me.addPart( BF_REQUEST_TYPE, new StringBody( BF_CERT_AUTH ) );
        me.addPart( BF_MESSAGE_FIELD, new StringBody( MSG_TO_SIGN ) );
        PrivateKey priv = loadPrivateKeyFromKS( ".keystore", "testcert", 
                new char[]{ 's', '3', 'c', 'r', 'e', 't' } );
        
        assertNotNull( priv );
        
        Signature sig = Signature.getInstance( "SHA1withDSA", "SUN" );
        
        assertNotNull( sig );
        
        sig.initSign( priv );
        byte[] sigBytes = signMessage( sig, MSG_TO_SIGN );
        
        assertNotNull( sigBytes );
        
        writeByteArrayToFile( sigBytes, SIG_FILE );
        me.addPart( BF_SIG_FILE_FIELD, new FileBody( new File( SIG_FILE ) ) );
        post.setEntity( me );
        HttpResponse response = client.execute( post );
        
        assertEquals( HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode() );
    }
    
    @Override
    public void tearDown() throws Exception {
        client.getConnectionManager().shutdown();
        tester.stop();
    }
    
    /**
     * 
     * @param response
     * @return 
     */
    private String readMessageFromStream( HttpResponse response ) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = null;
            try {
                is = response.getEntity().getContent();
                byte[] buff = new byte[4096];
                int bytesRead;
                while( ( bytesRead = is.read( buff ) ) != -1 ) {
                    sb.append( new String( buff ) );
                }
            } finally {
                is.close();
            }
        } catch( IOException e ) {
            System.out.println( "failed to read message from the stream " + e.getMessage());
        }
        return sb.toString();
    }
    /**
     * 
     * @param response
     * @return 
     */
    private Object readObjectFromStream( HttpResponse response ) {
        Object obj = null;
        try {
            InputStream is = null;
            ObjectInputStream ois = null;
            try {
                is = response.getEntity().getContent();                
                ois = new ObjectInputStream( is );
                obj = ois.readObject();
            } finally {
                is.close();
                ois.close();
            }
        } catch( ClassNotFoundException e ) {
            System.out.println( "failed to read object from the stream " + e.getMessage() );
        } catch( IOException e ) {
            System.out.println( "failed to read object from the stream " + e.getMessage());
        }
        return obj;
    }
    /**
     * 
     * @param schemeReg 
     */
    private void registerHttpScheme( SchemeRegistry schemeReg ) {
        Scheme http = new Scheme("http", 80, new PlainSocketFactory() );
        schemeReg.register(http);
    }
    /**
     * 
     * @param schemeReg 
     */
    private void registerHttpsScheme( SchemeRegistry schemeReg ) {
        try {
            SSLContext sslContext = SSLContext.getInstance( "SSL" );
            sslContext.init( null, new TrustManager[] { new EasyX509TrustManager() },
                new SecureRandom() );
            Scheme https = new Scheme( "https", 443, new SSLSocketFactory( sslContext, 
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER ) );
            schemeReg.register( https );
        } catch( Exception e ) {
            System.out.println( "failed to register https scheme " + e.getMessage() );
        }
    }
    /**
     * 
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException 
     */
    public KeyPair generateKeys() {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance( "DSA", "SUN" );
            SecureRandom random = SecureRandom.getInstance( "SHA1PRNG", "SUN" );
            keyGen.initialize( 1024, random );
        } catch( NoSuchAlgorithmException e ) {
            System.out.println( "failed to generate keys " + e.getMessage() );
        } catch( NoSuchProviderException e ) {
            System.out.println( "failed to generate keys " + e.getMessage() );
        }
        return keyGen.generateKeyPair();
    }
    /**
     * 
     * @param sig
     * @param message
     * @return 
     */
    public byte[] signMessage( Signature sig, String message ) {
        byte[] sigBytes = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream( message.getBytes() );
            try {
                byte[] buff = new byte[1024];
                int bytesRead;
                while( ( bytesRead = bis.read( buff ) ) >= 0 ) {
                    sig.update( buff, 0, bytesRead );
                }
            } finally {
                bis.close();
            }
            sigBytes = sig.sign();
        } catch( Exception e ) {
            System.out.println( "failed to sign message " + e.getMessage() );
        }
        return sigBytes;
    }
    /**
     * 
     * @param byteArray
     * @param filePath 
     */
    public void writeByteArrayToFile( byte[] byteArray, String filePath ) {
        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream( filePath );
                fos.write( byteArray );
            } finally {
                fos.close();
            }
        } catch( IOException e ) {
            System.out.println( "failed to write byte array to file " + e.getMessage() );
        }
    }
    /**
     * 
     * @param certFileName
     * @param ksFileName
     * @param passwd
     * @param alias
     * @return 
     */
    public Certificate loadCertFromKS( String ksFileName, String certAlias, char[] passwd ) {
        Certificate cer = null;
        try {
            KeyStore ks = KeyStore.getInstance( "JKS" );
            FileInputStream fis = new FileInputStream( ksFileName ); 
            BufferedInputStream bis = new BufferedInputStream( fis );
            ks.load( bis, passwd );
            cer = ks.getCertificate( certAlias );
        } catch( Exception e ) {
            System.out.println( "failed to load certificate from keystore " + e.getMessage() );
        }
        return cer;
    }
    /**
     * 
     * @param cert
     * @param certFileName 
     */
    public void saveCertToFile( Certificate cert, String certFileName ) {
        try {
            FileOutputStream fos = new FileOutputStream( new File( certFileName ) );
            try { 
                fos.write( cert.getEncoded() );
            } finally {
                fos.close();
            }
        } catch( Exception e ) {
            System.out.println( "failed to save certificate to file " + e.getMessage() );
        }
    }
    /**
     * 
     * @param ksFileName
     * @param keyAlias
     * @param passwd
     * @return 
     */
    public PrivateKey loadPrivateKeyFromKS( String ksFileName, String keyAlias, char[] passwd ) {
        PrivateKey priv = null;
        try {
            KeyStore ks = KeyStore.getInstance( "JKS" );
            FileInputStream fis = new FileInputStream( ksFileName ); 
            BufferedInputStream bis = new BufferedInputStream( fis );
            ks.load( bis, passwd );
            priv = ( PrivateKey )ks.getKey( keyAlias, passwd );
        } catch( Exception e ) {
            System.out.println( "failed to load private key from keystore " + e.getMessage() );
        }
        return priv;
    }
}
//--------------------------------------------------------------------------------------------------
