package academy.cashbackmerge.report;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SimulationReportMarkdownWriter {

    public String generate(SimulationReport report) {
        MarkdownWriter writer = new MarkdownWriter();

        generateHeader(writer, report);
        generateGeneralStatistics(writer, report);

        writer.generateSubHeader("Распределение плиток и наград");
        ReportMarkdownUtils.generateCashbackDistributionTable(writer, report);

        generateBusinessConstraints(writer, report);

        return writer.toString();
    }

    private void generateHeader(MarkdownWriter writer, SimulationReport report) {
        writer.generateHeader("Cashback Merge - Отчёт по симуляции");

        writer.generateLine("- seed: " + report.seed());
        writer.generateLine("- strategy: " + report.strategy());
        writer.generateLine("");
    }

    private void generateGeneralStatistics(MarkdownWriter writer, SimulationReport report) {
        writer.generateSubHeader("Общая статистика");

        writer.generateLine("- Игроков: " + report.players());
        writer.generateLine("- Лимит ходов: " + report.movesLimit());
        writer.generateLine("- Средние очки: " + report.averageScore().toPlainString());
        writer.generateLine("- Максимальные очки: " + report.maxScore());
        writer.generateLine("- Средняя награда: " + report.averageReward().toPlainString());
        writer.generateLine("");
    }

    private void generateBusinessConstraints(MarkdownWriter writer, SimulationReport report) {
        writer.generateSubHeader("Бизнес-ограничения");

        String[] headers = {
            "Максимальный кэшбэк", "Игроков с максимальной наградой", "Фактическая доля", "Допустимая доля", "Статус"
        };

        String[][] data = {
            {
                String.valueOf(report.maxRewardCashback()),
                String.valueOf(report.maxRewardCount()),
                ReportMarkdownUtils.formatPercent(report.actualMaxRewardPercent()),
                ReportMarkdownUtils.formatPercent(report.maxRewardAllowedPercent()),
                report.passed() ? "**OK**" : "**FAILED**"
            }
        };

        writer.generateTable(headers, data);
    }
}
