package academy.cashbackmerge;

import academy.cashbackmerge.commands.CompareConfigsCommand;
import academy.cashbackmerge.commands.ReplayCommand;
import academy.cashbackmerge.commands.RunCommand;
import academy.cashbackmerge.commands.SimulateCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/** Точка входа в Cashback Merge. */
@Command(name = "cashback-merge", mixinStandardHelpOptions = true)
public final class Main {
    private Main() {}

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new RootCommand());

        commandLine.addSubcommand("run", new RunCommand(System.out, System.err, System.in));

        commandLine.addSubcommand("replay", new ReplayCommand(System.out, System.err));

        commandLine.addSubcommand("simulate", new SimulateCommand(System.out, System.err));
        commandLine.addSubcommand("compare-configs", new CompareConfigsCommand(System.out, System.err));

        if (args.length == 0) {
            commandLine.usage(System.out);
            return;
        }

        System.exit(commandLine.execute(args));
    }

    @Command(name = "cashback-merge", mixinStandardHelpOptions = true, description = "Игра Cashback Merge")
    private static final class RootCommand {}
}
