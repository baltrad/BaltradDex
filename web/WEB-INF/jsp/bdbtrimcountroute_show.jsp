<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW

This file is part of the BaltradDex software.

BaltradDex is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BaltradDex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
----------------------------------------------------------------------------------------------------
Modifies a bdb_trim_count route
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Modify route" activeTab="processing">
    <jsp:body>
        <div class="left">
            <t:menu_processing/>
        </div>
        <div class="right">
            <div class="blttitle">
                Modify route
            </div>
            <div class="blttext">
              Modify or delete a DB Trim Count routing rule.
            </div>
            <div class="table">
              <t:error_message message="${emessage}"/>
              <div class="modifyroute">
                <form name="showRouteForm" action="bdbtrimcountroute_show.htm">
                  <div class="leftcol">
                    <div class="row">Name</div>
                    <div class="row">Author</div>
                    <div class="row">Active</div>
                    <div class="row">Description</div>
                    <div class="row">Count limit</div>
                  </div>
                  <div class="rightcol">
                    <div class="row">
                      <input type="text" name="name" value="${name}" disabled/>
                      <input type="hidden" name="name" value="${name}"/>
                      <div class="hint">
                        Route name
                      </div>
                    </div>
                    <div class="row">
                      <input type="text" name="author" value="${author}"/>
                      <div class="hint">
                        Route author's name
                      </div>
                    </div>
                    <div class="row">
                      <input type="checkbox" name="active" <c:if test="${active == true}">checked</c:if> />
                      <div class="hint">
                        Check to activate route
                      </div>
                    </div>
                    <div class="row">
                      <input type="text" name="description" value="${description}"/>
                      <div class="hint">
                        Verbose description
                      </div>
                    </div>
                    <div class="row">
                      <input type="text" name="countLimit" value="${countLimit}"/>
                      <div class="hint">
                        Specify maximum number of DB records
                      </div>
                    </div>                   
                  </div>
                  <div class="tablefooter">
                    <div class="buttons">
                      <button class="rounded" name="submitButton" type="submit" value="Modify">
                        <span>Modify</span>
                      </button>
                      <button class="rounded" name="submitButton" type="submit" value="Delete">
                        <span>Delete</span>
                      </button>
                    </div>
                  </div>
                </form>
              </div>
            </div>      
        </div>
    </jsp:body>
</t:page_tabbed>
