package academy.cashbackmerge.config;

import java.io.IOException;
import java.io.InputStream;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClasspathConfigLoader implements ConfigLoader {
    private final String fileName;

    @Override
    public InputStream load() throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);

        if (stream == null) {
            throw new IOException("Конфигурация не найдена в classpath: " + fileName);
        }

        return stream;
    }
}
