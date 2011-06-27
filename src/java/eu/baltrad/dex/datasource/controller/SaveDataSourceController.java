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

package eu.baltrad.dex.datasource.controller;

import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.radar.model.RadarManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.DataSourceManager;
import eu.baltrad.dex.datasource.model.FileObject;
import eu.baltrad.dex.datasource.model.FileObjectManager;
import eu.baltrad.dex.datasource.model.DataQuantity;
import eu.baltrad.dex.datasource.model.DataQuantityManager;
import eu.baltrad.dex.datasource.model.Product;
import eu.baltrad.dex.datasource.model.ProductManager;
import eu.baltrad.dex.datasource.model.ProductParameter;
import eu.baltrad.dex.datasource.model.ProductParameterManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.log.model.MessageLogger;

import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.db.AttributeFilter;
import eu.baltrad.beast.db.CoreFilterManager;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.sql.SQLException;

/**
 * Allows to configure new data source or to modify an existing one.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class SaveDataSourceController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    








    /** Available file objects key */
    private static final String AVAILABLE_FILE_OBJECTS_KEY = "available_file_objects";
    /** Number of available file_objects key */
    private static final String NUMBER_OF_AVAILABLE_FILE_OBJECTS_KEY = 
            "number_of_available_file_objects";
    /** Selected file objects key */
    private static final String SELECTED_FILE_OBJECTS_KEY = "selected_file_objects";
    /** Number of selected file objects key */
    private static final String NUMBER_OF_SELECTED_FILE_OBJECTS_KEY = 
            "number_of_selected_file_objects";
    /** File object list parameter key */
    private static final String FILE_OBJECT_LIST_PARAMETER_KEY = "file_object_list";
    /** Add file object operation key */
    private static final String ADD_FILE_OBJECT_KEY = "add_object";
    /** Remove file object operation key */
    private static final String REMOVE_FILE_OBJECT_KEY = "remove_object";

    /** Available data quantities key */
    private static final String AVAILABLE_DATA_QUANTITIES_KEY = "available_data_quantities";
    /** Number of available data quantities key */
    private static final String NUMBER_OF_AVAILABLE_DATA_QUANTITIES_KEY =
            "number_of_available_data_quantities";
    /** Selected data quantities key */
    private static final String SELECTED_DATA_QUANTITIES_KEY = "selected_data_quantities";
    /** Number of selected data quantities key */
    private static final String NUMBER_OF_SELECTED_DATA_QUANTITIES_KEY =
            "number_of_selected_data_quantities";
    /** Data quantity list parameter key */
    private static final String DATA_QUANTITY_LIST_PARAMETER_KEY = "data_quantity_list";
    /** Add data quantity operation key */
    private static final String ADD_DATA_QUANTITY_KEY = "add_quantity";
    /** Remove data quantity operation key */
    private static final String REMOVE_DATA_QUANTITY_KEY = "remove_quantity";

    /** Available products key */
    private static final String AVAILABLE_PRODUCTS_KEY = "available_products";
    /** Number of available products key */
    private static final String NUMBER_OF_AVAILABLE_PRODUCTS_KEY = "number_of_available_products";
    /** Selected products key */
    private static final String SELECTED_PRODUCTS_KEY = "selected_products";
    /** Number of selected products key */
    private static final String NUMBER_OF_SELECTED_PRODUCTS_KEY = "number_of_selected_products";
    /** Product list parameter key */
    private static final String PRODUCT_LIST_PARAMETER_KEY = "product_list";
    /** Add product operation key */
    private static final String ADD_PRODUCT_KEY = "add_product";
    /** Remove product operation key */
    private static final String REMOVE_PRODUCT_KEY = "remove_product";

    /** Available product parameters key */
    private static final String AVAILABLE_PRODUCT_PARAMETERS_KEY = "available_product_parameters";
    /** Products parameters values key */
    private static final String SELECTED_PRODUCT_PARAMETER_VALUES_KEY =
            "selected_product_parameter_values";
    /** Add product parameter operation key */
    private static final String ADD_PRODUCT_PARAMETER_KEY = "add_product_parameter";
    /** Product parameter name key */
    private static final String PRODUCT_PARAMETER_NAME_KEY = "product_parameter_name";
    /** Product parameter value key */
    private static final String PRODUCT_PARAMETER_VALUE_KEY = "product_parameter_value";

    /** Default option on the list */
    private static final String DEFAULT_LIST_OPTION_KEY = "select";
    /** Data source review page */
    private static final String SAVE_DATA_SOURCE_STATUS_PAGE = "saveDataSourceStatus";
    /** Data source name parameter */
    private static final String DATA_SOURCE_NAME_KEY = "name";
    /** Data source description parameter */
    private static final String DATA_SOURCE_DESCRIPTION_KEY = "description";

    /** Radar selection error key */
    private static final String SELECT_RADARS_ERROR_KEY = "select_radars_error";
    /** User selection error key */
    private static final String SELECT_USERS_ERROR_KEY = "select_users_error";
    /** Submit form key */
    private static final String SUBMIT_FORM_KEY = "submit_form";
    








    /** Save data source name view */
    private static final String DS_SAVE_NAME_VIEW = "dsSaveName";
    /** Save data source radars view */
    private static final String DS_SAVE_RADARS_VIEW = "dsSaveRadars";
    /** Save data source users view */
    private static final String DS_SAVE_USERS_VIEW = "dsSaveUsers";
    /** Save data source summary view */
    private static final String DS_SAVE_SUMMARY_VIEW = "dsSaveSummary";
    /** Save data source view */
    private static final String DS_SAVE_VIEW = "dsSave";

    /** Data source name parameter key */
    private static final String DS_NAME_KEY = "dsName";
    /** Data source description parameter key */
    private static final String DS_DESCRIPTION_KEY = "dsDescription";

    /** Data source name error key */
    private static final String DS_NAME_ERROR_KEY = "dsNameError";
    /** Data source name error message */
    private static final String DS_NAME_ERROR_MSG = "Data source name is required";
    /** Data source description error key */
    private static final String DS_DESCRIPTION_ERROR_KEY = "dsDescriptionError";
    /** Data source description error message */
    private static final String DS_DESCRIPTION_ERROR_MSG = "Data source description is required";
    /* Radar station selection error key */
    private static final String DS_SELECT_RADARS_ERROR_KEY = "dsSelectRadarsError";
    /* Radar station selection error message */
    private static final String DS_SELECT_RADARS_ERROR_MSG = "At least one radar station is required";
    /* User selection error key */
    private static final String DS_SELECT_USERS_ERROR_KEY = "dsSelectUsersError";
    /* User selection error message */
    private static final String DS_SELECT_USERS_ERROR_MSG = "At least one user is required";
    /** Generic error message key */
    private static final String ERROR_MSG_KEY = "error";
    /** Generic success message key */
    private static final String OK_MSG_KEY = "message";


    /** Next button key */
    private static final String DS_NEXT_BUTTON_KEY = "nextButton";
    /** Back button key */
    private static final String DS_BACK_BUTTON_KEY = "backButton";

    /** Available radars key */
    private static final String DS_AVAILABLE_RADARS_KEY = "availableRadars";
    /** Number of available radars key */
    private static final String DS_NUM_AVAILABLE_RADARS_KEY = "numAvailableRadars";
    /** Selected radars key */
    private static final String DS_SELECTED_RADARS_KEY = "selectedRadars";
    /** Number of selected radars key */
    private static final String DS_NUM_SELECTED_RADARS_KEY = "numSelectedRadars";
    /** Radar list parameter key */
    private static final String DS_RADARS_LIST_KEY = "radarsList";
    /** Add radar operation key */
    private static final String DS_ADD_RADAR_KEY = "addRadar";
    /** Remove radar operation key */
    private static final String DS_REMOVE_RADAR_KEY = "removeRadar";

    /** Available users key */
    private static final String DS_AVAILABLE_USERS_KEY = "availableUsers";
    /** Number of available users key */
    private static final String DS_NUM_AVAILABLE_USERS_KEY = "numAvailableUsers";
    /** Selected users key */
    private static final String DS_SELECTED_USERS_KEY = "selectedUsers";
    /** Number of selected users key */
    private static final String DS_NUM_SELECTED_USERS_KEY = "numSelectedUsers";
    /** User list parameter key */
    private static final String DS_USERS_LIST_KEY = "usersList";
    /** Add user operation key */
    private static final String DS_ADD_USER_KEY = "addUser";
    /** Remove user operation key */
    private static final String DS_REMOVE_USER_KEY = "removeUser";

    /** ODIM what/source attribute key */
    private static final String DS_SOURCE_ATTR_STR = "what/source:WMO";

