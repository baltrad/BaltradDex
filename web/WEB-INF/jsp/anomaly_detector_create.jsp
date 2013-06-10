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
Create anomaly detectors
@date 2011-09-22
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="Create quality control">
    <jsp:body>
        <div class="quality-controls">
            <div class="table">
                <div class="header">
                    <div class="row">Create quality control</div>
                </div>
                <div class="header-text">
                     Create quality control string. The string is 
                     a PGF-specific value and must be supported by the targeted 
                     PGF. Add a describing text so that it is possible to see 
                     what the control is supposed to be doing.
                </div>
                <form name="createAnomalyDetectorForm" 
                      action="anomaly_detector_create.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <div class="body">
                        <div class="row2">
                            <div class="leftcol">
                                Name:
                            </div>
                            <div class="rightcol">
                                <input type="text" name="name" value="${name}"
                                       title="Quality controls name, valid characters are [A-Za-z0-9_.-]+"/>
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
                                       name="submitButton" value="Add"/>
                            </div>
                        </div>
                    </div>                      
                </form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>
