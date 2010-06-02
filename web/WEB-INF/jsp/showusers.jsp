<%--
    Document   : User listing interface
    Created on : May 24, 2010, 12:40:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Registered users</title>
</head>

<body>
    <div id="container">
        <div id="header"></div>
        <div id="nav">
            <script type="text/javascript" src="includes/navigation.js"></script>
        </div>
        <div class="outer">
            <div class="inner">
                <div class="float-wrap">
                    <div id="main">
                        <h1>Registered users</h1>
                        <br/>
                        <h2>
                            <p>
                            User accounts currently available in the system.
                            </p>
                        </h2>
                        <display:table name="users" id="user" defaultsort="1"
                            requestURI="showusers.htm" cellpadding="0" cellspacing="2"
                            export="false" class="tableborder" pagesize="10">
                            <display:column sortProperty="id" sortable="true"
                                title="ID" class="tdcenter">
                                <fmt:formatNumber value="${user.id}" pattern="00" />
                            </display:column>
                            <display:column sortable="true" title="User name"
                                sortProperty="name" class="tdcenter"
                                value="${user.name}">
                            </display:column>
                            <display:column sortable="true" title="Role" sortProperty="role"
                                class="tdcenter" value="${user.role}">
                            </display:column>
                            <display:column sortable="true" title="Company"
                                sortProperty="factory" class="tdcenter"
                                value="${user.factory}">
                            </display:column>
                            <display:column sortable="true" title="Node address"
                                sortProperty="nodeAddress" class="tdcenter"
                                value="${user.nodeAddress}">
                            </display:column>
                        </display:table>
                        <div id="table-footer">
                            <a href="admin.htm">&#60&#60 System management</a>
                        </div>
                    </div>
                    <div id="left">
                        <script type="text/javascript" src="includes/mainmenu.js"></script>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </div>
    <div id="footer">
        <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</body>
</html>




