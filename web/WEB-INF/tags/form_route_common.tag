<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/functions.tld" %>

<%@attribute name="route" type="eu.baltrad.beast.router.RouteDefinition"%>
<%@attribute name="adaptors" type="java.util.List"%>

<%@attribute name="extraBottom" fragment="true"%>
<%@attribute name="extraButtons_pre" fragment="true"%>
<%@attribute name="extraButtons_post" fragment="true"%>

<%@attribute name="formAction"%>
<%@attribute name="encodingType"%>
<%@attribute name="formMethod"%>
<%@attribute name="create" type="java.lang.Boolean"
             description="set true if creating (not modifying) the rule"%>

<form action="${formAction}" method="${formMethod ? formMethod : 'POST'}" enctype="${encodingType != null ? encodingType : 'application/x-www-form-urlencoded'}">
    <div class="body">
        <div class="row2">
            <div class="leftcol">Name:</div>
            <div class="rightcol">
                <c:choose>
                    <c:when test="${!create}">
                        <c:out value="${route.name}"/>
                        <input type="hidden" name="name" value="${route.name}"/>
                    </c:when>
                    <c:otherwise>
                         <input type="text" name="name" value="${route.name}" 
                                title="Route name"/>
                    </c:otherwise>
                </c:choose> 
            </div>        
        </div>
        <div class="row2">
            <div class="leftcol">Author:</div>
            <div class="rightcol">
                <input type="text" name="author" 
                       value="${route.author}" 
                       title="Route author's name"/>
            </div>
        </div>
        <div class="row2">
            <div class="leftcol">Active:</div>
            <div class="rightcol">
                <input type="checkbox" name="active" 
                       title="Check to activate route"
                       ${route.active ? 'checked' : ''}/>
            </div>
        </div>
        <div class="row2">
            <div class="leftcol">Description:</div>
            <div class="rightcol">
                <input type="text" name="description" 
                       value="${route.description}"
                       title="Route's description"/>
            </div>
        </div>
        <c:if test="${adaptors != null}">               
            <div class="row2">
                <div class="leftcol">Recipients:</div>
                <div class="rightcol">
                    <select id="recipients" multiple size="4" 
                            name="recipients" 
                            title="Select target adaptors">
                        <c:forEach var="adaptor" items="${adaptors}">
                            <option value="${adaptor}" ${fn:listContains(route.recipients, adaptor) ? 'selected' : ''}>${adaptor}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </c:if>
        <jsp:invoke fragment="extraBottom"/>                
    </div>
    <div class="table-footer">
        <div class="buttons">
            <c:choose>
                <c:when test="${create}">
                    <jsp:invoke fragment="extraButtons_pre"/>                     
                    <div class="button-wrap">
                        <input class="button" type="submit" name="submitButton"
                               value="Add"/>
                    </div>
                    <jsp:invoke fragment="extraButtons_post"/>                     
                 </c:when>
                 <c:otherwise>
                    <jsp:invoke fragment="extraButtons_pre"/>
                    <div class="button-wrap">
                         <input class="button" type="submit" name="submitButton"
                                value="Save"/>
                     </div>
                     <div class="button-wrap">
                        <input class="button" type="submit" name="submitButton"
                               value="Delete"/>
                     </div> 
                    <jsp:invoke fragment="extraButtons_post"/>                     
                 </c:otherwise>
            </c:choose>
        </div>
    </div>                     
</form>
