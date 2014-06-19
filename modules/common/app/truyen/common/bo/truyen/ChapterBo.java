package truyen.common.bo.truyen;

import java.util.Date;

import play.i18n.Messages;
import truyen.common.Constants;

import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.plommon.bo.BaseBo;

public class ChapterBo extends BaseBo {

    public static final int TYPE_RAW = 0;
    public static final int TYPE_CONVERT = 1;
    public static final int TYPE_TRANSLATE = 2;

    public static String[] COL_BOOK_ID = { "cbook_id", "book_id" };
    public static String[] COL_INDEX = { "cindex", "chapter_index" };
    public static String[] COL_TYPE = { "ctype", "chapter_type" };
    public static String[] COL_IS_ACTIVE = { "cis_active", "is_active" };
    public static String[] COL_TIMESTAMP = { "ctimestamp", "timestamp" };
    public static String[] COL_TITLE = { "ctitle", "title" };
    public static String[] COL_CONTENT = { "ccontent", "content" };

    public boolean canBeActive() {
        BookBo book = getBook();
        return book.getNumPublishes() + 1 == getIndex();
    }

    public int getBookId() {
        Integer result = getAttribute(COL_BOOK_ID[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public ChapterBo setBookId(int bookId) {
        return (ChapterBo) setAttribute(COL_BOOK_ID[1], bookId);
    }

    public int getIndex() {
        Integer result = getAttribute(COL_INDEX[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public ChapterBo setIndex(int index) {
        return (ChapterBo) setAttribute(COL_INDEX[1], index);
    }

    public BookBo getBook() {
        return TruyenDao.getBook(getBookId());
    }

    public ChapterBo setBook(BookBo book) {
        return setBookId(book != null ? book.getId() : 0);
    }

    public int getType() {
        Integer result = getAttribute(COL_TYPE[1], Integer.class);
        int type = result != null ? result.intValue() : 0;
        switch (type) {
        case TYPE_CONVERT:
        case TYPE_TRANSLATE:
            return type;
        default:
            return TYPE_RAW;
        }
    }

    public String getTypeText() {
        return Messages.get("msg.chapter.type." + getType());
    }

    public ChapterBo setType(int type) {
        return (ChapterBo) setAttribute(COL_TYPE[1], type);
    }

    public boolean isActive() {
        Integer result = getAttribute(COL_IS_ACTIVE[1], Integer.class);
        return result != null ? result.intValue() != 0 : false;
    }

    public ChapterBo setActive(boolean isActive) {
        return (ChapterBo) setAttribute(COL_IS_ACTIVE[1], isActive ? Constants.INT_1
                : Constants.INT_0);
    }

    public ChapterBo setIsActive(int value) {
        return (ChapterBo) setAttribute(COL_IS_ACTIVE[1], value != 0 ? Constants.INT_1
                : Constants.INT_0);
    }

    public String getTitle() {
        return getAttribute(COL_TITLE[1], String.class);
    }

    public ChapterBo setTitle(String title) {
        return (ChapterBo) setAttribute(COL_TITLE[1], title);
    }

    public String getContent() {
        return getAttribute(COL_CONTENT[1], String.class);
    }

    public ChapterBo setContent(String content) {
        return (ChapterBo) setAttribute(COL_CONTENT[1], content);
    }

    public String getTimestampStr() {
        return DateFormatUtils.toString(getTimestamp(), Constants.DATETIME_FORMAT_FULL);
    }

    public Date getTimestamp() {
        return getAttribute(COL_TIMESTAMP[1], Date.class);
    }

    public ChapterBo setTimestamp(Date timestamp) {
        return (ChapterBo) setAttribute(COL_TIMESTAMP[1], timestamp);
    }

    public ChapterBo setTimestamp(long timestampMs) {
        return (ChapterBo) setAttribute(COL_TIMESTAMP[1], new Date(timestampMs));
    }
}
