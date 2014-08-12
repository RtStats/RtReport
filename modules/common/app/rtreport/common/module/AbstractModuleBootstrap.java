package rtreport.common.module;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public abstract class AbstractModuleBootstrap implements IModuleBootstrap {
    /**
     * Loads configurations from a resource.
     * 
     * @param resourcePath
     * @return
     */
    protected Config loadConfiguration(String resourcePath) {
        return ConfigFactory.parseResources(resourcePath).resolve();
    }
}
