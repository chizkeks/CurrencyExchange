package dao;

import dto.CurrencyFilter;
import exceptions.CurrencyAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CurrencyDAO {
    public void add(Currency currency) throws SQLException, CurrencyAlreadyExistsException, DatabaseConnectionException;
    public Optional<List<Currency>> findAll(CurrencyFilter currencyFilter) throws SQLException, DatabaseConnectionException;
}
