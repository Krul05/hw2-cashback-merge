package academy.cashbackmerge.config;

import java.util.List;
import lombok.Getter;

public class ConfigValidationException extends IllegalArgumentException {
    @Getter
    private final List<String> errors;

    public ConfigValidationException(List<String> errors) {
        super("Ошибки конфигурации:\n- " + String.join("\n- ", errors));
        this.errors = List.copyOf(errors);
    }
}
