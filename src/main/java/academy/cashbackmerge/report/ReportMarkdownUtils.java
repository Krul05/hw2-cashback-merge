package academy.cashbackmerge.report;

import academy.cashbackmerge.model.CashbackStatistics;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportMarkdownUtils {
    private static final int PERCENT_SCALE = 2;
    private static final BigDecimal PERCENT_MULTIPLIER = BigDecimal.valueOf(100);
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public void generateCashbackDistributionTable(MarkdownWriter writer, SimulationReport report) {

        String[] headers = {
            "Максимальная плитка", "Награда", "Игроков", "Процент игроков, получивших максимальную плитку"
        };

        String[][] data = ReportMarkdownUtils.generateDistributionTableRows(report, headers.length);

        writer.generateTable(headers, data);
    }

    private String[][] generateDistributionTableRows(SimulationReport report, int columnsCount) {
        String[][] data = new String[report.cashbackStatistics().size()][columnsCount];

        for (int i = 0; i < data.length; i++) {
            CashbackStatistics statistics = report.cashbackStatistics().get(i);

            data[i] = new String[] {
                String.valueOf(statistics.cashback()),
                String.valueOf(statistics.reward()),
                String.valueOf(statistics.playersCount()),
                ReportMarkdownUtils.calculatePercent(statistics.playersCount(), report.players())
            };
        }

        return data;
    }

    public String calculatePercent(long count, int players) {
        return BigDecimal.valueOf(count)
                        .multiply(PERCENT_MULTIPLIER)
                        .divide(BigDecimal.valueOf(players), PERCENT_SCALE, ROUNDING_MODE)
                        .toPlainString()
                + "%";
    }

    public String formatPercent(BigDecimal fraction) {
        return fraction.multiply(PERCENT_MULTIPLIER)
                        .setScale(PERCENT_SCALE, ROUNDING_MODE)
                        .toPlainString() + "%";
    }
}
