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

import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Datatype;

/**
 * @author Anders Henja
 */
public class BltAttribute {
  private Attribute attr = null;
  
  public BltAttribute(Attribute attr) {
    this.attr = attr;
  }
  
  public boolean isDouble() {
    return attr.getType().getDatatypeClass() == Datatype.CLASS_FLOAT;
  }
  
  public Double getDouble() {
    if (isDouble()) {
      Double result = null;
      try {
        float attrFloat[] = (float[]) attr.getValue();
        Float f = attrFloat[0];
        double d = f.doubleValue();
        result = d;
      } catch (ClassCastException e) {
        double attrDouble[] = (double[]) attr.getValue();
        Double d = attrDouble[0];
        result = d;
      }
      return result;
    } else {
      throw new RuntimeException("Can not return Double.");
    }
  }
  
  public boolean isLong() {
    return attr.getType().getDatatypeClass() == Datatype.CLASS_INTEGER;
  }

  public Long getLong() {
    if (isLong()) {
      Long result = null;
      try {
        int attrInt[] = (int[]) attr.getValue();
        Integer i = attrInt[0];
        long l = i.longValue();
        result = l;
      } catch (ClassCastException e) {
        long attrLong[] = (long[]) attr.getValue();
        Long l = attrLong[0];
        result = l;
      }
      return result;
    } else {
      throw new RuntimeException("Can not return Long.");
    }
  }

  public boolean isString() {
    return attr.getType().getDatatypeClass() == Datatype.CLASS_STRING;
  }
  
  public String getString() {
    if (isString()) {
      return ((String[]) attr.getValue())[0];
    } else {
      throw new RuntimeException("Can not return String.");
    }
  }
  
  public Object getValue() {
    return null;
  }
}
