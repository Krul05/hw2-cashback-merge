package academy.cashbackmerge.simulation;

import academy.cashbackmerge.game.GameEngine;
import academy.cashbackmerge.game.GameService;
import academy.cashbackmerge.game.GameSession;
import academy.cashbackmerge.model.CashbackReward;
import academy.cashbackmerge.model.Field;
import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.random.CashbackGenerator;
import academy.cashbackmerge.random.RandomSeedGenerator;
import academy.cashbackmerge.strategy.MoveFactoryStrategy;
import academy.cashbackmerge.strategy.MoveStrategy;
import academy.cashbackmerge.strategy.MoveStrategyType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SimulateRunner {
    private final RandomSeedGenerator randomSeedGenerator;
    private final GameSettings gameSettings;
    private final MoveStrategyType moveStrategyType;
    private final MoveFactoryStrategy moveFactoryStrategy = new MoveFactoryStrategy();

    public SimulateRunner(
            RandomSeedGenerator randomSeedGenerator, MoveStrategyType moveStrategyType, GameSettings gameSettings) {
        this.randomSeedGenerator = randomSeedGenerator;
        this.moveStrategyType = moveStrategyType;
        this.gameSettings = gameSettings;
    }

    public List<OneSimulationResult> runSimulate(int players, GameService service) throws IOException {
        List<OneSimulationResult> simulationResults = new ArrayList<>();
        for (int i = 0; i < players; i++) {
            OneSimulationResult result = simulateOneGame(service.startNewGame(
                    gameSettings,
                    new CashbackGenerator(gameSettings.spawnRules(), randomSeedGenerator.generateNextSeed())));
            simulationResults.add(result);
        }
        return simulationResults;
    }

    private OneSimulationResult simulateOneGame(GameSession gameSession) {
        Field field = gameSession.getField();
        MoveStrategy moveStrategy =
                moveFactoryStrategy.createMoveStrategy(moveStrategyType, randomSeedGenerator.generateNextSeed());
        while (!gameSession.isGameOver()) {
            GameEngine.applyMove(gameSession, moveStrategy.chooseMove(field).orElseThrow());
        }
        int maxCashback = Arrays.stream(field.getCells())
                .flatMapToInt(Arrays::stream)
                .max()
                .orElseThrow();
        int rewardResult = calculateReward(maxCashback);

        return new OneSimulationResult(maxCashback, gameSession.getScore(), rewardResult);
    }

    private int calculateReward(int maxCashback) {
        int cashback = Math.min(maxCashback, gameSettings.maxRewardCashback());

        return gameSettings.rewardTable().stream()
                .filter(reward -> reward.cashback() <= cashback)
                .max(Comparator.comparingInt(CashbackReward::cashback))
                .map(CashbackReward::reward)
                .orElse(0);
    }
}
