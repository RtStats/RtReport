# RtReports Config

CREATE TABLE config_site (
    id                  text,
    key                 text,
    value               text,
    PRIMARY KEY(id,key)
 ) WITH COMPACT STORAGE;
INSERT INTO config_site (id, key, value) VALUES ('localhost', 'modules', '["*"]');
INSERT INTO config_site (id, key, value) VALUES ('fv-rtreports', 'modules', '["paycharging"]');
INSERT INTO config_site (id, key, value) VALUES ('fv-rtreports', 'product', '"FV"');

CREATE TABLE config_user (
    id                  text,
    key                 text,
    value               text,
    PRIMARY KEY(id,key)
 ) WITH COMPACT STORAGE;
INSERT INTO config_user (id, key, value) VALUES ('thanhnb', 'email', '"thanhnb@vng.com.vn"');
INSERT INTO config_user (id, key, value) VALUES ('thanhnb', 'groups', '["superadmin"]');
