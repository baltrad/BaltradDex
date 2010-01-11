/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.util;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import pl.imgw.baltrad.dex.model.User;

/**
 * Class implementing functionality for validating user authentication information.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class SignInValidator implements Validator {

    /**
     * Method implemented by the Validator interface
     *
     * @param clazz User class object
     * @return True if classes are equal
     */
    public boolean supports( Class clazz ) {
        return clazz.equals( User.class );
    }
    /**
     * Method validating user object.
     *
     * @param command Command object
     * @param errors Errors
     * @return True if validation is successful, false otherwise
     */
    public void validate( Object command, Errors errors ) {

        User user = ( User )command;
        if( user == null ) return;
        String userName = user.getName().trim();
        String password = user.getPassword().trim();

        if( userName == null || password == null || userName.isEmpty() || password.isEmpty() ) {
            errors.reject( "error.login.invalid" );
   		}
    }

}