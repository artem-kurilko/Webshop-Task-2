import java.math.BigDecimal;

// Курс валюты
public enum CurrencyRate {
    DKK(new BigDecimal(100)),
    NOK(new BigDecimal("73.5")),
    SEK(new BigDecimal("70.23")),
    GBP(new BigDecimal("891.07")),
    EUR(new BigDecimal("743.93"));

    private final BigDecimal rate;

    CurrencyRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
