package academy.cashbackmerge.game;

import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.moves.Move;
import academy.cashbackmerge.moves.MoveResult;
import academy.cashbackmerge.random.CashbackGenerator;
import academy.cashbackmerge.random.RandomSeedGenerator;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

public final class ReplayRunner {

    public void replay(GameSettings settings, long seed, List<Move> moves, PrintStream output) {
        RandomSeedGenerator seedGenerator = new RandomSeedGenerator(Optional.of(seed));

        CashbackGenerator cashbackGenerator =
                new CashbackGenerator(settings.spawnRules(), seedGenerator.generateNextSeed());

        GameSession session = new GameService().startNewGame(settings, cashbackGenerator);

        output.println("Начальное поле:");
        session.printField(output);

        for (Move move : moves) {
            if (session.isGameOver()) {
                throw new IllegalStateException("Партия завершилась до конца записи ходов");
            }

            MoveResult result = GameEngine.applyMove(session, move);

            if (!result.isMoved()) {
                throw new IllegalArgumentException("Недопустимый ход в записи: " + move);
            }

            output.println("Ход: " + move);
            session.printField(output);
        }

        output.println("Очки: " + session.getScore());
    }
}
