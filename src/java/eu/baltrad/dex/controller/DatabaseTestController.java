/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.baltrad.dex.controller;

import eu.baltrad.dex.util.ServletContextUtil;
import eu.baltrad.dex.model.LogManager;
import eu.baltrad.fc.FileCatalog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.File;
import java.util.Date;


/**
 *
 * @author szewczenko
 */
public class DatabaseTestController implements Controller {

    private LogManager logManager;

    public DatabaseTestController() {}

    public ModelAndView handleRequest( HttpServletRequest request,
                            HttpServletResponse response ) throws ServletException, IOException {
        try {
            ServletContextUtil servletContextUtil = new ServletContextUtil();

            String servletCtx = servletContextUtil.getServletContextPath();
	    String storageDir = servletCtx + "FileCatalogStorage";
            File f = new File( storageDir );
            if( !f.exists() ) {
                f.mkdirs();
            }
            String databaseConnection = "postgresql://baltrad:baltrad@localhost:5432/baltrad_db";
            
            FileCatalog fc = new FileCatalog( databaseConnection, storageDir );

            // insert test data
            fc.catalog( servletCtx + "TestData/swi1.h5" );
            fc.catalog( servletCtx + "TestData/swi2.h5" );
            fc.catalog( servletCtx + "TestData/swi3.h5" );
            fc.catalog( servletCtx + "TestData/gda1.h5" );
            fc.catalog( servletCtx + "TestData/gda2.h5" );
            fc.catalog( servletCtx + "TestData/gda3.h5" );
            fc.catalog( servletCtx + "TestData/rze1.h5" );
            fc.catalog( servletCtx + "TestData/rze2.h5" );
            fc.catalog( servletCtx + "TestData/rze3.h5" );
            fc.catalog( servletCtx + "TestData/poz1.h5" );
            fc.catalog( servletCtx + "TestData/poz2.h5" );
            fc.catalog( servletCtx + "TestData/poz3.h5" );
            fc.catalog( servletCtx + "TestData/leg1.h5" );
            fc.catalog( servletCtx + "TestData/leg2.h5" );
            fc.catalog( servletCtx + "TestData/leg3.h5" );
            fc.catalog( servletCtx + "TestData/pas1.h5" );
            fc.catalog( servletCtx + "TestData/pas2.h5" );
            fc.catalog( servletCtx + "TestData/pas3.h5" );
            fc.catalog( servletCtx + "TestData/brz1.h5" );
            fc.catalog( servletCtx + "TestData/brz2.h5" );
            fc.catalog( servletCtx + "TestData/brz3.h5" );

            logManager.addLogEntry( new Date(), LogManager.MSG_WRN, "Successfully added test data "
                    + "to the file catalog" );

        } catch( Exception e ) {

            logManager.addLogEntry( new Date(), LogManager.MSG_ERR, "Failed to add test data to " +
                    "the file catalog: " + e.getMessage() );
            
        }
        return new ModelAndView();
    }

    /**
     * @return the logManager
     */
    public LogManager getLogManager() {
        return logManager;
    }

    /**
     * @param logManager the logManager to set
     */
    public void setLogManager(LogManager logManager) {
        this.logManager = logManager;
    }

}
