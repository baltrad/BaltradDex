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

package eu.baltrad.dex.config.model;

import eu.baltrad.dex.util.HibernateUtil;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

/**
 * Class implemens configuration object handling functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class ConfigurationManager {
//---------------------------------------------------------------------------------------- Constants
    // static record ID
    public static final int CONF_REC_ID = 1;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Fetches first record from Configuration table
     * 
     * @param id ID of the first record in the table
     * @return Configuration object
     */
    public Configuration getConfiguration( int id ) {
        Configuration conf = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            conf = ( Configuration )session.createQuery( 
                "FROM Configuration WHERE id = ?" ).setInteger( 0, id ).uniqueResult();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return conf;
    }
    /**
     * Saves configuration record in the databse.
     *
     * @param conf Configuration class object
     */
    public void saveConfiguration( Configuration conf ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate( conf );
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
}
//--------------------------------------------------------------------------------------------------
