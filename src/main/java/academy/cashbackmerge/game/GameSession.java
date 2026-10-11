package academy.cashbackmerge.game;

import academy.cashbackmerge.model.Field;
import academy.cashbackmerge.random.CashbackGenerator;
import java.io.PrintStream;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GameSession {
    private final int movesLimit;
    private final Field field;
    private final CashbackGenerator cashbackGenerator;

    private int movesUsed = 0;
    private int score = 0;
    private boolean isGameOver = false;

    public GameSession(Field field, int movesLimit, CashbackGenerator cashbackGenerator) {
        if (movesLimit <= 0) {
            throw new IllegalArgumentException("Лимит ходов должен быть положительным");
        }

        this.cashbackGenerator = Objects.requireNonNull(cashbackGenerator, "Генератор кэшбэка не может быть null");
        this.field = Objects.requireNonNull(field, "Поле не может быть null");
        this.movesLimit = movesLimit;
    }

    public int movesIncrement() {
        if (isGameOver) {
            throw new IllegalStateException("Игра окончена. Невозможно сделать ход.");
        }
        movesUsed++;
        if (movesUsed >= movesLimit) {
            isGameOver = true;
        }
        return movesUsed;
    }

    public int addScore(int points) {
        score += points;
        return score;
    }

    public void generateRandomCell() {
        cashbackGenerator.generateRandomCashback(field);
    }

    public void printField(PrintStream output) {
        FieldPrinter.printField(field, output);
    }
}
