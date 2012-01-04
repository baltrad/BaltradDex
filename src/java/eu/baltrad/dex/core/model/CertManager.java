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

package eu.baltrad.dex.core.model;

import eu.baltrad.dex.util.JDBCConnectionManager;
import eu.baltrad.dex.log.model.MessageLogger;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.security.PrivateKey;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.File;

/**
 * Implements SSL certificate management methods.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.8
 * @since 0.7.8
 */
public class CertManager {
//---------------------------------------------------------------------------------------- Constants    
    /** Keystore type */
    private final static String KEYSTORE_TYPE = "JKS";
    /** Certificate type */
    private final static String CERT_TYPE = "X.509";
    /** Issuer common name's prefix */
    private static final String COMMON_NAME_PREFIX = "CN=";
    /** Certificate organization prefix */
    private static final String CERT_ORG_PREFIX = "O=";
    /** Certificate organization unit prefix */
    private static final String CERT_ORG_UNIT_PREFIX = "OU=";
    /** Certificate locality prefix */
    private static final String CERT_LOCALITY_PREFIX = "L=";
    /** Certificate state prefix */
    private static final String CERT_STATE_PREFIX = "ST=";
    /** Certificate country code prefix */
    private static final String CERT_COUNTRY_CODE_PREFIX = "C="; 
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
    /** Logger */
    private Logger log;
//------------------------------------------------------------------------------------------ Methods 
    /**
     * Constructor.
     */
    public CertManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
    }
    /**
     * Gets all available certificates.
     * 
     * @return List of all available certificates
     */
    public List<Cert> get() {
        Connection conn = null;
        List<Cert> certs = new ArrayList<Cert>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_certificates";
            ResultSet resultSet = stmt.executeQuery(sql);
            while( resultSet.next() ) {
                int id = resultSet.getInt("id");
                String certAlias = resultSet.getString("cert_alias");
                String nodeAddress = resultSet.getString("node_address");
                String certFilePath = resultSet.getString("cert_file_path");
                boolean trusted = resultSet.getBoolean("trusted");
                Cert cert = new Cert(id, certAlias, nodeAddress, certFilePath, trusted);
                certs.add(cert);
            }
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to select certificates", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return certs;
    }
    /**
     * Gets certificate with a given ID.
     * 
     * @param id Certificate ID
     * @return Certificate with a given ID
     */
    public Cert get(int id) {
        Connection conn = null;
        Cert cert = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_certificates WHERE id = " + id + ";";
            ResultSet resultSet = stmt.executeQuery(sql);
            while( resultSet.next() ) {
                int certId = resultSet.getInt("id");
                String certAlias = resultSet.getString("cert_alias");
                String nodeAddress = resultSet.getString("node_address");
                String certFilePath = resultSet.getString("cert_file_path");
                boolean trusted = resultSet.getBoolean("trusted");
                cert = new Cert(certId, certAlias, nodeAddress, certFilePath, trusted);
            }
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to select certificate with a given ID", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return cert;
    }
    
    /**
     * Gets certificate with a given alias.
     * 
     * @param alias Certificate alias
     * @return Certificate with a given ID
     */
    public Cert get(String alias) {
        Connection conn = null;
        Cert cert = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_certificates WHERE cert_alias = '" + alias + "';";
            ResultSet resultSet = stmt.executeQuery(sql);
            while( resultSet.next() ) {
                int certId = resultSet.getInt("id");
                String certAlias = resultSet.getString("cert_alias");
                String nodeAddress = resultSet.getString("node_address");
                String certFilePath = resultSet.getString("cert_file_path");
                boolean trusted = resultSet.getBoolean("trusted");
                cert = new Cert(certId, certAlias, nodeAddress, certFilePath, trusted);
            }
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to select certificate with a given ID", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return cert;
    }
    /**
     * Saves certificate.
     * 
     * @param cert Certificate to save
     * @return Number of updated records
     * @throws Exception
     */
    public int saveOrUpdate(Cert cert) throws Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            if (cert.getId() == 0) {
                // insert
                sql = "INSERT INTO dex_certificates (cert_alias, node_address, cert_file_path, " +
                        "trusted) VALUES ('" + cert.getAlias() + "', '" + cert.getNodeAddress() +
                        "', '" + cert.getFilePath() + "', " + cert.getTrusted() + ");";
            } else {
                // update
                sql = "UPDATE dex_certificates SET trusted = " + cert.getTrusted() + " WHERE " +
                        "id = " + cert.getId() + ";";
            }
            update = stmt.executeUpdate(sql);
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to save certificate", e);
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes certificate with a given alias.
     * 
     * @param certAlias Certificate alias
     * @return Number of deleted records
     */
    public int delete(String certAlias) {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_certificates WHERE certAlias = '" + certAlias + "';";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( Exception e ) {
            log.error("Failed to delete certificate", e);
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Stores certificate in the keystore.
     * 
     * @param ksFileName Keystore file name
     * @param passwd Keystore password
     * @param cert Certificate to store
     * @param keyAlias Trusted entry alias
     * @throws Exception
     */
    public void storeTrustedEntry(String ksFileName, String passwd, Certificate cert, String 
                                    keyAlias) throws Exception {
        try {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
                fis = new FileInputStream(ksFileName); 
                BufferedInputStream bis = new BufferedInputStream(fis);
                ks.load(bis, passwd.toCharArray());
                ks.setCertificateEntry(keyAlias, cert);
                fos = new FileOutputStream(new File(ksFileName));
                ks.store(fos, passwd.toCharArray());   
            } finally {
                fis.close();
                fos.close();
            }
        } catch(Exception e) {
            log.error("Failed to store trusted entry in the keystore", e);
            throw e;
        }    
    }
    /**
     * Deletes certificate from the keystore.
     * 
     * @param ksFileName Keystore file name
     * @param passwd Password
     * @param alias Certificate alias
     * @throws Exception 
     */
    public void deleteFromKS(String ksFileName, String passwd, String alias) throws Exception {
        try {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
                fis = new FileInputStream(ksFileName); 
                BufferedInputStream bis = new BufferedInputStream(fis);
                ks.load(bis, passwd.toCharArray());
                ks.deleteEntry(alias);
                fos = new FileOutputStream(new File(ksFileName));
                ks.store(fos, passwd.toCharArray());
            } finally {
                fis.close();
                fos.close();
            }
        } catch(Exception e) {
            log.error("Failed delete certificate from the keystore", e);
            throw e;
        }
    }
    /**
     * Loads certificate from file.
     * 
     * @param certFileName Certificate file name
     * @return Certificate
     */
    public X509Certificate loadFromFile(String certFileName) throws Exception {
        X509Certificate cert = null;
        try {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(new File(certFileName));
                CertificateFactory x509CertFact = CertificateFactory.getInstance(CERT_TYPE);
                cert = (X509Certificate)x509CertFact.generateCertificate(fis);
            } finally {
                fis.close();
            }    
        } catch(Exception e) {
            log.error("Failed to load certificate from file", e);
            throw e;
        }
        return cert;
    }
    /**
     * Extracts issuer's common name from principal string. 
     * 
     * @param principalName Principal name string
     * @return Issuer's common name
     */
    public String getCommonName(String principalName) {
        return principalName.substring(principalName.indexOf(COMMON_NAME_PREFIX) + 
                COMMON_NAME_PREFIX.length(), principalName.indexOf(",", principalName.indexOf(
                COMMON_NAME_PREFIX)));
    }
    /**
     * Extracts organization name from principal string. 
     * 
     * @param principalName Principal name string
     * @return Organization name
     */
    public String getOrganization(String principalName) {
        return principalName.substring(principalName.indexOf(CERT_ORG_PREFIX) + 
                CERT_ORG_PREFIX.length(), principalName.indexOf(",", principalName.indexOf(
                CERT_ORG_PREFIX)));
    }
    /**
     * Extracts organization unit from principal string.
     * 
     * @param principalName Principal name string
     * @return Organization unit name
     */
    public String getOrganizationUnit(String principalName) {
        return principalName.substring(principalName.indexOf(CERT_ORG_UNIT_PREFIX) + 
                CERT_ORG_UNIT_PREFIX.length(), principalName.indexOf(",", principalName.indexOf(
                CERT_ORG_UNIT_PREFIX)));
    }
    /**
     * Extracts locality name from principal string.
     * 
     * @param principalName Principal name string
     * @return Locality name
     */
    public String getLocality(String principalName) {
        return principalName.substring(principalName.indexOf(CERT_LOCALITY_PREFIX) +
                CERT_LOCALITY_PREFIX.length(), principalName.indexOf(",", principalName.indexOf(
                CERT_LOCALITY_PREFIX)));
    }
    /**
     * Extracts state name from principal string.
     * 
     * @param principalName Principal name string
     * @return State name
     */
    public String getState(String principalName) {
        return principalName.substring(principalName.indexOf(CERT_STATE_PREFIX) + 
                CERT_STATE_PREFIX.length(), principalName.indexOf(",", principalName.indexOf(
                CERT_STATE_PREFIX)));
    }
    /**
     * Extracts country code from principal string.
     * 
     * @param principalName Principal name string
     * @return Country code
     */
    public String getCountryCode(String principalName) {
        return principalName.substring(principalName.indexOf(CERT_COUNTRY_CODE_PREFIX) +
                CERT_COUNTRY_CODE_PREFIX.length(), principalName.indexOf(CERT_COUNTRY_CODE_PREFIX) +
                CERT_COUNTRY_CODE_PREFIX.length() + 2);
    }
}
//--------------------------------------------------------------------------------------------------