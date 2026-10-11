package academy.cashbackmerge.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfigDirectoryConfigLoader implements ConfigLoader {
    private static final Path CONFIG_DIRECTORY = Path.of("config");
    private final String fileName;

    @Override
    public InputStream load() throws IOException {
        Path filePath = CONFIG_DIRECTORY.resolve(fileName);
        if (!filePath.toFile().exists()) {
            throw new IOException("Конфигурация не найдена по пути: " + filePath);
        }
        return Files.newInputStream(filePath);
    }
}
