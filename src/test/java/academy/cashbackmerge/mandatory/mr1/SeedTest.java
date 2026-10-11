package academy.cashbackmerge.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import academy.cashbackmerge.config.GameSettingsParser;
import academy.cashbackmerge.config.StringConfigLoader;
import academy.cashbackmerge.game.GameService;
import academy.cashbackmerge.game.GameSession;
import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.random.CashbackGenerator;
import java.io.IOException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: воспроизводимость по seed. */
@DisplayName("MR1. Воспроизводимость по seed")
class SeedTest {
    private static final int SEED = 42;
    private static final String CONFIG = """
        {
          "spawnRules": [
            { "cashback": "1", "probability": "0.85" },
            { "cashback": "2", "probability": "0.15" }
          ],
          "sizeOfField": 4,
          "movesLimit": 40,
          "maxRewardCashback": "32",
          "maxRewardAllowedRate": "0.20"
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
    @Disabled("MR3: реализуй тест и удали эту строку")
    @DisplayName("Одинаковый конфиг, seed и стратегия дают одинаковый отчёт симуляции")
    void sameInputsProduceSameSimulationReport() {
        fail("Тест не реализован");
    }
}
