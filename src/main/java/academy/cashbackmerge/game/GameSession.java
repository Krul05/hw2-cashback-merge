package academy.cashbackmerge.game;

import academy.cashbackmerge.model.Field;
import java.io.PrintStream;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GameSession {
    private final int movesLimit;
    private final Field field;

    private int movesUsed = 0;
    private int score = 0;
    private boolean isGameOver = false;

    public GameSession(Field field, int movesLimit) {
        if (movesLimit <= 0) {
            throw new IllegalArgumentException("Лимит ходов должен быть положительным");
        }

        this.field = Objects.requireNonNull(field, "Поле не может быть null");
        this.movesLimit = movesLimit;
    }

    public void printField(PrintStream output) {
        FieldPrinter.printField(field, output);
    }
}
