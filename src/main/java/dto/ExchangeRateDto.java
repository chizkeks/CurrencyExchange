package dto;

public class ExchangeRateDto {
    private int id;
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private double rate;
    public ExchangeRateDto(){}
    public ExchangeRateDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency, double rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
    public ExchangeRateDto(int id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CurrencyDto getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(CurrencyDto baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public CurrencyDto getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(CurrencyDto targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
