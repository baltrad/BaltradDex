/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.util;


import pl.imgw.baltrad.dex.model.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Class implementing session handling functionality and user authentication control.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class ApplicationSecurityManager {

    // Session user attribute
    private static final String USER = "user";

    // Message digest util
    private MessageDigestUtil messageDigestUtil;


    /**
     * Method compares current user with corresponding user in the database.
     *
     * @param formUser Current user trying to sign in
     * @param dbUser User in the database
     * @return True if users are the same, false otherwise
     */
    public boolean compareUsers( User formUser, User dbUser ) {

        if( formUser == null || dbUser == null ) {
            return false;
        } else {
            String formUserName = formUser.getName().trim();
            String dbUserName = dbUser.getName().trim();
            String formUserPassword = messageDigestUtil.createHash( formUser.getPassword().trim() );
            String dbUserPassword = dbUser.getPassword().trim();
            if( formUserName.equals( dbUserName ) && formUserPassword.equals( dbUserPassword ) ) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Method gets user session attribute.
     *
     * @param request Http request
     * @return User attribute
     */
    public Object getUser( HttpServletRequest request ) {
        return request.getSession( true ).getAttribute( USER );
    }
    /**
     * Method sets user session attribute.
     *
     * @param request Http request
     * @param user User object
     */
    public void setUser( HttpServletRequest request, Object user ) {
        request.getSession( true ).setAttribute( USER, user );
    }
    /**
     * Method removes user session attribute.
     *
     * @param request Http request
     */
    public void removeUser( HttpServletRequest request ) {
        request.getSession( true ).removeAttribute( USER );
    }

    /**
     * Method returns reference to message digest utility object.
     *
     * @return Reference to message digest utility object
     */
    public MessageDigestUtil getMessageDigestUtil() {
        return messageDigestUtil;
    }

    /**
     * Method sets reference to message digest utility object.
     *
     * @param messageDigestUtil Reference to message digest utility object
     */
    public void setMessageDigestUtil( MessageDigestUtil messageDigestUtil ) {
        this.messageDigestUtil = messageDigestUtil;
    }

}
