package global.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import play.Logger;
import rtreport.common.module.AbstractModuleBootstrap;
import bo.common.metadata.IMetadataDao;

import com.github.ddth.tsc.ICounter;
import com.github.ddth.tsc.ICounterFactory;
import com.typesafe.config.Config;

public class ModuleBootstrap extends AbstractModuleBootstrap {

    public final static String MODULE_ID = "common";

    private static IMetadataDao metadataDao;
    private static ICounterFactory counterFactory;
    private static ApplicationContext applicationContext;

    private static Config moduleConfig;

    public static IMetadataDao getMetadataDao() {
        return metadataDao;
    }

    public static ICounter getCounter(String name) {
        return counterFactory.getCounter(name);
    }

    @Override
    public void init() throws Exception {
        moduleConfig = loadConfiguration("common.conf");
        _initApplicationContext();
        _initMetadataDao();
        _initCounterFactory();
    }

    @Override
    public void destroy() throws Exception {
        _destroyApplicationContext();
    }

    private void _initMetadataDao() {
        metadataDao = applicationContext.getBean(IMetadataDao.class);
    }

    private void _initCounterFactory() {
        counterFactory = applicationContext.getBean(ICounterFactory.class);
    }

    private static void _initApplicationContext() {
        String configFile = "spring/beans-common.xml";
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                configFile);
        applicationContext.start();
        ModuleBootstrap.applicationContext = applicationContext;

    }

    private static void _destroyApplicationContext() {
        try {
            ((AbstractApplicationContext) applicationContext).destroy();
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
        } finally {
            applicationContext = null;
        }
    }
}
