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
Document   : Delete delivery registry page
Created on : May 27, 2013, 8:45 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Delete registry">
    <jsp:body>
        <div class="registry-delete">
            <div class="table">
                <div class="header">
                    <div class="row">Delete registry</div>
                </div>
                <c:choose>
                    <c:when test="${number_of_entries == 0}">
                        <div class="header-text">
                            No entries found in delivery registry.
                        </div>
                        <div class="table-footer">
                            <div class="buttons">
                                <div class="button-wrap">
                                    <input class="button" type="button" 
                                           value="OK"
                                           onclick="window.location.href='status.htm'"/>
                                </div>
                            </div>
                        </div>   
                    </c:when>
                    <c:otherwise>
                        <div class="header-text">
                            Click <i>Delete</i> to remove all entries from 
                            delivery registry.
                        </div>
                        <form action="registry_delete_status.htm">
                            <div class="body">
                                <div class="row">
                                    <div class="leftcol">
                                        <span>
                                            Number of entries in delivery 
                                            registry:
                                        </span>
                                    </div>
                                    <div class="rightcol">
                                        <span>    
                                            ${number_of_entries}
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div class="table-footer">
                                <div class="buttons">
                                    <div class="button-wrap">
                                        <input class="button" type="button" 
                                               value="Back"
                                               onclick="window.location.href='status.htm'"/>
                                    </div>
                                    <div class="button-wrap">
                                        <input class="button" type="submit" 
                                               value="Delete"/>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </jsp:body>
</t:generic_page>
