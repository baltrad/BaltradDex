<%@attribute name="pageTitle" required="true"%>
<%@attribute name="extraHeader" fragment="true" description="Extra code to put before </head>" %>
<%@attribute name="extraBottom" fragment="true" description="Extra code to put before </head>" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
  <script src="includes/js/jQuery.js" type="text/javascript"></script>
  <script src="includes/js/menu.js" type="text/javascript"></script>
  <script src="includes/js/load_menu.js" type="text/javascript"></script>
  <script src="includes/js/datetimepicker_css.js" type="text/javascript"></script>
  <script src="includes/js/validate_time_input.js" type="text/javascript"></script>
  <jsp:invoke fragment="extraHeader"/>
  <!-- favicon -->
  <link rel="icon" type="image/png" href="includes/images/favicon.png"/>
  <title>BALTRAD | ${pageTitle}</title>
</head>
<body>
   <div id="bltcontainer">
     <div id="bltheader">
       <script type="text/javascript" src="includes/js/header.js"></script>
     </div>
     <div id="bltmain">
       <jsp:doBody/>
     </div>
  </div>
  <div id="bltfooter">
       <div class="projectref">
          &#169 2009-2011 <a href="http://baltrad.eu/">baltrad.eu</a>
       </div>
       <div class="info">
         <a href="feedback.htm">Feedback</a> |
         <a href="about.htm">About</a> |
         <a href="security.htm">Security</a> |
         <a href="terms.htm">Terms And Conditions</a>
       </div>
    </div> 
  <jsp:invoke fragment="extraBottom"/>
</body>
