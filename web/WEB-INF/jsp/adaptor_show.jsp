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
Modify adaptor
@date 2010-03-23
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="Edit adaptor">
    <jsp:body>
        <div class="adaptors">
            <div class="table">
                <div class="header">
                    <div class="row">Edit adaptor</div>
                </div>
                <div class="header-text">
                     Edit XMLRPC adaptor.
                </div>
                <form name="showAdaptorForm" action="adaptor_edit.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <div class="body">
                        <div class="row2">
                            <div class="leftcol">
                                Name:
                            </div>
                            <div class="rightcol">
                                <c:out value="${name}"/>
                                <input type="hidden" name="name" 
                                       value="${name}"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">
                                Type:
                            </div>
                            <div class="rightcol"> 
                                <select name="type" title="Adaptor type">
                                    <c:forEach var="adtype" items="${types}">
                                        <c:choose>
                                            <c:when test="${adtype == type}">
                                                <option value="${adtype}" 
                                                        selected>
                                                    ${adtype}
                                                </option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${adtype}">
                                                    ${adtype}
                                                </option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>                          
                                </select>        
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">
                                URI:
                            </div>
                            <div class="rightcol">
                                <input type="text" name="uri" value="${uri}"
                                       title="Adaptor URI"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">
                                Timeout:
                            </div>
                            <div class="rightcol">
                                <input type="text" name="timeout"
                                       title="Timeout in milliseconds"
                                       value="<c:out value="${timeout}" 
                                       default="5000"/>"/>
                            </div>
                        </div>
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="submit"
                                       name="submitButton" value="Save"/>
                            </div>
                            <div class="button-wrap">
                                <input class="button" type="submit"
                                       name="submitButton" value="Delete">
                            </div>
                        </div>
                    </div>                      
                </form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>
