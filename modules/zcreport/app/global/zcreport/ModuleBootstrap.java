package global.zcreport;

import global.common.Registry;

import java.sql.Connection;
import java.sql.SQLException;

import play.Logger;
import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import vngup.rtreports.common.MenuItem;
import vngup.rtreports.common.module.AbstractModuleBootstrap;

import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.guava.GuavaCacheFactory;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.typesafe.config.Config;

public class ModuleBootstrap extends AbstractModuleBootstrap {

    public final static String MODULE_ID = "zcreport";

    private static Config moduleConfig;
    private static BoneCP connectionPool;
    private static AbstractCacheFactory cacheFactory;

    @Override
    public void init() throws Exception {
        moduleConfig = loadConfiguration("zcreport.conf");
        _registerMenu();
        _initDataSource();
        _initCache();
    }

    @Override
    public void destroy() throws Exception {
        _destroyCache();
        _destroyDataSource();
        _unregisterMenu();
    }

    public static ICache getCache(String name) {
        return cacheFactory.createCache(name);
    }

    private static void _initCache() {
        cacheFactory = new GuavaCacheFactory();
        cacheFactory.setDefaultCacheCapacity(10000).setDefaultExpireAfterWrite(3600);
        cacheFactory.init();
    }

    private static void _destroyCache() {
        try {
            if (cacheFactory != null) {
                cacheFactory.destroy();
            }
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
        } finally {
            cacheFactory = null;
        }
    }

    public static Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    private void _registerMenu() {
        Lang lang = Lang.forCode("vi");
        String url = controllers.zcreport.routes.ModuleController.dashboard().url();
        MenuItem menuBarItem = new MenuItem(MODULE_ID, Messages.get(lang, "msg.zcreport.title"),
                url, Messages.get(lang, "msg.zcreport.desc"));
        Registry.addMenuBarItem(menuBarItem);
    }

    private void _unregisterMenu() {
        Registry.removeMenuBarItem(MODULE_ID);
    }

    private void _initDataSource() throws SQLException {
        String configKey = Play.isProd() ? "prod.dbcp" : "dev.dbcp";
        Config config = moduleConfig.getConfig(configKey);
        BoneCPConfig dbConfig = new BoneCPConfig();
        dbConfig.setJdbcUrl(config.getString("url"));
        dbConfig.setUsername(config.getString("user"));
        dbConfig.setPassword(config.getString("password"));
        dbConfig.setMinConnectionsPerPartition(2);
        dbConfig.setMaxConnectionsPerPartition(4);
        dbConfig.setPartitionCount(4);
        connectionPool = new BoneCP(dbConfig);
        Logger.info("BoneCP initialized.");
    }

    private void _destroyDataSource() {
        try {
            if (connectionPool != null) {
                connectionPool.shutdown();
            }
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
        } finally {
            connectionPool = null;
        }
    }
}
