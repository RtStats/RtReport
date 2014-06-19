package truyen.common.bo.truyen;

import java.util.Date;

import play.i18n.Messages;
import truyen.common.Constants;

import com.github.ddth.plommon.bo.BaseBo;

public class BookBo extends BaseBo {

    public static final int STATUS_NEW = 0;
    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_INACTIVE = 2;
    public static final int STATUS_FINISHED = 3;
    public static final int STATUS_DELETED = 255;

    public static int parseStatusString(String strStatus) {
        int status = STATUS_NEW;
        try {
            status = Integer.parseInt(strStatus);
        } catch (Exception e) {
            status = STATUS_NEW;
        }
        switch (status) {
        case STATUS_DELETED:
        case STATUS_FINISHED:
        case STATUS_INACTIVE:
        case STATUS_NORMAL:
            return status;
        default:
            return STATUS_NEW;
        }
    }

    public static String[] COL_ID = { "bid", "id" };
    public static String[] COL_STATUS = { "bstatus", "status" };
    public static String[] COL_IS_PUBLISHED = { "bis_published", "is_published" };
    public static String[] COL_NUM_CHAPTERS = { "bnum_chapters", "num_chapters" };
    public static String[] COL_NUM_PUBLISHES = { "bnum_publishes", "num_publishes" };
    public static String[] COL_CATEGORY_ID = { "bcategory_id", "cat_id" };
    public static String[] COL_AUTHOR_ID = { "bauthor_id", "author_id" };
    public static String[] COL_TITLE = { "btitle", "title" };
    public static String[] COL_SUMMARY = { "bsummary", "summary" };
    public static String[] COL_AVATAR = { "bavatar", "avatar" };
    public static String[] COL_TIMESTAMP_CREATE = { "btimestamp_create", "timestamp_create" };
    public static String[] COL_TIMESTAMP_UPDATE = { "btimestamp_update", "timestamp_update" };

    public int getId() {
        Integer result = getAttribute(COL_ID[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public BookBo setId(int id) {
        return (BookBo) setAttribute(COL_ID[1], id);
    }

    public int getStatus() {
        Integer result = getAttribute(COL_STATUS[1], Integer.class);
        int status = result != null ? result.intValue() : 0;
        switch (status) {
        case STATUS_DELETED:
        case STATUS_FINISHED:
        case STATUS_INACTIVE:
        case STATUS_NORMAL:
            return status;
        default:
            return STATUS_NEW;
        }
    }

    public String getStatusText() {
        return Messages.get("msg.book.status." + getStatus());
    }

    public boolean isPublished() {
        Integer result = getAttribute(COL_IS_PUBLISHED[1], Integer.class);
        return result != null ? result.intValue() != 0 : false;
    }

    public BookBo setPublished(boolean isPublished) {
        return (BookBo) setAttribute(COL_IS_PUBLISHED[1], isPublished ? Constants.INT_1
                : Constants.INT_0);
    }

    public BookBo setIsPublished(int value) {
        return (BookBo) setAttribute(COL_IS_PUBLISHED[1], value != 0 ? Constants.INT_1
                : Constants.INT_0);
    }

    public BookBo setStatus(int status) {
        return (BookBo) setAttribute(COL_STATUS[1], status);
    }

    public int getNumChapters() {
        Integer result = getAttribute(COL_NUM_CHAPTERS[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public BookBo setNumChapters(int numChapters) {
        return (BookBo) setAttribute(COL_NUM_CHAPTERS[1], numChapters);
    }

    public int getNumPublishes() {
        Integer result = getAttribute(COL_NUM_PUBLISHES[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public BookBo setNumPublishes(int numPublishes) {
        return (BookBo) setAttribute(COL_NUM_PUBLISHES[1], numPublishes);
    }

    public int getCategoryId() {
        Integer result = getAttribute(COL_CATEGORY_ID[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public CategoryBo getCategory() {
        return TruyenDao.getCategory(getCategoryId());
    }

    public BookBo setCategoryId(int categoryId) {
        return (BookBo) setAttribute(COL_CATEGORY_ID[1], categoryId);
    }

    public BookBo setCategory(CategoryBo category) {
        return setCategoryId(category != null ? category.getId() : 0);
    }

    public int getAuthorId() {
        Integer result = getAttribute(COL_AUTHOR_ID[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public AuthorBo getAuthor() {
        return TruyenDao.getAuthor(getAuthorId());
    }

    public BookBo setAuthorId(int authorId) {
        return (BookBo) setAttribute(COL_AUTHOR_ID[1], authorId);
    }

    public BookBo setAuthor(AuthorBo author) {
        return setAuthorId(author != null ? author.getId() : 0);
    }

    public String getTitle() {
        return getAttribute(COL_TITLE[1], String.class);
    }

    public BookBo setTitle(String title) {
        return (BookBo) setAttribute(COL_TITLE[1], title);
    }

    public String getSummary() {
        return getAttribute(COL_SUMMARY[1], String.class);
    }

    public BookBo setSummary(String summary) {
        return (BookBo) setAttribute(COL_SUMMARY[1], summary);
    }

    public String getAvatar() {
        return getAttribute(COL_AVATAR[1], String.class);
    }

    public BookBo setAvatar(String avatar) {
        return (BookBo) setAttribute(COL_AVATAR[1], avatar);
    }

    public Date getTimestampCreate() {
        return getAttribute(COL_TIMESTAMP_CREATE[1], Date.class);
    }

    public BookBo setTimestampCreate(Date timestamp) {
        return (BookBo) setAttribute(COL_TIMESTAMP_CREATE[1], timestamp);
    }

    public BookBo setTimestampCreate(long timestampMs) {
        return (BookBo) setAttribute(COL_TIMESTAMP_CREATE[1], new Date(timestampMs));
    }

    public Date getTimestampUpdate() {
        return getAttribute(COL_TIMESTAMP_UPDATE[1], Date.class);
    }

    public BookBo setTimestampUpdate(Date timestamp) {
        return (BookBo) setAttribute(COL_TIMESTAMP_UPDATE[1], timestamp);
    }

    public BookBo setTimestampUpdate(long timestampMs) {
        return (BookBo) setAttribute(COL_TIMESTAMP_UPDATE[1], new Date(timestampMs));
    }
}
