<%------------------------------------------------------------------------------
Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW

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
--------------------------------------------------------------------------------
Document   : Connect to peer node
Created on : May 29, 2013, 12:11 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Connect">
    <jsp:body>
        <div class="node-connect">
            <div class="table">
                <div class="header">
                    <div class="row">Connect to peer node</div>
                </div>
                <div class="header-text">
                    Select existing node from the list or enter 
                    node's URL address. 
                </div>
                <form:form method="POST" action="node_connected.htm">
                    <t:message_box msgHeader="Success."
                                   msgBody="${success_message}"
                                   errorHeader="Problems encountered."
                                   errorBody="${error_message}"/>
                    <div class="body">
                        <div class="section" id="node-select">
                            Select node
                        </div>
                        <div class="section-text">
                            Select node and click <i>Connect</i> in order 
                            to access data sources available at selected node.
                        </div>
                        <div class="row" id="node-select">
                            <select name="node_select"
                                    title="Node to connect">
                                <option selected/>
                                <c:forEach items="${nodes}" var="node">
                                    <option value="${node}"> 
                                        <c:out value="${node}"/>
                                    </option>
                                </c:forEach>
                            </select>             
                        </div>
                        <div class="section-text"> &nbsp; </div>
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="submit" 
                                       name="connect" value="Connect"/>
                            </div>
                        </div>
                    </div>
                </form:form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>
