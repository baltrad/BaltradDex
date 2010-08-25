/***************************************************************************************************
Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW

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

-- dex_users ----------------------------------------------------------------------------

INSERT INTO dex_users (name, name_hash, role_name, password, node_address, factory, country, city,
    city_code, street, number, phone, email)
    VALUES ('admin', MD5('admin'), 'admin', MD5('baltrad'),
        'http://localhost:8084/BaltradDex/dispatch.htm', 'IMGW', 'Poland', 'Warsaw', '01-673',
        'Podleśna', '61', '+48 22 569 44 91', 'admin@baltrad.imgw.pl'),
        ('operator', MD5('operator'), 'operator', MD5('baltrad'),
        'http://localhost:8084/BaltradDex/dispatch.htm', 'IMGW', 'Poland', 'Warsaw', '01-673',
        'Podleśna', '61', '+48 22 569 44 91',
        'operator@baltrad.imgw.pl'),
        ('peer', MD5('peer'), 'peer', MD5('baltrad'),
        'http://localhost:8084/BaltradDex/dispatch.htm', 'IMGW', 'Poland', 'Warsaw', '01-673',
        'Podleśna', '61', '+48 22 569 44 91', 'peer@baltrad.imgw.pl'),
        ('user', MD5('user'), 'user', MD5('baltrad'), 'http://localhost:8084/BaltradDex/dispatch.htm',
        'IMGW', 'Poland', 'Warsaw', '01-673', 'Podleśna', '61', '+48 22 569 44 91',
        'user@baltrad.imgw.pl');

-- dex_channels ------------------------------------------------------------------------

INSERT INTO dex_channels (name, wmo_number) VALUES ('Legionowo', '12374'),
    ('Świdwin', '12220'), ('Brzuchania', '12568'), ('Pastewnik', '12544'),
    ('Rzeszów', '12579'), ('Ramża', '12514'), ('Poznań', '12331'), ('Gdańsk', '12151');

INSERT INTO dex_node_configuration (name, type, address, org_name, org_address, time_zone, temp_dir,
    email ) VALUES('baltrad.imgw.pl', 'Primary', 'http://localhost:8084/BaltradDex/dispatch.htm',
    'Institute of Meteorology and Water Management', '01-673 Warsaw, Podleśna 61, Poland',
    'GMT + 01:00', 'temp', 'admin@baltrad.imgw.pl');