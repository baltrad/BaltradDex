/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
***************************************************************************************************/

/*
 * System administrator's menu
 */

document.write( "<div class=\"menu\">" );

document.write( "<div class=\"group\">Local node</div>" );

document.write( "<a href=\"home.htm\">Home</a>" );
document.write( "<a href=\"radars.htm\">Radars</a>" );
document.write( "<a href=\"log.htm\">System messages</a>" );

document.write( "<div class=\"separator\"></div>" );

document.write( "<div class=\"group\">Data Exchange</div>" );

document.write( "<a href=\"connectToNode.htm\">Connect to node</a>" );
document.write( "<a href=\"showSubscriptions.htm\">Subscriptions</a>" );

document.write( "<div class=\"separator\"></div>" );

document.write( "<div class=\"group\">Data Processing</div>" );

document.write( "<a href=\"adaptors.htm\">Adaptors</a>" );
document.write( "<a href=\"showroutes.htm\">Routes</a>" );
document.write( "<a href=\"showschedule.htm\">Schedule</a>" );

document.write( "<div class=\"separator\"></div>" );

document.write( "<div class=\"group\">Administration</div>" );

document.write( "<a href=\"configuration.htm\">Configuration</a>" );
document.write( "<a href=\"nodeProperties.htm\">Node properties</a>" );

document.write( "<div class=\"separator\"></div>" );

document.write( "</div>" );
