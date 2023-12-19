package Bank.backBank.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SingleRate {
    @JsonProperty("UAH")
    private double uah;

    @JsonProperty("USD")
    private double usd;

    @JsonProperty("EUR")
    private double eur;
}
