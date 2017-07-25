package playconfig;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;

public class ConfigModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Config.class).toProvider(ConfigProvider.class);
  }
}