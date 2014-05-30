package com.litha.truyen.common;

import com.github.ddth.commons.utils.HashUtils;
import com.github.ddth.plommon.utils.PlayAppUtils;

public class Constants {
    public static final String APP_ID;
    public static final String APP_NAME = "Truyen";
    public static final String APP_VERSION;
    public static final String CACHE_PREFIX;
    static {
        APP_ID = APP_NAME.toLowerCase();
        APP_VERSION = PlayAppUtils.appConfigString("app.version");
        CACHE_PREFIX = HashUtils.crc32(Constants.APP_ID + "-" + Constants.APP_VERSION);
    }

    public final static Integer INT_0 = new Integer(0), INT_1 = new Integer(1);

    public final static String COUNTER_USER_ID = "user-id";
    public final static String COUNTER_USERGROUP_ID = "usergroup-id";

    public final static String CONFIG_FACEBOOK_APP_ID = "facebook.app_id";
    public final static String CONFIG_FACEBOOK_APP_SCOPE = "facebook.app_scope";
    public final static String CONFIG_FACEBOOK_LOGIN = "facebook.login";
    public final static String CONFIG_FACEBOOK_LIKE = "facebook.like";
    public final static String CONFIG_FACEBOOK_SHARE = "facebook.share";
    public final static String CONFIG_FACEBOOK_COMMENT = "facebook.comment";

    public final static String CONFIG_GOOGLE_GA_CODE = "google.ga_code";

    public final static String CONFIG_SITE_TITLE = "site.title";
    public final static String CONFIG_SITE_NAME = "site.name";
    public final static String CONFIG_SITE_KEYWORDS = "site.keywords";
    public final static String CONFIG_SITE_DESCRTIPTION = "site.description";

    public final static int DEFAULT_PAGE_SIZE_SMALL = 10;
    public final static int DEFAULT_PAGE_SIZE_MEDIUM = 20;
    public final static int DEFAULT_PAGE_SIZE_LARGE = 50;

    public final static String ASSETS_STORAGE_APPS = "storage/apps";
    public final static String ASSETS_STORAGE_PAPERCLIP = "storage/paperclip";
    public final static String POST_DIR_GALLERY = "gallery";

    public final static String ICON_FILENAME_APP = "logo.png";

    public final static long MAX_PICTURE_FILESIZE = 500 * 1024; // 500kb
    public final static long MAX_GALLERY_FILESIZE = 500 * 1024; // 500kb

    public final static long MAX_ICON_FILESIZE = 100 * 1024; // 100kb
    public final static int MIN_ICON_WIDTH = 80;
    public final static int MIN_ICON_HEIGHT = 80;

    public final static String SESSION_USER_ID = "USER_EMAIL";

    public final static String COOKIE_FB_ACC_TOKEN = "FB_ACC_TOKEN";
    public final static String COOKIE_FB_UID = "FB_UID";
    public final static String COOKIE_FB_EXPIRY = "FB_EXPIRY";

    public final static String USER_GROUP_ADMINISTRATOR = "1";
    public final static String USER_GROUP_MODERATOR = "2";
    public final static String USER_GROUP_MEMBER = "3";
    public final static String USER_GROUP_GUEST = "4";
}
