package controllers.common;

import play.api.templates.Html;
import play.data.Form;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import vngup.rtreports.common.Application;
import bo.common.user.UserBo;
import bo.common.user.UserDao;
import forms.common.FormLogin;

public class CommonController extends BaseController {

    private final static String SECTION = "common.";
    public final static String VIEW_LOGIN = SECTION + "login";

    public static Promise<Result> error404() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                return ok("Page not found!");
                // Html html = render(VIEW_DASHBOARD, xTimestamp, xVND, xXu,
                // xTransaction);
                // return ok(html);
            }
        });
        return promise;
    }

    public static Promise<Result> error403() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                return ok("Access denied!");
                // Html html = render(VIEW_DASHBOARD, xTimestamp, xVND, xXu,
                // xTransaction);
                // return ok(html);
            }
        });
        return promise;
    }

    public static Promise<Result> login(final String returnUrl) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Form<FormLogin> form = Form.form(FormLogin.class);
                Html html = render(VIEW_LOGIN, form);
                return ok(html);
            }
        });
        return promise;
    }

    public static Promise<Result> loginSubmit(final String returnUrl) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Form<FormLogin> form = Form.form(FormLogin.class).bindFromRequest();
                if (form.hasErrors()) {
                    Html html = render(VIEW_LOGIN, form);
                    return ok(html);
                }
                FormLogin model = form.get();
                UserBo user = UserDao.getUser(model.username);
                Application.login(user);
                if (returnUrl != null && returnUrl.startsWith("/")) {
                    return redirect(returnUrl);
                } else {
                    return redirect("/");
                }
            }
        });
        return promise;
    }
}
