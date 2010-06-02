<%-- 
    Document   : Confirm user account removal page
    Created on : May 25, 2010, 4:04:09 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Remove user account</title>
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
                        <h1>Remove user account</h1>
                        <br/>
                        <h2>
                            <p>
                            <c:choose>
                                <c:when test="${not empty hibernate_errors}">
                                    <p>
                                    Failed to remove user account:
                                    </p>
                                    <c:forEach var="error_msg" items="${hibernate_errors}">
                                        <c:out value="${error_msg}" escapeXml="false"/><br/>
                                    </c:forEach>
                                    <p>
                                    Try to clear data delivery register before removing
                                    user account.
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    Selected user accounts have been removed from the system.
                                </c:otherwise>
                            </c:choose>
                            </p>
                        </h2>
                        <form action="admin.htm">
                            <div id="table-footer-rightcol">
                                <input type="submit" value="OK" name="submit_button"/>
                            </div>
                        </form>
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
