package global.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import play.Logger;
import play.Play;
import utils.common.SiteUtils;
import vngup.rtreports.common.MenuItem;
import vngup.rtreports.common.MenuItemComparator;
import vngup.rtreports.common.module.IModuleBootstrap;
import bo.common.auth.IAuthenticationService;
import bo.common.site.SiteBo;
import bo.common.site.SiteDao;

public class Registry {

    private final static ConcurrentMap<String, IModuleBootstrap> activeModules = new ConcurrentHashMap<String, IModuleBootstrap>();

    private final static Map<String, MenuItem> menuBarRegistry = new HashMap<String, MenuItem>();
    private static MenuItem[] menuBarItemArr;

    private static IAuthenticationService authenticationService;

    public static String datasourceName() {
        return Play.isProd() ? "prod" : "dev";
    }

    public static void init() {
        menuBarRegistry.clear();
    }

    public static void destroy() {
        try {
            _destroyModules();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }
    }

    private static void _destroyModules() {
        for (String moduleName : activeModules.keySet()) {
            unregisterModule(moduleName);
        }
    }

    public static IAuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public static void registerAuthenticationService(IAuthenticationService authenticationService) {
        Registry.authenticationService = authenticationService;
    }

    public static void addMenuBarItem(MenuItem menuItem) {
        menuBarRegistry.put(menuItem.id, menuItem);
        menuBarItemArr = menuBarRegistry.values().toArray(MenuItem.EMPTY_ARRAY);
        Arrays.sort(menuBarItemArr, MenuItemComparator.instance);
    }

    public static void removeMenuBarItem(MenuItem menuItem) {
        removeMenuBarItem(menuItem.id);
    }

    public static void removeMenuBarItem(String id) {
        menuBarRegistry.remove(id);
        menuBarItemArr = menuBarRegistry.values().toArray(MenuItem.EMPTY_ARRAY);
        Arrays.sort(menuBarItemArr, MenuItemComparator.instance);
    }

    public static MenuItem[] getMenuBarItems() {
        String siteName = SiteUtils.extractSiteName();
        SiteBo site = SiteDao.getSite(siteName);
        List<MenuItem> result = new ArrayList<MenuItem>();
        if (menuBarItemArr != null) {
            for (MenuItem menuItem : menuBarItemArr) {
                if (site != null && site.isModuleVisible(menuItem.id)) {
                    result.add(menuItem);
                }
            }
        }
        return result.toArray(MenuItem.EMPTY_ARRAY);
    }

    /**
     * Registers a module.
     * 
     * @param moduleName
     * @param bootstrap
     * @return
     */
    public static boolean registerModule(String moduleName, IModuleBootstrap bootstrap) {
        return activeModules.putIfAbsent(moduleName, bootstrap) != null;
    }

    /**
     * Unregisters an existing module.
     * 
     * @param moduleName
     * @return {@code true} if module exists, {@code false} otherwise
     */
    public static boolean unregisterModule(String moduleName) {
        IModuleBootstrap bootstrap = activeModules.remove(moduleName);
        if (bootstrap != null) {
            try {
                bootstrap.destroy();
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            }
            return true;
        }
        return false;
    }
}
