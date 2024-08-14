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

    public Optional<ExchangeRate> get(String baseCurrency, String targetCurrency) throws SQLException, DatabaseConnectionException {
        return exchangeRateDAO.getByCurrencyPairCode(baseCurrency, targetCurrency);
    }

    public Optional<List<ExchangeRate>> getAllExchangeRates() throws SQLException, DatabaseConnectionException {
        return exchangeRateDAO.getList();
    }

    public void createExchangeRate(long baseCurrencyId, long targetCurrencyId, double rate)
            throws SQLException, DatabaseConnectionException, CurrencyPairAlreadyExistsException {
        exchangeRateDAO.add(baseCurrencyId, targetCurrencyId,rate);
    }

    public void updateExchangeRate(long baseCurrencyId, long targetCurrencyId, double rate) throws DatabaseConnectionException, SQLException{
        exchangeRateDAO.updateRateByCurrencyPairId(baseCurrencyId, targetCurrencyId, rate);
    }
}
