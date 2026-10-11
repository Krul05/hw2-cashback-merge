package academy.cashbackmerge.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.cashbackmerge.config.GameSettingsParser;
import academy.cashbackmerge.config.StringConfigLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: конфиг кампании. */
@DisplayName("MR1. Конфиг кампании")
class ConfigValidationTest {

    private static final String VALID_CONFIG = """
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
    private static final String INVALID_CONFIG_PROBABILITIES = """
        {
          "spawnRules": [
            { "cashback": "1", "probability": "0.85" },
            { "cashback": "2", "probability": "0.20" }
          ],
          "sizeOfField": 4,
          "movesLimit": 40,
          "maxRewardCashback": "32",
          "maxRewardAllowedRate": "0.20"
        }
        """;
    private static final String INVALID_CONFIG_MOVES_LIMIT = """
        {
          "spawnRules": [
            { "cashback": "1", "probability": "0.85" },
            { "cashback": "2", "probability": "0.15" }
          ],
          "sizeOfField": 4,
          "movesLimit": 0,
          "maxRewardCashback": "32",
          "maxRewardAllowedRate": "0.20"
        }
        """;
    private static final String INVALID_CONFIG_EMPTY_SPAWN_RULES = """
        {
          "spawnRules": [],
          "sizeOfField": 4,
          "movesLimit": 40,
          "maxRewardCashback": "32",
          "maxRewardAllowedRate": "0.20"
        }
        """;

    @Test
    @DisplayName("Валидный конфиг принимается")
    void validConfigIsAccepted() {
        assertThatCode(() -> new GameSettingsParser(new StringConfigLoader(VALID_CONFIG)).parseSettings())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Сумма вероятностей spawn-правил не равна 1 — конфиг отклоняется")
    void probabilitiesNotSummingToOneAreRejected() {
        assertThatThrownBy(() -> {
                    new GameSettingsParser(new StringConfigLoader(INVALID_CONFIG_PROBABILITIES)).parseSettings();
                })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Сумма вероятностей генерации кэшбэка должна быть равна 1.");
    }

    @Test
    @DisplayName("movesLimit <= 0 — конфиг отклоняется")
    void nonPositiveMovesLimitIsRejected() {
        assertThatThrownBy(() -> {
                    new GameSettingsParser(new StringConfigLoader(INVALID_CONFIG_MOVES_LIMIT)).parseSettings();
                })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Лимит ходов должен быть положительным числом.");
    }

    @Test
    @DisplayName("Пустой список spawn-правил — конфиг отклоняется")
    void emptySpawnRulesAreRejected() {
        assertThatThrownBy(() -> {
                    new GameSettingsParser(new StringConfigLoader(INVALID_CONFIG_EMPTY_SPAWN_RULES)).parseSettings();
                })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Список правил генерации кэшбэка не может быть пустым.");
    }
}
