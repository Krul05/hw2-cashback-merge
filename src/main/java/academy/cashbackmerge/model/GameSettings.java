package academy.cashbackmerge.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
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
        if (spawnRules == null || spawnRules.isEmpty()) {
            throw new IllegalArgumentException("Список правил генерации кэшбэка не может быть пустым.");
        }
        if (sizeOfField <= 0) {
            throw new IllegalArgumentException("Размер игрового поля должен быть положительным числом.");
        }
        if (movesLimit <= 0) {
            throw new IllegalArgumentException("Лимит ходов должен быть положительным числом.");
        }
        BigDecimal totalProbability =
                spawnRules.stream().map(CashbackProbability::probability).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalProbability.compareTo(BigDecimal.ONE) != 0) {
            throw new IllegalArgumentException("Сумма вероятностей генерации кэшбэка должна быть равна 1.");
        }
        boolean hasNotPositiveProbabilities =
                spawnRules.stream().anyMatch(rule -> rule.probability().compareTo(BigDecimal.ZERO) <= 0);
        if (hasNotPositiveProbabilities) {
            throw new IllegalArgumentException("Вероятности генерации кэшбэка должны быть положительными.");
        }

        boolean isPowerOfTwoCashback = spawnRules.stream().allMatch(rule -> isPowerOfTwo(rule.cashback()));
        if (!isPowerOfTwoCashback) {
            throw new IllegalArgumentException("Все значения кэшбэка должны быть степенями числа 2.");
        }

        if (!isPowerOfTwo(maxRewardCashback)) {
            throw new IllegalArgumentException("Максимальная награда должна быть положительной степенью двойки");
        }
        if (maxRewardAllowedRate == null
                || maxRewardAllowedRate.compareTo(BigDecimal.ZERO) < 0
                || maxRewardAllowedRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Допустимая доля количества максимальной награды должна быть от 0 до 1");
        }
    }

    private static boolean isPowerOfTwo(int n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }
}
