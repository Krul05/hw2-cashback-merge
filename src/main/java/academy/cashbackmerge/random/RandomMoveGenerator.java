package academy.cashbackmerge.random;

import academy.cashbackmerge.moves.Move;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomMoveGenerator {
    private final Random random;

    public RandomMoveGenerator() {
        this.random = new Random();
    }

    public RandomMoveGenerator(long seed) {
        this.random = new Random(seed);
    }

    public Optional<Move> generateRandomMove(List<Move> moves) {
        if (moves.isEmpty()) {
            return Optional.empty();
        }
        int randomIndex = random.nextInt(moves.size());
        return Optional.of(moves.get(randomIndex));
    }
}
