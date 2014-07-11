package controllers;

import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import controllers.common.BaseController;

public class Application extends BaseController {

    public final static String VIEW_INDEX = "index";
    public final static String VIEW_404 = "error_404";

    /*
     * Handles GET:/index
     */
    public static Promise<Result> index() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                return ok("Hello, world");
            }
        });
        return promise;
    }

}
