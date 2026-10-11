package academy.cashbackmerge;

import academy.cashbackmerge.config.ClasspathConfigLoader;
import academy.cashbackmerge.config.ConfigDirectoryConfigLoader;
import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.config.ConfigSource;
import academy.cashbackmerge.config.GameSettingsParser;
import academy.cashbackmerge.game.GameService;
import academy.cashbackmerge.game.GameSession;
import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.random.CashbackGenerator;
import java.io.IOException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Точка входа в Cashback Merge. */
@Command(name = "cashback-merge", mixinStandardHelpOptions = true)
public final class Main implements Runnable {
    @Option(
            names = {"-c", "--config"},
            defaultValue = "config.json",
            description = "Путь к файлу конфигурации игры.")
    private String configPath = "config.json";

    @Option(
            names = {"-s", "--seed"},
            description = "Сид для генератора случайных чисел.")
    @Nullable
    private Long seed;

    @Option(
            names = "--config-source",
            defaultValue = "CLASSPATH",
            description = "Источник конфигурации: ${COMPLETION-CANDIDATES}")
    private ConfigSource configSource = ConfigSource.CLASSPATH;

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @Override
    public void run() {
        GameSettings gameSettings;
        try {

            ConfigLoader loader =
                    switch (configSource) {
                        case CLASSPATH -> new ClasspathConfigLoader(configPath);
                        case CONFIG_DIRECTORY -> new ConfigDirectoryConfigLoader(configPath);
                    };
            GameSettingsParser parser = new GameSettingsParser(loader);
            gameSettings = parser.parseSettings();
        } catch (IOException e) {
            LOG.error("Ошибка при парсинге настроек игры", e);
            throw new RuntimeException(e);
        }
        CashbackGenerator cashbackGenerator = seed == null
                ? new CashbackGenerator(gameSettings.spawnRules())
                : new CashbackGenerator(gameSettings.spawnRules(), seed);
        GameSession gameSession = new GameService().startNewGame(gameSettings, cashbackGenerator);
        gameSession.printField(System.out);
    }

    public static void main(String[] args) {
        LOG.debug("Аргументы запуска: {}", String.join(" ", args));
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
