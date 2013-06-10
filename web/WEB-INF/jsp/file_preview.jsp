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
Document   : Radar image preview page using Google Maps API
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
        <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
        <script type="text/javascript" src="includes/js/slider.js"></script>
        <script type="text/javascript">

            RadarOverlay.prototype = new google.maps.OverlayView();
            var radarImageURL = "<%=radarImageURL%>";
            var overlay;

            function initialize() {
                var lat0 = "<%=lat0%>";
                var lon0 = "<%=lon0%>";
                var llLat = "<%=llLat%>";
                var llLon = "<%=llLon%>";
                var urLat = "<%=urLat%>";
                var urLon = "<%=urLon%>";

                var centerLatLng = new google.maps.LatLng(lat0, lon0);
                var radarOptions = {
                  zoom: 6,
                  center: centerLatLng,
                  mapTypeId: google.maps.MapTypeId.ROADMAP
                };

                var map = new google.maps.Map(document.getElementById("map_canvas"),
                    radarOptions);

                var swBound = new google.maps.LatLng( llLat, llLon );
                var neBound = new google.maps.LatLng( urLat, urLon );
                var bounds = new google.maps.LatLngBounds( swBound, neBound );

                overlay = new RadarOverlay( bounds, radarImageURL, map );
                
                new Slider('opacity-slider', {
                    callback: function( value ) {
                        var opacity = Math.round( 10 - ( value*10 ) )
                        overlay.setOpacity( opacity );
                    }
                });
            }
            function RadarOverlay( bounds, image, map ) {
                this.bounds_ = bounds;
                this.image_ = image;
                this.map_ = map;
                this.div_ = null;
                this.setMap( map );
            }
            RadarOverlay.prototype.onAdd = function( opacity ) {

                var div = document.createElement( 'DIV' );
                div.style.border = "none";
                div.style.borderWidth = "0px";
                div.style.position = "absolute";

                var img = document.createElement( "img" );
                img.src = this.image_;
                img.style.width = "100%";
                img.style.height = "100%";
                div.appendChild(img);

                this.div_ = div;
                
                var panes = this.getPanes();
                panes.overlayImage.appendChild(this.div_);
            }
            RadarOverlay.prototype.draw = function() {
                var overlayProjection = this.getProjection();
                var sw = overlayProjection.fromLatLngToDivPixel(this.bounds_.getSouthWest());
                var ne = overlayProjection.fromLatLngToDivPixel(this.bounds_.getNorthEast());
                var div = this.div_;
                div.style.left = sw.x + 'px';
                div.style.top = ne.y + 'px';
                div.style.width = (ne.x - sw.x) + 'px';
                div.style.height = (sw.y - ne.y) + 'px';
            }
            RadarOverlay.prototype.hide = function() {
                if( this.div_ ) {
                    this.div_.style.visibility = "hidden";
                }
            }
            RadarOverlay.prototype.show = function() {
                if( this.div_ ) {
                    this.div_.style.visibility = "visible";
                }
            }
            RadarOverlay.prototype.toggle = function() {
                if ( this.div_ ) {
                    if (this.div_.style.visibility == "hidden") {
                      this.show();
                    } else {
                      this.hide();
                    }
                }
            }
            RadarOverlay.prototype.setOpacity = function( opacity ) {
                if( this.div_ ) {
                    this.div_.style.opacity = opacity / 10;
                    this.div_.style.filter = 'alpha(opacity=' + opacity * 10 + ')';
                }
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
                                   onclick="overlay.toggle();"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>        
</html>
