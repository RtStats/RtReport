package controllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.api.templates.Html;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import truyen.common.bo.truyen.BookBo;
import truyen.common.bo.truyen.TruyenDao;
import controllers.common.BaseController;

public class Application extends BaseController {

    public final static String VIEW_INDEX = "index";
    public final static String VIEW_404 = "error_404";

    public final static String VIEW_CATEGORY = "category";
    public final static String VIEW_AUTHOR = "author";
    public final static String VIEW_BOOK = "book";
    public final static String VIEW_CHAPTER = "chapter";

    /*
     * Handles GET:/index
     */
    public static Promise<Result> index() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Html html = render(VIEW_INDEX);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles GET:/c<id-string>
     */
    public static Promise<Result> category(String strId) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Html html = render(VIEW_CATEGORY);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles GET:/a<id-string>
     */
    public static Promise<Result> author(String strId) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Html html = render(VIEW_AUTHOR);
                return ok(html);
            }
        });
        return promise;
    }

    private final static Pattern PATTERN_BOOK_ID = Pattern.compile("^b([0-9]+)");

    /*
     * Handles GET:/b<id-string>
     */
    public static Promise<Result> book(final String strId) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Matcher m = PATTERN_BOOK_ID.matcher(strId);
                int bookId = 0;
                if (m.find()) {
                    try {
                        bookId = Integer.parseInt(m.group(1));
                    } catch (Exception e) {
                    }
                }
                BookBo book = TruyenDao.getBook(bookId);
                Html html = book != null ? render(VIEW_BOOK, book) : render(VIEW_404);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles GET:/b<id-string>-c<id-string>
     */
    public static Promise<Result> chapter(String strId) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Html html = render(VIEW_CHAPTER);
                return ok(html);
            }
        });
        return promise;
    }
}
