package services;

import dao.CurrencyDAO;
import dao.CurrencyDAOImplSQLite;
import dao.ExchangeRateDAO;
import dao.ExchangeRateDAOImplSQLite;
import exceptions.CurrencyPairAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.ExchangeRate;
import org.sqlite.SQLiteException;

import java.sql.SQLException;
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
        try {
            return exchangeRateDAO.getByCurrencyPairCode(baseCurrency, targetCurrency);
        }catch(Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<List<ExchangeRate>> getAllExchangeRates() {
        try {
            return exchangeRateDAO.getList();
        }catch(Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void createExchangeRate(long baseCurrencyId, long targetCurrencyId, double rate)
            throws DatabaseConnectionException, CurrencyPairAlreadyExistsException {
        try {
            exchangeRateDAO.add(baseCurrencyId, targetCurrencyId,rate);
        }catch(SQLiteException e) {
            e.printStackTrace();
            throw new CurrencyPairAlreadyExistsException("Валютная пара с таким кодом уже существует");
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DatabaseConnectionException(e);
        }
    }

    public void updateExchangeRate(long baseCurrencyId, long targetCurrencyId, double rate) throws DatabaseConnectionException {
        exchangeRateDAO.updateRateByCurrencyPairId(baseCurrencyId, targetCurrencyId, rate);
    }
}
