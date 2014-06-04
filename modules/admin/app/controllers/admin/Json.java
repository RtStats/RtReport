package controllers.admin;

import java.util.Map;

import play.i18n.Messages;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import truyen.common.Constants;
import truyen.common.bo.truyen.AuthorBo;
import truyen.common.bo.truyen.CategoryBo;
import truyen.common.bo.truyen.TruyenDao;
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
}
