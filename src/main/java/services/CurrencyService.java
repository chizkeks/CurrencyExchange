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

    public Optional<List<Currency>> getAllCurrencies() {
        try {
            return currencyDAO.getList();
        }catch (SQLException e) {
            return Optional.empty();
        } catch (DatabaseConnectionException e) {
            return Optional.empty();
        }
    }

    public Optional<Currency> getCurrency(String code) {
        try {
            return currencyDAO.getByCode(code);
        }catch (SQLException e) {
            return Optional.empty();
        } catch (DatabaseConnectionException e) {
            return Optional.empty();
        }
    }
    public void createCurrency(String code, String name, String sign) throws CurrencyAlreadyExistsException, DatabaseConnectionException {
        currencyDAO.add(new Currency(code, name, sign));
    }
}
