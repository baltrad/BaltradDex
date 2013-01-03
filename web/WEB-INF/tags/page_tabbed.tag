<%@taglib prefix="t" tagdir="/WEB-INF/tags" %> 

<%@attribute name="pageTitle" required="true"%>
<%@attribute name="activeTab" required="true"%>
<%@attribute name="extraHeader" fragment="true" description="Extra code to put before </head>" %>
<%@attribute name="extraBottom" fragment="true" description="Extra code to put before </head>" %>

<t:page_generic pageTitle="${pageTitle}"
                extraHeader="${extraHeader}"
		extraBottom="${extraBottom}">
  <jsp:body>
    <div id="tabs">
       <t:tabs activeTab="${activeTab}"/>
    </div>
    <div id="tabcontent">
       <jsp:doBody/>
    </div>
  </jsp:body>
</t:page_generic>
