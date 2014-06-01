package controllers.admin;

import play.api.templates.Html;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import truyen.common.Constants;
import truyen.common.bo.truyen.AuthorBo;
import truyen.common.bo.truyen.TruyenDao;
import truyen.common.compisitions.AuthRequired;
import controllers.common.BaseController;

@AuthRequired(userGroups = { Constants.USER_GROUP_ADMINISTRATOR, Constants.USER_GROUP_MODERATOR,
        Constants.USER_GROUP_MEMBER })
public class Admin extends BaseController {

    public final static String SECTION = "admin.";
    public final static String VIEW_INDEX = SECTION + "index";
    public final static String VIEW_AUTHORS = SECTION + "authors";

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
     * Handles GET:/authors
     */
    public static Promise<Result> authors() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                AuthorBo[] authors = TruyenDao.getAllAuthors();
                Html html = render(VIEW_AUTHORS, (Object)authors);
                return ok(html);
            }
        });
        return promise;
    }
}
