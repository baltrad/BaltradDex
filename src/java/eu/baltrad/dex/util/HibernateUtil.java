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

package eu.baltrad.dex.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;
import java.io.InputStream;

/**
 * Utility class used to initialize Hibernate session factory.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class HibernateUtil {
//---------------------------------------------------------------------------------------- Constants
    // Properties file name
    private static final String PROPS_FILE_NAME = "dex.hibernate.properties";
//---------------------------------------------------------------------------------------- Variables
    // Hibernate session factory
    private static SessionFactory sessionFactory;
//------------------------------------------------------------------------------------------ Methods
    static {
        try {
            InputStream is = HibernateUtil.class.getResourceAsStream( PROPS_FILE_NAME );
            Properties props = new Properties();
            props.load( is );
            sessionFactory =
                        new Configuration().setProperties( props ).configure().buildSessionFactory();
        } catch( Throwable t ) {
            System.out.println( "Failed to create Hibernate SessionFactory:" + t.getMessage() );
            throw new ExceptionInInitializerError( t );
        }
    }
    /**
     * Method gets reference to session factory object.
     *
     * @return Reference to session factory object
     */
    public static SessionFactory getSessionFactory() { return sessionFactory; }
}
//--------------------------------------------------------------------------------------------------
