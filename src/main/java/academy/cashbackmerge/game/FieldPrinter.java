package academy.cashbackmerge.game;

import static java.util.Arrays.stream;

import academy.cashbackmerge.model.Field;
import java.io.PrintStream;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FieldPrinter {
    public void printField(Field field, PrintStream output) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(output);

        int[][] cells = field.getCells();
        int size = cells.length;
        int width = stream(cells)
                .flatMapToInt(java.util.Arrays::stream)
                .mapToObj(Integer::toString)
                .mapToInt(String::length)
                .max()
                .orElse(2);

        String topBorder = "┌" + ("─".repeat(width + 2) + "┬").repeat(size - 1) + "─".repeat(width + 2) + "┐";
        String bottomBorder = "└" + ("─".repeat(width + 2) + "┴").repeat(size - 1) + "─".repeat(width + 2) + "┘";
        String middleBorder = "├" + ("─".repeat(width + 2) + "┼").repeat(size - 1) + "─".repeat(width + 2) + "┤";
        output.println(topBorder);

        for (int i = 0; i < size; i++) {
            int[] row = cells[i];
            output.print("│");

            for (int value : row) {
                String cell = value == 0 ? " " : Integer.toString(value);

                output.printf(" %" + width + "s │", cell);
            }

            output.println();
            output.println((i == size - 1) ? bottomBorder : middleBorder);
        }
    }
}
