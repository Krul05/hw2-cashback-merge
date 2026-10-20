package academy.cashbackmerge.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;

import academy.cashbackmerge.config.GameSettingsParser;
import academy.cashbackmerge.config.StringConfigLoader;
import academy.cashbackmerge.game.GameService;
import academy.cashbackmerge.game.GameSession;
import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.random.CashbackGenerator;
import academy.cashbackmerge.random.RandomSeedGenerator;
import academy.cashbackmerge.report.SimulationReport;
import academy.cashbackmerge.report.SimulationReportCalculator;
import academy.cashbackmerge.simulation.SimulateRunner;
import academy.cashbackmerge.strategy.MoveStrategyType;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: воспроизводимость по seed. */
@DisplayName("MR1. Воспроизводимость по seed")
class SeedTest {
    private static final Long SEED = 42L;
    private static final int PLAYERS = 1000;
    private static final String CONFIG = """
        {
          "spawnRules": [
            { "cashback": "1", "probability": "0.85" },
            { "cashback": "2", "probability": "0.15" }
          ],
          "sizeOfField": 4,
          "movesLimit": 40,
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
    @DisplayName("Одинаковый конфиг и seed дают одинаковое начальное поле")
    void sameConfigAndSeedProduceSameInitialBoard() throws IOException {

        GameSettings gameSettings = new GameSettingsParser(new StringConfigLoader(CONFIG)).parseSettings();
        CashbackGenerator cashbackGenerator1 = new CashbackGenerator(gameSettings.spawnRules(), SEED);
        CashbackGenerator cashbackGenerator2 = new CashbackGenerator(gameSettings.spawnRules(), SEED);
        GameSession gameSession1 = new GameService().startNewGame(gameSettings, cashbackGenerator1);
        GameSession gameSession2 = new GameService().startNewGame(gameSettings, cashbackGenerator2);

        assertThat(gameSession1.getField().getCells())
                .isDeepEqualTo(gameSession2.getField().getCells());
    }

    @Test
    @DisplayName("Одинаковый конфиг, seed и стратегия дают одинаковый отчёт симуляции")
    void sameInputsProduceSameSimulationReport() throws IOException {
        GameSettings gameSettings = new GameSettingsParser(new StringConfigLoader(CONFIG)).parseSettings();

        SimulateRunner runner1 =
                new SimulateRunner(new RandomSeedGenerator(Optional.of(SEED)), MoveStrategyType.RANDOM, gameSettings);
        SimulateRunner runner2 =
                new SimulateRunner(new RandomSeedGenerator(Optional.of(SEED)), MoveStrategyType.RANDOM, gameSettings);

        GameService gameService = new GameService();

        SimulationReport report1 = SimulationReportCalculator.calculate(
                runner1.runSimulate(PLAYERS, gameService), gameSettings, SEED, MoveStrategyType.RANDOM);
        SimulationReport report2 = SimulationReportCalculator.calculate(
                runner2.runSimulate(PLAYERS, gameService), gameSettings, SEED, MoveStrategyType.RANDOM);

        assertThat(report1).isEqualTo(report2);
    }
}
