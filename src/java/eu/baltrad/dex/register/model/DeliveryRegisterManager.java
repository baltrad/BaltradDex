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

package eu.baltrad.dex.register.model;

import eu.baltrad.dex.subscription.model.Subscription;
import eu.baltrad.dex.util.HibernateUtil;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import java.util.List;

/**
 * Class implements data delivery register handling functionality..
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class DeliveryRegisterManager {
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method retrieves single entry from data delivery register.
     * 
     * @param userId User id
     * @param uuid File's identity string
     * @return Single entry from data delivery register
     */
    public DeliveryRegisterEntry getEntry( int userId, String uuid ) {
        DeliveryRegisterEntry deliveryRegisterEntry = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            deliveryRegisterEntry = ( DeliveryRegisterEntry )session.createQuery(
                    "FROM DeliveryRegisterEntry" + " WHERE userId = ?" +
                    " AND hashCode = ?" ).setInteger( 0, userId ).setString(
                    1, uuid ).uniqueResult();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return deliveryRegisterEntry;
    }
    /**
     * Method gets all available elivery register entries.
     *
     * @return List object containing all available delivery register entries
     */
    public List getAllEntries() {
        List regEntries = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            regEntries = session.createQuery( "FROM DeliveryRegisterEntry" ).list();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return regEntries;
    }
    /**
     * Method adds an entry to data delivery register.
     *
     * @param dataDeliveryRegister Data delivery register entry
     */
    public void addEntry( DeliveryRegisterEntry deliveryRegisterEntry ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate( deliveryRegisterEntry );
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
    /**
     * Method deletes given entry from data delivery register.
     *
     * @param id Register entry ID
     */
    public void deleteEntry( int id ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
	    session.beginTransaction();
        try {
            session.delete( session.load( Subscription.class, new Integer( id ) ) );
            session.flush();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
    }
    /**
     * Deletes all rows from data delivery register table.
     *
     * @return Number of deleted records
     */
    public int deleteAllEntries() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        int deletedEntries = 0;
	    session.beginTransaction();
        try {
            deletedEntries = session.createQuery(
                    "DELETE FROM DeliveryRegisterEntry" ).executeUpdate();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return deletedEntries;
    }
}
//--------------------------------------------------------------------------------------------------
