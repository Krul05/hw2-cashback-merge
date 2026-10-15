package academy.cashbackmerge.moves;

import academy.cashbackmerge.model.Field;
import java.util.Arrays;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MoveProcessor {

    public MoveResult processMove(Field field, Move move) {
        int movePoints = 0;
        int[][] originalCells = field.getCells();

        switch (move) {
            case UP -> {
                for (int col = 0; col < field.getSizeOfField(); col++) {
                    movePoints += applyLineProcess(field.getColumn(col), field, false, col, false);
                }
            }
            case DOWN -> {
                for (int col = 0; col < field.getSizeOfField(); col++) {
                    movePoints += applyLineProcess(field.getColumn(col), field, true, col, false);
                }
            }
            case LEFT -> {
                for (int row = 0; row < field.getSizeOfField(); row++) {
                    movePoints += applyLineProcess(field.getRow(row), field, false, row, true);
                }
            }
            case RIGHT -> {
                for (int row = 0; row < field.getSizeOfField(); row++) {
                    movePoints += applyLineProcess(field.getRow(row), field, true, row, true);
                }
            }
        }

        boolean isMoved = !Arrays.deepEquals(originalCells, field.getCells());

        return new MoveResult(isMoved, movePoints);
    }

    private int applyLineProcess(int[] line, Field field, boolean isReverse, int index, boolean isRow) {
        int[] newLine = line;
        LineProcessorResult result;
        if (isReverse) {
            reverseArray(newLine);
            result = LineProcessor.processLine(newLine);
            newLine = reverseArray(result.newLine());
        } else {
            result = LineProcessor.processLine(newLine);
            newLine = result.newLine();
        }
        if (isRow) {
            field.setRow(index, newLine);
        } else {
            field.setColumn(index, newLine);
        }
        return result.movePoints();
    }

    private int[] reverseArray(int[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
        return array;
    }
}
