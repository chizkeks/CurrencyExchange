package dao;

import dto.ExchangeRateFilter;
import exceptions.CurrencyPairAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.Currency;
import model.ExchangeRate;
import org.sqlite.SQLiteException;
import utils.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExchangeRateDAOImplSQLite implements ExchangeRateDAO{
    private static final String SELECT_ALL = """
            SELECT er.id as exchange_rate_id, c1.id as basecur_id, c1.fullname as basecur_fullname,
            c1.code as basecur_code, c1.sign as basecur_sign,
            c2.id as targetcur_id, c2.fullname as targetcur_fullname,
            c2.code as targetcur_code, c2.sign as targetcur_sign, er.rate
            FROM exchange_rates er INNER JOIN currencies c1 on er.basecurrencyid = c1.id
            INNER JOIN currencies c2 on er.targetcurrencyid = c2.id
            """;

    private static final String INSERT_DATA = "INSERT INTO exchange_rates VALUES(NULL, ?, ?, ?);";
    private static final String UPDATE_EXCHANGE_RATE = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE basecurrencyid = ? AND targetcurrencyid = ?;
            """;

    private ExchangeRateDAOImplSQLite() {}

    public static ExchangeRateDAOImplSQLite getInstance() {
        return ExchangeRateDAOImplSQLiteHelper.singletonObject;
    }

    private static class ExchangeRateDAOImplSQLiteHelper{
        public static ExchangeRateDAOImplSQLite singletonObject = new ExchangeRateDAOImplSQLite();
    }

    @Override
    public int add(int baseCurrencyId, int targetCurrencyId, double rate) throws SQLException, DatabaseConnectionException, CurrencyPairAlreadyExistsException {
        try(Connection connection = DBConnectionManager.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(INSERT_DATA, Statement.RETURN_GENERATED_KEYS)) {
                connection.setAutoCommit(true);
                statement.setLong(1, baseCurrencyId);
                statement.setLong(2, targetCurrencyId);
                statement.setDouble(3, rate);
                statement.executeUpdate();
                ResultSet keys = statement.getGeneratedKeys();
                if(keys.next())
                    return keys.getInt("id");
            } catch(SQLiteException e) {
                e.printStackTrace();
                throw new CurrencyPairAlreadyExistsException("Валютная пара с таким кодом уже существует");
            }
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DatabaseConnectionException(e);
        }
        return 0;
    }

    @Override
    public List<ExchangeRate> findAll(ExchangeRateFilter exchangeRateFilter) throws SQLException, DatabaseConnectionException{
        List<Object> parameters = new ArrayList<>();
        String sqlQuery = SELECT_ALL;
        if(exchangeRateFilter != null) {
            List<String> whereSQL = new ArrayList<>();
            if(exchangeRateFilter.getBaseCurrency() != null) {
                parameters.add(exchangeRateFilter.getBaseCurrency());
                whereSQL.add("c1.code = ?");
            }
            if(exchangeRateFilter.getTargetCurrency() != null) {
                parameters.add(exchangeRateFilter.getTargetCurrency());
                whereSQL.add("c2.code = ?");
            }
            if(exchangeRateFilter.getRate() != null) {
                parameters.add(exchangeRateFilter.getRate());
                whereSQL.add("er.rate = ?");
            }
            parameters.add(exchangeRateFilter.getLimit());
            parameters.add(exchangeRateFilter.getOffset());

            sqlQuery += whereSQL.stream().collect(Collectors.joining(" AND ", " WHERE "," LIMIT ? OFFSET ?"));
        }

        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try(Connection connection = DBConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            //Fill query params
            for(int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            //Execute the query
            try(ResultSet resultSetItem = statement.executeQuery()) {
                while (resultSetItem.next()) {
                    exchangeRates.add(buildExchangeRate(resultSetItem));
                }
                return exchangeRates;
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
    public void update(int baseCurrencyId, int targetCurrencyId, double rate) throws SQLException, DatabaseConnectionException {
        try(Connection connection = DBConnectionManager.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(UPDATE_EXCHANGE_RATE)) {
                connection.setAutoCommit(true);
                statement.setDouble(1, rate);
                statement.setInt(2, baseCurrencyId);
                statement.setInt(3, targetCurrencyId);
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
        return new ExchangeRate(resultSetItem.getInt("exchange_rate_id"),
                new Currency(resultSetItem.getInt("basecur_id"), resultSetItem.getString("basecur_fullname"), resultSetItem.getString("basecur_code"), resultSetItem.getString("basecur_sign")),
                new Currency(resultSetItem.getInt("targetcur_id"), resultSetItem.getString("targetcur_fullname"), resultSetItem.getString("targetcur_code"), resultSetItem.getString("targetcur_sign")),
                resultSetItem.getDouble("rate"));
    }



}
