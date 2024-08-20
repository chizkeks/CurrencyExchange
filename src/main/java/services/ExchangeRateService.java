package services;

import dao.CurrencyDAO;
import dao.CurrencyDAOImplSQLite;
import dao.ExchangeRateDAO;
import dao.ExchangeRateDAOImplSQLite;
import dto.CurrencyFilter;
import dto.ExchangeRateFilter;
import exceptions.CurrencyPairAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.Currency;
import model.ExchangeRate;
import org.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO;
    public ExchangeRateService() {
        this.exchangeRateDAO = ExchangeRateDAOImplSQLite.getInstance();
    }

    public Optional<ExchangeRate> get(String baseCurrency, String targetCurrency) throws SQLException, DatabaseConnectionException {
        Optional<List<ExchangeRate>> response = exchangeRateDAO.findAll(new ExchangeRateFilter(baseCurrency, targetCurrency, null, 1,0));
        if(response.isPresent() && !response.get().isEmpty())
            return response.map(exchangeRates -> exchangeRates.get(0));
        else
            return Optional.empty();
    }

    public Optional<List<ExchangeRate>> getAllExchangeRates() throws SQLException, DatabaseConnectionException {
        return exchangeRateDAO.findAll(null);
    }

    public void createExchangeRate(long baseCurrencyId, long targetCurrencyId, double rate)
            throws SQLException, DatabaseConnectionException, CurrencyPairAlreadyExistsException {
        exchangeRateDAO.add(baseCurrencyId, targetCurrencyId,rate);
    }

    public void updateExchangeRate(long baseCurrencyId, long targetCurrencyId, double rate) throws DatabaseConnectionException, SQLException{
        exchangeRateDAO.update(baseCurrencyId, targetCurrencyId, rate);
    }
}
