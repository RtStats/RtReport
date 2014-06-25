package compisitions.admin;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.SimpleResult;
import truyen.common.Application;
import truyen.common.bo.user.UserBo;

public class AuthRequiredAction extends Action<AuthRequired> {

    protected Promise<SimpleResult> goLogin(final Http.Context ctx) {
        return Promise.promise(new Function0<SimpleResult>() {
            public SimpleResult apply() {
                String urlLogin = configuration.urlLogin();
                if (StringUtils.isEmpty(urlLogin)) {
                    String returnedUrl = ctx.request().uri();
                    urlLogin = controllers.admin.routes.Backend.login(returnedUrl).url();
                }
                return redirect(urlLogin);
            }
        });
    }

    protected Promise<SimpleResult> go403(final Http.Context ctx) {
        return Promise.promise(new Function0<SimpleResult>() {
            public SimpleResult apply() {
                String url403 = configuration.url403();
                if (StringUtils.isEmpty(url403)) {
                    return Controller.unauthorized();
                }
                return redirect(url403);
            }
        });
    }

    public Promise<SimpleResult> call(final Http.Context ctx) throws Throwable {
        int[] userGroups = configuration.userGroups();
        if (userGroups != null && userGroups.length > 0) {
            UserBo user = Application.currentUser();
            if (user == null) {
                return goLogin(ctx);
            }
            if (ArrayUtils.indexOf(userGroups, user.getGroupId()) < 0) {
                return go403(ctx);
            }
        }

        return delegate.call(ctx);
    }
}
