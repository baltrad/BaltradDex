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

package eu.baltrad.dex.util;

/**
 * Defines interface allowing to display records from the database in a table.
 *
 * @author Maciej Szewczykowski :: maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public interface ITableScroller {
    /**
     * Gets current page number.
     *
     * @return Current page number
     */
    public int getCurrentPage();
    /**
     * Sets current page number.
     *
     * @param page Current page number to set
     */
    public void setCurrentPage( int page );
    /**
     * Sets current page number to the next page.
     */
    public void nextPage();
    /**
     * Sets current page number to the previous page.
     */
    public void previousPage();
    /**
     * Sets current page number to the first page.
     */
    public void firstPage();
    /**
     * Sets current page number to the last page.
     */
    public void lastPage();
}
//--------------------------------------------------------------------------------------------------
