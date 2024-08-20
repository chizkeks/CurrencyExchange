package services;

import dao.CurrencyDAO;
import dao.CurrencyDAOImplSQLite;
import dto.CurrencyFilter;
import exceptions.CurrencyAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyService {

    private final CurrencyDAO currencyDAO;

    public CurrencyService() {
        this.currencyDAO = CurrencyDAOImplSQLite.getInstance();
    }

    public Optional<List<Currency>> getAllCurrencies() throws SQLException, DatabaseConnectionException{
            return currencyDAO.findAll(null);
    }

    public Optional<Currency> getCurrency(String code) throws SQLException, DatabaseConnectionException{
        Optional<List<Currency>> response = currencyDAO.findAll(new CurrencyFilter(code, null, null, 1, 0));
        if(response.isPresent() && !response.get().isEmpty())
                return response.map(currencies -> currencies.get(0));
        else
            return Optional.empty();
    }
    public void createCurrency(String code, String name, String sign) throws SQLException, CurrencyAlreadyExistsException, DatabaseConnectionException {
        currencyDAO.add(new Currency(code, name, sign));
    }
}
