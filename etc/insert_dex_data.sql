/***************************************************************************************************
Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW

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
****************************************************************************************************
Document   : SQL script inserts data into BaltradDex tables
Created on : Jun 22, 2010, 11:57:02 AM
Author     : szewczenko
***************************************************************************************************/

-- dex_roles ---------------------------------------------------------------------------
INSERT INTO dex_roles (id, role) VALUES (1, 'admin'), (2, 'operator'),
    (3, 'peer'), (4, 'user');

-- dex_users ---------------------------------------------------------------------------------------
INSERT INTO dex_users (name, role_name, password, org_name, org_unit, locality, 
                        state, country_code, node_address)
    VALUES ('admin', 'admin', MD5('baltrad'), 'Organization name', 'Unit name', 
            'City', 'Country', 'XX', 'http://127.0.0.1:8084');

-- dex_radars --------------------------------------------------------------------------------------
--INSERT INTO dex_radars (name, wmo_number) VALUES ('Legionowo', '12374'),
--    ('Świdwin', '12220'), ('Brzuchania', '12568'), ('Pastewnik', '12544'),
--    ('Rzeszów', '12579'), ('Ramża', '12514'), ('Poznań', '12331'), ('Gdańsk', '12151');

-- dex_data_quantity -------------------------------------------------------------------------------
INSERT INTO dex_data_quantities (data_quantity, unit, description) VALUES
    ('TH', 'Th [dBZ]', 'Logged horizontally-polarized total (uncorrected) reflectivity factor'),
    ('TV', 'Tv [dBZ]', 'Logged vertically-polarized total (uncorrected) reflectivity factor'),
    ('DBZH', 'Zh [dBZ]', 'Logged horizontally-polarized (corrected) reflectivity factor'),
    ('DBZV', 'Zv [dBZ]', 'Logged vertically-polarized (corrected) reflectivity factor'),
    ('ZDR', 'ZDR [dBZ]', 'Logged differential reflectivity'),
    ('RHOHV', 'ρhv [0-1]', 'Correlation between Zh and Zv'),
    ('LDR', 'Ldr [dB]', 'Linear depolarization ratio'),
    ('PHIDP', 'φdp [degrees]', 'Differential phase'),
    ('KDP', 'Kdp [degrees/km]', 'Specific differential phase'),
    ('SQI', 'SQI [0-1]', 'Signal quality index'),
    ('SNR', 'SNR [0-1]', 'Normalized signal-to-noise ratio'),
    ('RATE', 'RR [mm/h]', 'Rain rate'),
    ('ACRR', 'RRaccum [mm]', 'Accumulated precipitation'),
    ('HGHT', 'H [km]', 'Height (of echotops)'),
    ('VIL', 'VIL [kg/m2 ]', 'Vertical Integrated Liquid water'),
    ('VRAD', 'Vrad [m/s]', 'Radial velocity'),
    ('WRAD', 'Wrad [m/s]', 'Spectral width of radial velocity'),
    ('UWND', 'U [m/s]', 'Component of wind in x-direction'),
    ('VWND', 'V [m/s]', 'Component of wind in y-direction'),
    ('BRDR', '0 or 1', '1 denotes a border where data from two or more radars meet in composites,
                        otherwise 0'),
    ('QIND', 'Quality [0-1]', 'Spatially analayzed quality indicator, according to OPERA II,
                                normalized to between 0 (poorest quality) to 1 (best quality)');

-- dex_file_objects --------------------------------------------------------------------------------
INSERT INTO dex_file_objects (file_object, description) VALUES
    ('PVOL', 'Polar volume'), ('CVOL', 'Cartesian volume'), ('SCAN', 'Polar scan'),
    ('RAY', 'Single polar ray'), ('AZIM', 'Azimuthal object'), ('IMAGE', '2-D cartesian image'),
    ('COMP', 'Cartesian composite image(s)'), ('XSEC', '2-D vertical cross section(s)'),
    ('VP', '1-D vertical profile'), ('PIC', 'Embedded graphical image');

-- dex_products ------------------------------------------------------------------------------------
INSERT INTO dex_products (product, description) VALUES
    ('SCAN', 'A scan of polar data'), ('PPI', 'Plan position indicator'),
    ('CAPPI', 'Constant altitude PPI'), ('PCAPPI', 'Pseudo-CAPPI'), ('ETOP', 'Echo top'),
    ('MAX', 'Maximum'), ('RR', 'Accumulation'), ('VIL', 'Vertically integrated liquid water'),
    ('COMP', 'Composite'), ('VP', 'Vertical profile'), ('RHI', 'Range height indicator'),
    ('XSEC', 'Arbitrary vertical slice'), ('VSP', 'Vertical side panel'),
    ('HSP', 'Horizontal side panel'), ('RAY', 'Ray'), ('AZIM', 'Azimuthal type product'),
    ('QUAL', 'Quality metric');

-- dex_product_parameters --------------------------------------------------------------------------
INSERT INTO dex_product_parameters (parameter, description) VALUES
    ('CAPPI', 'Layer height (meters above the radar)'), ('PPI', 'Elevation angle used (degrees)'),
    ('ETOP', 'Reflectivity level (dBZ)'), ('RHI', 'Azimuth angle (degrees)'),
    ('VIL', 'Bottom and top heights (m) of the integration layer');

