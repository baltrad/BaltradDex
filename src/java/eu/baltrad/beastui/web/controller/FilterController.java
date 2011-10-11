/* --------------------------------------------------------------------
Copyright (C) 2009-2011 Swedish Meteorological and Hydrological Institute, SMHI,

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
package eu.baltrad.beastui.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.baltrad.beast.db.AttributeFilter;
import eu.baltrad.beast.db.CombinedFilter;
import eu.baltrad.beast.db.IFilter;

@Controller
class FilterController {
  private static Logger logger = LogManager.getLogger(FilterController.class);
  private ObjectMapper mapper = new ObjectMapper();

  FilterController() {
    System.out.println("FilterController()");
  }

  @RequestMapping(value="/filter_isValid.json")
  public void isValid(HttpServletRequest req,
                      HttpServletResponse rsp)
      throws java.io.IOException {
    String jsonString = IOUtils.toString(req.getInputStream());
    logger.debug(jsonString);
    IFilter filter = mapper.readValue(jsonString, IFilter.class);
    writeJson(rsp, JsonNodeFactory.instance.booleanNode(filter.isValid()));
  }

  @RequestMapping(value="/filter_expressionString.json")
  public void expressionString(HttpServletRequest req,
                               HttpServletResponse rsp)
      throws java.io.IOException {
    String jsonString = IOUtils.toString(req.getInputStream());
    logger.debug(jsonString);
    IFilter filter = mapper.readValue(jsonString, IFilter.class);
    JsonNode json = JsonNodeFactory.instance.textNode(filter.getExpression().toString());
    writeJson(rsp, json);
  }

  protected void writeJson(HttpServletResponse rsp, Object json)
      throws java.io.IOException {
    rsp.setHeader("content-type", "application/json");
    mapper.writeValue(rsp.getOutputStream(), json);
  }
}
