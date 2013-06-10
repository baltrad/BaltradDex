<%-- 
    Document   : Generic page
    Created on : Apr 12, 2013, 2:24:23 PM
    Author     : szewczenko
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" 
    "http://www.w3.org/TR/html4/strict.dtd">

<%@tag pageEncoding="UTF-8" %>

<%@attribute name="pageTitle" required="true"%>
<%@attribute name="extraHeader" fragment="true" 
             description="Extra code to put before </head>" %>
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
        <jsp:invoke fragment="extraHeader"/>
        <title>BALTRAD | ${pageTitle}</title>
	</head>
    <body>
        <div id="container">
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
            <div id="clearfooter"></div>
		</div>
        <%@include file="footer.tag" %>    
	</body>	
</html>