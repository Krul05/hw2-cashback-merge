package academy.cashbackmerge.model;

import academy.cashbackmerge.config.ConfigValidationException;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameSettings(
        List<CashbackProbability> spawnRules,
        @JsonAlias("boardSize") int sizeOfField,
        int movesLimit,
        int maxRewardCashback,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal maxRewardAllowedRate) {
    public GameSettings {
        if (spawnRules != null) {
            spawnRules = List.copyOf(spawnRules);
        }
    }

    public void validate() {
        List<String> errors = new ArrayList<>();

        validateStructures(errors);
        validateSpawnRules(errors);

        if (!errors.isEmpty()) {
            throw new ConfigValidationException(errors);
        }
    }

    private void validateSpawnRules(List<String> errors) {
        if (spawnRules == null || spawnRules.isEmpty()) {
            errors.add("Список правил генерации кэшбэка не может быть пустым.");
            return;
        }

        BigDecimal totalProbability =
                spawnRules.stream().map(CashbackProbability::probability).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalProbability.compareTo(BigDecimal.ONE) != 0) {
            errors.add("Сумма вероятностей генерации кэшбэка должна быть равна 1.");
        }
        boolean hasNotPositiveProbabilities =
                spawnRules.stream().anyMatch(rule -> rule.probability().compareTo(BigDecimal.ZERO) <= 0);
        if (hasNotPositiveProbabilities) {
            errors.add("Вероятности генерации кэшбэка должны быть положительными.");
        }

        boolean isPowerOfTwoCashback = spawnRules.stream().allMatch(rule -> isPowerOfTwo(rule.cashback()));
        if (!isPowerOfTwoCashback) {
            errors.add("Все значения кэшбэка должны быть степенями числа 2.");
        }
    }

    private void validateStructures(List<String> errors) {
        if (sizeOfField <= 0) {
            errors.add("Размер игрового поля должен быть положительным числом.");
        }
        if (movesLimit <= 0) {
            errors.add("Лимит ходов должен быть положительным числом.");
        }

        if (!isPowerOfTwo(maxRewardCashback)) {
            errors.add("Максимальная награда должна быть положительной степенью двойки");
        }
        if (maxRewardAllowedRate == null
                || maxRewardAllowedRate.compareTo(BigDecimal.ZERO) < 0
                || maxRewardAllowedRate.compareTo(BigDecimal.ONE) > 0) {
            errors.add("Допустимая доля количества максимальной награды должна быть от 0 до 1");
        }
    }

    private static boolean isPowerOfTwo(int n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }
}
