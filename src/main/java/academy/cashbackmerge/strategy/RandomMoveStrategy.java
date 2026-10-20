package academy.cashbackmerge.strategy;

import academy.cashbackmerge.model.Field;
import academy.cashbackmerge.moves.Move;
import academy.cashbackmerge.moves.MoveProcessor;
import academy.cashbackmerge.random.RandomMoveGenerator;
import java.util.List;
import java.util.Optional;

public class RandomMoveStrategy implements MoveStrategy {
    private final RandomMoveGenerator randomMoveGenerator;

    public RandomMoveStrategy(RandomMoveGenerator randomMoveGenerator) {
        this.randomMoveGenerator = randomMoveGenerator;
    }

    @Override
    public Optional<Move> chooseMove(Field field) {
        List<Move> moves = MoveProcessor.getAvailableMoves(field);
        return randomMoveGenerator.generateRandomMove(moves);
    }
}
