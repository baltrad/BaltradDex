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

import eu.baltrad.dex.model.channel.Channel;
import eu.baltrad.dex.model.channel.ChannelManager;

/**
 * Validator class used to validate add channel form input.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class SaveChannelValidator implements Validator {
//---------------------------------------------------------------------------------------- Variables
    private ChannelManager channelManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Declares classes supported by this validator.
     *
     * @param aClass Class instance of supported type
     * @return True if class is supported, false otherwise
     */
    public boolean supports( Class aClass ) {
        return Channel.class.equals( aClass );
    }
    /**
     * Validates form object.
     *
     * @param obj Form object
     * @param errors Errors object
     */
    public void validate( Object command, Errors errors ) {
        Channel channel = ( Channel )command;
        if( channel == null ) return;
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "name", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "wmoNumber", "error.field.required" );
    }
    /**
     * Method returns reference to data channel manager object.
     *
     * @return Reference to data channel manager object
     */
    public ChannelManager getChannelManager() { return channelManager; }
    /**
     * Method sets reference to data channel manager object.
     *
     * @param Reference to data channel manager object
     */
    public void setChannelManager( ChannelManager channelManager ) {
        this.channelManager = channelManager;
    }
}
//--------------------------------------------------------------------------------------------------