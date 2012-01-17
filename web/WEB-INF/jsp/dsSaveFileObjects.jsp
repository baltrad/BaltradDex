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
Document   : Save data source file objects parameter page
Created on : Jun 27, 2011, 11:38 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>BALTRAD | Configure data source</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/settingsTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/settingsMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            Configure data source <div class="stepno">Step 3</div>
                        </div>
                        <div class="blttext">
                            Select file object
                            <div class="hint">
                                Select file objects that will be available with this
                                data source.
                            </div>
                        </div>
                        <div class="table">
                            <div class="dssave">
                                <form method="post" action="dsSaveUsers.htm">
                                    <div class="rightcol">
                                        <c:forEach items="${selectedFileObjects}" var="fobject">
                                            <div class="dsparam">
                                                <c:out value="${fobject.fileObject}"></c:out>
                                                &nbsp;
                                                <c:out value="${fobject.description}"></c:out>
                                                &nbsp;
                                            </div>
                                        </c:forEach>
                                        <div class="row">
                                            <c:if test="${numSelectedFileObjects <
                                                          numAvailableFileObjects}">
                                                <select name="fileObjectsList"
                                                        title="Select file object from the list">
                                                    <option value="select">
                                                        <c:out value="-- Select file object --"/>
                                                    </option>
                                                    <c:forEach items="${availableFileObjects}"
                                                               var="fobject">
                                                        <option value="${fobject.fileObject}">
                                                            <c:out value="${fobject.description}"/>
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                                <div class="dscontrol">
                                                    <input type="submit" name="addFileObject"
                                                        title="Add file object" value="+">
                                                </div>
                                            </c:if>
                                            <c:if test="${numSelectedFileObjects > 0}">
                                                <div class="dscontrol">
                                                    <input type="submit" name="removeFileObject"
                                                        title="Remove file object" value="-">
                                                </div>
                                            </c:if>
                                        </div>
                                        <c:if test="${not empty dsSelectFileObjectsError}">
                                            <div class="error">
                                                <c:out value="${dsSelectFileObjectsError}"/>
                                            </div>
                                        </c:if>
                                    </div>
                                    <div class="tablefooter">
                                        <div class="buttons">
                                           <button class="rounded" type="submit" name="backButton">
                                                <span>Back</span>
                                            </button>
                                            <button class="rounded" type="submit" name="nextButton">
                                                <span>Next</span>
                                            </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>