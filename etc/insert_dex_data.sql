-- SQL script inserts data into baltrad_db database 

INSERT INTO dex_users (name, password, role, factory, country, city, zip_code,
    street, number, phone, email, node_address, local_directory)
    VALUES ('imgw', MD5('baltrad'), 'operator',
        'Institute of Meteorology and Water Management', 'Poland', 'Warsaw',
        '01-673', 'Podleśna', '61', '+48 22 569 44 91', 'baltrad.admin@imgw.pl',
        'baltrad.imgw.pl', 'incoming'),
        ('admin', MD5('admin'), 'administrator',
        'Institute of Meteorology and Water Management', 'Poland', 'Warsaw',
        '01-673', 'Podleśna', '61', '+48 22 569 44 91', 'baltrad.admin@imgw.pl',
        'http://localhost:8081/BaltradDex/receiver.htm', 'incoming'),
        ('laptok', MD5('baltrad'), 'user',
        'Institute of Meteorology and Water Management', 'Poland', 'Warsaw',
        '01-673', 'Podleśna', '61', '+48 22 569 44 91', 'baltrad.admin@imgw.pl',
        'http://172.30.9.171:8081/BaltradDex/receiver.htm', 'incoming'),
        ('rand', MD5('randomize'), 'user',
        'Institute of Meteorology and Water Management', 'Poland', 'Warsaw',
        '01-673', 'Podleśna', '61', '+48 22 569 44 91', 'baltrad.admin@imgw.pl',
        'http://172.30.9.34:8081/BaltradDex/receiver.htm', 'incoming');

-- dex_channels ------------------------------------------------------------------------

INSERT INTO dex_channels (name, wmo_number) VALUES ('legionowo', '12374'),
    ('świdwin', '12220'), ('brzuchania', '12568'), ('pastewnik', '12544'),
    ('rzeszów', '12579'), ('ramża', '12514'), ('poznań', '12331'), ('gdańsk', '12151');
