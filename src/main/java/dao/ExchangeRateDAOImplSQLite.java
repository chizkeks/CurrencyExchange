package dao;

import exceptions.CurrencyPairAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.Currency;
import model.ExchangeRate;
import org.sqlite.SQLiteException;
import utils.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAOImplSQLite implements ExchangeRateDAO{
    private static final String SELECT_ALL = """
            SELECT er.id as exchange_rate_id, c1.id as basecur_id, c1.fullname as basecur_fullname,
            c1.code as basecur_code, c1.sign as basecur_sign,
            c2.id as targetcur_id, c2.fullname as targetcur_fullname,
            c2.code as targetcur_code, c2.sign as targetcur_sign, er.rate
            FROM exchange_rates er INNER JOIN currencies c1 on er.basecurrencyid = c1.id
            INNER JOIN currencies c2 on er.targetcurrencyid = c2.id;
            """;

    private static final String INSERT_DATA = "INSERT INTO exchange_rates VALUES(NULL, ?, ?, ?);";

    private static final String SELECT_BY_CURRENCY_PAIR = """
            SELECT er.id as exchange_rate_id, c1.id as basecur_id, c1.fullname as basecur_fullname,
            c1.code as basecur_code, c1.sign as basecur_sign,
            c2.id as targetcur_id, c2.fullname as targetcur_fullname,
            c2.code as targetcur_code, c2.sign as targetcur_sign, er.rate
            FROM exchange_rates er INNER JOIN currencies c1 on er.basecurrencyid = c1.id
            INNER JOIN currencies c2 on er.targetcurrencyid = c2.id
            WHERE c1.code = ? AND c2.code = ?;
            """;

    private static final String UPDATE_RATE_BY_CURRENCY_PAIR_CODE = "UPDATE exchange_rates SET rate = %.2f " +
            "FROM (SELECT id FROM currencies WHERE code = ?) AS baseCur, (SELECT id FROM currencies WHERE code = %s) AS targetCur" +
            "WHERE basecurrencyid = baseCur.id AND targetcurrencyid = targetCur.id;";
    private static final String UPDATE_RATE_BY_CURRENCY_PAIR_ID = """
            UPDATE exchange_rates SET rate = ?
            WHERE basecurrencyid = ? AND targetcurrencyid = ?;
            """;

    public ExchangeRateDAOImplSQLite() {}

    public static ExchangeRateDAOImplSQLite getInstance() {
        return ExchangeRateDAOImplSQLiteHelper.singletonObject;
    }

    private static class ExchangeRateDAOImplSQLiteHelper{
        public static ExchangeRateDAOImplSQLite singletonObject = new ExchangeRateDAOImplSQLite();
    }

    @Override
    public void add(long baseCurrencyId, long targetCurrencyId, double rate) throws SQLException, DatabaseConnectionException, CurrencyPairAlreadyExistsException {
        try(Connection connection = DBConnectionManager.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(INSERT_DATA)) {
                connection.setAutoCommit(true);
                statement.setLong(1, baseCurrencyId);
                statement.setLong(2, targetCurrencyId);
                statement.setDouble(3, rate);
                statement.executeUpdate();
            } catch(SQLiteException e) {
                e.printStackTrace();
                throw new CurrencyPairAlreadyExistsException("Валютная пара с таким кодом уже существует");
            }
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DatabaseConnectionException(e);
        }
    }

    @Override
    public Optional<List<ExchangeRate>> getList() throws SQLException, DatabaseConnectionException{
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try(Connection connection = DBConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
            ResultSet resultSetItem = statement.executeQuery()) {
                while(resultSetItem.next()) {
                    exchangeRates.add(buildExchangeRate(resultSetItem));
                }
                return Optional.of(exchangeRates);
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DatabaseConnectionException(e);
        }
    }

    @Override
    public Optional<ExchangeRate> getByCurrencyPairCode(String baseCurrencyCode, String targetCurrencyCode) throws SQLException, DatabaseConnectionException {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try(Connection connection = DBConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_CURRENCY_PAIR)) {
            statement.setString(1, baseCurrencyCode);
            statement.setString(2, targetCurrencyCode);
            try(ResultSet resultSetItem = statement.executeQuery()) {
                while(resultSetItem.next()) {
                    exchangeRates.add(buildExchangeRate(resultSetItem));
                }
                return exchangeRates.isEmpty() ? Optional.empty() : Optional.of(exchangeRates.get(0));
            } catch(SQLException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DatabaseConnectionException(e);
        }
    }
    @Override
    public void updateRateByCurrencyPairId(long baseCurrencyId, long targetCurrencyId, double rate) throws DatabaseConnectionException {
        try(Connection connection = DBConnectionManager.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(UPDATE_RATE_BY_CURRENCY_PAIR_ID)) {
                connection.setAutoCommit(true);
                statement.setDouble(1, rate);
                statement.setLong(2, baseCurrencyId);
                statement.setLong(3, targetCurrencyId);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DatabaseConnectionException(e);
        }
    }


    private ExchangeRate buildExchangeRate(ResultSet resultSetItem) throws SQLException {
        return new ExchangeRate(resultSetItem.getLong("exchange_rate_id"),
                new Currency(resultSetItem.getLong("basecur_id"), resultSetItem.getString("basecur_fullname"), resultSetItem.getString("basecur_code"), resultSetItem.getString("basecur_sign")),
                new Currency(resultSetItem.getLong("targetcur_id"), resultSetItem.getString("targetcur_fullname"), resultSetItem.getString("targetcur_code"), resultSetItem.getString("targetcur_sign")),
                resultSetItem.getDouble("rate"));
    }
}
