package dao;

import model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateDAO {
    public boolean add(String baseCurrencyCode, String targetCurrencyCode, double rate);
    public Optional<List<ExchangeRate>> getList();
    public Optional<ExchangeRate>  getByCurrencyPairCode(String baseCurrencyCode, String targetCurrencyCode);
    public boolean updateRateByCurrencyPairCode(String baseCurrenccyCode, String targetCurrencyCode, double rate);
}
