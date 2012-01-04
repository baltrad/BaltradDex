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

<%@page import="java.util.List" %>
<%
    List certs = (List)request.getAttribute("allCerts");
    if(certs == null || certs.size() <= 0) {
        request.getSession().setAttribute("certs_status", 0);
    } else {
        request.getSession().setAttribute("certs_status", 1);
    }
%>

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
                <c:when test="${certs_status == 1}">
                    <div class="blttext">
                        List of submitted certificates. Click certificate alias for details.
                        Use check boxes to accept or revoke a certificate.
                    </div>
                    <form method="post">
                        <div class="table">
                            <%@include file="/WEB-INF/jsp/formMessages.jsp"%>
                            <div class="certs">
                                <div class="tableheader">
                                    <div id="cell" class="count">&nbsp;</div>
                                    <div id="cell" class="alias">
                                        Certificate alias
                                    </div>
                                    <div id="cell" class="check">
                                        Trusted
                                    </div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach var="cert" items="${allCerts}">
                                    <div class="entry">
                                        <div id="cell" class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <div id="cell" class="alias">
                                            <a href="showCertificateDetails.htm?certId=${cert.id}" 
                                               title="Show certificate details">
                                                <c:out value="${cert.alias}"/>
                                            </a>
                                        </div>
                                        <div id="cell" class="check">
                                            <c:choose>
                                                <c:when test="${cert.trusted == true}">
                                                    <input type="checkbox" name="trustedCerts"
                                                           title="Trust/revoke certificate" 
                                                           value="${cert.id}" checked>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="checkbox" name="trustedCerts"
                                                           title="Trust/revoke certificate"
                                                           value="${cert.id}">    
                                                </c:otherwise>    
                                            </c:choose>    
                                        </div>
                                    </div>
                                </c:forEach>
                                <div class="tablefooter">
                                    <div class="buttons">
                                        <button class="rounded" type="submit">
                                            <span>Save</span>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>                
                </c:when>
                <c:otherwise>
                    <div class="blttext">
                        No submitted certificates have been found.
                    </div>
                    <div class="table">
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                        onclick="window.location.href='settings.htm'">
                                    <span>OK</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </jsp:body>
</t:page_tabbed>