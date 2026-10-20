package academy.cashbackmerge.strategy;

import academy.cashbackmerge.random.RandomMoveGenerator;

public class MoveFactoryStrategy {

    public MoveStrategy createMoveStrategy(MoveStrategyType moveStrategyType, long seed) {
        return switch (moveStrategyType) {
            case RANDOM -> new RandomMoveStrategy(new RandomMoveGenerator(seed));
            case GREEDY_SCORE -> new GreedyScoreMoveStrategy();
        };
    }
}
