package dao;

import dto.CurrencyFilter;
import exceptions.CurrencyAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.Currency;

import java.sql.SQLException;
import java.util.List;

public interface CurrencyDAO {
    public int add(Currency currency) throws SQLException, CurrencyAlreadyExistsException, DatabaseConnectionException;
    public List<Currency> findAll(CurrencyFilter currencyFilter) throws SQLException, DatabaseConnectionException;
}
