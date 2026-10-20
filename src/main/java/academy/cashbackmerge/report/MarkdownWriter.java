package academy.cashbackmerge.report;

public final class MarkdownWriter {

    private final StringBuilder markdownContent = new StringBuilder();

    public void generateHeader(String header) {
        markdownContent.append("# ").append(header).append("\n\n");
    }

    public void generateSubHeader(String subHeader) {
        markdownContent.append("## ").append(subHeader).append("\n\n");
    }

    public void generateLine(String paragraph) {
        markdownContent.append(paragraph).append('\n');
    }

    public void generateTable(String[] headers, String[][] data) {
        if (headers.length == 0) {
            throw new IllegalArgumentException("Таблица должна содержать столбцы.");
        }

        appendRow(headers);

        markdownContent.repeat("|---", headers.length);
        markdownContent.append("|\n");

        for (String[] row : data) {
            if (row.length != headers.length) {
                throw new IllegalArgumentException("Неверное количество столбцов в строке.");
            }

            appendRow(row);
        }

        markdownContent.append('\n');
    }

    private void appendRow(String[] cells) {
        for (String cell : cells) {
            markdownContent.append("| ").append(cell.replace("|", "\\|")).append(' ');
        }

        markdownContent.append("|\n");
    }

    @Override
    public String toString() {
        return markdownContent.toString();
    }
}
