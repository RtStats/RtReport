package controllers;

import play.api.templates.Html;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import controllers.common.BaseController;

public class Application extends BaseController {

    public final static String VIEW_INDEX = "index";
    public final static String VIEW_CATEGORY = "category";

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
     * Handles GET:/cat/:id-string
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
}
