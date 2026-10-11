package academy.cashbackmerge.model;

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

    public void setCell(int i, int j, int cashback) {
        if (cashback <= 0 || (cashback & (cashback - 1)) != 0) {
            throw new IllegalArgumentException("Значение плитки должно быть положительной степенью двойки");
        }
        checkCoordinates(i, j);
        cells[i][j] = cashback;
    }

    public List<List<Integer>> getEmptyCells() {
        List<List<Integer>> emptyCells = new java.util.ArrayList<>();
        for (int i = 0; i < sizeOfField; i++) {
            for (int j = 0; j < sizeOfField; j++) {
                if (cells[i][j] == 0) {
                    emptyCells.add(List.of(i, j));
                }
            }
        }
        return emptyCells;
    }

    public void checkCoordinates(int i, int j) {
        if (i < 0 || i >= sizeOfField || j < 0 || j >= sizeOfField) {
            throw new IndexOutOfBoundsException("Координаты выходят за пределы поля");
        }
    }
}
