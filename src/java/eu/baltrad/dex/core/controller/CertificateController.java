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

import eu.baltrad.dex.core.model.CertManager;
import eu.baltrad.dex.core.model.Cert;
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.util.ServletContextUtil;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.util.MessageDigestUtil;

import org.apache.log4j.Logger;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.security.cert.X509Certificate;
import java.security.Principal;

/**
 * Implements SSL certificate management. Trusted certificates are read from submitted files
 * and saved in the keystore.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.8
 * @since 0.7.8
 */
public class CertificateController extends SimpleFormController {
//---------------------------------------------------------------------------------------- Constants
    /** All certificates list key */
    private static final String ALL_CERTS = "allCerts";
    /** Selected certificates list key */
    private static final String TRUSTED_CERTS = "trustedCerts";
    /** Success message key */
    private static final String OK_MSG_KEY = "message";
    /** Error message key */
    private static final String ERROR_MSG_KEY = "error";
//---------------------------------------------------------------------------------------- Variables    
    /** Message logger */
    private Logger log;
    /** Certificate manager */
    private CertManager certManager;
    /** User manager */
    private UserManager userManager;
//------------------------------------------------------------------------------------------ Methods    
    /**
     * Constructor.
     */
    public CertificateController() {
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
    }
    /**
     * Creates hash map holding list of available certificates.
     * 
     * @param request HTTP servlet request
     * @return Hash map holding list of available certificates
     * @throws Exception 
     */
    @Override
    protected HashMap referenceData(HttpServletRequest request) throws Exception {
        HashMap model = new HashMap();
        model.put(ALL_CERTS, certManager.get());
        return model;
    }
    /**
     * Saves certificate configuration.
     * 
     * @param request Http servlet request 
     * @param response Http servlet response
     * @param command Command object
     * @param errors Errors
     * @return Model and view 
     */
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) {
        try {
            String ksFileName = ServletContextUtil.getServletContextPath() + 
                    InitAppUtil.KS_FILE_PATH;
            String ksPasswd = InitAppUtil.getConf().getKeystorePass();
            List<Cert> certs = certManager.get();
            String certIdsParm[] = request.getParameterValues(TRUSTED_CERTS);
            List certIds = null;
            if (certIdsParm != null) {
                certIds = Arrays.asList(certIdsParm);
                for (int i = 0; i < certs.size(); i++) {
                    Cert cert = certs.get(i);
                    if (certIds.contains(Integer.toString(cert.getId())) && 
                            userManager.getByName(cert.getAlias()) == null) {
                        cert.setTrusted(true);
                        X509Certificate x509Cert = certManager.loadFromFile(cert.getFilePath());
                        Principal principal = x509Cert.getSubjectX500Principal();
                        certManager.storeTrustedEntry(ksFileName, ksPasswd, x509Cert, 
                                    cert.getAlias());
                        userManager.saveOrUpdate(new User(cert.getAlias(), 
                            MessageDigestUtil.createHash(cert.getAlias()), User.ROLE_PEER,
                            certManager.getOrganization(principal.getName()),
                            certManager.getOrganizationUnit(principal.getName()),
                            certManager.getLocality(principal.getName()),
                            certManager.getState(principal.getName()),
                            certManager.getCountryCode(principal.getName()),
                            cert.getNodeAddress()));
                        log.warn("New peer account created: " + cert.getAlias());
                    }
                    if( !certIds.contains(Integer.toString(cert.getId())) &&
                            userManager.getByName(cert.getAlias()) != null) {
                        cert.setTrusted(false);
                        certManager.deleteFromKS(ksFileName, ksPasswd, cert.getAlias());
                        userManager.deleteUser(userManager.getByName(cert.getAlias()).getId());
                        log.warn("Peer account removed: " + cert.getAlias());
                    }
                    certManager.saveOrUpdate(cert);
                }
            } else {
                for (int i = 0; i < certs.size(); i++) {
                    Cert cert = certs.get(i);
                    if(userManager.getByName(cert.getAlias()) != null) {
                        cert.setTrusted(false);
                        certManager.deleteFromKS(ksFileName, ksPasswd, cert.getAlias());
                        userManager.deleteUser(userManager.getByName(cert.getAlias()).getId());
                        log.warn("Peer account removed: " + cert.getAlias());
                    }
                    certManager.saveOrUpdate(cert);
                }
            }
            request.getSession().setAttribute(OK_MSG_KEY, getMessageSourceAccessor().getMessage(
                    "message.savecert.savesuccess"));
        } catch(Exception e) {
            request.getSession().setAttribute(ERROR_MSG_KEY, getMessageSourceAccessor().getMessage(
                    "message.savecert.savesuccess"));
        }
        return new ModelAndView(getSuccessView());
    }
    /**
     * Certificate manager getter.
     * 
     * @return Reference to certificate manager
     */
    public CertManager getCertManager() { return certManager; }
    /**
     * Certificate manager setter.
     * 
     * @param certManager Reference to certificate manager to set
     */
    public void setCertManager(CertManager certManager) { this.certManager = certManager; }
    /**
     * User manager getter,
     * 
     * @return Reference to user manager 
     */
    public UserManager getUserManager() { return userManager; }
    /**
     * User manager setter.
     * 
     * @param userManager Reference to user manager to set
     */
    public void setUserManager(UserManager userManager) { this.userManager = userManager; }
}
//--------------------------------------------------------------------------------------------------
