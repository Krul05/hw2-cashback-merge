package academy.cashbackmerge.simulation;

import academy.cashbackmerge.game.GameService;
import academy.cashbackmerge.model.ComparisonConfig;
import academy.cashbackmerge.model.ConfigSimulationResult;
import academy.cashbackmerge.random.RandomSeedGenerator;
import academy.cashbackmerge.report.SimulationReport;
import academy.cashbackmerge.report.SimulationReportCalculator;
import academy.cashbackmerge.strategy.MoveStrategyType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CompareConfigsRunner {

    public List<ConfigSimulationResult> compare(
            List<ComparisonConfig> configs, int players, MoveStrategyType strategy, long seed) throws IOException {
        List<ConfigSimulationResult> results = new ArrayList<>();

        for (ComparisonConfig config : configs) {
            SimulateRunner runner =
                    new SimulateRunner(new RandomSeedGenerator(Optional.of(seed)), strategy, config.settings());

            List<OneSimulationResult> simulationResults = runner.runSimulate(players, new GameService());

            SimulationReport report =
                    SimulationReportCalculator.calculate(simulationResults, config.settings(), seed, strategy);

            results.add(new ConfigSimulationResult(config.name(), report));
        }

        return results;
    }
}
