package academy.cashbackmerge.strategy;

import academy.cashbackmerge.model.Field;
import academy.cashbackmerge.moves.Move;
import academy.cashbackmerge.moves.MoveProcessor;
import academy.cashbackmerge.moves.MoveResult;
import java.util.Optional;

public class GreedyScoreMoveStrategy implements MoveStrategy {

    @Override
    public Optional<Move> chooseMove(Field field) {
        Move bestMove = null;
        int bestScore = -1;

        for (Move move : Move.values()) {
            MoveResult result = MoveProcessor.calculateMove(field, move);
            int score = result.movePoints();
            if (score > bestScore && result.isMoved()) {
                bestScore = score;
                bestMove = move;
            }
        }

        return Optional.ofNullable(bestMove);
    }
}
