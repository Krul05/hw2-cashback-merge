package academy.cashbackmerge.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;

import academy.cashbackmerge.model.Field;
import academy.cashbackmerge.moves.LineProcessor;
import academy.cashbackmerge.moves.Move;
import academy.cashbackmerge.moves.MoveProcessor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Обязательные тесты: правила слияния плиток. */
@DisplayName("MR2. Слияние плиток")
class MergeRulesTest {

    private static final int[] BEFORE_ROW = {1, 1, 2, 0};
    private static final int[] AFTER_ROW = {2, 2, 0, 0};

    private static final int[][] BEFORE_ARRAY = {
        {1, 1, 1, 1},
        {1, 1, 1, 1},
        {1, 1, 1, 1},
        {1, 1, 1, 1}
    };
    private static final int SIZE = 4;
    private static final List<Move> MOVES = List.of(Move.LEFT, Move.UP, Move.DOWN, Move.RIGHT);
    private static final Map<Move, int[][]> AFTER_ARRAYS = Map.ofEntries(
            Map.entry(Move.UP, new int[][] {
                {2, 2, 2, 2},
                {2, 2, 2, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
            }),
            Map.entry(Move.DOWN, new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {2, 2, 2, 2},
                {2, 2, 2, 2}
            }),
            Map.entry(Move.LEFT, new int[][] {
                {2, 2, 0, 0},
                {2, 2, 0, 0},
                {2, 2, 0, 0},
                {2, 2, 0, 0}
            }),
            Map.entry(Move.RIGHT, new int[][] {
                {0, 0, 2, 2},
                {0, 0, 2, 2},
                {0, 0, 2, 2},
                {0, 0, 2, 2}
            }));

    @ParameterizedTest
    @CsvSource({
        "'1,1,.,.', '2,.,.,.'",
        "'1,1,1,.', '2,1,.,.'",
        "'1,1,1,1', '2,2,.,.'",
        "'2,2,4,4', '4,8,.,.'",
    })
    @DisplayName("Сдвиг влево: {0} -> {1}")
    void rowShiftsLeftAndMerges(String before, String expected) {
        int[] beforeRow = parseLine(before);
        int[] expectedRow = parseLine(expected);

        int[] afterRow = LineProcessor.processLine(beforeRow).newLine();
        assertThat(afterRow).isEqualTo(expectedRow);
    }

    @Test
    @DisplayName("Одна плитка не сливается дважды за ход: [1,1,2,.] -> [2,2,.,.]")
    void tileDoesNotMergeTwiceInOneMove() {
        int[] after = LineProcessor.processLine(BEFORE_ROW).newLine();
        assertThat(after).isEqualTo(AFTER_ROW);
    }

    @Test
    @DisplayName("Слияние работает во всех четырёх направлениях")
    void mergeWorksInAllDirections() {
        Field field = new Field(SIZE);
        for (Move move : MOVES) {
            field.setField(BEFORE_ARRAY);
            MoveProcessor.processMove(field, move);
            assertThat(field.getCells()).isEqualTo(AFTER_ARRAYS.get(move));
        }
    }

    private int[] parseLine(String line) {
        return Arrays.stream(line.split(","))
                .map(String::trim)
                .mapToInt(value -> value.equals(".") ? 0 : Integer.parseInt(value))
                .toArray();
    }
}
