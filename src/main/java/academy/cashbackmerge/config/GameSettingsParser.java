package academy.cashbackmerge.config;

import academy.cashbackmerge.model.GameSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class GameSettingsParser {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConfigLoader configLoader;

    public GameSettingsParser(ConfigLoader configLoader) {
        this.configLoader = Objects.requireNonNull(configLoader, "ConfigLoader не может быть null");
    }

    public GameSettings parseSettings() throws IOException {
        try (InputStream stream = configLoader.load()) {
            GameSettings gameSettings = Objects.requireNonNull(
                    objectMapper.readValue(stream, GameSettings.class), "Файл конфигурации пуст");
            gameSettings.validate();
            return gameSettings;
        }
    }
}
