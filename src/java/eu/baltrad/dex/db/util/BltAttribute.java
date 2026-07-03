/* --------------------------------------------------------------------
Copyright (C) 2009-2014 Swedish Meteorological and Hydrological Institute, SMHI,

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

package eu.baltrad.dex.db.util;

import io.jhdf.api.Attribute;

/**
 * @author Anders Henja
 */
public class BltAttribute {
  private Attribute attr = null;

  public BltAttribute(Attribute attr) {
    this.attr = attr;
  }

  public boolean isDouble() {
    Object data = attr.getData();
    return data instanceof Double || data instanceof Float;
  }

  public Double getDouble() {
    Object data = attr.getData();
    if (data instanceof Float) {
      return ((Float) data).doubleValue();
    } else if (data instanceof Double) {
      return (Double) data;
    } else {
      throw new RuntimeException("Can not return Double.");
    }
  }

  public boolean isLong() {
    Object data = attr.getData();
    return data instanceof Long || data instanceof Integer;
  }

  public Long getLong() {
    Object data = attr.getData();
    if (data instanceof Integer) {
      return ((Integer) data).longValue();
    } else if (data instanceof Long) {
      return (Long) data;
    } else {
      throw new RuntimeException("Can not return Long.");
    }
  }

  public boolean isString() {
    return attr.getData() instanceof String;
  }

  public String getString() {
    if (isString()) {
      return (String) attr.getData();
    } else {
      throw new RuntimeException("Can not return String.");
    }
  }

  public Object getValue() {
    return null;
  }
}
