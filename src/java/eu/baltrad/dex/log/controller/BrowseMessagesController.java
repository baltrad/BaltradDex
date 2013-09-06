/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.log.controller;

import eu.baltrad.dex.log.manager.impl.LogManager;
import eu.baltrad.dex.log.model.impl.LogEntry;
import eu.baltrad.dex.log.model.impl.LogParameter;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Implements functionality allowing to browse and search system messages.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.6.1
 * @since 0.1.6
 */
@Controller
@RequestMapping("/messages_browser.htm")
@SessionAttributes("log_parameter")
public class BrowseMessagesController {
    
    /** Drop down lists */ 
    private static final String[] LEVELS = {"INFO", "WARN", "ERROR"};
    private static final String[] LOGGERS = {"DEX", "BEAST", "PGF"};
    /** Form view */
    private static final String FORM_VIEW = "messages_browser";
    /** Log parameter model key */
    private static final String LOG_PARAMETER_KEY = "log_parameter";
    /** Messages model key */
    private static final String MESSAGES_KEY = "messages";
    
    private static final String FIRST_PAGE_KEY = "first_page";
    private static final String LAST_PAGE_KEY = "last_page";
    private static final String CURRENT_PAGE_KEY = "current_page";
    
    private LogManager logManager;
    
    /** Current page number */
    private static int currentPage;
    
    /**
     * Process form submission.
     * @param model Model map
     * @param selectedPage Selected page
     * @param logger Logger name
     * @param level Message level
     * @param startDate Start date
     * @param startHour Start hour 
     * @param startMinutes Start minutes
     * @param startSeconds Start seconds
     * @param endDate End date 
     * @param endHour End hour
     * @param endMinutes End minutes
     * @param endSeconds End seconds
     * @param phrase Message phrase
     * @return View name
     */
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(ModelMap model,
            @RequestParam(value="selected_page", required=false) 
                    String selectedPage,
            @RequestParam(value="logger", required=false) String logger,
            @RequestParam(value="level", required=false) String level,
            @RequestParam(value="startDate", required=false) String startDate,
            @RequestParam(value="startHour", required=false) String startHour,
            @RequestParam(value="startMinutes", required=false) 
                    String startMinutes,
            @RequestParam(value="startSeconds", required=false) 
                    String startSeconds,
            @RequestParam(value="endDate", required=false) String endDate,
            @RequestParam(value="endHour", required=false) String endHour,
            @RequestParam(value="endMinutes", required=false) 
                    String endMinutes,
            @RequestParam(value="endSeconds", required=false)
                    String endSeconds,
            @RequestParam(value="phrase", required=false) String phrase)
                throws Exception {
        
        LogParameter logParameter = new LogParameter();
        if (logger != null) {
            logParameter.setLogger(logger);
        }
        if (level != null) {
            logParameter.setLevel(level);
        }
        if (startDate != null) {
            logParameter.setStartDate(startDate);
        }
        if (startHour != null) {
            logParameter.setStartHour(startHour);
        }
        if (startMinutes != null) {
            logParameter.setStartMinutes(startMinutes);
        }
        if (startSeconds != null) {
            logParameter.setStartSeconds(startSeconds);
        }
        if (endDate != null) {
            logParameter.setEndDate(endDate);
        }
        if (endHour != null) {
            logParameter.setEndHour(endHour);
        }
        if (endMinutes != null) {
            logParameter.setEndMinutes(endMinutes);
        }
        if (endSeconds != null) {
            logParameter.setEndSeconds(endSeconds);
        }
        if (phrase != null) {
            logParameter.setPhrase(phrase);
        }
        
        List<LogEntry> entries = null;
        
        if (selectedPage != null) {
            if (selectedPage.matches("<<")) {
                firstPage();
                entries = logManager.load(
                        logManager.createQuery(logParameter, false), 0,
                        LogManager.ENTRIES_PER_PAGE);
            } else {
                if (selectedPage.matches(">>")) {
                    lastPage(logParameter);
                } else if(selectedPage.matches(">")) {
                    nextPage(logParameter);
                } else if(selectedPage.matches("<")) {
                    previousPage();
                } else {
                    int page = Integer.parseInt(selectedPage);
                    setCurrentPage(page);
                }
                int offset = (getCurrentPage() * LogManager.ENTRIES_PER_PAGE)
                        - LogManager.ENTRIES_PER_PAGE;
                entries = logManager.load(
                        logManager.createQuery(logParameter, false), offset, 
                        LogManager.ENTRIES_PER_PAGE);
            }
        } else {
            setCurrentPage(1);
            entries = logManager.load(
                    logManager.createQuery(logParameter, false), 0, 
                    LogManager.ENTRIES_PER_PAGE);
        }
        int[] pages = getPages(logParameter);
        model.addAttribute(FIRST_PAGE_KEY, pages[0]);
        model.addAttribute(LAST_PAGE_KEY, pages[1]);
        model.addAttribute(CURRENT_PAGE_KEY, pages[2]);
        model.addAttribute(MESSAGES_KEY, entries);
        model.addAttribute(LOG_PARAMETER_KEY, logParameter);
        return FORM_VIEW;
    }
    
