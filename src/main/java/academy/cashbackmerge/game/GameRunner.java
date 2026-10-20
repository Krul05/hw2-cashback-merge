package academy.cashbackmerge.game;

import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.moves.Move;
import academy.cashbackmerge.random.CashbackGenerator;
import academy.cashbackmerge.random.RandomSeedGenerator;
import java.io.PrintStream;
import java.util.Scanner;

public class GameRunner {
    private final PrintStream output;
    private final GameSettings gameSettings;
    private final RandomSeedGenerator randomSeedGenerator;
    private final Scanner scanner;

    public GameRunner(
            PrintStream output, GameSettings gameSettings, RandomSeedGenerator randomSeedGenerator, Scanner scanner) {
        this.output = output;
        this.gameSettings = gameSettings;
        this.randomSeedGenerator = randomSeedGenerator;
        this.scanner = scanner;
    }

    public void run() {
        long seed = randomSeedGenerator.generateNextSeed();
        GameSession session =
                new GameService().startNewGame(gameSettings, new CashbackGenerator(gameSettings.spawnRules(), seed));
        while (!session.isGameOver()) {
            session.printField(output);
            Move move = getMoveFromUser();
            GameEngine.applyMove(session, move);
        }
        session.printField(output);
        output.println("Игра окончена! Ваши очки: " + session.getScore());
    }

    private Move getMoveFromUser() {
        output.print("Введите желаемый ход (DOWN, UP, LEFT, RIGHT): ");
        try {
            return Move.valueOf(scanner.nextLine().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            output.println("Неверный ход. Пожалуйста, введите снова.");
            return getMoveFromUser();
        }
    }
}
