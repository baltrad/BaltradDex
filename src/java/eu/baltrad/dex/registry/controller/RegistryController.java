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

package eu.baltrad.dex.registry.controller;

import eu.baltrad.dex.registry.model.impl.RegistryEntry;
import eu.baltrad.dex.registry.manager.impl.RegistryManager;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * Delivery registry controller.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0.0
 */
@Controller
public class RegistryController {

    // View names
    private static final String SHOW_REGISTRY_VIEW = "registry_show";
    private static final String CLEAR_REGISTRY_VIEW = "registry_delete";
    private static final String CLEAR_REGISTRY_STATUS_VIEW = 
            "registry_delete_status";
    
    // Model keys
    private static final String PAGE_NUMBER = "selected_page";
    private static final String REGISTRY_ENTRIES = "entries";
    private static final String NUMBER_OF_ENTRIES_KEY = "number_of_entries";
    private static final String CLEAR_REGISTRY_OK_MSG_KEY = 
            "clearregistry.completed_success";
    private static final String CLEAR_REGISTRY_ERROR_MSG_KEY = 
            "clearregistry.completed_failure";
    private static final String OK_MSG_KEY = "registry_delete_success";
    private static final String ERROR_MSG_KEY = "registry_delete_error";
    
    private RegistryManager registryManager;
    private MessageResourceUtil messages;
    private Logger log;
    
    /** Current page number */
    private static int currentPage;

    /**
     * Constructor.
     */
    public RegistryController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Get page numbers for a current set of entries.
     * @return Numbers of first, last and current page for a given dataset 
     */
    private int[] getPages() {
        long numEntries = registryManager.count(RegistryEntry.UPLOAD);
        int numPages = (int) Math.ceil(
                numEntries / RegistryManager.ENTRIES_PER_PAGE);
        if ((numPages * RegistryManager.ENTRIES_PER_PAGE) < numEntries) {
            ++numPages;
        }
        if (numPages < 1) {
            numPages = 1;
        }
        int curPage = getCurrentPage();
        int scrollStart = (RegistryManager.SCROLL_RANGE - 1) / 2;
        int firstPage = 1;
        int lastPage = RegistryManager.SCROLL_RANGE;
        if (numPages <= RegistryManager.SCROLL_RANGE && curPage 
                <= RegistryManager.SCROLL_RANGE) {
            firstPage = 1;
            lastPage = numPages;
        }
        if (numPages > RegistryManager.SCROLL_RANGE && curPage > scrollStart 
                && curPage < numPages - scrollStart) {
            firstPage = curPage - scrollStart;
            lastPage = curPage + scrollStart;
        }
        if (numPages > RegistryManager.SCROLL_RANGE && curPage > scrollStart 
                && curPage >= numPages - (RegistryManager.SCROLL_RANGE - 1)) {
            firstPage = numPages - (RegistryManager.SCROLL_RANGE - 1);
            lastPage = numPages;
        }
        return new int[] {firstPage, lastPage, curPage};
    }
    
    /**
     * Loads delivery register entries.
     * @param request Http servlet request
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/registry_show.htm")
    public String showRegistry(HttpServletRequest request, ModelMap model) {
        String pageNum = request.getParameter(PAGE_NUMBER);
        List<RegistryEntry> entries = null;
        if (pageNum != null) {
            if (pageNum.matches("<<")) {
                firstPage();
                entries = registryManager.load(RegistryEntry.UPLOAD, 0, 
                        RegistryManager.ENTRIES_PER_PAGE);
            } else {
                if (pageNum.matches(">>")) {
                    lastPage();
                } else if(pageNum.matches(">")) {
                    nextPage();
                } else if(pageNum.matches("<")) {
                    previousPage();
                } else {
                    int page = Integer.parseInt(pageNum);
                    setCurrentPage(page);
                }
                int offset = (getCurrentPage() 
                        * RegistryManager.ENTRIES_PER_PAGE)
                        - RegistryManager.ENTRIES_PER_PAGE;
                entries = registryManager.load(RegistryEntry.UPLOAD, offset,
                        RegistryManager.ENTRIES_PER_PAGE);
            }
        } else {
            setCurrentPage(1);
            entries = registryManager.load(RegistryEntry.UPLOAD, 0, 
                    RegistryManager.ENTRIES_PER_PAGE );
        }
        int pages[] = getPages();
        model.addAttribute("first_page", pages[0]);
        model.addAttribute("last_page", pages[1]);
        model.addAttribute("current_page", pages[2]);
        model.addAttribute(REGISTRY_ENTRIES, entries);
        return SHOW_REGISTRY_VIEW;
    }
    
    /**
     * Renders clear delivery registry page.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/registry_delete.htm")
    public String clearRegistry(ModelMap model) {
        model.addAttribute(NUMBER_OF_ENTRIES_KEY, 
                registryManager.count(RegistryEntry.UPLOAD));        
        return CLEAR_REGISTRY_VIEW;
    }
    
    /**
     * Removes all entries from delivery registry. 
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/registry_delete_status.htm")
    public String clearRegistryStatus(ModelMap model) {
        try {
            int delete = registryManager.delete();
            String msg = messages.getMessage(CLEAR_REGISTRY_OK_MSG_KEY, 
                    new String[] {Integer.toString(delete)});
            model.addAttribute(OK_MSG_KEY, msg);
            log.warn(msg);
        } catch (Exception e) {
            String msg = messages.getMessage(CLEAR_REGISTRY_ERROR_MSG_KEY);
            model.addAttribute(ERROR_MSG_KEY, msg);
            log.error(msg, e);
        }
        return CLEAR_REGISTRY_STATUS_VIEW;
    }
    
    /**
     * Gets current page number.
     *
     * @return Current page number
     */
    public int getCurrentPage() { return currentPage; }
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
        int lastPage = (int) Math.ceil(
                registryManager.count(RegistryEntry.UPLOAD) 
                / RegistryManager.ENTRIES_PER_PAGE);
        if ((lastPage * RegistryManager.ENTRIES_PER_PAGE) 
                < registryManager.count(RegistryEntry.UPLOAD)) {
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
     */
    public void lastPage() {
        long numEntries = registryManager.count(RegistryEntry.UPLOAD);
        int lastPage = (int) Math.ceil(numEntries 
                / RegistryManager.ENTRIES_PER_PAGE);
        if ((lastPage * RegistryManager.ENTRIES_PER_PAGE) 
                < registryManager.count(RegistryEntry.UPLOAD)) {
            ++lastPage;
        }
        if (lastPage == 0) {
            ++lastPage;
        }
        currentPage = lastPage;
    }

    /**
     * @param registryManager the registryManager to set
     */
    @Autowired
    public void setRegistryManager(RegistryManager registryManager) {
        this.registryManager = registryManager;
    }

    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
}

