/***************************************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.db.controller;

import eu.baltrad.dex.db.model.BltFileManager;
import eu.baltrad.dex.db.model.BltFile;
import eu.baltrad.dex.datasource.model.FileObjectManager;
import eu.baltrad.dex.util.ITableScroller;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;

/**
 * File browser controller implements fileset selection and browsing functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 0.7.2
 */
public class BltFileBrowserController extends SimpleFormController implements ITableScroller {
//---------------------------------------------------------------------------------------- Constants
    /** File objects list key */
    private static final String FILE_OBJECTS_KEY = "file_objects";
    /** Radar stations list key */
    private static final String RADAR_STATIONS_KEY = "radar_stations";
    /** File entries key */
    private static final String FILE_ENTRIES_KEY = "fileEntries";
    /** Default option on the list */
    private static final String DEFAULT_LIST_OPTION_KEY = "select";
    /** Page number parameter */
    private static final String PAGE_NUM_PARAM = "pagenum";
    /** Radar selection list parameter */
    private static final String RADARS_LIST_PARAM = "radarsList";
    /** File object selection parameter */
    private static final String FILE_OBJECTS_LIST_PARAM = "fileObjectsList";
    /** Start date parameter */
    private static final String START_DATE_PARAM = "startDate";
    /** Start hour parameter */
    private static final String START_HOUR_PARAM = "startHour";
    /** Start minute paramater */
    private static final String START_MINUTE_PARAM = "startMinute";
    /** Start second parameter */
    private static final String START_SECOND_PARAM = "startSecond";
    /** End date parameter */
    private static final String END_DATE_PARAM = "endDate";
    /** End hour parameter */
    private static final String END_HOUR_PARAM = "endHour";
    /** End minute paramater */
    private static final String END_MINUTE_PARAM = "endMinute";
    /** End second parameter */
    private static final String END_SECOND_PARAM = "endSecond";
    /** Sort by date parameter */
    private static final String SORT_BY_DATE_PARAM = "sortByDate";
    /** Sort by time parameter */
    private static final String SORT_BY_TIME_PARAM = "sortByTime";
    /** Sort by source parameter */
    private static final String SORT_BY_SRC_PARAM = "sortBySource";
    /** Sort by type parameter */
    private static final String SORT_BY_TYPE_PARAM = "sortByType";
//---------------------------------------------------------------------------------------- Variables
    /** Reference to file object manager */
    private FileObjectManager fileObjectManager;
    /** Reference to file manager */
    private BltFileManager bltFileManager;
    /** Selected radar station name */
    private static String radarStation;
    /** Selected file object name */
    private static String fileObject;
    /** Selected start date */
    private static String startDate;
    /** Selected start time */
    private static String startTime;
    /** Selected end date */
    private static String endDate;
    /** Selected end time */
    private static String endTime;
    /** Selected start hour */
    private static String startHour;
    /** Selected start minute */
    private static String startMinute;
    /** Selected start second */
    private static String startSecond;
    /** Selected end hour */
    private static String endHour;
    /** Selected end minute */
    private static String endMinute;
    /** Selected end second */
    private static String endSecond;
    /** Current page number, used for page scrolling */
    private static int currentPage;
    /** Number of selected file entries */
    private static long numEntries;
    /** Sort by date in ascending order */
    private static boolean sortByDateAsc;
    /** Sort by date in descending order */
    private static boolean sortByDateDesc;
    /** Sort by time in ascending order */
    private static boolean sortByTimeAsc;
    /** Sort by time in descending order */
    private static boolean sortByTimeDesc;
    /** Sort by source in ascending order  */
    private static boolean sortBySourceAsc;
    /** Sort by source in descending order */
    private static boolean sortBySourceDesc;
    /** Sort by type in ascending order */
    private static boolean sortByTypeAsc;
    /** Sort by type in descending order */
    private static boolean sortByTypeDesc;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Retrieve a backing object for the current form from the given request.
     * 
     * @param request Current servlet request
     * @return The backing object
     */
    @Override
    protected Object formBackingObject( HttpServletRequest request ) { return new Object(); }
    /**
     * Create a reference data map for the given request.
     *
     * @param request Current servlet request
     * @return Map with reference data entries, or null if none
     * @throws Exception
     */
    @Override
    protected HashMap referenceData( HttpServletRequest request ) throws Exception {
        HashMap model = new HashMap();
        model.put( FILE_OBJECTS_KEY, fileObjectManager.load() );
        model.put( RADAR_STATIONS_KEY, bltFileManager.getDistinctRadarStations() );
        return model;
    }
    /**
     * Prepares the form model and view, including reference and error data.
     *
     * @param request Current servlet request
     * @param response Current servlet response
     * @param errors Validation errors holder
     * @param obj Allows to add given object to model and view
     * @return The prepared form view, or null if handled directly
     * @throws Exception
     */
    protected ModelAndView showForm( HttpServletRequest request, HttpServletResponse response,
            BindException errors, Object obj )
            throws Exception {
        ModelAndView modelAndView = new ModelAndView( getSuccessView() );
        modelAndView.addObject( FILE_OBJECTS_KEY, fileObjectManager.load() );
        modelAndView.addObject( RADAR_STATIONS_KEY, bltFileManager.getDistinctRadarStations() );
        if( obj != null ) {
            modelAndView.addObject( FILE_ENTRIES_KEY, obj );
        }
        return modelAndView;
    }
    /**
     * Submit callback with file selection parameters or page scrolling parameters.
     * Calls showForm() method instead of directly returning model and view. This allows to deal
     * with form view and success view being the same address.
     *
     * @param request Current servlet request
     * @param response Current servlet response
     * @param command Form object with request parameters bound onto it
     * @param Errors instance without errors
     * @return The prepared model and view or null
     * @throws Exception
     */
    @Override
    protected ModelAndView onSubmit( HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors ) throws Exception {
        String pageNum = request.getParameter( PAGE_NUM_PARAM );
        // set sort parameters
        setSortByDate( request );
        setSortByTime( request );
        setSortBySource( request );
        setSortByType( request );
        List<BltFile> fileEntries;
        /* File selection option */
        if( pageNum == null ) {
            setRadarStation(request.getParameter(RADARS_LIST_PARAM) );
            if( getRadarStation().equals( DEFAULT_LIST_OPTION_KEY ) ) setRadarStation("");
            setFileObject(request.getParameter(FILE_OBJECTS_LIST_PARAM));
            if( getFileObject().equals( DEFAULT_LIST_OPTION_KEY ) ) setFileObject("");
            setStartDate(request.getParameter(START_DATE_PARAM) );
            setStartHour( request.getParameter( START_HOUR_PARAM ) );
            setStartMinute( request.getParameter( START_MINUTE_PARAM ) );
            setStartSecond( request.getParameter( START_SECOND_PARAM ) );
            setEndDate(request.getParameter(END_DATE_PARAM) );
            setEndHour( request.getParameter( END_HOUR_PARAM ) );
            setEndMinute( request.getParameter( END_MINUTE_PARAM ) );
            setEndSecond( request.getParameter( END_SECOND_PARAM ) );
            startTime = getTimeString( startHour, startMinute, startSecond);
            endTime = getTimeString( endHour, endMinute, endSecond );
            /* Set current page number */
            setCurrentPage( 1 );
            /* Count selected files */
            setNumEntries(bltFileManager.countFileEntries(
                getRadarStation().trim(), getFileObject(), getStartDate(), 
                startTime, getEndDate(), endTime));
            
            /* Select files */
            fileEntries = bltFileManager.getFileEntries(
                getRadarStation().trim(), getFileObject(), getStartDate(), 
                startTime, getEndDate(), endTime, Integer.toString(0), 
                Integer.toString(BltFileManager.ENTRIES_PER_PAGE), 
                getSortByDateAsc(), getSortByDateDesc(), getSortByTimeAsc(), 
                getSortByTimeDesc(), getSortBySourceAsc(), 
                getSortBySourceDesc(), getSortByTypeAsc(), getSortByTypeDesc());
            
            
        } else {
            /* Page scrolling option */
            if( pageNum.matches( "<<" ) ) {
                firstPage(); 
                fileEntries = bltFileManager.getFileEntries(getRadarStation(), 
                    getFileObject(), getStartDate(), startTime, getEndDate(), 
                    endTime, Integer.toString(0), Integer.toString( 
                    BltFileManager.ENTRIES_PER_PAGE), getSortByDateAsc(), 
                    getSortByDateDesc(), getSortByTimeAsc(), 
                    getSortByTimeDesc(), getSortBySourceAsc(), 
                    getSortBySourceDesc(), getSortByTypeAsc(), 
                    getSortByTypeDesc());
                
            } else {
                if( pageNum.matches( ">>" ) ) {
                    lastPage();
                } else if( pageNum.matches( ">" ) ) {
                    nextPage();
                } else if( pageNum.matches( "<" ) ) {
                    previousPage();
                } else {
                    int page = Integer.parseInt( pageNum );
                    setCurrentPage( page );
                }
                int offset = ( getCurrentPage() * BltFileManager.ENTRIES_PER_PAGE )
                        - BltFileManager.ENTRIES_PER_PAGE;
                fileEntries = bltFileManager.getFileEntries(getRadarStation(), 
                    getFileObject(), getStartDate(), startTime, getEndDate(), 
                    endTime, Integer.toString(offset), Integer.toString(
                    BltFileManager.ENTRIES_PER_PAGE ), getSortByDateAsc(), 
                    getSortByDateDesc(), getSortByTimeAsc(), 
                    getSortByTimeDesc(), getSortBySourceAsc(),
                    getSortBySourceDesc(), getSortByTypeAsc(), 
                    getSortByTypeDesc());
            }
        }
        /*
         * Form view and success view have the same address, so we call showForm() instead of
         * directly returning model and view object.
         */
        return showForm( request, response, errors, fileEntries );
    }
    /**
     * Creates time string from given time parameters.
     *
     * @param hour Hour
     * @param minute Minute
     * @param second Second
     * @return Time string
     */
    private String getTimeString( String hour, String minute, String second ) {
        String time = "";
        if( hour != null && !hour.isEmpty() ) {
            time = hour;
            if( minute != null && !minute.isEmpty() ) {
                time += ":" + minute;
            } else {
                time += ":00";
            }
            if( second != null && !second.isEmpty() ) {
                time += ":" + second;
            } else {
                time += ":00";
            }
        }
        return time;
    }
    /**
     * Gets radar station name parameter.
     *
     * @return radarStation Radar station name parameter
     */
    public static String getRadarStation() { return radarStation; }
    /**
     * Sets radar station name parameter.
     *
     * @param _radarStation Radar station name parameter to set
     */
    public static void setRadarStation( String _radarStation ) { radarStation = _radarStation; }
    /**
     * Gets file object parameter.
     *
     * @return fileObject File object parameter
     */
    public static String getFileObject() { return fileObject; }
    /**
     * Sets file object parameter.
     *
     * @param _fileObject File object parameter to set
     */
    public static void setFileObject( String _fileObject ) { fileObject = _fileObject; }
    /**
     * Gets start date parameter.
     *
     * @return startDate Start date parameter
     */
    public static String getStartDate() { return startDate; }
    /**
     * Sets start date parameter.
     *
     * @param _startDate Start date parameter to set
     */
    public static void setStartDate( String _startDate ) { startDate = _startDate; }
    /**
     * Gets end date parameter.
     *
     * @return endDate End date parameter
     */
    public static String getEndDate() { return endDate; }
    /**
     * Sets end date parameter.
     *
     * @param _endDate End date parameter to set
     */
    public static void setEndDate( String _endDate ) { endDate = _endDate; }
    /**
     * Gets start hour parameter.
     *
     * @return startHour Start hour parameter
     */
    public static String getStartHour() { return startHour; }
    /**
     * Sets start hour parameter.
     *
     * @param _startHour Start hour parameter to set
     */
    public static void setStartHour( String _startHour ) { startHour = _startHour; }
    /**
     * Gets start minute parameter.
     *
     * @return startMinute Start minute parameter
     */
    public static String getStartMinute() { return startMinute; }
    /**
     * Sets start minute parameter.
     *
     * @param _startMinute Start minute parameter to set
     */
    public static void setStartMinute( String _startMinute ) { startMinute = _startMinute; }
    /**
     * Gets start second parameter.
     *
     * @return startSecond Start second parameter
     */
    public static String getStartSecond() { return startSecond; }
    /**
     * Sets start second parameter.
     *
     * @param _startSecond Start second parameter to set
     */
    public static void setStartSecond( String _startSecond ) { startSecond = _startSecond; }
    /**
     * Gets end hour parameter.
     *
     * @return endHour End hour parameter
     */
    public static String getEndHour() { return endHour; }
    /**
     * Sets end hour parameter.
     *
     * @param _endHour End hour parameter to set
     */
    public static void setEndHour( String _endHour) { endHour = _endHour; }
    /**
     * Gets end minute paramater.
     *
     * @return endMinute End minute parameter
     */
    public static String getEndMinute() { return endMinute; }
    /**
     * Sets end minute parameter.
     *
     * @param _endMinute End minute parameter to set
     */
    public static void setEndMinute( String _endMinute ) { endMinute = _endMinute; }
    /**
     * Gets end second parameter
     *
     * @return endSecond End second parameter
     */
    public static String getEndSecond() { return endSecond; }
    /**
     * Sets end second parameter.
     *
     * @param _endSecond End second parameter to set
     */
    public static void setEndSecond( String _endSecond) { endSecond = _endSecond; }
    /**
     * Gets current page number.
     *
     * @return Current page number
     */
    public int getCurrentPage() { return currentPage; }
    /**
     * Gets current page number.
     *
     * @return Current page number
     */
    public static int getCurPage() { return currentPage; }
    /**
     * Sets current page number.
     *
     * @param page Current page number to set
     */
    public void setCurrentPage( int page ) { currentPage = page; }
    /**
     * Sets page number to the next page number.
     */
    public void nextPage() {
        int lastPage = ( int )Math.ceil( getNumEntries() / BltFileManager.ENTRIES_PER_PAGE );
        if( ( lastPage * BltFileManager.ENTRIES_PER_PAGE ) < getNumEntries() ) {
            ++lastPage;
        }
        if( lastPage == 0 ) {
            ++lastPage;
        }
        if( getCurrentPage() != lastPage ) {
            ++currentPage;
        }
    }
    /**
     * Sets page number to the previous page number.
     */
    public void previousPage() {
        if( getCurrentPage() != 1 ) {
            --currentPage;
        }
    }
    /**
     * Sets page number to the first page.
     */
    public void firstPage() {
        currentPage = 1;
    }
    /**
     * Sets page number to the last page.
     */
    public void lastPage() {
        int lastPage = ( int )Math.ceil( getNumEntries() / BltFileManager.ENTRIES_PER_PAGE );
        if( ( lastPage * BltFileManager.ENTRIES_PER_PAGE ) < getNumEntries() ) {
            ++lastPage;
        }
        if( lastPage == 0 ) {
            ++lastPage;
        }
        currentPage = lastPage;
    }
    /**
     * Gets number of selected file entries.
     *
     * @return numEntries Number of selected file entries
     */
    public static long getNumEntries() { return numEntries; }
    /**
     * Sets number of selected file entries.
     *
     * @param aNumEntries Number of selected file entries
     */
    public static void setNumEntries( long _numEntries ) { numEntries = _numEntries; }
    /**
     * Gets reference to FileObjectManager object.
     *
     * @return fileObjectManager Reference to FileObjectManager object
     */
    public FileObjectManager getFileObjectManager() { return fileObjectManager; }
    /**
     * Sets reference to FileObjectManager object.
     *
     * @param fileObjectManager Reference to FileObjectManager object
     */
    public void setFileObjectManager( FileObjectManager fileObjectManager ) {
        this.fileObjectManager = fileObjectManager;
    }
    /**
     * Gets reference to BltFileManager object.
     *
     * @return bltFileManager Reference to BltFileManager object
     */
    public BltFileManager getBltFileManager() { return bltFileManager; }
    /**
     * Sets reference to BltFileManager object
     *
     * @param bltFileManager Reference to bltFileManager object
     */
    public void setBltFileManager( BltFileManager bltFileManager ) {
        this.bltFileManager = bltFileManager;
    }
    /**
     * Get sort by date toggle.
     *
     * @return Sort by date toggle
     */
    public static boolean getSortByDateAsc() { return sortByDateAsc; }
    /**
     * Get sort by date toggle.
     *
     * @return Sort by date toggle
     */
    public static boolean getSortByDateDesc() { return sortByDateDesc; }
    /**
     * Set sort by date toggle
     *
     * @param request Http servlet request
     */
    public static void setSortByDate( HttpServletRequest request ) {
        if( request.getParameter( SORT_BY_DATE_PARAM ) != null ) {
            if( sortByDateAsc ) {
                sortByDateAsc = false;
                sortByDateDesc = true;
            } else {
                sortByDateAsc = true;
                sortByDateDesc = false;
            }
        } 
    }
    /**
     * Get sort by time toggle.
     *
     * @return Sort by time toggle
     */
    public static boolean getSortByTimeAsc() { return sortByTimeAsc; }
    /**
     * Get sort by time toggle.
     * 
     * @return Sort by time toggle
     */
    public static boolean getSortByTimeDesc() { return sortByTimeDesc; }
    /**
     * Set sort by time toggle.
     * 
     * @param request Http servlet request
     */
    public static void setSortByTime( HttpServletRequest request ) {
        if( request.getParameter( SORT_BY_TIME_PARAM ) != null ) {
            if( sortByTimeAsc ) {
                sortByTimeAsc = false;
                sortByTimeDesc = true;
            } else {
                sortByTimeAsc = true;
                sortByTimeDesc = false;
            }
        } 
    }
    /**
     * Get sort by source toggle.
     *
     * @return Sort by source toggle
     */
    public boolean getSortBySourceAsc() { return sortBySourceAsc; }
    /**
     * Get sort by source toggle.
     * 
     * @return Sort by source toggle
     */
    public boolean getSortBySourceDesc() { return sortBySourceDesc; }
    /**
     * Set sort by source toggle.
     * 
     * @param request Http servlet request
     */
    public static void setSortBySource( HttpServletRequest request ) {
        if( request.getParameter( SORT_BY_SRC_PARAM ) != null ) {
            if( sortBySourceAsc ) {
                sortBySourceAsc = false;
                sortBySourceDesc = true;
            } else {
                sortBySourceAsc = true;
                sortBySourceDesc = false;
            }
        } 
    }
    /**
     * Get sort by type toggle
     *
     * @return Sort by time toggle
     */
    public static boolean getSortByTypeAsc() { return sortByTypeAsc; }
    /**
     * Get sort by type toggle
     * 
     * @return Sort by time toggle
     */
    public static boolean getSortByTypeDesc() { return sortByTypeDesc; }
    /**
     * Set sort by time toggle.
     * 
     * @param request Http servlet request
     */
    public static void setSortByType( HttpServletRequest request ) {
        if( request.getParameter( SORT_BY_TYPE_PARAM ) != null ) {
            if( sortByTypeAsc ) {
                sortByTypeAsc = false;
                sortByTypeDesc = true;
            } else {
                sortByTypeAsc = true;
                sortByTypeDesc = false;
            }
        }
    }
}
//--------------------------------------------------------------------------------------------------
