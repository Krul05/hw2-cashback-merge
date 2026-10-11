package academy.cashbackmerge.config;

import java.io.IOException;
import java.io.InputStream;

public interface ConfigLoader {
    InputStream load() throws IOException;
}
