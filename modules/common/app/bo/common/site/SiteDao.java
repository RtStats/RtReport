package bo.common.site;

import global.common.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.ddth.plommon.bo.nosql.BaseNosqlDao;

public class SiteDao extends BaseNosqlDao {

    private final static String TABLE_NAME = "config_site";

    /**
     * Loads a site's configurations.
     * 
     * @param siteName
     * @return
     */
    public static SiteBo getSite(String siteName) {
        Map<String, Object> result = null;
        Map<Object, Object> dbRows = loadAsMap(Registry.datasourceName(), TABLE_NAME, siteName);
        if (dbRows != null) {
            result = new HashMap<String, Object>();
            for (Entry<Object, Object> entry : dbRows.entrySet()) {
                result.put(entry.getKey().toString(), entry.getValue());
            }
        }
        return result != null ? (SiteBo) (new SiteBo().fromMap(result)) : null;
    }
}
