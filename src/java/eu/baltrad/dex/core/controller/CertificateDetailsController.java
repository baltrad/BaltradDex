/***************************************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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
import static eu.baltrad.frame.model.Protocol.*;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.cert.X509Certificate;

/**
 * Displays detailed information about selected SSL certificate. 
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.8
 * @since 0.7.8
 */
public class CertificateDetailsController implements Controller {
//---------------------------------------------------------------------------------------- Constants    
    /** Certificate key */
    private static final String CERT_KEY = "certId";
    /** Common name key  */
    private static final String COMMON_NAME_KEY = "commonName";
    /** Organization name key  */
    private static final String ORG_KEY = "organization";
    /** Unit name key  */
    private static final String UNIT_KEY = "unit";
    /** Locality name key  */
    private static final String LOCALITY_KEY = "locality";
    /** State name key  */
    private static final String STATE_KEY = "state";
    /** Country code key  */
    private static final String COUNTRY_CODE_KEY = "countryCode";
    /** Fingerprint key  */
    private static final String FINGERPRINT_KEY = "fingerprint";
    /** Error message key */
    private static final String ERROR_MSG_KEY = "error";
    /** Error message */
    private static final String ERROR_MSG = "Failed to read certificate details";
//---------------------------------------------------------------------------------------- Variables    
    /** Success view */
    private String successView;
    /** Certificate manager */
    private CertManager certManager;
    /** Message logger */
    private Logger log;
//------------------------------------------------------------------------------------------ Methods    
    /**
     * Constructor.
     */
    public CertificateDetailsController() {
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
    }
    /**
     * Handles HTTP requests.
     * 
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return Model and view 
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView(getSuccessView());
        try {
            int certId = Integer.parseInt(request.getParameter(CERT_KEY));
            Cert cert = certManager.get(certId);
            X509Certificate x509Cert = certManager.loadFromFile(cert.getFilePath());
            String commonName = certManager.getCommonName(x509Cert.getIssuerDN().getName());
            String orgName = certManager.getOrganization(x509Cert.getIssuerDN().getName());
            String unitName = certManager.getOrganizationUnit(x509Cert.getIssuerDN().getName());
            String locality = certManager.getLocality(x509Cert.getIssuerDN().getName());
            String state = certManager.getState(x509Cert.getIssuerDN().getName());
            String countryCode = certManager.getCountryCode(x509Cert.getIssuerDN().getName());
            String fingerprint = getCertFingerprint(x509Cert);
            modelAndView.addObject(COMMON_NAME_KEY, commonName);
            modelAndView.addObject(ORG_KEY, orgName);
            modelAndView.addObject(UNIT_KEY, unitName);
            modelAndView.addObject(LOCALITY_KEY, locality);
            modelAndView.addObject(STATE_KEY, state);
            modelAndView.addObject(COUNTRY_CODE_KEY, countryCode);
            modelAndView.addObject(FINGERPRINT_KEY, fingerprint);
        } catch(Exception e) {
            modelAndView.addObject(ERROR_MSG_KEY, ERROR_MSG);
            log.error(ERROR_MSG, e);
        }
        return modelAndView;
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
     * Gets success view.
     * 
     * @return Success view
     */
    public String getSuccessView() { return successView; }
    /**
     * Sets success view. 
     * 
     * @param successView Success view to set
     */
    public void setSuccessView(String successView) { this.successView = successView; }
}
//--------------------------------------------------------------------------------------------------