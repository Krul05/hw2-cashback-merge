package academy.cashbackmerge.random;

import academy.cashbackmerge.moves.Move;
import java.util.Random;

public class RandomMoveGenerator {
    private final Random random;

    public RandomMoveGenerator() {
        this.random = new Random();
    }

    public RandomMoveGenerator(long seed) {
        this.random = new Random(seed);
    }

    public Move generateRandomMove() {
        int randomIndex = random.nextInt(Move.getSize());
        return Move.getByOrdinal(randomIndex);
    }
}
