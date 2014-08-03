package bo.common.user;

import java.util.List;

import vngup.rtreports.common.Constants;

import com.github.ddth.plommon.bo.BaseBo;

public class UserBo extends BaseBo {

    public final static String COL_USERNAME = "username";
    public final static String COL_EMAIL = "email";
    public final static String COL_PASSWORD = "password";
    public final static String COL_GROUPS = "groups";
    public final static String COL_SITES = "sites";
    public final static String COL_MODULES = "modules";

    public String getId() {
        return getUsername();
    }

    public String getUsername() {
        return getAttribute(COL_USERNAME, String.class);
    }

    public String getEmail() {
        return getAttribute(COL_EMAIL, String.class);
    }

    public String getPassword() {
        return getAttribute(COL_PASSWORD, String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getGroups() {
        return getAttribute(COL_GROUPS, List.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getSites() {
        return getAttribute(COL_SITES, List.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getModules() {
        return getAttribute(COL_MODULES, List.class);
    }

    public boolean isInGroup(String groupName) {
        List<String> groupList = getGroups();
        return groupList != null && groupList.contains(groupName);
    }

    public boolean isSuperAdmin() {
        return isInGroup(Constants.USERGROUP_SUPERADMIN);
    }

    public boolean canAccessSite(String siteName) {
        if (isSuperAdmin()) {
            return true;
        }
        List<String> siteList = getSites();
        return siteList != null && siteList.contains(siteList);
    }

    public boolean canAccessModule(String moduleName) {
        if (isSuperAdmin()) {
            return true;
        }
        List<String> moduleList = getModules();
        return moduleList != null && moduleList.contains(moduleName);
    }
}
