package academy.cashbackmerge.commands;

import academy.cashbackmerge.game.GameService;
import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.random.RandomSeedGenerator;
import academy.cashbackmerge.report.SimulationReport;
import academy.cashbackmerge.report.SimulationReportCalculator;
import academy.cashbackmerge.report.SimulationReportMarkdownWriter;
import academy.cashbackmerge.simulation.OneSimulationResult;
import academy.cashbackmerge.simulation.SimulateRunner;
import academy.cashbackmerge.strategy.MoveStrategyType;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Callable;
import org.jspecify.annotations.Nullable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(name = "simulate", description = "Запустить пакетную симуляцию", mixinStandardHelpOptions = true)
public final class SimulateCommand implements Callable<Integer> {

    @Mixin
    private final ConfigOptions config = new ConfigOptions();

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
    @Option(
            names = "--seed",
            description = "Сид для генерации случайных чисел. Если не указан, будет сгенерирован случайный сид.")
    private Long seed;

    @SuppressWarnings("NullAway.Init")
    @Option(
            names = "--output",
            defaultValue = "report.md",
            description = "Путь к файлу для сохранения отчёта в формате Markdown")
    private Path output;

    private final PrintStream out;
    private final PrintStream err;

    public SimulateCommand(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    @Override
    public Integer call() {
        try {
            if (players <= 0) {
                throw new IllegalArgumentException("Количество игроков должно быть больше нуля");
            }

            GameSettings settings = config.load();

            long baseSeed = seed != null ? seed : new Random().nextLong();

            RandomSeedGenerator randomSeedGenerator = new RandomSeedGenerator(Optional.of(baseSeed));

            SimulateRunner runner = new SimulateRunner(randomSeedGenerator, strategy, settings);

            List<OneSimulationResult> results = runner.runSimulate(players, new GameService());

            SimulationReport report = SimulationReportCalculator.calculate(results, settings, baseSeed, strategy);

            Files.writeString(output, SimulationReportMarkdownWriter.generate(report), StandardCharsets.UTF_8);

            out.println("Seed: " + baseSeed);
            out.println("Отчёт сохранён: " + output);
            out.println("Бизнес-ограничение: " + (report.passed() ? "OK" : "FAILED"));
            out.flush();

            return 0;

        } catch (IOException | IllegalArgumentException | IllegalStateException e) {
            err.println("Ошибка: " + e.getMessage());
            return 1;
        }
    }
}
