package dao;

import exceptions.CurrencyAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CurrencyDAO {
    public void add(Currency currency) throws CurrencyAlreadyExistsException, DatabaseConnectionException;
    public Optional<List<Currency>> getList() throws SQLException, DatabaseConnectionException ;
    public Optional<Currency> getByCode(String code) throws SQLException, DatabaseConnectionException;
}
