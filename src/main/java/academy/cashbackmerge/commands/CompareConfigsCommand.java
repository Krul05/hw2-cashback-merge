package academy.cashbackmerge.commands;

import academy.cashbackmerge.config.ConfigLoaderFactory;
import academy.cashbackmerge.config.ConfigSource;
import academy.cashbackmerge.config.GameSettingsParser;
import academy.cashbackmerge.model.ComparisonConfig;
import academy.cashbackmerge.model.ConfigSimulationResult;
import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.report.ComparisonReportMarkdownWriter;
import academy.cashbackmerge.simulation.CompareConfigsRunner;
import academy.cashbackmerge.strategy.MoveStrategyType;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import org.jspecify.annotations.Nullable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "compare-configs",
        mixinStandardHelpOptions = true,
        description = "Сравнение нескольких конфигураций кампании")
public final class CompareConfigsCommand implements Callable<Integer> {

    @SuppressWarnings("NullAway.Init")
    @Option(names = "--configs", required = true, split = ",", description = "Список конфигураций через запятую")
    private List<String> configs;

    @SuppressWarnings("NullAway.Init")
    @Option(
            names = "--config-source",
            defaultValue = "CLASSPATH",
            description = "Источник конфигураций: ${COMPLETION-CANDIDATES}")
    private ConfigSource configSource;

    @Option(names = "--players", required = true, description = "Количество игроков для симуляции")
    private int players;

    @SuppressWarnings("NullAway.Init")
    @Option(
            names = "--strategy",
            defaultValue = "random",
            converter = StrategyConverter.class,
            description = "Стратегия выбора хода: ${COMPLETION-CANDIDATES}")
    private MoveStrategyType strategy;

    @Nullable
    @Option(names = "--seed")
    private Long seed;

    @SuppressWarnings("NullAway.Init")
    @Option(names = "--output", defaultValue = "comparison.md")
    private Path output;

    private final PrintStream out;
    private final PrintStream err;

    public CompareConfigsCommand(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    @Override
    public Integer call() {

        try {
            if (players <= 0) {
                throw new IllegalArgumentException("Количество игроков должно быть больше нуля");
            }
            if (configs.size() < 2) {
                throw new IllegalArgumentException("Необходимо указать как минимум две конфигурации для сравнения");
            }

            List<ComparisonConfig> comparisonConfigs = new ArrayList<>();

            for (String configPath : configs) {
                GameSettings settings = new GameSettingsParser(
                                ConfigLoaderFactory.createLoader(configSource, configPath))
                        .parseSettings();
                comparisonConfigs.add(new ComparisonConfig(configPath, settings));
            }

            long baseSeed = seed != null ? seed : new Random().nextLong();

            List<ConfigSimulationResult> report =
                    new CompareConfigsRunner().compare(comparisonConfigs, players, strategy, baseSeed);

            Files.writeString(output, ComparisonReportMarkdownWriter.generate(report), StandardCharsets.UTF_8);

            out.println("Seed: " + baseSeed);
            out.println("Конфигураций: " + comparisonConfigs.size());
            out.println("Отчёт сохранён: " + output);
            out.flush();

            return 0;

        } catch (IOException | IllegalArgumentException e) {
            err.println("Ошибка: " + e.getMessage());
            return 1;
        }
    }
}
