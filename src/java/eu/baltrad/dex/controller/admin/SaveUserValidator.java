/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller.admin;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import eu.baltrad.dex.model.user.User;
import eu.baltrad.dex.model.user.UserManager;

/**
 * Validator class used to validate input from add user form.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class SaveUserValidator implements Validator {
//---------------------------------------------------------------------------------------- Variables
    private UserManager userManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Declares classes supported by this validator.
     * 
     * @param aClass Class instance of supported type
     * @return True if class is supported, false otherwise
     */
    public boolean supports( Class aClass ) {
        return User.class.equals( aClass );
    }
    /**
     * Validates form object.
     *
     * @param obj Form object
     * @param errors Errors object
     */
    public void validate( Object command, Errors errors ) {
        User user = ( User )command;
        if( user == null ) return;
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "name", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "password", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "retPassword", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "nodeAddress", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "factory", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "country", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "city", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "cityCode", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "street", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "number", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "phone", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "email", "error.field.required" );
        // validate user name
        //if( userManager.userExists( user.getName() ) ) {
        //    errors.rejectValue( "name", "error.field.user.nameexists" );
        //}
        // validate password
        if( !errors.hasFieldErrors( "password" ) && !errors.hasFieldErrors( "retPassword" ) ) {
            if( !user.getPassword().trim().equals( user.getRetPassword().trim() ) ) {
                errors.rejectValue( "retPassword", "error.field.passwd.mismatch" );
            }      
        }
        if( user.getPassword().trim().length() > 0 && user.getPassword().trim().length() < 6 ) {
            errors.rejectValue( "password", "error.field.passwd.tooshort" );
        }
        // validate user role
        if( user.getRole().toLowerCase().equals( User.ROLE_0 )
                || user.getRole().toLowerCase().equals( User.ROLE_1 ) ) {
            if( userManager.getUserByRole( user.getRole().toLowerCase() ).getRole().equals(
                User.ROLE_0 )
                || userManager.getUserByRole( user.getRole().toLowerCase() ).getRole().equals(
                User.ROLE_1 ) ) {
                errors.rejectValue( "role", "error.field.user.roleexists" );
            }
        }
        // validate node address
        if( user.getNodeAddress().trim().length() > 0
                && user.getNodeAddress().trim().length() < 6 ) {
            errors.rejectValue( "nodeAddress", "error.field.nodeaddress.invalid" );
        }
        // validate email address
        if( user.getEmail().trim().length() > 0 && user.getEmail().trim().length() < 6 ) {
            errors.rejectValue( "email", "error.field.email.invalid" );
        }
    }
    /**
     * Method gets reference to user manager object.
     *
     * @return Reference to user manager object
     */
    public UserManager getUserManager() { return userManager; }
    /**
     * Method sets reference to user manager object.
     *
     * @param userManager Reference to user manager object
     */
    public void setUserManager( UserManager userManager ) { this.userManager = userManager; }
}
//--------------------------------------------------------------------------------------------------