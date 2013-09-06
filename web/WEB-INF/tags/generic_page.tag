<%-- 
    Document   : Generic page
    Created on : Apr 12, 2013, 2:24:23 PM
    Author     : szewczenko
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" 
    "http://www.w3.org/TR/html4/strict.dtd">

<%@tag pageEncoding="UTF-8" %>
<%@ tag import="eu.baltrad.dex.user.model.Role" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="pageTitle" required="true"%>
<%@attribute name="extraHeader" fragment="true" 
             description="Extra code to put before </head>" %>

<jsp:useBean id="securityManager"
             scope="session"
             class="eu.baltrad.dex.auth.manager.SecurityManager">
</jsp:useBean>
<jsp:useBean id="configurationManager"
             scope="session"
             class="eu.baltrad.dex.config.manager.impl.ConfigurationManager">
</jsp:useBean>

<%
    Role role = (Role) securityManager.getSessionRole(session);
    if (role == null) {
        request.getSession().setAttribute("sessionInvalid", 1);
    } else {
        if (role.getName().equals(Role.ADMIN)) {
        request.getSession().setAttribute("sessionRole", 1);
        }
        if (role.getName().equals(Role.OPERATOR)) {
            request.getSession().setAttribute("sessionRole", 2);
        }
        if (role.getName().equals(Role.USER)) {
            request.getSession().setAttribute("sessionRole", 3);
        }
    }
    request.getSession().setAttribute("nodeName", 
            configurationManager.getAppConf().getNodeName());
%>

<html>
	<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="StyleSheet" href="includes/dex.css" type="text/css" 
              media="screen"/>
        <link rel="icon" type="image/png" href="includes/images/favicon.png"/>
        <script src="includes/js/main_menu.js" type="text/javascript"></script>
        <script src="includes/js/show_hide_status.js" type="text/javascript"></script>
        <script src="includes/js/jQuery.js" type="text/javascript"></script>
        <script src="includes/js/datetimepicker_css.js" type="text/javascript"></script>
        <script src="includes/js/validate_time_input.js" type="text/javascript"></script>
        <script src="includes/js/submit_center_id.js" type="text/javascript"></script>
        <script src="includes/js/copy_select_option.js" type="text/javascript"></script>          
        <script src="includes/js/filter.js" type="text/javascript"></script>
        <script src="includes/js/messages_ajax.js" type="text/javascript"></script>
        <jsp:invoke fragment="extraHeader"/>
        
        <jsp:invoke fragment="extraHeader"/>
        <title>BALTRAD | ${pageTitle}</title>
	</head>
    <body>
        <div id="container">
            <c:choose>
                <c:when test="${sessionInvalid == 1}">
                    <div id="header"></div>
                    <div id="sidebar">
                        <div id="logo-init"></div>
                    </div>
                    <div id="content">
                        <div id="session-expiry">
                            <div class="messagebox" id="msg">
                                <div class="header">
                                    <c:out value="Session expired."/>
                                </div>
                                <div class="body">
                                    Session has timed out.
                                    <a href="login.htm">Log in</a>
                                    to start a new session.
                                </div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div id="header">
                        <div class="buttons">
                            <a href="help/index.html">Help</a>
                            <a href="j_spring_security_logout">Log Out</a>
                        </div>	
                    </div>
                    <div id="sidebar">
                        <div id="logo"></div>
                        <div id="logo-title"></div>
                        <%@include file="main_menu.tag" %>
                    </div>
                    <div id="content">
                        <%@include file="title_bar.tag" %>
                        <jsp:doBody/>
                    </div>
                </c:otherwise>
            </c:choose>
            <div id="clearfooter"></div>
		</div>
        <%@include file="footer.tag" %>    
	</body>	
</html>