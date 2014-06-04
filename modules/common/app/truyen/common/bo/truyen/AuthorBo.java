package truyen.common.bo.truyen;

import com.github.ddth.plommon.bo.BaseBo;

public class AuthorBo extends BaseBo {
    public static String[] COL_ID = { "aid", "id" };
    public static String[] COL_NAME = { "aname", "name" };
    public static String[] COL_NUM_BOOKS = { "anum_books", "num_books" };
    public static String[] COL_INFO = { "ainfo", "info" };

    public int getId() {
        Integer result = getAttribute(COL_ID[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public AuthorBo setId(int id) {
        return (AuthorBo) setAttribute(COL_ID[1], id);
    }

    public int getNumBooks() {
        Integer result = getAttribute(COL_NUM_BOOKS[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public AuthorBo setNumBooks(int numBooks) {
        return (AuthorBo) setAttribute(COL_NUM_BOOKS[1], numBooks);
    }

    public String getName() {
        return getAttribute(COL_NAME[1], String.class);
    }

    public AuthorBo setName(String name) {
        return (AuthorBo) setAttribute(COL_NAME[1], name);
    }

    public String getInfo() {
        return getAttribute(COL_INFO[1], String.class);
    }

    public AuthorBo setInfo(String info) {
        return (AuthorBo) setAttribute(COL_INFO[1], info);
    }
}
