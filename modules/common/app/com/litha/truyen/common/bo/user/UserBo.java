package com.litha.truyen.common.bo.user;

import java.util.Date;

import com.github.ddth.plommon.bo.BaseBo;

public class UserBo extends BaseBo {
    /* virtual db columns */
    public final static String[] COL_ID = { "uid", "user_id" };
    public final static String[] COL_EMAIL = { "uemail", "user_email" };
    public final static String[] COL_DISPLAY_NAME = { "display_name", "display_name" };
    public final static String[] COL_PASSWORD = { "upassword", "user_password" };
    public final static String[] COL_GROUP_ID = { "group_id", "group_id" };
    public final static String[] COL_TIMESTAMP_CREATE = { "timestamp_create", "timestamp_create" };

    public UsergroupBo getGroup() {
        return UserDao.getUsergroup(getGroupId());
    }

    public int getId() {
        Integer result = getAttribute(COL_ID[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public UserBo setId(int id) {
        return (UserBo) setAttribute(COL_ID[1], id);
    }

    public String getPassword() {
        return getAttribute(COL_PASSWORD[1], String.class);
    }

    public UserBo setPassword(String password) {
        return (UserBo) setAttribute(COL_PASSWORD[1], password);
    }

    public String getEmail() {
        return getAttribute(COL_EMAIL[1], String.class);
    }

    public UserBo setEmail(String email) {
        return (UserBo) setAttribute(COL_EMAIL[1], email);
    }

    public String getDisplayName() {
        return getAttribute(COL_DISPLAY_NAME[1], String.class);
    }

    public UserBo setDisplayName(String displayName) {
        return (UserBo) setAttribute(COL_DISPLAY_NAME[1], displayName);
    }

    public int getGroupId() {
        Integer result = getAttribute(COL_GROUP_ID[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public UserBo setGroupId(int groupId) {
        return (UserBo) setAttribute(COL_GROUP_ID[1], groupId);
    }

    public Date getTimestampCreate() {
        return getAttribute(COL_TIMESTAMP_CREATE[1], Date.class);
    }

    public UserBo setTimestampCreate(Date timestamp) {
        return (UserBo) setAttribute(COL_TIMESTAMP_CREATE[1], timestamp);
    }
}
