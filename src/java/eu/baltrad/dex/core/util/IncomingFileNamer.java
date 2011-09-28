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

package eu.baltrad.dex.core.util;

import java.util.ArrayList;
import java.util.List;

import eu.baltrad.fc.FileEntry;
import eu.baltrad.fc.FileNamer;
import eu.baltrad.fc.Oh5Attribute;
import eu.baltrad.fc.Oh5File;
import eu.baltrad.fc.Oh5Metadata;
import eu.baltrad.fc.Oh5Source;

public class IncomingFileNamer extends FileNamer {
  private List<String> sourceKeyPriorities;

  public IncomingFileNamer() {
    sourceKeyPriorities = new ArrayList<String>();
    sourceKeyPriorities.add("_name");
    sourceKeyPriorities.add("NOD");
    sourceKeyPriorities.add("PLC");
    sourceKeyPriorities.add("WMO");
    sourceKeyPriorities.add("ORG");
    sourceKeyPriorities.add("CTY");
  }

  public List<String> getSourceKeyPriorities() {
    return sourceKeyPriorities;
  }

  public void setSourceKeyPriorities(List<String> value) {
    sourceKeyPriorities = value;
  }

  @Override
  protected String do_name(Oh5File file) {
    return nameMetadata(file.metadata());
  }

  @Override
  protected String do_name(FileEntry entry) {
    return nameMetadata(entry.metadata(), entry.source());
  }

  protected String nameMetadata(Oh5Metadata meta) {
    return nameMetadata(meta, meta.source());
  }

  /**
   * Give names similar to `(PVOL seang 2011-06-13T13:14)`
   */
  protected String nameMetadata(Oh5Metadata meta, Oh5Source source) {
    String name = "";
    name += meta.what_object();
    name += " ";
    name += getSourceRepr(source);
    name += " ";
    name += meta.what_date().to_iso_string(true);
    name += "T";
    name += meta.what_time().to_iso_string(true);
    if (meta.what_object().equals("SCAN")) {
      name += " elangle=";
      Oh5Attribute elangle = meta.attribute("/dataset1/where/elangle");
      if (elangle != null) {
        name += String.valueOf(elangle.value().to_double());
      } else {
        name += "?";
      }
    }
    return name;
  }

  protected String getSourceRepr(Oh5Source src) {
    for (String key : sourceKeyPriorities) {
      if (src.has(key)) {
        return key + ":" + src.get(key);
      }
    }
    return "unknown";
  }

}
