/*******************************************************************************
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
*******************************************************************************/

package eu.baltrad.dex.db.controller;

import eu.baltrad.dex.db.manager.impl.BltFileManager;
import eu.baltrad.dex.datasource.manager.impl.FileObjectManager;
import eu.baltrad.dex.datasource.model.FileObject;
import eu.baltrad.dex.util.MessageResourceUtil;

import eu.baltrad.dex.db.model.BltFile;
import eu.baltrad.dex.db.model.BltQueryParameter;

import org.springframework.stereotype.Controller;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * File browser controller implements file selection and browsing functionality.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.7.2
 */
@Controller
@RequestMapping("/file_browser.htm")
@SessionAttributes("query_param")
public class BltFileBrowserController {

    // View name
    private static final String FORM_VIEW = "file_browser";  
    
    // Model keys
    private static final String FC_ERROR_KEY = "fc_error";
    private static final String FC_ERROR_MESSAGE_KEY = 
            "browsefiles.file_catalog_error";
    private static final String QUERY_PARAMETER_KEY = "query_param";
    private static final String FILE_ENTRIES_KEY = "files";
    private static final String FIRST_PAGE_KEY = "first_page";
    private static final String LAST_PAGE_KEY = "last_page";
    private static final String CURRENT_PAGE_KEY = "current_page";
    private static final String SELECTED_RADAR_KEY = "selected_radar";
    private static final String SELECTED_FILE_OBJECT_KEY = "selected_file_object";
    private static final String SELECTED_START_DATE_KEY = "selected_start_date";
    private static final String SELECTED_END_DATE_KEY = "selected_end_date";
    private static final String SELECTED_START_HOURS_KEY = "selected_start_hours";
    private static final String SELECTED_START_MINUTES_KEY = "selected_start_minutes";
    private static final String SELECTED_START_SECONDS_KEY = "selected_start_seconds";
    private static final String SELECTED_END_HOURS_KEY = "selected_end_hours";
    private static final String SELECTED_END_MINUTES_KEY = "selected_end_minutes";
    private static final String SELECTED_END_SECONDS_KEY = "selected_end_seconds";
    
    private static final String NO_MATCH_MSG_KEY = "no_match_msg";
    private static final String DEFAULT_NO_MATCH_MSG = "No matching data files found.";
    private static final String INVALID_DATE_MSG = "Invalid date format entered.";
    
    /** Sort by date parameter */
    private static final String SORT_BY_DATE_PARAM = "sortByDate";
    /** Sort by time parameter */
    private static final String SORT_BY_TIME_PARAM = "sortByTime";
    /** Sort by source parameter */
    private static final String SORT_BY_SRC_PARAM = "sortBySource";
    /** Sort by type parameter */
    private static final String SORT_BY_OBJECT_PARAM = "sortByObject";

    /** Current page number, used for page scrolling */
    private static int currentPage;
    /** Sort by date toggle */
    private static boolean sortByDate;
    /** Sort by date key */
    private static String sortByDateKey = BltQueryParameter.SORT_NONE;
    /** Sort by time toggle */
    private static boolean sortByTime;
    /** Sort by time key */
    private static String sortByTimeKey = BltQueryParameter.SORT_NONE;
    /** Sort by source toggle */
    private static boolean sortBySource;
    /** Sort by source key */
    private static String sortBySourceKey = BltQueryParameter.SORT_NONE;
    /** Sort by file object toggle */
    private static boolean sortByObject;  
    /** Sort by object key */
    private static String sortByObjectKey = BltQueryParameter.SORT_NONE;
    
    private FileObjectManager fileObjectManager;
    private BltFileManager fileManager;
    private MessageResourceUtil messages;
    
    private String currentSelectedPage;
    private BltQueryParameter currentQueryParameter;
    private String currentSelectedRadar = "";
    private String currentSelectedFileObject = "";
    private String selectedStartDate = "";
    private String selectedEndDate = "";
    private String selectedStartTimeHours = "";
    private String selectedStartTimeMinutes = "";
    private String selectedStartTimeSeconds = "";
    private String selectedEndTimeHours = "";
    private String selectedEndTimeMinutes = "";
    private String selectedEndTimeSeconds = "";
    