//---------------------------------------------------------------------------------------- Variables
    /** References data source manager */
    private static DataSourceManager dataSourceManager;
    /** References radar manager */
    private static RadarManager channelManager;
    /** References user manager */
    private static UserManager userManager;
     /** References CoreFilterManager */
    private CoreFilterManager coreFilterManager;
    /** References message logger */
    private static Logger log;

    /** Data source name */
    private String dsName;
    /** Data source description */
    private String dsDescription;

    /** Number of radars used to configure a data source */
    private static int numSelectedRadars;
    /** Number of all available radars */
    //private static int numAvailableRadars;
    /** List holding all available radars */
    //private List<Radar> availableRadars;
    /** List holding radars selected for data source configuration */
    private List<Radar> selectedRadars;

    /** Number of users allowed to use this data source */
    private static int numSelectedUsers;
    /** Number of all available users */
    //private static int numAvailableUsers;
    /** List holding all available users */
    //private List<User> availableUsers;
    /** List holding selected users */
    private List<User> selectedUsers;






    
    
    /** References file object manager */
    private static FileObjectManager fileObjectManager;
    /** References data quantity manager */
    private static DataQuantityManager dataQuantityManager;
    /** References product manager */
    private static ProductManager productManager;
    /** References product parameter manager */
    private static ProductParameterManager productParameterManager;
    

    

    /** Number of file objects used to configure a data source */
    private static int numberOfSelectedFileObjects;
    /** Number of all available file objects */
    private static int numberOfAvailableFileObjects;
    /** List holding all available file objects */
    private List<FileObject> availableFileObjects;
    /** List holding selected file objects */
    private List<FileObject> selectedFileObjects;

    /** Number of data quantities used to configure a data source */
    private static int numberOfSelectedDataQuantities;
    /** Number of all available data quantities */
    private static int numberOfAvailableDataQuantities;
    /** List holding all available data quantities */
    private List<DataQuantity> availableDataQuantities;
    /** List holding selected data quantities */
    private List<DataQuantity> selectedDataQuantities;

    /** Number of products used to configure a data source */
    private static int numberOfSelectedProducts;
    /** Number of all available products */
    private static int numberOfAvailableProducts;
    /** List holding all available products */
    private List<Product> availableProducts;
    /** List holding selected products */
    private List<Product> selectedProducts;

    /** List holding all available product parameters */
    private List<ProductParameter> availableProductParameters;
    /** Hash map storing product parameters values*/
    private HashMap<String, String> selectedProductParameterValues;

    
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public SaveDataSourceController() {
        log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
        dataSourceManager = new DataSourceManager();
        //
        channelManager = new RadarManager();
        //availableRadars = channelManager.getChannels();
        //numAvailableRadars = availableRadars.size();
        selectedRadars = new ArrayList<Radar>();
        numSelectedRadars = 0;
        //
        userManager = new UserManager();
        //availableUsers = userManager.getUsers();
        //numAvailableUsers = availableUsers.size();
        selectedUsers = new ArrayList<User>();
        numSelectedUsers = 0;
    }
    /**
     * Renders data source name and description page.
     *
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @return The prepared model and view
     */
    public ModelAndView dsSaveName( HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView modelAndView = new ModelAndView();
        
        // Check if data source name is given as a request parameter
        if( request.getParameter( DS_NAME_KEY ) != null
                && !request.getParameter( DS_NAME_KEY ).isEmpty() ) {
            // Clear the lists
            selectedRadars.clear();
            selectedUsers.clear();
            // Recover data source parameters for modification
            String dataSourceName = request.getParameter( DS_NAME_KEY );
            try {
                DataSource dataSource = dataSourceManager.getDataSource( dataSourceName );
                dsName = dataSource.getName();
                dsDescription = dataSource.getDescription();
                setDSNameModel( modelAndView );
                List<Integer> radarIds = dataSourceManager.getRadarIds( dataSource.getId() );
                for( int i = 0; i < radarIds.size(); i++ ){
                    selectedRadars.add( channelManager.getChannel( radarIds.get( i ) ) );
                }
                numSelectedRadars = selectedRadars.size();
                List<Integer> userIds = dataSourceManager.getUserIds( dataSource.getId() );
                for( int i = 0; i< userIds.size(); i++ ){
                    selectedUsers.add( userManager.getUserById( userIds.get( i ) ) );
                }
                numSelectedUsers = selectedUsers.size();
            } catch( SQLException e ) {
                String msg = "Failed to recover data source parameters: " + e.getMessage();
                modelAndView.addObject( ERROR_MSG_KEY, msg );
                log.error( msg );
            } catch( Exception e ) {
                String msg = "Failed to recover data source parameters: " + e.getMessage();
                modelAndView.addObject( ERROR_MSG_KEY, msg );
                log.error( msg );
            }
        }
        modelAndView.setViewName( DS_SAVE_NAME_VIEW );
        return modelAndView;
    }
    /**
     * Renders radar station selection page.
     *
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @return The prepared model and view
     */
    public ModelAndView dsSaveRadars( HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView modelAndView = new ModelAndView();
        if( request.getParameter( DS_NAME_KEY ) != null &&
                !request.getParameter( DS_NAME_KEY ).isEmpty() ) {
            dsName = request.getParameter( DS_NAME_KEY );
        }
        if( request.getParameter( DS_DESCRIPTION_KEY ) != null
                && !request.getParameter( DS_DESCRIPTION_KEY ).isEmpty() ) {
            dsDescription = request.getParameter( DS_DESCRIPTION_KEY );
        }
        if( dsName == null || dsName.trim().isEmpty() ) {
            modelAndView.addObject( DS_NAME_ERROR_KEY, DS_NAME_ERROR_MSG );
            modelAndView.setViewName( DS_SAVE_NAME_VIEW );
        }
        if( dsDescription == null || dsDescription.trim().isEmpty() ) {
            modelAndView.addObject( DS_DESCRIPTION_ERROR_KEY, DS_DESCRIPTION_ERROR_MSG );
            modelAndView.setViewName( DS_SAVE_NAME_VIEW );
        }
        if( dsName != null && !dsName.trim().isEmpty() && dsDescription != null &&
                !dsDescription.trim().isEmpty() ) {
            modelAndView.setViewName( DS_SAVE_RADARS_VIEW );
        }
        setDSNameModel( modelAndView );
        setDSRadarsModel( modelAndView );
        return modelAndView;
    }
    /**
     * Renders users selection page.
     *
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @return The prepared model and view
     */
    public ModelAndView dsSaveUsers( HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView modelAndView = new ModelAndView();
        // Get parameter map
        Map parameterMap = request.getParameterMap();
        String[] parameterValues = null;
        // Add radar
        if( parameterMap.containsKey( DS_ADD_RADAR_KEY ) ) {
            parameterValues = ( String[] )parameterMap.get( DS_RADARS_LIST_KEY );
            addSelectedRadar( parameterValues[ 0 ] );
            modelAndView.setViewName( DS_SAVE_RADARS_VIEW );
            setDSRadarsModel( modelAndView );
        }
        // Remove radar
        if( parameterMap.containsKey( DS_REMOVE_RADAR_KEY ) ) {
            removeSelectedRadar();
            modelAndView.setViewName( DS_SAVE_RADARS_VIEW );
            setDSRadarsModel( modelAndView );
        }
        // Go to next page
        if( parameterMap.containsKey( DS_NEXT_BUTTON_KEY ) ) {
            modelAndView.setViewName( DS_SAVE_USERS_VIEW );
        }
        // Go to previous page
        if( parameterMap.containsKey( DS_BACK_BUTTON_KEY ) ) {
            modelAndView.setViewName( DS_SAVE_NAME_VIEW );
            setDSNameModel( modelAndView );
        }
        // At least one radar station has to be selected
        if( selectedRadars.size() == 0 ) {
            modelAndView.setViewName( DS_SAVE_RADARS_VIEW );
            modelAndView.addObject( DS_SELECT_RADARS_ERROR_KEY, DS_SELECT_RADARS_ERROR_MSG );
            setDSRadarsModel( modelAndView );
        }
        setDSUsersModel( modelAndView );
        return modelAndView;
    }
    /**
     * Renders save data source summary page.
     *
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @return The prepared model and view
     */
    public ModelAndView dsSaveSummary( HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView modelAndView = new ModelAndView();
        // Get parameter map
        Map parameterMap = request.getParameterMap();
        String[] parameterValues = null;
        // Add user
        if( parameterMap.containsKey( DS_ADD_USER_KEY ) ) {
            parameterValues = ( String[] )parameterMap.get( DS_USERS_LIST_KEY );
            addSelectedUser( parameterValues[ 0 ] );
            modelAndView.setViewName( DS_SAVE_USERS_VIEW );
            setDSUsersModel( modelAndView );
        }
        // Remove user
        if( parameterMap.containsKey( DS_REMOVE_USER_KEY ) ) {
            removeSelectedUser();
            modelAndView.setViewName( DS_SAVE_USERS_VIEW );
            setDSUsersModel( modelAndView );
        }
        // Go to next page
        if( parameterMap.containsKey( DS_NEXT_BUTTON_KEY ) ) {
            modelAndView.setViewName( DS_SAVE_SUMMARY_VIEW );
            setDSNameModel( modelAndView );
            setDSRadarsModel( modelAndView );
            setDSUsersModel( modelAndView );
        }
        // Go to previous page
        if( parameterMap.containsKey( DS_BACK_BUTTON_KEY ) ) {
            modelAndView.setViewName( DS_SAVE_RADARS_VIEW );
            setDSRadarsModel( modelAndView );
        }
        // At least one user has to be selected
        if( selectedUsers.size() == 0 ) {
            modelAndView.setViewName( DS_SAVE_USERS_VIEW );
            modelAndView.addObject( DS_SELECT_USERS_ERROR_KEY, DS_SELECT_USERS_ERROR_MSG );
            setDSUsersModel( modelAndView );
        }
        return modelAndView;
    }
    /**
     * Saves data source.
     *
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @return The prepared model and view
     */
    public ModelAndView dsSave( HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView modelAndView = new ModelAndView();
        // Get parameter map
        Map parameterMap = request.getParameterMap();
        // Go to next page
        if( parameterMap.containsKey( DS_NEXT_BUTTON_KEY ) ) {
            // Save data source
            DataSource dataSource = new DataSource( dsName, dsDescription );
            try {
                int saved = dataSourceManager.saveOrUpdate( dataSource );
                // Delete filters if exist - important upon data source modification
                int dataSourceId = dataSourceManager.getDataSource( dsName ).getId();
                int filterId = dataSourceManager.getFilterId( dataSourceId );
                if( filterId > 0 ) {
                    IFilter filter = coreFilterManager.load( filterId );
                    coreFilterManager.remove( filter );
                }
                if( saved > 0 ) {
                    // Initialize filter
                    AttributeFilter sourceFilter = new AttributeFilter();
                    sourceFilter.setAttribute( DS_SOURCE_ATTR_STR );
                    sourceFilter.setValueType( AttributeFilter.ValueType.STRING );
                    sourceFilter.setOperator( AttributeFilter.Operator.IN );

                    // Save data source radars
                    int dsId = ( dataSourceManager.getDataSource( dsName ) ).getId();
                    dataSourceManager.deleteRadars( dsId );
                    String wmoAttrValue = "";
                    for( int i = 0; i < selectedRadars.size(); i++ ) {
                        String radarName = selectedRadars.get( i ).getChannelName();
                        int radarId = ( channelManager.getChannel( radarName ) ).getId();
                        String wmoNumber = ( channelManager.getChannel( radarName ) ).getWmoNumber();
                        dataSourceManager.saveRadar( dsId, radarId );
                        // Set filter values
                        wmoAttrValue += wmoNumber + ",";
                    }
                    wmoAttrValue = wmoAttrValue.substring( 0, wmoAttrValue.lastIndexOf( "," ) );
                    // Save data source users
                    dataSourceManager.deleteUsers( dsId );
                    for( int i = 0; i < selectedUsers.size(); i++ ) {
                        String userName = selectedUsers.get( i ).getName();
                        int userId = ( userManager.getUserByName( userName ) ).getId();
                        dataSourceManager.saveUser( dsId, userId );
                    }
                    sourceFilter.setValue( wmoAttrValue );
                    // Save filter
                    coreFilterManager.store( sourceFilter );
                    dataSourceManager.deleteFilters( dsId );
                    dataSourceManager.saveFilter( dsId, sourceFilter.getId() );
                }
                String msg = "Data source successfully saved: " + dataSource.getName();
                modelAndView.addObject( OK_MSG_KEY, msg );
                log.warn( msg );
            } catch( SQLException e ) {
                String msg = "Failed to save data source: " + e.getMessage();
                modelAndView.addObject( ERROR_MSG_KEY, msg );
                log.error( msg );
            } catch( Exception e ) {
                String msg = "Failed to save data source: " + e.getMessage();
                modelAndView.addObject( ERROR_MSG_KEY, msg );
                log.error( msg );
            }
            resetModel();
            modelAndView.setViewName( DS_SAVE_VIEW );
        }
        // Go to previous page
        if( parameterMap.containsKey( DS_BACK_BUTTON_KEY ) ) {
            modelAndView.setViewName( DS_SAVE_USERS_VIEW );
            setDSUsersModel( modelAndView );
        }
        return modelAndView;
    }
    /**
     * Fills ModelAndView with objects representing data source name and description.
     * 
     * @param modelAndView ModelAndView to be filled
     */
    private void setDSNameModel( ModelAndView modelAndView ) {
        modelAndView.addObject( DS_NAME_KEY, dsName );
        modelAndView.addObject( DS_DESCRIPTION_KEY, dsDescription );
    }
    /**
     * Fills ModelAndView with radar station parameters.
     *
     * @param modelAndView ModelAndView to be filled
     */
    private void setDSRadarsModel( ModelAndView modelAndView ) {
        modelAndView.addObject( DS_AVAILABLE_RADARS_KEY, channelManager.getChannels() );
        modelAndView.addObject( DS_NUM_AVAILABLE_RADARS_KEY, channelManager.getChannels().size() );
        modelAndView.addObject( DS_SELECTED_RADARS_KEY, selectedRadars );
        modelAndView.addObject( DS_NUM_SELECTED_RADARS_KEY, numSelectedRadars );
    }
    /**
     * Fills ModelAndView with users.
     *
     * @param modelAndView ModelAndView to be filled
     */
    private void setDSUsersModel( ModelAndView modelAndView ) {
        modelAndView.addObject( DS_AVAILABLE_USERS_KEY, userManager.getUsers() );
        modelAndView.addObject( DS_NUM_AVAILABLE_USERS_KEY, userManager.getUsers().size() );
        modelAndView.addObject( DS_SELECTED_USERS_KEY, selectedUsers );
        modelAndView.addObject( DS_NUM_SELECTED_USERS_KEY, numSelectedUsers );
    }
    /**
     * Resets model parameters.
     */
    private void resetModel() {
        selectedRadars.clear();
        numSelectedRadars = 0;
        //
        selectedUsers.clear();
        numSelectedUsers = 0;
    }
    /**
     * Increments number of radars used to configure data source.
     *
     * @param radarName Radar station name
     */
    private  void addSelectedRadar( String radarName ) {
        if( numSelectedRadars < channelManager.getChannels().size() &&
                !radarName.equals( DEFAULT_LIST_OPTION_KEY ) ) {
            if( !selectedRadars.contains( channelManager.getChannel( radarName ) ) ) {
                ++numSelectedRadars;
                selectedRadars.add( channelManager.getChannel( radarName ) );
            }
        }
    }
    /**
     * Decrements number of radars used to configure data source.
     */
    private void removeSelectedRadar() {
        if( numSelectedRadars > 0 ) {
            selectedRadars.remove( selectedRadars.size() - 1 );
            --numSelectedRadars;
        }
    }
    /**
     * Increments number of users allowed to use this data source.
     *
     * @param userName User name
     */
    public void addSelectedUser( String userName ) {
        if( numSelectedUsers < userManager.getUsers().size() &&
                !userName.equals( DEFAULT_LIST_OPTION_KEY ) ) {
            if( !selectedUsers.contains( userManager.getUserByName( userName ) ) ) {
                ++numSelectedUsers;
                selectedUsers.add( userManager.getUserByName( userName ) );
            }
        }
    }
    /**
     * Decrements number of users allowed to use this data source.
     */
    public void removeSelectedUser() {
        if( numSelectedUsers > 0 ) {
            selectedUsers.remove( selectedUsers.size() - 1 );
            --numSelectedUsers;
        }
    }

   
    /*
     * Increments number of file objects used to configure data source.
     *
     * @param fileObject File object identifier
     *
    public void addSelectedFileObject( String fileObject ) {
        if( numberOfSelectedFileObjects < numberOfAvailableFileObjects &&
                !fileObject.equals( DEFAULT_LIST_OPTION_KEY ) ) {
            if( !selectedFileObjects.contains( fileObjectManager.getFileObject( fileObject ) ) ) {
                ++numberOfSelectedFileObjects;
                selectedFileObjects.add( fileObjectManager.getFileObject( fileObject ) );
            }
        }
    }
    /**
     * Decrements number of file objects used to configure data source.
     *
    public void removeSelectedFileObject() {
        if( numberOfSelectedFileObjects > 0 ) {
            selectedFileObjects.remove( selectedFileObjects.size() - 1 );
            --numberOfSelectedFileObjects;
        }
    }
    /**
     * Increments number of data quantities used to configure data source.
     *
     * @param dataQuantity Data quantity identifier
     *
    public void addSelectedDataQuantity( String dataQuantity ) {
        if( numberOfSelectedDataQuantities < numberOfAvailableDataQuantities &&
                !dataQuantity.equals( DEFAULT_LIST_OPTION_KEY ) ) {
            if( !selectedDataQuantities.contains( dataQuantityManager.getDataQuantity(
                    dataQuantity ) ) ) {
                ++numberOfSelectedDataQuantities;
                selectedDataQuantities.add( dataQuantityManager.getDataQuantity( dataQuantity ) );
            }
        }
    }
    /**
     * Decrements number of data quantities used to configure data source.
     *
    public void removeSelectedDataQuantity() {
        if( numberOfSelectedDataQuantities > 0 ) {
            selectedDataQuantities.remove( selectedDataQuantities.size() - 1 );
            --numberOfSelectedDataQuantities;
        }
    }
    /**
     * Increments number of products used to configure data source.
     *
     * @param product Product identifier
     *
    public void addSelectedProduct( String product ) {
        if( numberOfSelectedProducts < numberOfAvailableProducts &&
                !product.equals( DEFAULT_LIST_OPTION_KEY ) ) {
            if( !selectedProducts.contains( productManager.getProduct( product ) ) ) {
                ++numberOfSelectedProducts;
                selectedProducts.add( productManager.getProduct( product ) );
            }
        }
    }
    /**
     * Decrements number of products used to configure data source.
     *
    public void removeSelectedProduct() {
        if( numberOfSelectedProducts > 0 ) {
            // Get identifier for the removed product
            String product = ( selectedProducts.get( selectedProducts.size() -1 ) ).getProduct();
            // Remove prouduct parameter if exists
            if( selectedProductParameterValues.containsKey( product ) ) {
                removeSelectedProductParameterValue( product );
            }
            // Remove product
            selectedProducts.remove( selectedProducts.size() - 1 );
            --numberOfSelectedProducts;
        }
    }
    /**
     * Adds prodict parameter value to the list of selected product parameters.
     *
     * @param productParameterName Product parameter identifier
     * @param productParameterValue Product parameter value
     *
    public void addSelectedProductParameterValue( String productParameterName,
            String productParameterValue ) {
        selectedProductParameterValues.put( productParameterName, productParameterValue );
    }
    /**
     * Removes product parameter value from the list of selected product parameters.
     *
     * @param productParameterName Product parameter identifier
     *
    public void removeSelectedProductParameterValue( String productParameterName ) {
        selectedProductParameterValues.remove( productParameterName );
    }
   
    /**
     * Recover selected parameters
     *
    private void recoverSelection( int dataSourceId ) {
        try {
            // Recover selected radars
            List<Integer> radarIds = dataSourceManager.getRadarIds( dataSourceId );
            for( int i = 0; i < radarIds.size(); i++ ) {
                selectedRadars.add( channelManager.getChannel( radarIds.get( i ) ) );
            }
            numberOfSelectedRadars = selectedRadars.size();
            // Recover selected file objects
            List<Integer> fileObjectIds = dataSourceManager.getFileObjectIds( dataSourceId );
            for( int i = 0; i < fileObjectIds.size(); i++ ) {
                selectedFileObjects.add( fileObjectManager.getFileObject( fileObjectIds.get( i ) ) );
            }
            numberOfSelectedFileObjects = selectedFileObjects.size();
            // Recover selected data quantities
            List<Integer> dataQuantityIds = dataSourceManager.getDataQuantityIds( dataSourceId );
            for( int i = 0; i < dataQuantityIds.size(); i++ ) {
                selectedDataQuantities.add( dataQuantityManager.getDataQuantity(
                        dataQuantityIds.get( i ) ) );
            }
            numberOfSelectedDataQuantities = selectedDataQuantities.size();
            // Recover selected products
            List<Integer> productIds = dataSourceManager.getProductIds( dataSourceId );
            for( int i = 0; i < productIds.size(); i++ ) {
                selectedProducts.add( productManager.getProduct( productIds.get( i ) ) );
            }
            // Recover selected product parmater values
            List<Integer> productParameterIds = dataSourceManager.getProductParameterIds(
                    dataSourceId );
            for( int i = 0; i < productParameterIds.size(); i++ ) {
                ProductParameter productParameter =
                        productParameterManager.getProductParameter( productParameterIds.get( i ) );
                String productParameterValue = productParameterManager.getProductParameterValue(
                        productParameter.getId(), dataSourceId );
                selectedProductParameterValues.put( productParameter.getParameter(),
                        productParameterValue );
            }




            numberOfSelectedProducts = selectedProducts.size();
            // Recover selected users
            List<Integer> userIds = dataSourceManager.getUserIds( dataSourceId );
            for( int i = 0; i < userIds.size(); i++ ) {
                selectedUsers.add( userManager.getUserById( userIds.get( i ) ) );
            }
            numberOfSelectedUsers = selectedUsers.size();
        } catch( SQLException e ) {
            logManager.addEntry( System.currentTimeMillis(), LogManager.MSG_ERR,
                    "Failed to recover data source parameters: " + e.getMessage() );
        } catch( Exception e ) {
            logManager.addEntry( System.currentTimeMillis(), LogManager.MSG_ERR,
                    "Failed to recover data source parameters: " + e.getMessage() );
        }
    }*/

    /**
     * Gets reference to CoreFilterManager.
     *
     * @return Reference to CoreFilterManager
     */
    public CoreFilterManager getCoreFilterManager() {
        return coreFilterManager;
    }
    /**
     * Sets reference to CoreFilterManager.
     *
     * @param coreFilterManager Reference tp CoreFilterManager to set
     */
    public void setCoreFilterManager( CoreFilterManager coreFilterManager ) {
        this.coreFilterManager = coreFilterManager;
    }
}
//--------------------------------------------------------------------------------------------------
