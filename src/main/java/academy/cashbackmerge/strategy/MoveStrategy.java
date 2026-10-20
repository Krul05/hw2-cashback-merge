package academy.cashbackmerge.strategy;

import academy.cashbackmerge.model.Field;
import academy.cashbackmerge.moves.Move;
import java.util.Optional;

public interface MoveStrategy {
    Optional<Move> chooseMove(Field field);
}
