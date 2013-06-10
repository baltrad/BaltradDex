<%------------------------------------------------------------------------------
Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW

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
--------------------------------------------------------------------------------
Modify quality control
@date 2011-09-22
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="Edit quality control">
    <jsp:body>
        <div class="quality-controls">
            <div class="table">
                <div class="header">
                    <div class="row">Edit quality control</div>
                </div>
                <div class="header-text">
                     Modify or delete quality control. The description should 
                     explain what the quality control is doing.
                </div>
                <form name="modifyAnomalyDetectorForm" 
                      action="anomaly_detector_edit.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <div class="body">
                        <div class="row2">
                            <div class="leftcol">
                                Name:
                            </div>
                            <div class="rightcol">
                                <c:out value="${name}"/>
                                <input type="hidden" name="name" 
                                       value="${name}"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">
                                Description:
                            </div>
                            <div class="rightcol"> 
                                <input type="text" name="description" 
                                       value="${description}"
                                       title="Description of the quality control"/>      
                            </div>
                        </div>
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="submit"
                                       name="submitButton" value="Save"/>
                            </div>
                            <div class="button-wrap">
                                <input class="button" type="submit"
                                       name="submitButton" value="Delete">
                            </div>
                        </div>
                    </div>                      
                </form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>
