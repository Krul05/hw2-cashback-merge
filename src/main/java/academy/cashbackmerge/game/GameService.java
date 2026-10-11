package academy.cashbackmerge.game;

import academy.cashbackmerge.model.Field;
import academy.cashbackmerge.model.GameSettings;
import academy.cashbackmerge.random.CashbackGenerator;

public class GameService {
    private static final int INITIAL_CELLS = 2;

    public GameSession startNewGame(GameSettings gameSettings, CashbackGenerator cashbackGenerator) {
        Field field = new Field(gameSettings.sizeOfField());
        for (int i = 0; i < INITIAL_CELLS; i++) {
            cashbackGenerator.generateRandomCashback(field);
        }
        return new GameSession(field, gameSettings.movesLimit(), cashbackGenerator);
    }
}
