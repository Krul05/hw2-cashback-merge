package academy.cashbackmerge.simulation;

import academy.cashbackmerge.game.GameEngine;
import academy.cashbackmerge.game.GameSession;
import academy.cashbackmerge.model.Field;
import academy.cashbackmerge.moves.Move;
import academy.cashbackmerge.moves.MoveProcessor;
import academy.cashbackmerge.moves.MoveResult;

public class GreedyScoreMoveStrategy implements MoveStrategy {
    @Override
    public void moveField(GameSession gameSession) {
        Field field = gameSession.getField();
        Move bestMove = findBestMove(field);
        GameEngine.applyMove(gameSession, bestMove);
    }

    private Move findBestMove(Field field) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Move move : Move.values()) {
            MoveResult result = MoveProcessor.processMove(field, move);
            int score = result.movePoints();
            if (score > bestScore && result.isMoved()) {
                bestScore = score;
                bestMove = move;
            }
        }
        if (bestMove == null) {
            throw new IllegalStateException("Нет валидных ходов. Игра завершена.");
        }

        return bestMove;
    }
}
