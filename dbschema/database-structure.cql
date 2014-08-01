# RtReports Config

CREATE TABLE config_site (
    id                  text,
    key                 text,
    value               text,
    PRIMARY KEY(id,key)
 ) WITH COMPACT STORAGE;
INSERT INTO config_site (id, key, value) VALUES ('localhost', 'modules', '["*"]');
INSERT INTO config_site (id, key, value) VALUES ('fv-rtreports', 'modules', '["paycharging"]');
