package academy.cashbackmerge.report;

import academy.cashbackmerge.model.CashbackStatistics;
import academy.cashbackmerge.strategy.MoveStrategyType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record SimulationReport(
        int players,
        int movesLimit,
        long seed,
        MoveStrategyType strategy,
        List<CashbackStatistics> cashbackStatistics,
        BigDecimal averageScore,
        BigDecimal averageReward,
        int maxScore,
        long maxRewardCount,
        BigDecimal actualMaxRewardPercent,
        BigDecimal maxRewardAllowedPercent,
        int maxRewardCashback,
        boolean passed) {
    public SimulationReport {
        Objects.requireNonNull(strategy);
        cashbackStatistics = List.copyOf(cashbackStatistics);
    }
}
