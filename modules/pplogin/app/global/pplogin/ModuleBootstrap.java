package global.pplogin;

import global.common.Registry;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Play;
import play.i18n.Messages;
import vngup.rtreports.common.MenuItem;
import vngup.rtreports.common.module.AbstractModuleBootstrap;

import com.github.ddth.tsc.ICounterFactory;
import com.github.ddth.tsc.cassandra.CassandraCounterFactory;
import com.github.ddth.tsc.redis.RedisCounterFactory;
import com.typesafe.config.Config;

public class ModuleBootstrap extends AbstractModuleBootstrap {

    public final static String MODULE_ID = "pplogin";

    private static Config moduleConfig;
    private static RedisCounterFactory redisCounterFactory;
    private static CassandraCounterFactory cassandraCounterFactory;

    public static ICounterFactory getCassandraCounterFactory() {
        return cassandraCounterFactory;
    }

    public static ICounterFactory getRedisCounterFactory() {
        return redisCounterFactory;
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
        String url = controllers.pplogin.routes.ModuleController.dashboard().url();
        MenuItem menuBarItem = new MenuItem(MODULE_ID, Messages.get("msg.pplogin.title"), url,
                Messages.get("msg.pplogin.desc"));
        Registry.addMenuBarItem(menuBarItem);
    }

    private void _unregisterMenu() {
        Registry.removeMenuBarItem(MODULE_ID);
    }

    private void _initCounterFactory() {
        {
            String configKey = Play.isProd() ? "prod.redis_settings" : "dev.redis_settings";
            Config config = moduleConfig.getConfig(configKey);
            redisCounterFactory = new RedisCounterFactory();
            String redisHost = config.getString("host");
            int redisPort = config.getInt("port");
            redisCounterFactory.setHost(redisHost).setPort(redisPort);
            redisCounterFactory.init();
            Logger.info("Redis Counter Factory initialized.");
        }
        {
            String configKey = Play.isProd() ? "prod.cassandra_settings" : "dev.cassandra_settings";
            Config config = moduleConfig.getConfig(configKey);
            cassandraCounterFactory = new CassandraCounterFactory();
            String cassandraHost = config.getString("host");
            String[] hosts = StringUtils.split(cassandraHost, ",");
            int cassandraPort = config.getInt("port");

            String cassandraKeyspace = config.getString("keyspace");
            cassandraCounterFactory.setHosts(hosts).setPort(cassandraPort)
                    .setKeyspace(cassandraKeyspace);
            cassandraCounterFactory.init();
            Logger.info("Cassandra Counter Factory initialized.");

        }
    }

    private void _destroyCounterFactory() {
        try {
            if (redisCounterFactory != null) {
                redisCounterFactory.destroy();
            }
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
        } finally {
            redisCounterFactory = null;
        }

        try {
            if (cassandraCounterFactory != null) {
                cassandraCounterFactory.destroy();
            }
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
        } finally {
            cassandraCounterFactory = null;
        }
    }
}
