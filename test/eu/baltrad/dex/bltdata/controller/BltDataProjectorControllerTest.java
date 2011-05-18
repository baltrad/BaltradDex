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

package eu.baltrad.dex.bltdata.controller;

import eu.baltrad.dex.bltdata.model.BltDataProjector;

import junit.framework.TestCase;

/**
 * Test class for data projector model.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class BltDataProjectorControllerTest extends TestCase {
//---------------------------------------------------------------------------------------- Variables
    private static BltDataProjector bltDataProjector;
//------------------------------------------------------------------------------------------ Methods

    public void testInit() {
        bltDataProjector = new BltDataProjector();
        assertNotNull( bltDataProjector );
    }

    public void testInitializeProjection() {
        String[] projParms = new String[] { "+proj=aeqd", "+lat_0=52.40522", "+lon_0=20.96063",
            "+ellps=WGS84", "+a=6371000" };
        int res = bltDataProjector.initializeProjection( projParms );

        assertEquals( 0, res );

    }
}
//--------------------------------------------------------------------------------------------------
