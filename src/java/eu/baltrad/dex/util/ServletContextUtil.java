/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Utility class implementing servlet context detection functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class ServletContextUtil implements ServletContextListener {

//---------------------------------------------------------------------------------------- Constants
    private static final String SERVLET_VIRTUAL_PATH = "/";
//---------------------------------------------------------------------------------------- Variables
    private static ServletContext servletContext;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method sets servlet context.
     *
     * @param contextEvent Servlet context event
     */
    public void contextInitialized( ServletContextEvent contextEvent ) {
		servletContext = contextEvent.getServletContext();
	}
    /**
     * Method sets servlet context.
     *
     * @param contextEvent Servlet context event
     */
	public void contextDestroyed( ServletContextEvent contextEvent ) {
		servletContext = contextEvent.getServletContext();
	}
    /**
     * Method determines real servlet context path.
     *
     * @return Real servlet context path
     */
    public static String getServletContextPath() {
        return servletContext.getRealPath( SERVLET_VIRTUAL_PATH );
    }
}
//--------------------------------------------------------------------------------------------------