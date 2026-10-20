package academy.cashbackmerge.mandatory.mr3;

import static org.assertj.core.api.Assertions.assertThat;

import academy.cashbackmerge.config.GameSettingsParser;
import academy.cashbackmerge.config.StringConfigLoader;
import academy.cashbackmerge.game.GameService;
import academy.cashbackmerge.model.CashbackProbability;
import academy.cashbackmerge.model.CashbackStatistics;
import academy.cashbackmerge.model.Field;
import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.random.CashbackGenerator;
import academy.cashbackmerge.random.RandomSeedGenerator;
import academy.cashbackmerge.report.SimulationReport;
import academy.cashbackmerge.report.SimulationReportCalculator;
import academy.cashbackmerge.report.SimulationReportMarkdownWriter;
import academy.cashbackmerge.simulation.OneSimulationResult;
import academy.cashbackmerge.simulation.SimulateRunner;
import academy.cashbackmerge.strategy.MoveStrategyType;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: награды, симуляция и отчёт. */
@DisplayName("MR3. Награды и симуляция")
class RewardAndSimulationTest {

    private static final Long SEED = 42L;
    private static final int PLAYERS = 5;
    private static final int SPAWN_SAMPLES = 10000;
    private static final int TOLERANCE = SPAWN_SAMPLES * 3 / 100;
    private static final String CONFIG = """
        {
          "spawnRules": [
            { "cashback": "16", "probability": "1.0" }
          ],
          "sizeOfField": 4,
          "movesLimit": 1,
          "maxRewardCashback": "32",
          "maxRewardAllowedRate": "0.20",
          "rewardTable": [
              { "cashback": "2",  "reward": "0" },
              { "cashback": "4",  "reward": "50" },
              { "cashback": "8",  "reward": "150" },
              { "cashback": "16", "reward": "500" },
              { "cashback": "32", "reward": "2000" }
            ]
        }
        """;

    @Test
    @DisplayName("Максимальная плитка 16% даёт награду соответствующего tier")
    void rewardMatchesHighestTile() throws IOException {
        GameSettings settings = new GameSettingsParser(new StringConfigLoader(CONFIG)).parseSettings();

        SimulateRunner runner =
                new SimulateRunner(new RandomSeedGenerator(Optional.of(SEED)), MoveStrategyType.GREEDY_SCORE, settings);

        List<OneSimulationResult> results = runner.runSimulate(1, new GameService());

        List<OneSimulationResult> resultsWith16 = results.stream()
                .filter(result -> result.maxCashbackResult() == 16)
                .toList();

        for (OneSimulationResult result : resultsWith16) {
            assertThat(result.reward()).isEqualTo(500);
        }
    }

    @Test
    @DisplayName("Weighted random учитывает вероятности из конфига")
    void spawnRespectsConfiguredProbabilities() {
        List<CashbackProbability> spawnRules = List.of(
                new CashbackProbability(1, new BigDecimal("0.85")), new CashbackProbability(2, new BigDecimal("0.15")));

        CashbackGenerator generator = new CashbackGenerator(spawnRules, SEED);

        int onesCount = 0;
        int twosCount = 0;

        for (int i = 0; i < SPAWN_SAMPLES; i++) {
            Field field = new Field(1);

            generator.generateRandomCashback(field);

            int cashback = field.getCells()[0][0];

            switch (cashback) {
                case 1 -> onesCount++;
                case 2 -> twosCount++;
            }
        }

        int expectedOnes = spawnRules
                .get(0)
                .probability()
                .multiply(BigDecimal.valueOf(SPAWN_SAMPLES))
                .intValue();
        int expectedTwos = spawnRules
                .get(1)
                .probability()
                .multiply(BigDecimal.valueOf(SPAWN_SAMPLES))
                .intValue();

        assertThat(onesCount + twosCount)
                .as("Генератор может генерировать только 1 и 2")
                .isEqualTo(SPAWN_SAMPLES);

        assertThat(Math.abs(onesCount - expectedOnes)).isLessThanOrEqualTo(TOLERANCE);

        assertThat(Math.abs(twosCount - expectedTwos)).isLessThanOrEqualTo(TOLERANCE);
    }

