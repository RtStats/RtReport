package truyen.common.bo.worker;

import java.util.Date;

import play.i18n.Messages;
import truyen.common.bo.truyen.BookBo;
import truyen.common.bo.truyen.TruyenDao;

import com.github.ddth.plommon.bo.BaseBo;

public class WorkerBo extends BaseBo {

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

    public static String[] COL_ID = { "wid", "id" };
    public static String[] COL_BOOK_ID = { "wbook_id", "book_id" };
    public static String[] COL_ENGINE = { "wengine", "engine" };
    public static String[] COL_URL = { "wurl", "url" };
    public static String[] COL_STATUS = { "wstatus", "status" };
    public static String[] COL_LAST_TIMESTAME = { "wlast_timestamp", "last_timestamp" };
    public static String[] COL_LAST_STATUS = { "wlast_status", "last_status" };

    public int getId() {
        Integer result = getAttribute(COL_ID[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public WorkerBo setId(int id) {
        return (WorkerBo) setAttribute(COL_ID[1], id);
    }

    public int getBookId() {
        Integer result = getAttribute(COL_BOOK_ID[1], Integer.class);
        return result != null ? result.intValue() : 0;
    }

    public WorkerBo setBookId(int bookId) {
        return (WorkerBo) setAttribute(COL_BOOK_ID[1], bookId);
    }

    public BookBo getBook() {
        return TruyenDao.getBook(getBookId());
    }

    public WorkerBo setBook(BookBo book) {
        return setBookId(book != null ? book.getId() : 0);
    }

    public String getEngine() {
        return getAttribute(COL_ENGINE[1], String.class);
    }

    public WorkerBo setEngine(String engine) {
        return (WorkerBo) setAttribute(COL_ENGINE[1], engine);
    }

    public String getUrl() {
        return getAttribute(COL_URL[1], String.class);
    }

    public WorkerBo setUrl(String url) {
        return (WorkerBo) setAttribute(COL_URL[1], url);
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
        return Messages.get("msg.engine.status." + getStatus());
    }

    public WorkerBo setStatus(int status) {
        return (WorkerBo) setAttribute(COL_STATUS[1], status);
    }

    public Date getLastTimestamp() {
        return getAttribute(COL_LAST_TIMESTAME[1], Date.class);
    }

    public WorkerBo setLastTimestamp(Date timestamp) {
        return (WorkerBo) setAttribute(COL_LAST_TIMESTAME[1], timestamp);
    }

    public WorkerBo setLastTimestamp(long timestampMs) {
        return (WorkerBo) setAttribute(COL_LAST_TIMESTAME[1], new Date(timestampMs));
    }

    public String getLastStatus() {
        return getAttribute(COL_LAST_STATUS[1], String.class);
    }

    public WorkerBo setLastStatus(String lastStatus) {
        return (WorkerBo) setAttribute(COL_LAST_STATUS[1], lastStatus);
    }
}
