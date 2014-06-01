package truyen.common.bo.user;

import com.github.ddth.plommon.bo.BaseBo;

public class UsergroupBo extends BaseBo {
    /* virtual db columns */
    public final static String[] COL_ID = { "gid", "group_id" };
    public final static String[] COL_TITLE = { "gtitle", "group_title" };
    public final static String[] COL_DESC = { "gdesc", "group_desc" };

    public int getId() {
        Integer result = getAttribute(COL_ID[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public UsergroupBo setId(int id) {
        return (UsergroupBo) setAttribute(COL_ID[1], id);
    }

    public String getTitle() {
        return getAttribute(COL_TITLE[1], String.class);
    }

    public UsergroupBo setTitle(String title) {
        return (UsergroupBo) setAttribute(COL_TITLE[1], title);
    }

    public String getDescription() {
        return getAttribute(COL_DESC[1], String.class);
    }

    public UsergroupBo setDescription(String desc) {
        return (UsergroupBo) setAttribute(COL_DESC[1], desc);
    }
}
