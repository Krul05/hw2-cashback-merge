package academy.cashbackmerge.random;

import academy.cashbackmerge.model.CashbackProbability;
import academy.cashbackmerge.model.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CashbackGenerator {
    private final List<CashbackProbability> spawnRules;
    private final Random random;

    public CashbackGenerator(List<CashbackProbability> spawnRules, long seed) {
        this.spawnRules = Objects.requireNonNull(List.copyOf(spawnRules));
        this.random = new Random(seed);
    }

    public CashbackGenerator(List<CashbackProbability> spawnRules) {
        this.spawnRules = Objects.requireNonNull(List.copyOf(spawnRules));
        this.random = new Random();
    }

    public void generateRandomCashback(Field field) {
        List<Integer> emptyCellIndex = generateRandomEmptyCellIndex(field);
        int cashbackValue = generateRandomCashbackValue();
        field.setCell(emptyCellIndex.get(0), emptyCellIndex.get(1), cashbackValue);
    }

    private List<Integer> generateRandomEmptyCellIndex(Field field) {
        List<List<Integer>> emptyCells = field.getEmptyCells();
        int emptyCellsCount = emptyCells.size();
        if (emptyCellsCount == 0) {
            throw new IllegalStateException("Не осталось пустых ячеек для генерации кэшбэка.");
        }
        int randomIndex = random.nextInt(emptyCellsCount);
        return emptyCells.get(randomIndex);
    }

    private int generateRandomCashbackValue() {
        BigDecimal randomValue = BigDecimal.valueOf(random.nextDouble());
        BigDecimal cumulativeProbability = BigDecimal.valueOf(0.0);

        for (CashbackProbability rule : spawnRules) {
            cumulativeProbability = cumulativeProbability.add(rule.probability());
            if (randomValue.compareTo(cumulativeProbability) <= 0) {
                return rule.cashback();
            }
        }
        throw new IllegalStateException("Не удалось выбрать значение кэшбэка");
    }
}
