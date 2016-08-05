package config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * Created by lowery on 8/4/16.
 */
public class KuratorConfigFactory {

    public static KuratorConfig load() {
        Config config = ConfigFactory.load();
        return new KuratorConfig(config.getConfig("workflows"));
    }
    
}
