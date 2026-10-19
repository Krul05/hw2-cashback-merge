package academy.cashbackmerge.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;

@Getter
@NullMarked
public class Field {
    private final int sizeOfField;
    private final int[][] cells;

    public Field(int sizeOfField) {
        this.sizeOfField = sizeOfField;
        cells = new int[sizeOfField][sizeOfField];
    }

    public int[][] getCells() {
        return copyMatrix(cells);
    }

    private int[][] copyMatrix(int[][] matrix) {
        int[][] result = new int[sizeOfField][];
        for (int i = 0; i < sizeOfField; i++) {
            result[i] = Arrays.copyOf(matrix[i], sizeOfField);
        }
        return result;
    }

    public Field copy() {
        Field copy = new Field(sizeOfField);
        copy.setField(getCells());
        return copy;
    }

    public int[] getRow(int rowIndex) {
        checkCoordinates(rowIndex, 0);
        return Arrays.copyOf(cells[rowIndex], sizeOfField);
    }

    public int[] getColumn(int colIndex) {
        checkCoordinates(0, colIndex);
        int[] column = new int[sizeOfField];
        for (int i = 0; i < sizeOfField; i++) {
            column[i] = cells[i][colIndex];
        }
        return column;
    }

    public void setRow(int rowIndex, int[] row) {
        checkCoordinates(rowIndex, 0);
        validateLine(row);

        System.arraycopy(row, 0, cells[rowIndex], 0, sizeOfField);
    }

    public void setColumn(int colIndex, int[] column) {
        checkCoordinates(0, colIndex);
        validateLine(column);

        for (int i = 0; i < sizeOfField; i++) {
            cells[i][colIndex] = column[i];
        }
    }

    public void setCell(int i, int j, int cashback) {
        checkCoordinates(i, j);
        validateCashback(cashback);
        cells[i][j] = cashback;
    }

    public void setField(int[][] newCells) {
        if (newCells.length != sizeOfField) {
            throw new IllegalArgumentException("Некорректный размер поля");
        }
        for (int[] row : newCells) {
            validateLine(row);
        }
        for (int i = 0; i < sizeOfField; i++) {
            System.arraycopy(newCells[i], 0, cells[i], 0, sizeOfField);
        }
    }

    public List<List<Integer>> getEmptyCells() {
        List<List<Integer>> emptyCells = new ArrayList<>();
        for (int i = 0; i < sizeOfField; i++) {
            for (int j = 0; j < sizeOfField; j++) {
                if (cells[i][j] == 0) {
                    emptyCells.add(List.of(i, j));
                }
            }
        }
        return emptyCells;
    }

    private void validateCashback(int cashback) {
        if (cashback < 0 || (cashback & (cashback - 1)) != 0) {
            throw new IllegalArgumentException("Некорректное значение кэшбэка");
        }
    }

    private void validateLine(int[] line) {
        if (line.length != sizeOfField) {
            throw new IllegalArgumentException("Некорректный размер строки");
        }
        for (int cashback : line) {
            validateCashback(cashback);
        }
    }

    private void checkCoordinates(int i, int j) {
        if (i < 0 || i >= sizeOfField || j < 0 || j >= sizeOfField) {
            throw new IndexOutOfBoundsException("Координаты выходят за пределы поля");
        }
    }
}