    /**
     * Create form backing object.
     * @param model Model map 
     * @return View name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(ModelMap model) {
        model.addAttribute(QUERY_PARAMETER_KEY, new BltQueryParameter());
        model.addAttribute(NO_MATCH_MSG_KEY, "");
        
        if (currentQueryParameter != null) {
        	updateSearchResults(model, currentQueryParameter, currentSelectedPage);
        }
        
        return FORM_VIEW;
    }
    
    /**
     * Process form submission.
     * @param model Model map 
     * @param request Http servlet request
     * @param param Query parameters
     * @param selectedPage Currently selected page number
     * @return View name
     */
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(ModelMap model, HttpServletRequest request,
            @ModelAttribute(value="query_param") BltQueryParameter param,
            @RequestParam(value="selected_page", required=false) 
                    String selectedPage) {
    		
      setSortByDate(request);
      setSortByTime(request);
      setSortBySource(request);
      setSortByObject(request);

      updateSearchResults(model, param, selectedPage);

      return FORM_VIEW;
    }
    
    /**
     * Evaluates and executes search based on parameters in the search form. If valid parameters, 
     * search results will be updated and search parameters stored.
     * @param model Model map 
     * @param param Query parameters
     * @param selectedPage Currently selected page number
     */
    private void updateSearchResults(ModelMap model, BltQueryParameter param, String selectedPage) {
    	
    	boolean invalidDate = false;
    	
    	if (!isValidDate(param.getStartDate())) {
    		invalidDate = true;
    		selectedStartDate = "";
		}
    	
    	if (!isValidDate(param.getEndDate())) {
    		invalidDate = true;
    		selectedEndDate = "";
		}
    	
    	if (invalidDate) {
    		model.addAttribute(NO_MATCH_MSG_KEY, INVALID_DATE_MSG);
    		return;
    	}
    	
    	try {
    		List<BltFile> files = null;
    		param.setLimit(Integer.toString(BltFileManager.ENTRIES_PER_PAGE));

    		param.setSortByDate(sortByDateKey);
    		param.setSortByTime(sortByTimeKey);
    		param.setSortBySource(sortBySourceKey);
    		param.setSortByObject(sortByObjectKey);

    		if (selectedPage != null) {
    			if (selectedPage.matches("<<")) {
    				firstPage();
    				param.setOffset(Integer.toString(0));
    				files = fileManager.load(param);
    			} else {
    				if (selectedPage.matches(">>")) {
    					lastPage(param);
    				} else if (selectedPage.matches(">")) {
    					nextPage(param);
    				} else if (selectedPage.matches("<")) {
    					previousPage();
    				} else {
    					int page = Integer.parseInt(selectedPage);
    					setCurrentPage(page);
    				}
    				int offset = (getCurrentPage() 
    						* BltFileManager.ENTRIES_PER_PAGE)
    						- BltFileManager.ENTRIES_PER_PAGE;
    				param.setOffset(Integer.toString(offset));
    				files = fileManager.load(param);
    			}
    		} else {
    			setCurrentPage(1);
    			param.setOffset(Integer.toString(0));
    			files = fileManager.load(param);
    		}

    		int[] pages = getPages(param);

    		model.addAttribute(FIRST_PAGE_KEY, pages[0]);
    		model.addAttribute(LAST_PAGE_KEY, pages[1]);
    		model.addAttribute(CURRENT_PAGE_KEY, pages[2]);
    		model.addAttribute(FILE_ENTRIES_KEY, files);
    		
    		model.addAttribute(NO_MATCH_MSG_KEY, DEFAULT_NO_MATCH_MSG);
    		
    		storeSelection(model, param, selectedPage);
    		
    	} catch (Exception e) {
    		model.addAttribute(FC_ERROR_KEY, 
    				messages.getMessage(FC_ERROR_MESSAGE_KEY, 
    						new String[] {e.getMessage()}));
    	}
    }
    
