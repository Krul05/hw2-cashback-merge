package academy.cashbackmerge.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

public record CashbackProbability(
        int cashback,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal probability) {}
