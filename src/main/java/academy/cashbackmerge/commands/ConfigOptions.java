package academy.cashbackmerge.commands;

import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.config.ConfigLoaderFactory;
import academy.cashbackmerge.config.ConfigSource;
import academy.cashbackmerge.config.GameSettingsParser;
import academy.cashbackmerge.model.GameSettings;
import java.io.IOException;
import picocli.CommandLine.Option;

public final class ConfigOptions {

    @SuppressWarnings("NullAway.Init")
    @Option(
            names = {"-c", "--config"},
            defaultValue = "config.json",
            description = "Путь к конфигурации")
    private String configPath;

    @SuppressWarnings("NullAway.Init")
    @Option(names = "--config-source", defaultValue = "CLASSPATH", description = "Источник: ${COMPLETION-CANDIDATES}")
    private ConfigSource configSource;

    public GameSettings load() throws IOException {
        ConfigLoader loader = ConfigLoaderFactory.createLoader(configSource, configPath);

        return new GameSettingsParser(loader).parseSettings();
    }
}
