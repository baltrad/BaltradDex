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

import eu.baltrad.fc.FileEntry;
import eu.baltrad.fc.FileNamer;
import eu.baltrad.fc.Oh5Attribute;
import eu.baltrad.fc.Oh5File;
import eu.baltrad.fc.Oh5Metadata;

public class IncomingFileNamer extends FileNamer {
  @Override
  protected String do_name(Oh5File file) {
    return name_metadata(file.metadata());
  }

  @Override
  protected String do_name(FileEntry entry) {
    return name_metadata(entry.metadata());
  }

  /**
   * Give names similar to `(PVOL seang 2011-06-13T13:14)`
   */
  protected String name_metadata(Oh5Metadata meta) {
    String name = "";
    name += meta.what_object();
    name += " ";
    name += meta.source().get("_name");
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
}
