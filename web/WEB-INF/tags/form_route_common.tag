<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tags/functions.tld" %>

<%@attribute name="route" type="eu.baltrad.beast.router.RouteDefinition"%>
<%@attribute name="adaptors" type="java.util.List"%>

<%@attribute name="extraLeft" fragment="true"%>
<%@attribute name="extraRight" fragment="true"%>

<%@attribute name="formAction"%>
<%@attribute name="formMethod"%>
<%@attribute name="create" type="java.lang.Boolean"
             description="set true if creating (not modifying) the rule"%>

<form action="${formAction}" method="${formMethod ? formMethod : 'POST'}">
    <div class="leftcol">
        <div class="row">Name</div>
        <div class="row">Author</div>
        <div class="row">Active</div>
        <div class="row">Description</div>
        <c:if test="${adaptors != null}">
          <div class="row4">Recipients</div>
        </c:if>
        <jsp:invoke fragment="extraLeft"/>
    </div>
    <div class="rightcol">
        <div class="row">
            <input type="text" name="name" value="${route.name}" ${create ? '' : 'disabled' }/>
            <c:if test="${!create}">
              <input type="hidden" name="name" value="${route.name}"/>
            </c:if>
            <div class="hint">
               Route name
            </div>
        </div>
        <div class="row">
            <input type="text" name="author" value="${route.author}"/>
            <div class="hint">
               Route author's name
            </div>
        </div>
        <div class="row">
            <input type="checkbox" name="active" ${route.active ? 'checked' : ''}/>
            <div class="hint">
               Check to activate route
            </div>
        </div>
        <div class="row">
            <input type="text" name="description" value="${route.description}"/>
            <div class="hint">
               Verbose description
            </div>
        </div>
        <c:if test="${adaptors!=null}">
        <div class="row4">
            <select multiple size="4" name="recipients">
            <c:forEach var="adaptor" items="${adaptors}">
                <option value="${adaptor}" ${fn:listContains(route.recipients, adaptor) ? 'selected' : ''}>${adaptor}</option>
            </c:forEach>
            </select>
            <div class="hint">
               Select target adaptors
            </div>
        </div>
        </c:if>
        <jsp:invoke fragment="extraRight"/>
    </div>
    <div class="tablefooter">
      <div class="buttons">
       <c:choose>
         <c:when test="${create}">
           <button class="rounded" name="submitButton" type="submit" value="Add">
             <span>Add</span>
           </button>
         </c:when>
         <c:otherwise>
           <button class="rounded" name="submitButton" type="submit" value="Modify">
             <span>Modify</span>
           </button>
           <button class="rounded" name="submitButton" type="submit" value="Delete">
             <span>Delete</span>
           </button>
         </c:otherwise>
       </c:choose>
      </div>
   </div>
</form>
