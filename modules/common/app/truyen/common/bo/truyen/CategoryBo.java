package truyen.common.bo.truyen;

import com.github.ddth.plommon.bo.BaseBo;

public class CategoryBo extends BaseBo {

    public String getUrl() {
        return null;
    }

    public static String[] COL_ID = { "cid", "id" };
    public static String[] COL_POSITION = { "cposition", "position" };
    public static String[] COL_NUM_BOOKS = { "cnum_books", "num_books" };
    public static String[] COL_TITLE = { "ctitle", "title" };
    public static String[] COL_SUMMARY = { "csummary", "summary" };

    public int getId() {
        Integer result = getAttribute(COL_ID[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public CategoryBo setId(int id) {
        return (CategoryBo) setAttribute(COL_ID[1], id);
    }

    public int getPosition() {
        Integer result = getAttribute(COL_POSITION[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public CategoryBo setPosition(int position) {
        return (CategoryBo) setAttribute(COL_POSITION[1], position);
    }

    public int getNumBooks() {
        Integer result = getAttribute(COL_NUM_BOOKS[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public CategoryBo setNumBooks(int numBooks) {
        return (CategoryBo) setAttribute(COL_NUM_BOOKS[1], numBooks);
    }

    public String getTitle() {
        return getAttribute(COL_TITLE[1], String.class);
    }

    public CategoryBo setTitle(String title) {
        return (CategoryBo) setAttribute(COL_TITLE[1], title);
    }

    public String getSummary() {
        return getAttribute(COL_SUMMARY[1], String.class);
    }

    public CategoryBo setSummary(String summary) {
        return (CategoryBo) setAttribute(COL_SUMMARY[1], summary);
    }
}
