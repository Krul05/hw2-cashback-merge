package academy.cashbackmerge.simulation;

import academy.cashbackmerge.game.GameEngine;
import academy.cashbackmerge.game.GameSession;
import academy.cashbackmerge.moves.Move;
import academy.cashbackmerge.random.RandomMoveGenerator;

public class RandomMoveStrategy implements MoveStrategy {
    private final RandomMoveGenerator randomMoveGenerator;

    public RandomMoveStrategy(RandomMoveGenerator randomMoveGenerator) {
        this.randomMoveGenerator = randomMoveGenerator;
    }

    @Override
    public void moveField(GameSession gameSession) {
        Move randomMove = randomMoveGenerator.generateRandomMove();
        GameEngine.applyMove(gameSession, randomMove);
    }
}
