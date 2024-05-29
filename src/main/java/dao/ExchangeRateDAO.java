package dao;

import model.ExchangeRate;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateDAO {
    public boolean add(ExchangeRate value);
    public Optional<List<ExchangeRate>> getList();
    public Optional<ExchangeRate>  getByCurrencyPair(String baseCurrencyCode, String targetCurrencyCode);
}
