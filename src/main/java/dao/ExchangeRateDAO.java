package dao;

import exceptions.CurrencyPairAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.ExchangeRate;
import org.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateDAO {
    public void add(long baseCurrencyId, long targetCurrencyId, double rate) throws SQLException, DatabaseConnectionException, CurrencyPairAlreadyExistsException;
    public Optional<List<ExchangeRate>> getList() throws SQLException, DatabaseConnectionException;
    public Optional<ExchangeRate>  getByCurrencyPairCode(String baseCurrencyCode, String targetCurrencyCode) throws SQLException, DatabaseConnectionException;
    public void updateRateByCurrencyPairId(long baseCurrencyId, long targetCurrencyId, double rate) throws SQLException, DatabaseConnectionException;
}
