package dto;

public class ExchangeRateFilter {
    private String baseCurrency;
    private String targetCurrency;
    private String rate;
    private int limit;
    private int offset;

    public ExchangeRateFilter(String baseCurrency, String targetCurrency, String rate, int limit, int offset) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.limit = limit;
        this.offset = offset;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "ExchangeRate filter:" +
                "\nbaseCurrency = " + this.baseCurrency +
                "\ntargetCurrency = " + this.targetCurrency +
                "\nrate = " + this.rate +
                "\nlimit = " + this.limit +
                "\noffset = " + this.offset;
    }
}
