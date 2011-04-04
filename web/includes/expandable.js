/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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
 * Expandable HTML section script.
 */
function dsp( loc ) {
    if( document.getElementById ) {
        var foc=loc.firstChild;
        foc=loc.firstChild.innerHTML?
           loc.firstChild:
           loc.firstChild.nextSibling;
        foc.innerHTML=foc.innerHTML=='+'?'-':'+';
        foc=loc.parentNode.nextSibling.style?
           loc.parentNode.nextSibling:
           loc.parentNode.nextSibling.nextSibling;
        foc.style.display=foc.style.display=='block'?'none':'block';
    }
}
if( !document.getElementById )
   document.write( '<style type="text/css"><!--\n'+
      '.dspcont{display:block;}\n'+
      '//--></style>' );

