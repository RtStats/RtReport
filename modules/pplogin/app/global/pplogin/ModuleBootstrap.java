package global.pplogin;

import global.common.Registry;
import play.Logger;
import play.Play;
import vngup.rtreports.common.MenuItem;
import vngup.rtreports.common.module.AbstractModuleBootstrap;

import com.github.ddth.tsc.ICounterFactory;
import com.github.ddth.tsc.redis.RedisCounterFactory;
import com.typesafe.config.Config;

public class ModuleBootstrap extends AbstractModuleBootstrap {

    public final static String MODULE_ID = "pplodin";

    private static Config moduleConfig;
    private static RedisCounterFactory counterFactory;

    public static ICounterFactory getCounterFactory() {
        return counterFactory;
    }

    @Override
    public void init() throws Exception {
        moduleConfig = loadConfiguration("pplogin.conf");
        _registerMenu();
        _initCounterFactory();
    }

    @Override
    public void destroy() throws Exception {
        _destroyCounterFactory();
        _unregisterMenu();
    }

    private void _registerMenu() {
        String url = controllers.pplogin.routes.PpLoginController.dashboard().url();
        MenuItem menuBarItem = new MenuItem(MODULE_ID, "Passport Login", url);
        Registry.addMenuBarItem(menuBarItem);
    }

    private void _unregisterMenu() {
        Registry.removeMenuBarItem(MODULE_ID);
    }

    private void _initCounterFactory() {
        String configKey = Play.isProd() ? "dev.redis_settings" : "prod.redis_settings";
        Config config = moduleConfig.getConfig(configKey);
        counterFactory = new RedisCounterFactory();
        String redisHost = config.getString("host");
        int redisPort = config.getInt("port");
        counterFactory.setHost(redisHost).setPort(redisPort);
        counterFactory.init();
        Logger.info("Redis Counter Factory initialized.");
    }

    private void _destroyCounterFactory() {
        try {
            if (counterFactory != null) {
                counterFactory.destroy();
            }
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
        } finally {
            counterFactory = null;
        }
    }
}
