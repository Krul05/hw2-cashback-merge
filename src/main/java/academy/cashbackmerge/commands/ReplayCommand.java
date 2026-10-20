package academy.cashbackmerge.commands;

import academy.cashbackmerge.game.ReplayRunner;
import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.moves.Move;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(name = "replay", description = "Повторить партию по seed и списку ходов", mixinStandardHelpOptions = true)
public final class ReplayCommand implements Callable<Integer> {

    @Mixin
    private final ConfigOptions config = new ConfigOptions();

    @Option(names = "--seed", required = true)
    private long seed;

    @SuppressWarnings("NullAway.Init")
    @Option(names = "--moves", required = true, split = ",", description = "Список ходов через запятую")
    private List<Move> moves;

    private final PrintStream out;
    private final PrintStream err;

    public ReplayCommand(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    @Override
    public Integer call() {
        try {
            GameSettings settings = config.load();

            ReplayRunner runner = new ReplayRunner();
            runner.replay(settings, seed, moves, out);

            return 0;

        } catch (IOException | IllegalArgumentException | IllegalStateException e) {
            err.println("Ошибка: " + e.getMessage());
            return 1;
        }
    }
}
