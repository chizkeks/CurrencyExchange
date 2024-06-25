package services;

import dao.CurrencyDAO;
import dao.CurrencyDAOImplSQLite;
import dao.ExchangeRateDAO;
import dao.ExchangeRateDAOImplSQLite;
import model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public class ExchangeRateService {

    private final ExchangeRateDAO exchangeRateDAO;
    private CurrencyDAO currencyDAO;
    public ExchangeRateService() {
        this.exchangeRateDAO = new ExchangeRateDAOImplSQLite();
        this.currencyDAO = new CurrencyDAOImplSQLite();
    }

    public Optional<ExchangeRate> get(String baseCurrency, String targetCurrency) {
        return exchangeRateDAO.getByCurrencyPairCode(baseCurrency, targetCurrency);
    }

    public Optional<List<ExchangeRate>> getAllExchangeRates() {
        return exchangeRateDAO.getList();
    }

    public boolean createExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        return exchangeRateDAO.add(baseCurrencyCode, targetCurrencyCode,rate);
    }

    public boolean updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        return exchangeRateDAO.updateRateByCurrencyPairCode(baseCurrencyCode, targetCurrencyCode, rate);
    }
}
