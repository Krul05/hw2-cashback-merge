package academy.cashbackmerge.commands;

import academy.cashbackmerge.strategy.MoveStrategyType;
import java.util.Locale;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

public final class StrategyConverter implements ITypeConverter<MoveStrategyType> {

    @Override
    public MoveStrategyType convert(String value) {
        String normalized = value.toUpperCase(Locale.ROOT).replace('-', '_');

        try {
            return MoveStrategyType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new TypeConversionException("Неизвестная стратегия: " + value);
        }
    }
}
