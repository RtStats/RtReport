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
   ,('site.name'         , 'Site name')
   ,('site.title'        , 'Site Title')
   ,('site.keywords'     , 'Keywords')
   ,('site.description'  , 'Description')
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
    gid                             INT                         NOT NULL,
        PRIMARY KEY (gid),
    gtitle                          VARCHAR(50),
    gdesc                           VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
INSERT INTO truyen_usergroup (gid, gtitle, gdesc)
VALUES
    (1, 'Administrator'    , 'Administrators have all permissions')
   ,(2, 'Content Moderator', 'Content Moderators can edit/delete/approve contents')
   ,(3, 'Member'           , 'Website members have normal permissions')
   ,(4, 'Guest'            , 'Visitors who have read-only permissions')
   ;
UPDATE truyen_counter SET cvalue=cvalue+4 WHERE cname='usergroup-id';
   
CREATE TABLE truyen_user (
    uid                             INT                         NOT NULL,
        UNIQUE INDEX (uid),
    uemail                          VARCHAR(100),
        PRIMARY KEY (uid),
    display_name                    VARCHAR(64),
    upassword                       VARCHAR(64),
    group_id                        INT,
        INDEX(group_id),
    timestamp_create                DATETIME
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
-- password for admin@local is 'password' (without quotes)
INSERT INTO truyen_user (uid, uemail, display_name, upassword, group_id, timestamp_create)
VALUES (1, 'admin@local', 'Administrator', 'a153ba7319d815ada7f473a54c209b4e7c9e3836', 1, UTC_TIMESTAMP());
UPDATE truyen_counter SET cvalue=cvalue+1 WHERE cname='user-id';

DROP TABLE IF EXISTS truyen_author;
CREATE TABLE truyen_author (
    aid                             INT                         NOT NULL,
        PRIMARY KEY (aid),
    aname                           VARCHAR(50),
    anum_books                      INT                         NOT NULL DEFAULT 0,
    ainfo                           TEXT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS truyen_category;
CREATE TABLE truyen_category (
    cid                             INT                         NOT NULL,
        PRIMARY KEY (cid),
    cposition                       INT                         NOT NULL DEFAULT 0,
        INDEX (cposition),
    cnum_books                      INT                         NOT NULL DEFAULT 0,
    ctitle                          VARCHAR(100),
    csummary                        TEXT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS truyen_book;
CREATE TABLE truyen_book (
    bid                             INT                         NOT NULL,
        PRIMARY KEY (bid),
    bstatus                         INT                         NOT NULL DEFAULT 0,
        INDEX (bstatus),
    bis_published                   INT                         NOT NULL DEFAULT 0,
        INDEX (bis_published),
    bnum_chapters                   INT                         NOT NULL DEFAULT 0,
    bnum_publishes                  INT                         NOT NULL DEFAULT 0,
    bcategory_id                    INT                         NOT NULL,
        INDEX (bcategory_id),
    bauthor_id                      INT                         NOT NULL,
        INDEX (bauthor_id),
    btitle                          VARCHAR(100),
    btimestamp_create               DATETIME,
    btimestamp_update               DATETIME,
        INDEX (btimestamp_update),
    bavatar                         VARCHAR(255),
    bsummary                        TEXT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS truyen_chapter;
CREATE TABLE truyen_chapter(
    cbook_id                        INT                         NOT NULL,
    cindex                          INT                         NOT NULL,
        PRIMARY KEY (cbook_id, cindex),
    ctype                           INT                         NOT NULL DEFAULT 0,
    cis_active                      INT                         NOT NULL DEFAULT 0,
        INDEX (cis_active),
    ctimestamp                      DATETIME,
        INDEX (ctimestamp),
    ctitle                          VARCHAR(255),
    ccontent                        TEXT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS truyen_worker;
CREATE TABLE truyen_worker (
    wid                             INT                         NOT NULL,
        PRIMARY KEY (wid),
    wbook_id                        INT                         NOT NULL,
        INDEX (wbook_id),
    wengine                         VARCHAR(32),
    wurl                            VARCHAR(255),
    wstatus                         INT                         NOT NULL DEFAULT 0,
        INDEX (wstatus),
    wlast_timestamp                 DATETIME,
    wlast_status                    VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
