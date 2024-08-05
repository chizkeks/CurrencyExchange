package dao;

import exceptions.DatabaseConnectionException;
import model.ExchangeRate;
import org.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateDAO {
    public void add(long baseCurrencyId, long targetCurrencyId, double rate) throws SQLException, DatabaseConnectionException, SQLiteException;
    public Optional<List<ExchangeRate>> getList() throws SQLException;
    public Optional<ExchangeRate>  getByCurrencyPairCode(String baseCurrencyCode, String targetCurrencyCode) throws SQLException;
    //public boolean updateRateByCurrencyPairCode(String baseCurrencyCode, String targetCurrencyCode, double rate) throws SQLException;
    public void updateRateByCurrencyPairId(long baseCurrencyId, long targetCurrencyId, double rate) throws DatabaseConnectionException;
}
