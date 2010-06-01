-- SQL script inserts data into baltrad_db database 

-- dex_users ----------------------------------------------------------------------------

INSERT INTO dex_users (name, password, role, factory, country, city, city_code,
    street, number, phone, email, node_address)
    VALUES ('imgw', MD5('baltrad'), 'operator',
        'IMGW', 'Poland', 'Warsaw',
        '01-673', 'Podleśna', '61', '+48 22 569 44 91', 'baltrad.admin@imgw.pl',
        'baltrad.imgw.pl'),
        ('admin', MD5('admin'), 'administrator',
        'IMGW', 'Poland', 'Warsaw',
        '01-673', 'Podleśna', '61', '+48 22 569 44 91', 'baltrad.admin@imgw.pl',
        'http://localhost:8084/BaltradDex/receiver.htm'),
        ('laptok', MD5('baltrad'), 'user',
        'IMGW', 'Poland', 'Warsaw',
        '01-673', 'Podleśna', '61', '+48 22 569 44 91', 'baltrad.admin@imgw.pl',
        'http://172.30.9.171:8084/BaltradDex/receiver.htm'),
        ('rand', MD5('randomize'), 'user',
        'IMGW', 'Poland', 'Warsaw',
        '01-673', 'Podleśna', '61', '+48 22 569 44 91', 'baltrad.admin@imgw.pl',
        'http://172.30.9.34:8084/BaltradDex/receiver.htm'),
        ('test', MD5('password'), 'user',
        'IMGW', 'Poland', 'Warsaw',
        '01-673', 'Podleśna', '61', '+48 22 569 44 91', 'baltrad.admin@imgw.pl',
        'http://172.30.9.34:8084/BaltradDex/receiver.htm');

-- dex_roles ---------------------------------------------------------------------------

INSERT INTO dex_roles (id, role) VALUES (1, 'Operator'), (2, 'Administrator'),
    (3, 'User'), (4, 'Guest');


-- dex_channels ------------------------------------------------------------------------

INSERT INTO dex_channels (name, wmo_number) VALUES ('Legionowo', '12374'),
    ('Świdwin', '12220'), ('Brzuchania', '12568'), ('Pastewnik', '12544'),
    ('Rzeszów', '12579'), ('Ramża', '12514'), ('Poznań', '12331'), ('Gdańsk', '12151');


-- just for testing until baltrad-db is available --------------------------------------

--INSERT INTO dex_data (id, channel_name, path, date, time) VALUES
--                (1, 'Brzuchania', 'TestData/brz1.h5', '2007/10/02', '21:40:00'),
--                (2, 'Brzuchania', 'TestData/brz2.h5', '2007/10/02', '00:00:00'),
--                (3, 'Brzuchania', 'TestData/brz3.h5', '2008/06/16', '15:00:29'),
--
--                (4, 'Gdańsk', 'TestData/gda1.h5', '2008/06/16', '15:00:33'),
--                (5, 'Gdańsk', 'TestData/gda2.h5', '2007/10/02', '14:00:00'),
--                (6, 'Gdańsk', 'TestData/gda3.h5', '2008/06/16', '15:00:00'),
--
--                (7, 'Legionowo', 'TestData/leg1.h5', '2007/10/02', '14:00:00'),
--                (8, 'Legionowo', 'TestData/leg2.h5', '2007/10/02', '01:10:00'),
--                (9, 'Legionowo', 'TestData/leg3.h5', '2008/06/16', '15:00:00'),
--
--                (10, 'Poznań', 'TestData/poz1.h5', '2007/10/02', '14:00:00'),
--                (11, 'Poznań', 'TestData/poz2.h5', '2008/06/16', '15:00:00'),
--		(12, 'Poznań', 'TestData/poz3.h5', '2008/06/16', '15:00:00'),
--
--                (13, 'Pastewnik', 'TestData/pas1.h5', '2008/06/16', '15:00:00'),
--                (14, 'Pastewnik', 'TestData/pas2.h5', '2008/06/16', '15:00:00'),
--                (15, 'Pastewnik', 'TestData/pas3.h5', '2008/06/16', '15:00:00'),
--
--                (16, 'Rzeszów', 'TestData/rze1.h5', '2007/10/02', '14:00:00'),
--                (17, 'Rzeszów', 'TestData/rze2.h5', '2008/06/16', '15:00:00'),
--                (18, 'Rzeszów', 'TestData/rze3.h5', '2008/06/16', '15:00:00'),
--
--                (19, 'Świdwin', 'TestData/swi1.h5', '2007/10/02', '14:00:00'),
--                (20, 'Świdwin', 'TestData/swi2.h5', '2008/06/16', '15:00:00'),
--                (21, 'Świdwin', 'TestData/swi3.h5', '2008/06/16', '15:00:00');

-- test subscriptions -----
--INSERT INTO dex_delivery_register (user_id, data_id) VALUES
--                (2, 8), (2, 11), (2, 14), (3, 21), (5, 10);

