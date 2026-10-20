package academy.cashbackmerge.random;

import java.util.Optional;
import java.util.Random;

public class RandomSeedGenerator {
    private final Random random;

    public RandomSeedGenerator(Optional<Long> seed) {
        this.random = seed.map(Random::new).orElseGet(Random::new);
    }

    public long generateNextSeed() {
        return random.nextLong();
    }
}
