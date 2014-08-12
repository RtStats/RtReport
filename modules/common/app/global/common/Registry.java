package global.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import play.Logger;
import play.Play;
import play.i18n.Lang;
import rtreport.common.MenuItem;
import rtreport.common.MenuItemComparator;
import rtreport.common.module.IModuleBootstrap;
import bo.common.auth.IAuthenticationService;

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

    public static Lang getLanguage() {
        return Lang.forCode("vi");
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
        return menuBarItemArr != null ? menuBarItemArr : MenuItem.EMPTY_ARRAY;
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