    /**
     * Fill model map with list of available radars.
     * @return List of radar names
     */
    @ModelAttribute("radars")
    public List<String> getRadars() {
        return fileManager.loadDistinctRadarStations();
    }
    
    /**
     * Fill model map with list of available file objects.
     * @param model Model map
     * @return List of file objects
     */
    @ModelAttribute("file_objects")
    public List<FileObject> getFileObjects(ModelMap model) {
        try {
            return fileObjectManager.load();
        } catch (Exception e) {
            model.addAttribute(FC_ERROR_KEY, 
                    messages.getMessage(FC_ERROR_MESSAGE_KEY, 
                        new String[] {e.getMessage()}));
            return null;        
        }
    }
    
    /**
     * Stores selected parameters in search form. The stored parameters will be 
     * showed in search fields when returning to page. 
     * @param model Model map 
     * @param param Query parameters
     * @param selectedPage Currently selected page number
     */
    private void storeSelection(ModelMap model, BltQueryParameter param, String selectedPage) {
    	currentQueryParameter = param;
		currentSelectedPage = selectedPage;
		currentSelectedRadar = param.getRadar();
		currentSelectedFileObject = param.getFileObject();

		selectedStartDate = param.getStartDate();
		selectedEndDate = param.getEndDate();
		
		selectedStartTimeHours = param.getStartHour();
		selectedStartTimeMinutes = param.getStartMinute();
		selectedStartTimeSeconds = param.getStartSecond();
		selectedEndTimeHours = param.getEndHour();
		selectedEndTimeMinutes = param.getEndMinute();
		selectedEndTimeSeconds = param.getEndSecond();
		
		model.addAttribute(SELECTED_RADAR_KEY, currentSelectedRadar);
		model.addAttribute(SELECTED_FILE_OBJECT_KEY, currentSelectedFileObject);
		
		model.addAttribute(SELECTED_START_DATE_KEY, selectedStartDate);
		model.addAttribute(SELECTED_END_DATE_KEY, selectedEndDate);
		
		model.addAttribute(SELECTED_START_HOURS_KEY, selectedStartTimeHours);
		model.addAttribute(SELECTED_START_MINUTES_KEY, selectedStartTimeMinutes);
		model.addAttribute(SELECTED_START_SECONDS_KEY, selectedStartTimeSeconds);
		model.addAttribute(SELECTED_END_HOURS_KEY, selectedEndTimeHours);
		model.addAttribute(SELECTED_END_MINUTES_KEY, selectedEndTimeMinutes);
		model.addAttribute(SELECTED_END_SECONDS_KEY, selectedEndTimeSeconds);
    }
    
    /**
     * Determines whether a string is a date provided in the correct format. Correct 
     * format is here 'yyy-MM-dd'.
     * @param date String containing date to be evaluated
     * @return isValid True if date in correct format is input, false otherwise.
     */
    private static boolean isValidDate(String date) {
        boolean valid = true;
        if (date != null && !date.equals(""))
        {
        	try {
        		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        		formatter.parseDateTime(date);
        	} catch (Exception e) {
        		valid = false;
        	}	
        }
        return valid;
    }
    
    /**
     * Get sort toggle parameter from request.
     * @param request Http servlet request
     */
    private static void setSortByDate(HttpServletRequest request) {
        if (request.getParameter(SORT_BY_DATE_PARAM) != null) {
            sortByDate = !sortByDate;
            if (sortByDate) {
                sortByDateKey = BltQueryParameter.SORT_DESC;
            } else {
                sortByDateKey = BltQueryParameter.SORT_ASC;
            }
        }
    }
    
