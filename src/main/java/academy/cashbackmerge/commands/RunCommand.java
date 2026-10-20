package academy.cashbackmerge.commands;

import academy.cashbackmerge.game.GameRunner;
import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.random.RandomSeedGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Callable;
import org.jspecify.annotations.Nullable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(name = "run", description = "Создать новую игровую партию", mixinStandardHelpOptions = true)
public final class RunCommand implements Callable<Integer> {

    @Mixin
    private final ConfigOptions config = new ConfigOptions();

    @Nullable
    @Option(names = "--seed", description = "Seed партии")
    private Long seed;

    private final PrintStream out;
    private final PrintStream err;
    private final InputStream in;

    public RunCommand(PrintStream out, PrintStream err, InputStream in) {
        this.out = out;
        this.err = err;
        this.in = in;
    }

    @Override
    public Integer call() {
        try {
            try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {
                GameSettings settings = config.load();

                RandomSeedGenerator seedGenerator = new RandomSeedGenerator(Optional.ofNullable(seed));

                GameRunner runner = new GameRunner(out, settings, seedGenerator, scanner);
                runner.run();

                return 0;
            }

        } catch (IOException | IllegalArgumentException e) {
            err.println("Ошибка: " + e.getMessage());
            return 1;
        }
    }
}
