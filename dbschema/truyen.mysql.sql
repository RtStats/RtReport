# Truyen DB Schema for MySQL

DROP TABLE IF EXISTS truyen_config;
CREATE TABLE truyen_config (
    conf_key                        VARCHAR(64),
        PRIMARY KEY (conf_key),
    conf_value                      TEXT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
INSERT INTO truyen_config (conf_key, conf_value)
VALUES
    ('facebook.app_id'   , 'your FB AppId')
   ,('facebook.app_scope', 'email,user_about_me,user_birthday')
   ,('facebook.login'    , 'true')
   ;

DROP TABLE IF EXISTS truyen_counter;
CREATE TABLE truyen_counter (
    cname                           VARCHAR(32),
        PRIMARY KEY (cname),
    cvalue                          INT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
INSERT INTO truyen_counter (cname, cvalue)
VALUES
    ('user-id', 0)
   ,('usergroup-id', 0)
   ;

DROP TABLE IF EXISTS truyen_user;
DROP TABLE IF EXISTS truyen_usergroup;
CREATE TABLE truyen_usergroup (
    group_id                        INT                         NOT NULL,
        PRIMARY KEY (group_id),
    group_title                     VARCHAR(50),
    group_desc                      VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
INSERT INTO truyen_usergroup (group_id, group_title, group_desc)
VALUES
    (1, 'Administrator'    , 'Administrators have all permissions')
   ,(2, 'Content Moderator', 'Content Moderators can edit/delete/approve contents')
   ,(3, 'Member'           , 'Website members have normal permissions')
   ,(4, 'Guest'            , 'Visitors who have read-only permissions')
   ;
UPDATE truyen_counter SET cvalue=cvalue+4 WHERE cname='usergroup-id';
   
CREATE TABLE truyen_user (
    user_id                         INT                         NOT NULL,
        UNIQUE INDEX (user_id),
    user_email                      VARCHAR(100),
        PRIMARY KEY (user_email),
    display_name                    VARCHAR(64),
    user_password                   VARCHAR(64),
    group_id                        INT,
        INDEX(group_id),
    timestamp_create                DATETIME
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
-- password for admin@local is 'password' (without quotes)
INSERT INTO truyen_user (user_id, user_email, display_name, user_password, group_id, timestamp_create)
VALUES (1, 'admin@local', 'Administrator', 'a153ba7319d815ada7f473a54c209b4e7c9e3836', 1, UTC_TIMESTAMP());
UPDATE truyen_counter SET cvalue=cvalue+1 WHERE cname='user-id';
