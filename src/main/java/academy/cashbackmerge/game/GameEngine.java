package academy.cashbackmerge.game;

import academy.cashbackmerge.moves.Move;
import academy.cashbackmerge.moves.MoveProcessor;
import academy.cashbackmerge.moves.MoveResult;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GameEngine {

    public MoveResult applyMove(GameSession session, Move move) {
        if (session.isGameOver()) {
            throw new IllegalStateException("Игра завершена. Невозможно сделать ход.");
        }

        MoveResult result = MoveProcessor.processMove(session.getField(), move);

        if (!result.isMoved()) {
            return result;
        }

        session.addScore(result.movePoints());
        session.movesIncrement();
        session.generateRandomCell();

        return result;
    }
}