    /**
     * Set up form.
     * @param model Model map
     * @return View name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(ModelMap model) throws Exception {
        setCurrentPage(1);
        LogParameter param = new LogParameter();
        int[] pages = getPages(param);
        model.addAttribute(FIRST_PAGE_KEY, pages[0]);
        model.addAttribute(LAST_PAGE_KEY, pages[1]);
        model.addAttribute(CURRENT_PAGE_KEY, pages[2]);
        model.addAttribute(MESSAGES_KEY, logManager.load(0, 
                LogManager.ENTRIES_PER_PAGE));
        model.addAttribute(LOG_PARAMETER_KEY, param);
        return FORM_VIEW;
    }
    
    /**
     * Creates list of levels.
     * @return List of levels
     */
    @ModelAttribute("levels")
    public List<String> getLevels() {
        return Arrays.asList(LEVELS);
    }
    
    /**
     * Creates list of loggers.
     * @return List of loggers
     */
    @ModelAttribute("loggers")
    public List<String> getLoggers() {
        return Arrays.asList(LOGGERS);
    }
    
    /**
     * Get page numbers for a current set of entries.
     * @param param Log parameter
     * @return Numbers of first, last and current page for a log parameter 
     */
    private int[] getPages(LogParameter param) throws Exception {
        long numEntries = logManager.count(logManager.createQuery(param, true));
        int numPages = (int) Math.ceil(numEntries / 
                LogManager.ENTRIES_PER_PAGE);
        if ((numPages * LogManager.ENTRIES_PER_PAGE) < numEntries) {
            ++numPages;
        }
        if (numPages < 1) {
            numPages = 1;
        }
        int curPage = getCurrentPage();
        int scrollStart = (LogManager.SCROLL_RANGE - 1) / 2;
        int firstPage = 1;
        int lastPage = LogManager.SCROLL_RANGE;
        if (numPages <= LogManager.SCROLL_RANGE && curPage 
                <= LogManager.SCROLL_RANGE) {
            firstPage = 1;
            lastPage = numPages;
        }
        if (numPages > LogManager.SCROLL_RANGE && curPage > scrollStart 
                && curPage < numPages - scrollStart) {
            firstPage = curPage - scrollStart;
            lastPage = curPage + scrollStart;
        }
        if (numPages > LogManager.SCROLL_RANGE && curPage > scrollStart 
                && curPage >= numPages - (LogManager.SCROLL_RANGE - 1)) {
            firstPage = numPages - (LogManager.SCROLL_RANGE - 1);
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
    public void setCurrentPage( int page ) { currentPage = page; }
    
    /**
     * Sets page number to the next page number.
     * @param param Log parameter
     */
    public void nextPage(LogParameter param) throws Exception {
        int lastPage = (int) Math.ceil(logManager.count(
            logManager.createQuery(param, true)) / 
                LogManager.ENTRIES_PER_PAGE);
        if ((lastPage * LogManager.ENTRIES_PER_PAGE) 
                < logManager.count(logManager.createQuery(param, true))) {
            ++lastPage;
        }
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
        if (getCurrentPage() != 1) {
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
     * @param param Log parameter
     */
    public void lastPage(LogParameter param) throws Exception {
        long numEntries = logManager.count(logManager.createQuery(param, true));
        int lastPage = (int) Math.ceil(numEntries / 
                LogManager.ENTRIES_PER_PAGE);
        if ((lastPage * LogManager.ENTRIES_PER_PAGE) 
                < logManager.count(logManager.createQuery(param, true))) {
            ++lastPage;
        }
        if (lastPage == 0) {
            ++lastPage;
        }
        currentPage = lastPage;
    }
    
    /**
     * @param logManager the logManager to set
     */
    @Autowired
    public void setLogManager(LogManager logManager) {
        this.logManager = logManager;
    }
    
}
