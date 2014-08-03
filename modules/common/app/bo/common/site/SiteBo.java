package bo.common.site;

import java.util.List;

import com.github.ddth.plommon.bo.BaseBo;

public class SiteBo extends BaseBo {

    public final static String COL_MODULES = "modules";

    @SuppressWarnings("unchecked")
    public List<String> getModules() {
        return getAttribute(COL_MODULES, List.class);
    }

    public boolean isModuleVisible(String moduleName) {
        if ("common".equalsIgnoreCase(moduleName)) {
            return true;
        }

        List<String> moduleList = getModules();
        return moduleList != null && (moduleList.contains("*") || moduleList.contains(moduleName));
    }
}
