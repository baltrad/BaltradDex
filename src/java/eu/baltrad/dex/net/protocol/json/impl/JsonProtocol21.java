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
package eu.baltrad.dex.net.protocol.json.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import eu.baltrad.dex.datasource.model.DataSource;

/**
 * Json protocol 2.1. Added support for fileObject and source.
 * @author Anders Henja
 */
public class JsonProtocol21 extends JsonProtocol20 {
  private ObjectMapper mapper = new ObjectMapper();
  private static Logger logger = LogManager.getLogger(JsonProtocol21.class);
  public JsonProtocol21() {
    super();
  }
  
  @Override
  public String dataSourcesToJson(Set<DataSource> dataSources) {
    try {
      return mapper.writer().writeValueAsString(dataSources);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create json string from data sources", e);
    }
  }

  @Override
  public Set<DataSource> jsonToDataSources(String json) {
    try {
      return mapper.readValue(json, new TypeReference<HashSet<DataSource>>() {});
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse create json string from data sources", e);
    }
  }
}
