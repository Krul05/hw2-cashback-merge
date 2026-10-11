package academy.cashbackmerge.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;

@AllArgsConstructor
public class StringConfigLoader implements ConfigLoader {
    @NonNull
    private final String configString;

    @Override
    public InputStream load() {
        return new ByteArrayInputStream(configString.getBytes(StandardCharsets.UTF_8));
    }
}
