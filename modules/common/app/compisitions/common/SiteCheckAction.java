package compisitions.common;

import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;
import utils.common.SiteUtils;
import bo.common.site.SiteBo;
import bo.common.site.SiteDao;

public class SiteCheckAction extends Action<SiteCheck> {

    private Promise<SimpleResult> go404(final Http.Context ctx) {
        return Promise.promise(new Function0<SimpleResult>() {
            public SimpleResult apply() {
                String url404 = controllers.common.routes.CommonController.error404().url();
                return redirect(url404);
            }
        });
    }

    public Promise<SimpleResult> call(final Http.Context ctx) throws Throwable {
        String siteName = SiteUtils.extractSiteName();
        SiteBo site = SiteDao.getSite(siteName);
        String moduleName = SiteUtils.extractModuleName(ctx.request());
        if (site == null || !site.isModuleVisible(moduleName)) {
            return go404(ctx);
        }
        return delegate.call(ctx);
    }
}
