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
Document   : Radar image preview page using Leaflet + OpenStreetMap
Created on : Dec 10, 2010, 10:14 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="java.util.HashMap"%>

<%
    String lat0 = (String) request.getAttribute("lat0");
    String lon0 = (String) request.getAttribute("lon0");
    String llLat = (String) request.getAttribute("llLat");
    String llLon = (String) request.getAttribute("llLon");
    String urLat = (String) request.getAttribute("urLat");
    String urLon = (String) request.getAttribute("urLon");
    String radarImageURL = (String) request.getAttribute("image_url");
%>

<html>
    <head>
        <title>BALTRAD | Data preview</title>
        <link href="includes/dex.css" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
        <script type="text/javascript" src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
        <script type="text/javascript" src="includes/js/slider.js"></script>
        <script type="text/javascript">

            var radarImageURL = "<%=radarImageURL%>";
            var map;
            var overlay;
            var overlayVisible = true;

            function initialize() {
                var lat0 = parseFloat("<%=lat0%>");
                var lon0 = parseFloat("<%=lon0%>");
                var llLat = parseFloat("<%=llLat%>");
                var llLon = parseFloat("<%=llLon%>");
                var urLat = parseFloat("<%=urLat%>");
                var urLon = parseFloat("<%=urLon%>");

                map = L.map('map_canvas').setView([lat0, lon0], 6);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
                    maxZoom: 19
                }).addTo(map);

                var bounds = L.latLngBounds([llLat, llLon], [urLat, urLon]);
                overlay = L.imageOverlay(radarImageURL, bounds).addTo(map);

                new Slider('opacity-slider', {
                    callback: function( value ) {
                        overlay.setOpacity( 1 - value );
                    }
                });
            }

            function toggleOverlay() {
                if (overlayVisible) {
                    map.removeLayer(overlay);
                } else {
                    overlay.addTo(map);
                }
                overlayVisible = !overlayVisible;
            }
        </script>
    </head>

    <body onload="initialize()">
        <div id="map_canvas"></div>
        <div id ="map-toolbar">
            <div class="center">
                <div class="left">
                    <div id="opacity-slider" class="slider">
                        <div class="handle">Opacity</div>
                    </div>
                </div>
                <div class="right">
                    <div class="buttons">
                        <div class="button-wrap">
                            <input class="button" type="button" value="Visible"
                                   onclick="toggleOverlay();"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
