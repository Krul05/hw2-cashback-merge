package academy.cashbackmerge.game;

import academy.cashbackmerge.model.Field;
import academy.cashbackmerge.moves.Move;
import academy.cashbackmerge.moves.MoveProcessor;
import academy.cashbackmerge.random.CashbackGenerator;
import java.io.PrintStream;
import java.util.Objects;
import lombok.Getter;

@Getter
public class GameSession {
    private final int movesLimit;
    private final Field field;
    private final CashbackGenerator cashbackGenerator;

    private int movesUsed = 0;
    private int score = 0;

    public GameSession(Field field, int movesLimit, CashbackGenerator cashbackGenerator) {
        if (movesLimit <= 0) {
            throw new IllegalArgumentException("Лимит ходов должен быть положительным");
        }

        this.cashbackGenerator = Objects.requireNonNull(cashbackGenerator, "Генератор кэшбэка не может быть null");
        this.field = Objects.requireNonNull(field, "Поле не может быть null");
        this.movesLimit = movesLimit;
    }

    public void movesIncrement() {
        movesUsed++;
    }

    public boolean hasAvailableMoves() {
        for (Move move : Move.values()) {
            if (MoveProcessor.calculateMove(field, move).isMoved()) {
                return true;
            }
        }
        return false;
    }

    public boolean isGameOver() {
        return movesUsed >= movesLimit || !hasAvailableMoves();
    }

    public void addScore(int points) {
        score += points;
    }

    public void generateRandomCell() {
        cashbackGenerator.generateRandomCashback(field);
    }

    public void printField(PrintStream output) {
        FieldPrinter.printField(field, output);
    }
}
