package global;

import global.common.Registry;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import play.Application;
import play.Configuration;
import play.GlobalSettings;
import play.Logger;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import play.mvc.Action;
import play.mvc.Http.Request;
import vngup.rtreports.common.module.IModuleBootstrap;
import bo.common.auth.IAuthenticationService;

import com.github.ddth.plommon.bo.BaseDao;
import com.github.ddth.plommon.utils.AkkaUtils;
import com.github.ddth.plommon.utils.PlayAppUtils;

public class Global extends GlobalSettings {

    /*
     * Enable Gzip response.
     * 
     * @see play.GlobalSettings#filters()
     */
    @SuppressWarnings("unchecked")
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[] { GzipFilter.class };
    }

    @Override
    public void onStart(Application app) {
        super.onStart(app);
        try {
            _init();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
            _destroy();
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onStop(Application app) {
        try {
            _destroy();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }

        super.onStop(app);
    }

    @Override
    public Action<?> onRequest(Request request, Method method) {
        BaseDao.startProfiling();
        return super.onRequest(request, method);
    }

    private void _init() throws Exception {
        Registry.init();

        _initAuthenticationService();

        _initModules();
    }

    private void _destroy() {
        try {
            AkkaUtils.actorSystem().shutdown();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }

        try {
            Registry.destroy();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }
    }

    private void _initAuthenticationService() throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        String clazz = PlayAppUtils.appConfigString("application.auth_service");
        IAuthenticationService authService = (IAuthenticationService) Class.forName(clazz)
                .newInstance();
        Registry.registerAuthenticationService(authService);
    }

    private void _initModules() throws Exception {
        Configuration temp = PlayAppUtils.appConfig("application.modules");
        Map<String, Object> moduleBootstraps = PlayAppUtils.appConfigMap(temp);
        for (Entry<String, Object> entry : moduleBootstraps.entrySet()) {
            String moduleName = entry.getKey();
            String bootstrapClazz = entry.getValue().toString();
            try {
                __initModule(moduleName, bootstrapClazz);
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            }
        }
    }

    private void __initModule(String moduleName, String bootstrapClazz) throws Exception {
        Logger.info("Boostraping module [" + moduleName + "] with bootstrap clazz ["
                + bootstrapClazz + "]...");
        Class<?> cl = Class.forName(bootstrapClazz);
        if (!IModuleBootstrap.class.isAssignableFrom(cl)) {
            throw new Exception(cl.getName() + " is not of type ["
                    + IModuleBootstrap.class.getName() + "]!");
        }
        IModuleBootstrap bootstrap = (IModuleBootstrap) cl.newInstance();
        Registry.registerModule(moduleName, bootstrap);
        bootstrap.init();
    }
}
