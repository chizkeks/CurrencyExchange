package dao;

import dto.ExchangeRateFilter;
import exceptions.CurrencyPairAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.ExchangeRate;
import org.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.List;

public interface ExchangeRateDAO {
    public int add(int baseCurrencyId, int targetCurrencyId, double rate) throws SQLException, DatabaseConnectionException, CurrencyPairAlreadyExistsException;
    public List<ExchangeRate> findAll(ExchangeRateFilter exchangeRateFilter) throws SQLException, DatabaseConnectionException;
    public void update(int baseCurrencyId, int targetCurrencyId, double rate) throws SQLException, DatabaseConnectionException;
}
