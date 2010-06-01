/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.util;

import eu.baltrad.dex.model.user.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Class implementing session handling functionality and user authentication control.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class ApplicationSecurityManager {

//---------------------------------------------------------------------------------------- Constants
    // Session user attribute
    private static final String USER = "user";
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method authenticates user based on credentials provided in the login form.
     * Credentials are compared with corresponding user record in the database.
     *
     * @param formUser Current user signin in
     * @param dbUser User in the database
     * @return True if users are the same, false otherwise
     */
    public boolean authenticateFormUser( User formUser, User dbUser ) {

        if( formUser == null || dbUser == null ) {
            return false;
        } else {
            String formUserName = formUser.getName().trim();
            String dbUserName = dbUser.getName().trim();
            String formUserPassword = MessageDigestUtil.createHash( formUser.getPassword().trim() );
            String dbUserPassword = dbUser.getPassword().trim();
            if( formUserName.equals( dbUserName ) && formUserPassword.equals( dbUserPassword ) ) {
                return true;
            } else {
                return false;
            }
        }
    }
    /**
     * Method authenticates user based on session data. Credentials retrieved from session data
     * are compared with corresponding user record in the database. Method is used to control
     * user privileges in order to restrict access to administrative functionalities.
     *
     * @param sessionUser Currently signed user
     * @param dbUser User in the database
     * @return True if users are the same, false otherwise
     */
    public boolean authenticateSessionUser( User sessionUser, User dbUser ) {

        if( sessionUser == null || dbUser == null ) {
            return false;
        } else {
            String sessionUserName = sessionUser.getName().trim();
            String dbUserName = dbUser.getName().trim();
            String sessionUserPassword = sessionUser.getPassword().trim();
            String dbUserPassword = dbUser.getPassword().trim();
            if( sessionUserName.equals( dbUserName ) && sessionUserPassword.equals(
                    dbUserPassword ) ) {
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
}
//--------------------------------------------------------------------------------------------------
