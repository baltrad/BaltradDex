<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW

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
Document   : Certificate management page
Created on : Dec 13, 2011, 10:00 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Certificate management" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Certificate management
            </div>
            <c:choose>
                <c:when test="${not empty error}">
                    <div class="systemerror">
                        <div class="header">
                            Problems encountered.
                        </div>
                        <div class="message">
                            ${error}
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="blttext">
                        Certificate details.
                    </div>
                    <div class="table">
                        <div class="leftcol">
                            <div class="row">Issuer's name:</div>
                            <div class="row">Organization:</div>
                            <div class="row">Unit:</div>
                            <div class="row">Locality:</div>
                            <div class="row">State:</div>
                            <div class="row">Country code:</div>
                            <div class="row">Fingerprint:</div>
                        </div>
                        <div class="rightcol">
                            <div class="row">${commonName}</div>
                            <div class="row">${organization}</div>
                            <div class="row">${unit}</div>
                            <div class="row">${locality}</div>
                            <div class="row">${state}</div>
                            <div class="row">${countryCode}</div>
                            <div class="row">${fingerprint}</div>
                        </div>
                    </div>        
                </c:otherwise>
            </c:choose>
            <div class="table">            
                <div class="tablefooter">
                    <div class="buttons">
                        <button class="rounded" type="button"
                                onclick="window.location.href='showCertificates.htm'">
                            <span>OK</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:page_tabbed>