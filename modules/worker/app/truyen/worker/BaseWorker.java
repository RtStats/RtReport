package truyen.worker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import play.Logger;
import truyen.common.bo.truyen.BookBo;
import truyen.common.bo.truyen.ChapterBo;
import truyen.common.bo.truyen.TruyenDao;
import truyen.common.bo.worker.WorkerBo;
import truyen.common.bo.worker.WorkerDao;
import akka.actor.UntypedActor;

import com.github.ddth.commons.utils.DPathUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public abstract class BaseWorker extends UntypedActor {

    protected final static String CHAPTER_METADATA_URL = "url";
    protected final static String CHAPTER_METADATA_TITLE = "title";
    protected final static String CHAPTER_METADATA_INDEX = "index";

    protected final static String CHAPTER_CONTENT = "content";
    protected final static String CHAPTER_TITLE = "title";
    protected final static String CHAPTER_TYPE = "type";
    protected final static String CHAPTER_URL = "url";

    private static LoadingCache<Integer, Lock> workerLocks = CacheBuilder.newBuilder()
            .maximumSize(Integer.MAX_VALUE).expireAfterAccess(3600, TimeUnit.SECONDS)
            .build(new CacheLoader<Integer, Lock>() {
                @Override
                public Lock load(Integer workrId) throws Exception {
                    return new ReentrantLock();
                }
            });

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof WorkerBo) {
            WorkerBo worker = (WorkerBo) msg;
            Lock lock = workerLocks.get(worker.getId());
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                try {
                    process(worker);
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    protected void process(WorkerBo worker) {
        BookBo book = worker.getBook();
        if (book == null) {
            processBookNotFound(worker);
        } else if (book.getStatus() == BookBo.STATUS_DELETED
                || book.getStatus() == BookBo.STATUS_FINISHED) {
            return;
        } else {
            processBook(worker, book);
        }
    }

    protected void processBookNotFound(WorkerBo worker) {
        Logger.info("Book not found, delete worker [" + worker.getId() + "].");
        WorkerDao.delete(worker);
    }

    protected void processBook(WorkerBo worker, BookBo book) {
        Date now = new Date();
        Date last = worker.getLastTimestamp();
        switch (worker.getStatus()) {
        case WorkerBo.STATUS_NORMAL: {
            // normal book: update once per minute.
            if (last != null && (now.getTime() - last.getTime() < 60 * 1000)) {
                return;
            }
            break;
        }
        case WorkerBo.STATUS_INACTIVE: {
            // inactive book: update once per hour.
            if (last != null && (now.getTime() - last.getTime() < 60 * 60 * 1000)) {
                return;
            }
            break;
        }
        }

        Logger.debug("Worker [" + worker.getId() + "] - " + worker.getUrl());
        List<Map<String, Object>> chapterList = extractChapterList(worker, book);
        Logger.debug("\t" + chapterList.size() + " chapters.");
        int chapterIndex = 0;
        int numChapters = book.getNumChapters();
        List<Integer> processedChapters = new ArrayList<Integer>();
        for (Map<String, Object> chapterMetadata : chapterList) {
            chapterIndex++;
            if (chapterIndex > numChapters) {
                processChapter(worker, book, chapterIndex, chapterMetadata);
                processedChapters.add(chapterIndex);
                if (processedChapters.size() >= 5) {
                    break;
                }
            }
        }
        String statusMsg = "Processed chapters: " + processedChapters.toString();
        worker.setLastTimestamp(new Date()).setLastStatus(statusMsg);
        if (processedChapters.size() == 0) {
            if (worker.getStatus() == WorkerBo.STATUS_NEW) {
                worker.setStatus(WorkerBo.STATUS_NORMAL);
            } else if (worker.getStatus() == WorkerBo.STATUS_NORMAL) {
                worker.setStatus(WorkerBo.STATUS_INACTIVE);
            }
        } else {
            if (worker.getStatus() == WorkerBo.STATUS_INACTIVE) {
                worker.setStatus(WorkerBo.STATUS_NORMAL);
            }
        }
        worker = WorkerDao.update(worker);
    }

    protected void processChapter(WorkerBo worker, BookBo book, int chapterIndex,
            Map<String, Object> chapterMetadata) {
        Logger.debug("\t" + chapterMetadata.toString());
        Map<String, Object> chapterData = extractChapterContent(worker, book, chapterMetadata);
        String content = DPathUtils.getValue(chapterData, CHAPTER_CONTENT, String.class);
        String title = DPathUtils.getValue(chapterData, CHAPTER_TITLE, String.class);
        int type = DPathUtils.getValue(chapterData, CHAPTER_TYPE, int.class);
        ChapterBo chapter = new ChapterBo();
        chapter.setActive(false).setBook(book).setContent(content).setIndex(chapterIndex)
                .setTimestamp(new Date()).setTitle(title).setType(type);
        chapter = TruyenDao.create(chapter);
        // book.setNumChapters(chapterIndex);
        // book = TruyenDao.update(book);
    }

    protected abstract List<Map<String, Object>> extractChapterList(WorkerBo worker, BookBo book);

    protected abstract Map<String, Object> extractChapterContent(WorkerBo worker, BookBo book,
            Map<String, Object> chapterMetadata);
}
