package controllers.admin;

import play.api.templates.Html;
import play.data.Form;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;

import common.Application;
import common.bo.user.UserBo;
import common.bo.user.UserDao;
import common.compisitions.AuthRequired;

import controllers.common.BaseController;
import forms.admin.FormLogin;

@AuthRequired(userGroups = {})
public class Backend extends BaseController {

    protected final static String SECTION = "admin.";
    public final static String VIEW_LOGIN = SECTION + "login";
    public final static String VIEW_404 = SECTION + "error_404";

    /*
     * Handles GET:/login
     */
    public static Promise<Result> login(String returnUrl) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Form<FormLogin> form = Form.form(FormLogin.class);
                Html html = render(VIEW_LOGIN, /* message */null, form, /* captcha */
                        Boolean.FALSE);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles POST:/login
     */
    public static Promise<Result> loginSubmit(final String returnUrl) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Form<FormLogin> form = Form.form(FormLogin.class).bindFromRequest();
                if (form.hasErrors()) {
                    Html html = render(VIEW_LOGIN, /* message */null, form,
                    /* captcha */Boolean.FALSE);
                    return ok(html);
                }
                FormLogin model = form.get();
                UserBo user = UserDao.getUserByEmail(model.email);
                Application.login(user);
                if (returnUrl != null && returnUrl.startsWith("/")) {
                    return redirect(returnUrl);
                } else {
                    return redirect(admin.routes.Admin.index().url());
                }
            }
        });
        return promise;
    }

    /*
     * Handles GET:/logout
     */
    public static Promise<Result> logout(final String returnUrl) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Application.logout();
                if (returnUrl != null && returnUrl.startsWith("/")) {
                    return redirect(returnUrl);
                } else {
                    return redirect(admin.routes.Admin.index().url());
                }
            }
        });
        return promise;
    }

    /*
     * Handles GET:/404
     */
    public static Promise<Result> error404(final String returnUrl) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Html html = render(VIEW_404, returnUrl);
                return ok(html);
            }
        });
        return promise;
    }
}
