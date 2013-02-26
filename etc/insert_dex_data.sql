/*******************************************************************************
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
********************************************************************************
Document   : SQL script inserts data into BaltradDex tables
Created on : Jun 22, 2010, 11:57:02 AM
Author     : szewczenko
*******************************************************************************/

INSERT INTO dex_roles (name) VALUES ('admin'), ('operator'), ('peer'), ('user'),
                                    ('node');

INSERT INTO dex_users (name, password, org_name, org_unit, locality, state, 
                       country_code)
            VALUES ('admin', MD5('baltrad'), 'Company', 'Unit', 'City', 
                    'Country', 'XX');

INSERT INTO dex_users_roles (user_id, role_id) VALUES (1, 1);

INSERT INTO dex_data_quantities (name, unit, description) VALUES
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

INSERT INTO dex_file_objects (name, description) VALUES
    ('PVOL', 'Polar volume'), ('CVOL', 'Cartesian volume'), ('SCAN', 'Polar scan'),
    ('RAY', 'Single polar ray'), ('AZIM', 'Azimuthal object'), ('IMAGE', '2-D cartesian image'),
    ('COMP', 'Cartesian composite image(s)'), ('XSEC', '2-D vertical cross section(s)'),
    ('VP', '1-D vertical profile'), ('PIC', 'Embedded graphical image');

INSERT INTO dex_products (name, description) VALUES
    ('SCAN', 'A scan of polar data'), ('PPI', 'Plan position indicator'),
    ('CAPPI', 'Constant altitude PPI'), ('PCAPPI', 'Pseudo-CAPPI'), ('ETOP', 'Echo top'),
    ('MAX', 'Maximum'), ('RR', 'Accumulation'), ('VIL', 'Vertically integrated liquid water'),
    ('COMP', 'Composite'), ('VP', 'Vertical profile'), ('RHI', 'Range height indicator'),
    ('XSEC', 'Arbitrary vertical slice'), ('VSP', 'Vertical side panel'),
    ('HSP', 'Horizontal side panel'), ('RAY', 'Ray'), ('AZIM', 'Azimuthal type product'),
    ('QUAL', 'Quality metric');

INSERT INTO dex_product_parameters (name, description) VALUES
    ('CAPPI', 'Layer height (meters above the radar)'), ('PPI', 'Elevation angle used (degrees)'),
    ('ETOP', 'Reflectivity level (dBZ)'), ('RHI', 'Azimuth angle (degrees)'),
    ('VIL', 'Bottom and top heights (m) of the integration layer');

