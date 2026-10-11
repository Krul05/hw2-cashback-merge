package academy.cashbackmerge.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;

import academy.cashbackmerge.game.GameEngine;
import academy.cashbackmerge.game.GameSession;
import academy.cashbackmerge.model.CashbackProbability;
import academy.cashbackmerge.model.Field;
import academy.cashbackmerge.moves.Move;
import academy.cashbackmerge.random.CashbackGenerator;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: движок ходов — что считается ходом и что после него происходит. */
@DisplayName("MR2. Движок ходов")
class MoveEngineTest {
    private static final List<CashbackProbability> SPAWN_RULES = List.of(new CashbackProbability(1, BigDecimal.ONE));
    private static final int MOVES_LIMIT1 = 3;
    private static final int MOVES_LIMIT2 = 5;
    private static final int SIZE = 4;
    private static final int[][] BEFORE_ARRAY = {
        {2, 16, 0, 0},
        {2, 0, 0, 0},
        {4, 0, 0, 0},
        {8, 0, 0, 0}
    };
    private static final int[][] AFTER_UP = {
        {4, 16, 0, 0},
        {4, 0, 0, 0},
        {8, 0, 0, 0},
        {0, 0, 0, 0}
    };

    @Test
    @DisplayName("Невалидный ход не меняет поле, не тратит ход и не создаёт плитку")
    void invalidMoveChangesNothing() {
        CashbackGenerator cashbackGenerator = new CashbackGenerator(SPAWN_RULES);
        Field field = new Field(SIZE);
        field.setField(BEFORE_ARRAY);
        GameSession gameSession = new GameSession(field, MOVES_LIMIT1, cashbackGenerator);
        GameEngine.applyMove(gameSession, Move.LEFT);

        assertThat(gameSession.getField().getCells()).isDeepEqualTo(BEFORE_ARRAY);
        assertThat(gameSession.getMovesUsed()).isEqualTo(0);
    }

    @Test
    @DisplayName("После валидного хода появляется ровно одна новая плитка")
    void validMoveSpawnsExactlyOneTile() {
        CashbackGenerator cashbackGenerator = new CashbackGenerator(SPAWN_RULES);
        Field field = new Field(SIZE);
        field.setField(BEFORE_ARRAY);
        GameSession gameSession = new GameSession(field, MOVES_LIMIT1, cashbackGenerator);
        GameEngine.applyMove(gameSession, Move.UP);

        int differenceCount = 0;
        int[][] afterArray = gameSession.getField().getCells();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (AFTER_UP[i][j] != afterArray[i][j]) {
                    differenceCount++;
                }
            }
        }
        assertThat(differenceCount).isEqualTo(1);
    }

    @Test
    @DisplayName("После N валидных ходов игра завершена")
    void gameEndsAfterMovesLimit() {
        CashbackGenerator cashbackGenerator = new CashbackGenerator(SPAWN_RULES);
        Field field = new Field(SIZE);
        field.setField(BEFORE_ARRAY);
        GameSession gameSession = new GameSession(field, MOVES_LIMIT1, cashbackGenerator);
        for (int i = 0; i < MOVES_LIMIT1; i++) {
            GameEngine.applyMove(gameSession, Move.UP);
        }
        assertThat(gameSession.isGameOver()).isTrue();
        assertThat(gameSession.getMovesUsed()).isEqualTo(MOVES_LIMIT1);
    }

    @Test
    @DisplayName("Достижение плитки 32% не заканчивает игру — доигрываем до лимита ходов")
    void reachingMaxTileDoesNotEndGame() {
        CashbackGenerator cashbackGenerator = new CashbackGenerator(SPAWN_RULES);
        Field field = new Field(SIZE);
        field.setField(BEFORE_ARRAY);
        GameSession gameSession = new GameSession(field, MOVES_LIMIT2, cashbackGenerator);
        for (int i = 0; i < MOVES_LIMIT2 - 2; i++) {
            GameEngine.applyMove(gameSession, Move.UP);
        }
        GameEngine.applyMove(gameSession, Move.LEFT);
        assertThat(gameSession.isGameOver()).isFalse();
        assertThat(Arrays.stream(gameSession.getField().getCells())
                        .flatMapToInt(Arrays::stream)
                        .toArray())
                .contains(32);
    }

    @Test
    @DisplayName("Score считается по правилам и растёт только при слияниях")
    void scoreIsCalculatedOnMerges() {
        CashbackGenerator cashbackGenerator = new CashbackGenerator(SPAWN_RULES);
        Field field = new Field(SIZE);
        field.setField(BEFORE_ARRAY);
        GameSession gameSession = new GameSession(field, MOVES_LIMIT1, cashbackGenerator);

        GameEngine.applyMove(gameSession, Move.LEFT);
        assertThat(gameSession.getScore()).isEqualTo(0);
        GameEngine.applyMove(gameSession, Move.UP);
        assertThat(gameSession.getScore()).isEqualTo(4);
    }
}
