/* --------------------------------------------------------------------
Copyright (C) 2009-2010 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the BaltradDex package.

The BaltradDex package is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The BaltradDex package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex package library.  If not, see <http://www.gnu.org/licenses/>.
------------------------------------------------------------------------*/
package eu.baltrad.beastui.web.pojo;

/**
 * Mapper object that maps between a name and a value
 * 
 * @author Anders Henja
 */
public class CronEntryMapping {
  private String name = null;
  private String value = null;
  
  /**
   * Constructor
   * @param value - the valid cron value
   * @param name - the name (or if null, then name will be same as value)
   */
  public CronEntryMapping(String value, String name) {
    if (value == null) {
      throw new NullPointerException("value must be != null");
    }
    this.value = value;
    if (name != null) {
      this.name = name;
    } else {
      this.name = value;
    }
  }
  
  /**
   * @return the value to return
   */
  public String getValue() {
    return this.value;
  }
  
  /**
   * @return the name to return
   */
  public String getName() {
    return this.name;
  }
}
