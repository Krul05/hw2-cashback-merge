package academy.cashbackmerge.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;

import academy.cashbackmerge.model.CashbackProbability;
import academy.cashbackmerge.model.Field;
import academy.cashbackmerge.moves.Move;
import academy.cashbackmerge.moves.MoveProcessor;
import academy.cashbackmerge.moves.MoveResult;
import academy.cashbackmerge.random.CashbackGenerator;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Генеративные тесты Cashback Merge")
class GenerativePropertiesTest {

    private static final long SEED = 42L;
    private static final int FIELD_SIZE = 4;
    private static final int FIELD_SAMPLES = 1000;
    private static final int MERGE_SAMPLES = 500;
    private static final int SPAWN_SAMPLES = 1000;
    private static final int EMPTY_CELL = 0;
    private static final int MERGE_FACTOR = 2;

    private static final int[] TILE_VALUES = {0, 1, 2, 4, 8, 16, 32};

    private static final String CONFIG = """
            {
              "spawnRules": [
                { "cashback": "16", "probability": "1.0" }
              ],
              "sizeOfField": 4,
              "movesLimit": 1,
              "maxRewardCashback": "32",
              "maxRewardAllowedRate": "0.20",
              "rewardTable": [
                { "cashback": "2", "reward": "0" },
                { "cashback": "4", "reward": "50" },
                { "cashback": "8", "reward": "150" },
                { "cashback": "16", "reward": "500" },
                { "cashback": "32", "reward": "2000" }
              ]
            }
            """;

    @Test
    @DisplayName("Сумма плиток сохраняется при любом ходе")
    void movePreservesTotalCashback() {
        Random random = new Random(SEED);

        for (int i = 0; i < FIELD_SAMPLES; i++) {
            Field field = generateRandomField(random);

            long expectedSum = calculateTotalCashback(field);

            for (Move move : Move.values()) {
                MoveResult result = MoveProcessor.calculateMove(field, move);
                long actualSum = calculateTotalCashback(result.field());

                assertThat(actualSum).isEqualTo(expectedSum);
            }
        }
    }

    @Test
    @DisplayName("Одна плитка не объединяется дважды за ход")
    void tileDoesNotMergeTwice() {
        Random random = new Random(SEED);

        for (Move move : Move.values()) {
            for (int i = 0; i < MERGE_SAMPLES; i++) {
                Field field = new Field(FIELD_SIZE);

                int value = TILE_VALUES[random.nextInt(TILE_VALUES.length)];
                int[] initial = {value, value, value * MERGE_FACTOR, EMPTY_CELL};
                int mergedValue = value * MERGE_FACTOR;
                int[] expected = {mergedValue, mergedValue, EMPTY_CELL, EMPTY_CELL};

                boolean horizontal = move == Move.LEFT || move == Move.RIGHT;

                if (move == Move.RIGHT || move == Move.DOWN) {
                    expected = new int[] {EMPTY_CELL, EMPTY_CELL, mergedValue, mergedValue};
                }

                int lineIndex = random.nextInt(FIELD_SIZE);

                for (int j = 0; j < FIELD_SIZE; j++) {
                    if (horizontal) {
                        field.setCell(lineIndex, j, initial[j]);
                    } else {
                        field.setCell(j, lineIndex, initial[j]);
                    }
                }

                Field result = MoveProcessor.calculateMove(field, move).field();
                int[] actual = new int[FIELD_SIZE];
                for (int j = 0; j < FIELD_SIZE; j++) {
                    actual[j] = horizontal ? result.getCells()[lineIndex][j] : result.getCells()[j][lineIndex];
                }

                assertThat(actual).containsExactly(expected);
            }
        }
    }

    @Test
    @DisplayName("Одинаковые seed дают одинаковую генерацию плиток")
    void sameSeedProducesSameCashback() {
        List<CashbackProbability> spawnRules = List.of(
                new CashbackProbability(1, new BigDecimal("0.85")), new CashbackProbability(2, new BigDecimal("0.15")));

        CashbackGenerator generator1 = new CashbackGenerator(spawnRules, SEED);
        CashbackGenerator generator2 = new CashbackGenerator(spawnRules, SEED);

        for (int i = 0; i < SPAWN_SAMPLES; i++) {
            Field field1 = new Field(FIELD_SIZE);
            Field field2 = new Field(FIELD_SIZE);

            generator1.generateRandomCashback(field1);
            generator2.generateRandomCashback(field2);

            assertThat(field1.getCells()).isDeepEqualTo(field2.getCells());
        }
    }

    private Field generateRandomField(Random random) {
        Field field = new Field(FIELD_SIZE);

        for (int row = 0; row < FIELD_SIZE; row++) {
            for (int column = 0; column < FIELD_SIZE; column++) {
                int value = TILE_VALUES[random.nextInt(TILE_VALUES.length)];

                field.setCell(row, column, value);
            }
        }

        return field;
    }

    private long calculateTotalCashback(Field field) {
        return Arrays.stream(field.getCells())
                .flatMapToInt(Arrays::stream)
                .asLongStream()
                .sum();
    }
}
