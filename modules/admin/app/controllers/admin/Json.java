package controllers.admin;

import java.util.Map;

import play.i18n.Messages;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import truyen.common.Constants;
import truyen.common.bo.truyen.AuthorBo;
import truyen.common.bo.truyen.BookBo;
import truyen.common.bo.truyen.CategoryBo;
import truyen.common.bo.truyen.ChapterBo;
import truyen.common.bo.truyen.TruyenDao;
import truyen.common.bo.worker.WorkerBo;
import truyen.common.bo.worker.WorkerDao;
import truyen.common.compisitions.AuthRequired;

import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.common.BaseController;

@AuthRequired(userGroups = { Constants.USER_GROUP_ADMINISTRATOR, Constants.USER_GROUP_MODERATOR })
public class Json extends BaseController {

    public final static String SECTION = "admin.";
    public final static String VIEW_INDEX = SECTION + "index";
    public final static String VIEW_AUTHORS = SECTION + "authors";

    private static Result doResponse(int status, Object message) {
        ObjectNode result = play.libs.Json.newObject();
        result.put("status", status);
        result.put("message", play.libs.Json.toJson(message));
        return ok(result);
    }

    /*
     * Handles GET:/jsonAuthor
     */
    public static Promise<Result> author(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                AuthorBo author = TruyenDao.getAuthor(id);
                if (author == null) {
                    return doResponse(404, Messages.get("error.author.not_found"));
                } else {
                    Map<String, Object> authorData = author.toMap();
                    return doResponse(200, authorData);
                }
            }
        });
        return promise;
    }

    /*
     * Handles GET:/jsonCategory
     */
    public static Promise<Result> category(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                CategoryBo category = TruyenDao.getCategory(id);
                if (category == null) {
                    return doResponse(404, Messages.get("error.category.not_found"));
                } else {
                    Map<String, Object> categoryData = category.toMap();
                    return doResponse(200, categoryData);
                }
            }
        });
        return promise;
    }

    /*
     * Handles GET:/jsonBook
     */
    public static Promise<Result> book(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                BookBo book = TruyenDao.getBook(id);
                if (book == null) {
                    return doResponse(404, Messages.get("error.book.not_found"));
                } else {
                    Map<String, Object> bookData = book.toMap();
                    return doResponse(200, bookData);
                }
            }
        });
        return promise;
    }

    /*
     * Handles GET:/jsonChapter
     */
    public static Promise<Result> chapter(final int bookId, final int chapterIndex) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                ChapterBo chapter = TruyenDao.getChapter(bookId, chapterIndex);
                if (chapter == null) {
                    return doResponse(404, Messages.get("error.chapter.not_found"));
                } else {
                    Map<String, Object> chapterData = chapter.toMap();
                    chapterData.put("can_be_active", chapter.canBeActive());
                    return doResponse(200, chapterData);
                }
            }
        });
        return promise;
    }

    /*
     * Handles GET:/jsonWorker
     */
    public static Promise<Result> worker(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                WorkerBo worker = WorkerDao.getWorker(id);
                if (worker == null) {
                    return doResponse(404, Messages.get("error.worker.not_found"));
                } else {
                    Map<String, Object> workerData = worker.toMap();
                    return doResponse(200, workerData);
                }
            }
        });
        return promise;
    }
}
