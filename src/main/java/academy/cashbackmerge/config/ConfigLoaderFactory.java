package academy.cashbackmerge.config;

import java.io.IOException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConfigLoaderFactory {
    public ConfigLoader createLoader(ConfigSource source, String config) throws IOException {
        return switch (source) {
            case CLASSPATH -> new ClasspathConfigLoader(config);
            case CONFIG_DIRECTORY -> new ConfigDirectoryConfigLoader(config);
            case STRING -> new StringConfigLoader(config);
            default -> throw new IllegalArgumentException("Неизвестный источник конфигурации: " + source);
        };
    }
}
