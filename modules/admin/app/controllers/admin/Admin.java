package controllers.admin;

import play.api.templates.Html;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import truyen.common.Constants;

import compisitions.admin.AuthRequired;

import controllers.common.BaseController;

@AuthRequired(userGroups = { Constants.USER_GROUP_ADMINISTRATOR, Constants.USER_GROUP_MODERATOR })
public class Admin extends BaseController {

    public final static String VIEW_INDEX = SECTION + "index";

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
}
