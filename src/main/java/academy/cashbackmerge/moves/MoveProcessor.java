package academy.cashbackmerge.moves;

import academy.cashbackmerge.model.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MoveProcessor {

    public MoveResult processMove(Field field, Move move) {
        MoveResult result = calculateMove(field, move);
        if (result.isMoved()) {
            field.setField(result.field().getCells());
        }
        return result;
    }

    public static List<Move> getAvailableMoves(Field field) {
        List<Move> moves = new ArrayList<>();

        for (Move move : Move.values()) {
            if (calculateMove(field, move).isMoved()) {
                moves.add(move);
            }
        }

        return moves;
    }

    public MoveResult calculateMove(Field field, Move move) {
        int movePoints = 0;
        Field newField = field.copy();

        switch (move) {
            case UP -> {
                for (int col = 0; col < newField.getSizeOfField(); col++) {
                    movePoints += applyLineProcess(newField.getColumn(col), newField, false, col, false);
                }
            }
            case DOWN -> {
                for (int col = 0; col < newField.getSizeOfField(); col++) {
                    movePoints += applyLineProcess(newField.getColumn(col), newField, true, col, false);
                }
            }
            case LEFT -> {
                for (int row = 0; row < newField.getSizeOfField(); row++) {
                    movePoints += applyLineProcess(newField.getRow(row), newField, false, row, true);
                }
            }
            case RIGHT -> {
                for (int row = 0; row < newField.getSizeOfField(); row++) {
                    movePoints += applyLineProcess(newField.getRow(row), newField, true, row, true);
                }
            }
        }

        boolean isMoved = !Arrays.deepEquals(field.getCells(), newField.getCells());
        return new MoveResult(isMoved, movePoints, newField);
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
