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

package eu.baltrad.dex.datasource.controller;

import eu.baltrad.dex.db.manager.impl.BltFileManager;
import eu.baltrad.dex.db.model.BltFile;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.ui.ModelMap;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * Get files for a given data source.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.1.6
 */
@Controller
public class DataSourceFilesController {

    // View name
    private static final String SUCCESS_VIEW = "datasource_files";
    
    // Model keys
    private static final String FC_ERROR_KEY = "fc_error";
    private static final String FC_ERROR_MESSAGE_KEY = 
            "browsefiles.file_catalog_error";
    private static final String DATASOURCE_NAME_KEY = "data_source_name";
    private static final String FILES_KEY = "files";
    private static final String FIRST_PAGE_KEY = "first_page";
    private static final String LAST_PAGE_KEY = "last_page";
    private static final String CURRENT_PAGE_KEY = "current_page";

    /** Reference to file manager object */
    private BltFileManager fileManager;
    /** Message source */
    private MessageResourceUtil messages;
    /** Current page number */
    private static int currentPage;
    /** Data source name */
    private static String dsName;
    
    /**
     * Get file entries for a given data source.
     * @param request Http servlet request
     * @param model Model map
     * @param dataSourceName Data source name
     * @param selectedPage Selected page number
     * @return View name
     */
    @RequestMapping("/datasource_files.htm")
    public String processSubmit(HttpServletRequest request, ModelMap model,
            @RequestParam(value="ds_name", required=false) 
                    String dataSourceName,
            @RequestParam(value="selected_page", required=false) 
                    String selectedPage) {
        if (dataSourceName != null && !dataSourceName.isEmpty()) {
            dsName = dataSourceName;
        }
        try {
            List<BltFile> fileEntries = null;
            if (selectedPage != null) {
                if (selectedPage.matches("<<")) {
                    firstPage();
                    fileEntries = fileManager.load(dsName, 0,
                            BltFileManager.ENTRIES_PER_PAGE);
                } else {
                    if (selectedPage.matches(">>")) {
                        lastPage(dsName);
                    } else if (selectedPage.matches(">")) {
                        nextPage(dsName);
                    } else if (selectedPage.matches("<")) {
                        previousPage();
                    } else {
                        int page = Integer.parseInt(selectedPage);
                        setCurrentPage(page);
                    }
                    int offset = (getCurrentPage() 
                            * BltFileManager.ENTRIES_PER_PAGE)
                            - BltFileManager.ENTRIES_PER_PAGE;
                    fileEntries = fileManager.load(dsName, offset,
                            BltFileManager.ENTRIES_PER_PAGE );
                }
            } else {
                setCurrentPage(1);
                fileEntries = fileManager.load(dsName, 0, 
                        BltFileManager.ENTRIES_PER_PAGE );
            }

            int[] pages = getPages(dsName);
            model.addAttribute(DATASOURCE_NAME_KEY, dsName);
            model.addAttribute(FIRST_PAGE_KEY, pages[0]);
            model.addAttribute(LAST_PAGE_KEY, pages[1]);
            model.addAttribute(CURRENT_PAGE_KEY, pages[2]);
            model.addAttribute(FILES_KEY, fileEntries);
        } catch (Exception e) {
            model.addAttribute(FC_ERROR_KEY, 
                    messages.getMessage(FC_ERROR_MESSAGE_KEY, 
                        new String[] {e.getMessage()}));
        }
        return SUCCESS_VIEW;
    }
    
    /**
     * Get page numbers for a current set of file entries.
     * @return Numbers of first, last and current page
     */
    private int[] getPages(String dataSourceName) {
        long numEntries = fileManager.count(dataSourceName);
        int numPages = (int) Math.ceil(
                numEntries / BltFileManager.ENTRIES_PER_PAGE);
        if ((numPages * BltFileManager.ENTRIES_PER_PAGE ) < numEntries) {
            ++numPages;
        }
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
     */
    public void nextPage(String dataSourceName) {
        int lastPage = (int) 
                Math.ceil(fileManager.count(dataSourceName) /
                    BltFileManager.ENTRIES_PER_PAGE);
        if ((lastPage * BltFileManager.ENTRIES_PER_PAGE ) < 
                fileManager.count(dataSourceName)) {
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
    public void lastPage(String dataSourceName) {
        long numEntries = fileManager.count(dataSourceName);
        int lastPage = (int) Math.ceil(
                numEntries / BltFileManager.ENTRIES_PER_PAGE);
        if ((lastPage * BltFileManager.ENTRIES_PER_PAGE) 
                < fileManager.count(dataSourceName)) {
            ++lastPage;
        }
        if (lastPage == 0) {
            ++lastPage;
        }
        currentPage = lastPage;
    }
    
    /**
     * @param fileManager the bltFileManager to set
     */
    @Autowired
    public void setBltFileManager(BltFileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
}
