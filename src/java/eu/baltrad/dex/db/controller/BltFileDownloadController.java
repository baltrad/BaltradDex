/*******************************************************************************
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
*******************************************************************************/

package eu.baltrad.dex.db.controller;

import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.ApplicationSecurityManager;

import eu.baltrad.bdb.FileCatalog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.util.FileCopyUtils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Download controller class implementing data download functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class BltFileDownloadController implements Controller {
//-------------------------------------------------------------------- Constants
    public static final String ENTRY_UUID = "uuid";
//-------------------------------------------------------------------- Variables
    private Logger log;
    private FileCatalog fileCatalog;
    private String successView;
//---------------------------------------------------------------------- Methods
    /**
     * Controller.
     */
    public BltFileDownloadController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Method handles file download request.
     * 
     * @param request Http request
     * @param response Http response
     * @return Model and view
     */
    public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) {
        User user = (User) ApplicationSecurityManager.getUser(request);
        ServletContext servletContext = request.getSession().getServletContext();
        String entryUuid = request.getParameter(ENTRY_UUID);
        File fi = fileCatalog.getLocalPathForUuid(UUID.fromString(entryUuid));
        String filePath = fi.getAbsolutePath();
        String fileName = fi.getName();
        int fileSize = (int) fi.length();
        BufferedInputStream bis = null;
        if( fileSize > 0 ) {
            try {
                try {
                    bis = new BufferedInputStream(new FileInputStream(fi));
                    String mimeType = servletContext.getMimeType(filePath);
                    response.setBufferSize(fileSize);
                    response.setContentType(mimeType);
                    response.setHeader("Content-Disposition", "attachement; " +
                            "filename=\"" + fileName + "\"" );
                    response.setContentLength(fileSize);
                    FileCopyUtils.copy(bis, response. getOutputStream());
                    log.info("User " + user.getName() + " downloading file: " + 
                                                                      fileName);
                } finally {
                    response.getOutputStream().flush();
                    response.getOutputStream().close();
                    bis.close();
                }
            } catch (IOException e) {
                log.error("Failed to download file", e);
            } 
        } else {
            log.error("Invalid file size: " + fileSize);
        }
        return null;
    }
    /**
     * Method returns reference to success view name string.
     *
     * @return Reference to success view name string
     */
    public String getSuccessView() {
        return successView;
    }
    /**
     * Method sets reference to success view name string.
     *
     * @param Reference to success view name string
     */
    public void setSuccessView( String successView ) {
        this.successView = successView;
    }
    public FileCatalog getFileCatalog() { return fileCatalog; }

    public void setFileCatalog(FileCatalog fileCatalog) {
        this.fileCatalog = fileCatalog;
    }
}
//--------------------------------------------------------------------------------------------------