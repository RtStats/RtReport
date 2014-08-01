package compisitions.common;

import java.util.Map;

import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;
import utils.common.SiteUtils;
import bo.common.SiteDao;

public class SiteCheckAction extends Action<SiteCheck> {

    private Promise<SimpleResult> go404(final Http.Context ctx) {
        return Promise.promise(new Function0<SimpleResult>() {
            public SimpleResult apply() {
                String url404 = controllers.common.routes.ErrorsController.error404().url();
                return redirect(url404);
            }
        });
    }

    public Promise<SimpleResult> call(final Http.Context ctx) throws Throwable {
        String siteName = SiteUtils.extractSiteName();
        Map<String, Object> siteConfig = SiteDao.siteConfig(siteName);
        if (siteConfig == null) {
            return go404(ctx);
        }
        String moduleName = SiteUtils.extractModuleName(ctx.request());
        if (!SiteUtils.isModuleVisible(moduleName, siteConfig)) {
            return go404(ctx);
        }

        return delegate.call(ctx);
    }
}
