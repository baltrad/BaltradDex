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

package eu.baltrad.dex.net.util;

import java.util.ArrayList;
import java.util.List;

import eu.baltrad.bdb.db.FileEntry;
import eu.baltrad.bdb.oh5.Attribute;
import eu.baltrad.bdb.oh5.Metadata;
import eu.baltrad.bdb.oh5.Source;
import eu.baltrad.bdb.util.FileNamer;

public class IncomingFileNamer implements FileNamer {
  private List<String> sourceKeyPriorities;

  public IncomingFileNamer() {
    sourceKeyPriorities = new ArrayList<String>();
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
  public String name(FileEntry entry) {
    return nameMetadata(entry.getMetadata(), entry.getSource());
  }

  protected String nameMetadata(Metadata meta) {
    return nameMetadata(meta, meta.getSource());
  }

  /**
   * Give names similar to `(PVOL seang 2011-06-13T13:14)`
   */
  protected String nameMetadata(Metadata meta, Source source) {
    String name = "";
    name += meta.getWhatObject();
    name += " ";
    name += getSourceRepr(source);
    name += " ";
    name += meta.getWhatDate().toExtendedIsoString();
    name += "T";
    name += meta.getWhatTime().toExtendedIsoString();
    if (meta.getWhatObject().equals("SCAN")) {
      name += " elangle=";
      Attribute elangle = meta.getAttribute("/dataset1/where/elangle");
      if (elangle != null) {
        name += elangle.toString();
      } else {
        name += "?";
      }
    }
    return name;
  }

  protected String getSourceRepr(Source src) {
    for (String key : sourceKeyPriorities) {
      if (src.has(key)) {
        return key + ":" + src.get(key);
      }
    }
    return "unknown";
  }

}
