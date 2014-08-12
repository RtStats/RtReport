package compositions.common;

import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;
import rtreport.common.Application;
import utils.common.SiteUtils;
import bo.common.user.UserBo;

public class AuthRequiredAction extends Action<AuthRequired> {

    protected Promise<SimpleResult> goLogin(final Http.Context ctx) {
        return Promise.promise(new Function0<SimpleResult>() {
            public SimpleResult apply() {
                String urlReturn = ctx.request().uri();
                String urlLogin = controllers.common.routes.CommonController.login(urlReturn).url();
                return redirect(urlLogin);
            }
        });
    }

    protected Promise<SimpleResult> go403(final Http.Context ctx) {
        return Promise.promise(new Function0<SimpleResult>() {
            public SimpleResult apply() {
                String url403 = controllers.common.routes.CommonController.error403().url();
                return redirect(url403);
            }
        });
    }

    public Promise<SimpleResult> call(final Http.Context ctx) throws Throwable {
        UserBo user = Application.currentUser();
        if (user == null) {
            return goLogin(ctx);
        }

        String siteName = SiteUtils.extractSiteName();
        if (!user.canAccessSite(siteName)) {
            return go403(ctx);
        }

        String moduleName = SiteUtils.extractModuleName(ctx.request());
        if (!user.canAccessModule(moduleName)) {
            return go403(ctx);
        }

        return delegate.call(ctx);
    }
}