    /**
     * Get sort toggle parameter from request.
     * @param request Http servlet request
     */
    private static void setSortByTime(HttpServletRequest request) {
        if (request.getParameter(SORT_BY_TIME_PARAM) != null) {
            sortByTime = !sortByTime;
            if (sortByTime) {
                sortByTimeKey = BltQueryParameter.SORT_DESC;
            } else {
                sortByTimeKey = BltQueryParameter.SORT_ASC;
            }
        }
    }
    
    /**
     * Get sort toggle parameter from request.
     * @param request Http servlet request
     */
    private static void setSortBySource(HttpServletRequest request) {
        if (request.getParameter(SORT_BY_SRC_PARAM) != null) {
            sortBySource = !sortBySource;
            if (sortBySource) {
                sortBySourceKey = BltQueryParameter.SORT_DESC;
            } else {
                sortBySourceKey = BltQueryParameter.SORT_ASC;
            }
        } 
    }
    
    /**
     * Get sort toggle parameter from request.
     * @param request Http servlet request
     */
    private static void setSortByObject(HttpServletRequest request) {
        if (request.getParameter(SORT_BY_OBJECT_PARAM) != null) {
            sortByObject = !sortByObject;
            if (sortByObject) {
                sortByObjectKey = BltQueryParameter.SORT_DESC;
            } else {
                sortByObjectKey = BltQueryParameter.SORT_ASC;
            }
        } 
    }
    
    /**
     * Get page numbers for a current set of file entries.
     * @param param File query parameters
     * @return Numbers of first, last and current page
     */
    private int[] getPages(BltQueryParameter param) {
        int numPages = (int) Math.ceil(
                (double) fileManager.count(param) / BltFileManager.ENTRIES_PER_PAGE);
        if (numPages < 1) {
            numPages = 1;
        }
        int curPage = getCurrentPage();
        int scrollStart = (BltFileManager.SCROLL_RANGE - 1) / 2;
        int firstPage = 1;
        int lastPage = BltFileManager.SCROLL_RANGE;
        if (numPages <= BltFileManager.SCROLL_RANGE 
                && curPage <= BltFileManager.SCROLL_RANGE) {
            firstPage = 1;
            lastPage = numPages;
        }
        if (numPages > BltFileManager.SCROLL_RANGE && curPage > scrollStart &&
                curPage < numPages - scrollStart) {
            firstPage = curPage - scrollStart;
            lastPage = curPage + scrollStart;
        }
        if (numPages > BltFileManager.SCROLL_RANGE && curPage > scrollStart &&
                curPage >= numPages - (BltFileManager.SCROLL_RANGE - 1)) {
            firstPage = numPages - (BltFileManager.SCROLL_RANGE - 1);
            lastPage = numPages;
        }
        return new int[] {firstPage, lastPage, curPage};
    }
    
    /**
     * Gets current page number.
     * @return Current page number
     */
    public int getCurrentPage() { return currentPage; }
    
    /**
     * Sets current page number.
     * @param page Current page number to set
     */
    public void setCurrentPage(int page) { currentPage = page; }
   
    /**
     * Sets page number to the next page number.
     * @param param File query parameters
     */
    public void nextPage(BltQueryParameter param) {
        int lastPage = (int) Math.ceil(
                (double) fileManager.count(param) / BltFileManager.ENTRIES_PER_PAGE);
        if (lastPage == 0) {
            ++lastPage;
        }
        if (getCurrentPage() != lastPage) {
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
     * @param param File query parameters
     */
    public void lastPage(BltQueryParameter param) {
        int lastPage = (int) Math.ceil( 
                (double)fileManager.count(param) / BltFileManager.ENTRIES_PER_PAGE);
        if (lastPage == 0) {
            ++lastPage;
        }
        currentPage = lastPage;
    }

    /**
     * @param fileObjectManager the fileObjectManager to set
     */
    @Autowired
    public void setFileObjectManager(FileObjectManager fileObjectManager) {
        this.fileObjectManager = fileObjectManager;
    }

    /**
     * @param fileManager the fileManager to set
     */
    @Autowired
    public void setFileManager(BltFileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * @param mesages the mesages to set
     */
    @Autowired
    public void setMesages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
}

