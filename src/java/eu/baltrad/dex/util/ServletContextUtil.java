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

package eu.baltrad.dex.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Utility class implementing servlet context detection functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.0
 * @since 0.1.0
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