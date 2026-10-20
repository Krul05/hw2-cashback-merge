package academy.cashbackmerge.report;

import academy.cashbackmerge.model.ConfigSimulationResult;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ComparisonReportMarkdownWriter {

    private static final int PERCENT_SCALE = 2;
    private static final BigDecimal PERCENT_MULTIPLIER = BigDecimal.valueOf(100);
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public String generate(List<ConfigSimulationResult> configSimulationResults) {
        MarkdownWriter writer = new MarkdownWriter();

        generateHeader(writer, configSimulationResults);
        generateGeneralStatistics(writer, configSimulationResults);
        generateBusinessConstraints(writer, configSimulationResults);
        generateDistributions(writer, configSimulationResults);

        return writer.toString();
    }

    private void generateHeader(MarkdownWriter writer, List<ConfigSimulationResult> configSimulationResults) {
        writer.generateHeader("Cashback Merge - Отчёт по сравнению конфигураций");

        SimulationReport firstReport = configSimulationResults.getFirst().report();

        writer.generateLine("- seed: " + firstReport.seed());
        writer.generateLine("- strategy: " + firstReport.strategy());
        writer.generateLine("");
    }

    private void generateGeneralStatistics(
            MarkdownWriter writer, List<ConfigSimulationResult> configSimulationResults) {
        writer.generateSubHeader("Общая статистика");

        String[][] data = {
            generateRow("Игроков", configSimulationResults, r -> String.valueOf(r.players())),
            generateRow("Лимит ходов", configSimulationResults, r -> String.valueOf(r.movesLimit())),
            generateRow(
                    "Средние очки",
                    configSimulationResults,
                    r -> r.averageScore().toPlainString()),
            generateRow("Максимальные очки", configSimulationResults, r -> String.valueOf(r.maxScore())),
            generateRow(
                    "Средняя награда",
                    configSimulationResults,
                    r -> r.averageReward().toPlainString())
        };

        writer.generateTable(generateHeaders(configSimulationResults), data);
    }

    private void generateBusinessConstraints(
            MarkdownWriter writer, List<ConfigSimulationResult> configSimulationResults) {
        writer.generateSubHeader("Бизнес-ограничения");

        String[][] data = {
            generateRow("Максимальный кэшбэк", configSimulationResults, r -> String.valueOf(r.maxRewardCashback())),
            generateRow(
                    "Игроков с максимальной наградой",
                    configSimulationResults,
                    r -> String.valueOf(r.maxRewardCount())),
            generateRow("Фактическая доля", configSimulationResults, r -> formatPercent(r.actualMaxRewardPercent())),
            generateRow("Допустимая доля", configSimulationResults, r -> formatPercent(r.maxRewardAllowedPercent())),
            generateRow("Статус", configSimulationResults, r -> r.passed() ? "**OK**" : "**FAILED**")
        };

        writer.generateTable(generateHeaders(configSimulationResults), data);
    }

    private String[] generateHeaders(List<ConfigSimulationResult> results) {
        String[] headers = new String[results.size() + 1];
        headers[0] = "Показатель";

        for (int i = 0; i < results.size(); i++) {
            headers[i + 1] = results.get(i).configName();
        }

        return headers;
    }

    private String[] generateRow(
            String name, List<ConfigSimulationResult> results, Function<SimulationReport, String> extractor) {
        String[] row = new String[results.size() + 1];
        row[0] = name;

        for (int i = 0; i < results.size(); i++) {
            row[i + 1] = extractor.apply(results.get(i).report());
        }

        return row;
    }

    private void generateDistributions(MarkdownWriter writer, List<ConfigSimulationResult> configSimulationResults) {
        for (ConfigSimulationResult result : configSimulationResults) {
            writer.generateSubHeader("Распределение плиток и наград - " + result.configName());

            ReportMarkdownUtils.generateCashbackDistributionTable(writer, result.report());
        }
    }

    private String formatPercent(BigDecimal rate) {
        return rate.multiply(PERCENT_MULTIPLIER)
                        .setScale(PERCENT_SCALE, ROUNDING_MODE)
                        .toPlainString() + "%";
    }
}
