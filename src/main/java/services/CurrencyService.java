package services;

import dao.CurrencyDAO;
import dao.CurrencyDAOImplSQLite;
import exceptions.CurrencyAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyService {

    private final CurrencyDAO currencyDAO;

    public CurrencyService() {
        this.currencyDAO = new CurrencyDAOImplSQLite();
    }

    public Optional<List<Currency>> getAllCurrencies() throws SQLException, DatabaseConnectionException{
            return currencyDAO.getList();
    }

    public Optional<Currency> getCurrency(String code) throws SQLException, DatabaseConnectionException{
        return currencyDAO.getByCode(code);
    }
    public void createCurrency(String code, String name, String sign) throws SQLException, CurrencyAlreadyExistsException, DatabaseConnectionException {
        currencyDAO.add(new Currency(code, name, sign));
    }
}
