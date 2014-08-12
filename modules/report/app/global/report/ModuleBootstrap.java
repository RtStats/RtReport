package global.report;

import global.common.Registry;
import play.i18n.Lang;
import play.i18n.Messages;
import rtreport.common.MenuItem;
import rtreport.common.module.AbstractModuleBootstrap;

import com.typesafe.config.Config;

public class ModuleBootstrap extends AbstractModuleBootstrap {

    public final static String MODULE_ID = "report";

    private static Config moduleConfig;

    @Override
    public void init() throws Exception {
        moduleConfig = loadConfiguration("report.conf");
        _registerMenu();
    }

    @Override
    public void destroy() throws Exception {
        _unregisterMenu();
    }

    private void _registerMenu() {
        Lang lang = Registry.getLanguage();
        // String url = controllers.report.routes.ModuleController.dashboard(
        // ModuleController.PERIOD_HOUR).url();
        String url = "/report/dashboard";
        MenuItem menuBarItem = new MenuItem(MODULE_ID, Messages.get(lang, "msg.report.title"), url,
                Messages.get(lang, "msg.report.desc"));
        Registry.addMenuBarItem(menuBarItem);
    }

    private void _unregisterMenu() {
        Registry.removeMenuBarItem(MODULE_ID);
    }

}