    @Test
    @DisplayName("Отчёт содержит распределение максимальных плиток и наград")
    void reportContainsDistributions() throws IOException {
        GameSettings gameSettings = new GameSettingsParser(new StringConfigLoader(CONFIG)).parseSettings();

        SimulateRunner runner =
                new SimulateRunner(new RandomSeedGenerator(Optional.of(SEED)), MoveStrategyType.RANDOM, gameSettings);
        SimulationReport report = SimulationReportCalculator.calculate(
                runner.runSimulate(PLAYERS, new GameService()), gameSettings, SEED, MoveStrategyType.RANDOM);

        String markdown = SimulationReportMarkdownWriter.generate(report);

        assertThat(markdown).contains("Распределение плиток и наград");

        assertThat(markdown)
                .contains(
                        "| Максимальная плитка | Награда | Игроков | Процент игроков, получивших максимальную плитку |");

        assertThat(report.cashbackStatistics()).isNotEmpty();

        for (CashbackStatistics statistics : report.cashbackStatistics()) {
            String percent = BigDecimal.valueOf(statistics.playersCount())
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(PLAYERS), 2, RoundingMode.HALF_UP)
                            .toPlainString()
                    + "%";

            String expectedRow = String.format(
                    "| %d | %d | %d | %s |",
                    statistics.cashback(), statistics.reward(), statistics.playersCount(), percent);

            assertThat(markdown)
                    .as("В отчёте должна присутствовать строка для плитки %d%%", statistics.cashback())
                    .contains(expectedRow);
        }
    }

    @Test
    @DisplayName("Отчёт содержит долю игроков с максимальной наградой")
    void reportContainsMaxRewardRate() throws IOException {
        GameSettings gameSettings = new GameSettingsParser(new StringConfigLoader(CONFIG)).parseSettings();
        SimulateRunner runner =
                new SimulateRunner(new RandomSeedGenerator(Optional.of(SEED)), MoveStrategyType.RANDOM, gameSettings);
        List<OneSimulationResult> results = runner.runSimulate(PLAYERS, new GameService());
        SimulationReport report =
                SimulationReportCalculator.calculate(results, gameSettings, SEED, MoveStrategyType.RANDOM);
        String markdown = SimulationReportMarkdownWriter.generate(report);

        long maxRewardCount = results.stream()
                .filter(result -> result.maxCashbackResult() >= gameSettings.maxRewardCashback())
                .count();
        BigDecimal expectedRate =
                BigDecimal.valueOf(maxRewardCount).divide(BigDecimal.valueOf(PLAYERS), 10, RoundingMode.HALF_UP);
        String expectedPercent =
                expectedRate
                                .multiply(BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP)
                                .toPlainString() + "%";

        assertThat(report.maxRewardCount()).isEqualTo(maxRewardCount);
        assertThat(report.actualMaxRewardPercent()).isEqualByComparingTo(expectedRate);
        assertThat(markdown).contains("Бизнес-ограничения");
        assertThat(markdown).contains(expectedPercent);
    }

    @Test
    @DisplayName("Статус OK, если actualMaxRewardRate <= maxRewardAllowedPercent, иначе FAILED")
    void reportStatusReflectsBusinessConstraint() throws IOException {
        GameSettings gameSettings = new GameSettingsParser(new StringConfigLoader(CONFIG)).parseSettings();

        List<OneSimulationResult> passedResults = List.of(
                new OneSimulationResult(16, 100, 500),
                new OneSimulationResult(16, 120, 500),
                new OneSimulationResult(16, 140, 500),
                new OneSimulationResult(16, 160, 500),
                new OneSimulationResult(32, 180, 2000));

        List<OneSimulationResult> failedResults = List.of(
                new OneSimulationResult(16, 100, 500),
                new OneSimulationResult(16, 120, 500),
                new OneSimulationResult(16, 140, 500),
                new OneSimulationResult(32, 160, 2000),
                new OneSimulationResult(32, 180, 2000));

        SimulationReport passedReport =
                SimulationReportCalculator.calculate(passedResults, gameSettings, SEED, MoveStrategyType.RANDOM);

        SimulationReport failedReport =
                SimulationReportCalculator.calculate(failedResults, gameSettings, SEED, MoveStrategyType.RANDOM);

        String passedMarkdown = SimulationReportMarkdownWriter.generate(passedReport);
        String failedMarkdown = SimulationReportMarkdownWriter.generate(failedReport);

        assertThat(passedReport.actualMaxRewardPercent()).isEqualByComparingTo("0.20");
        assertThat(passedReport.passed()).isTrue();
        assertThat(passedMarkdown).contains("**OK**");
        assertThat(failedReport.actualMaxRewardPercent()).isEqualByComparingTo("0.40");
        assertThat(failedReport.passed()).isFalse();
        assertThat(failedMarkdown).contains("**FAILED**");
    }
}
