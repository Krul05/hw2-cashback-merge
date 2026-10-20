package academy.cashbackmerge.report;

import academy.cashbackmerge.model.CashbackStatistics;
import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.simulation.OneSimulationResult;
import academy.cashbackmerge.strategy.MoveStrategyType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SimulationReportCalculator {

    private static final int AVERAGE_SCALE = 2;
    private static final int PERCENT_SCALE = 10;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public SimulationReport calculate(
            List<OneSimulationResult> results, GameSettings settings, long seed, MoveStrategyType strategy) {
        Objects.requireNonNull(results);
        Objects.requireNonNull(settings);
        Objects.requireNonNull(strategy);

        if (results.isEmpty()) {
            throw new IllegalArgumentException("Список результатов симуляции не может быть пустым.");
        }

        int players = results.size();

        List<CashbackStatistics> cashbackStatistics = calculateCashbackStatistics(results);

        BigDecimal averageScore = calculateAverageScore(results);
        BigDecimal averageReward = calculateAverageReward(results);

        int maxScore = calculateMaxScore(results);

        long maxRewardCount = calculateMaxRewardCount(results, settings.maxRewardCashback());

        BigDecimal actualMaxRewardPercent = calculateMaxRewardPercent(maxRewardCount, players);

        boolean passed = checkBusinessConstraint(maxRewardCount, players, settings.maxRewardAllowedRate());

        return new SimulationReport(
                players,
                settings.movesLimit(),
                seed,
                strategy,
                cashbackStatistics,
                averageScore,
                averageReward,
                maxScore,
                maxRewardCount,
                actualMaxRewardPercent,
                settings.maxRewardAllowedRate(),
                settings.maxRewardCashback(),
                passed);
    }

    private List<CashbackStatistics> calculateCashbackStatistics(List<OneSimulationResult> results) {
        List<OneSimulationResult> sorted = results.stream()
                .sorted(Comparator.comparingInt(OneSimulationResult::maxCashbackResult))
                .toList();

        List<CashbackStatistics> statistics = new ArrayList<>();

        int currentCashback = sorted.getFirst().maxCashbackResult();
        int currentReward = sorted.getFirst().reward();
        long playersCount = 0;

        for (OneSimulationResult result : sorted) {
            int cashback = result.maxCashbackResult();
            int reward = result.reward();

            if (cashback != currentCashback) {
                statistics.add(new CashbackStatistics(currentCashback, currentReward, playersCount));

                currentCashback = cashback;
                currentReward = reward;
                playersCount = 0;
            } else if (reward != currentReward) {
                throw new IllegalStateException("Для одинаковой плитки найдены разные награды.");
            }

            playersCount++;
        }

        statistics.add(new CashbackStatistics(currentCashback, currentReward, playersCount));

        return List.copyOf(statistics);
    }

    private BigDecimal calculateAverageScore(List<OneSimulationResult> results) {
        long totalScore = 0;

        for (OneSimulationResult result : results) {
            totalScore = Math.addExact(totalScore, result.scores());
        }

        return BigDecimal.valueOf(totalScore).divide(BigDecimal.valueOf(results.size()), AVERAGE_SCALE, ROUNDING_MODE);
    }

    private BigDecimal calculateAverageReward(List<OneSimulationResult> results) {
        long totalReward = 0;

        for (OneSimulationResult result : results) {
            totalReward = Math.addExact(totalReward, result.reward());
        }

        return BigDecimal.valueOf(totalReward).divide(BigDecimal.valueOf(results.size()), AVERAGE_SCALE, ROUNDING_MODE);
    }

    private int calculateMaxScore(List<OneSimulationResult> results) {
        return results.stream().mapToInt(OneSimulationResult::scores).max().orElseThrow();
    }

    private long calculateMaxRewardCount(List<OneSimulationResult> results, int maxRewardCashback) {
        return results.stream()
                .filter(result -> result.maxCashbackResult() >= maxRewardCashback)
                .count();
    }

    private BigDecimal calculateMaxRewardPercent(long maxRewardCount, int players) {
        return BigDecimal.valueOf(maxRewardCount).divide(BigDecimal.valueOf(players), PERCENT_SCALE, ROUNDING_MODE);
    }

    private boolean checkBusinessConstraint(long maxRewardCount, int players, BigDecimal maxRewardAllowedRate) {
        return BigDecimal.valueOf(maxRewardCount).compareTo(maxRewardAllowedRate.multiply(BigDecimal.valueOf(players)))
                <= 0;
    }
}
