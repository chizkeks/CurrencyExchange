package dao;

import dto.CurrencyFilter;
import exceptions.CurrencyAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.Currency;
import org.checkerframework.checker.units.qual.A;
import org.sqlite.SQLiteException;
import utils.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyDAOImplSQLite implements CurrencyDAO{
    private static final String SELECT_ALL = "SELECT * FROM currencies";
    private static final String INSERT_DATA = "INSERT INTO currencies VALUES(NULL, ?, ?, ?);";

    public static CurrencyDAOImplSQLite getInstance() {
        return CurrencyDAOImplSQLiteHelper.singletonObject;
    }

    private static class CurrencyDAOImplSQLiteHelper{
        public static CurrencyDAOImplSQLite singletonObject = new CurrencyDAOImplSQLite();
    }

    private CurrencyDAOImplSQLite() {}

    @Override
    public void add(Currency currency) throws SQLException, CurrencyAlreadyExistsException, DatabaseConnectionException {
        try (Connection connection = DBConnectionManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_DATA)) {
                statement.setString(1, currency.getCode());
                statement.setString(2, currency.getFullName());
                statement.setString(3, currency.getSign());
                connection.setAutoCommit(true);
                statement.executeUpdate();
            } catch (SQLiteException e) {
                throw new CurrencyAlreadyExistsException("Валюта с таким кодом уже существует");
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseConnectionException(e);

        }
    }

    public Optional<List<Currency>> findAll(CurrencyFilter currencyFilter) throws SQLException, DatabaseConnectionException {
        List<Object> parameters = new ArrayList<>();
        String sqlQuery = SELECT_ALL;
        if(currencyFilter != null) {
            List<String> whereSQL = new ArrayList<>();
            if(currencyFilter.getCode() != null) {
                parameters.add(currencyFilter.getCode());
                whereSQL.add("code = ?");
            }
            if(currencyFilter.getFullName() != null) {
                parameters.add(currencyFilter.getFullName());
                whereSQL.add("fullname = ?");
            }
            if(currencyFilter.getSign() != null) {
                parameters.add(currencyFilter.getSign());
                whereSQL.add("sign = ?");
            }
            parameters.add(currencyFilter.getLimit());
            parameters.add(currencyFilter.getOffset());

            sqlQuery += whereSQL.stream().collect(Collectors.joining(" AND ", " WHERE "," LIMIT ? OFFSET ?"));
        }

        List<Currency> currencies = new ArrayList<>();
        try(Connection connection = DBConnectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            connection.setAutoCommit(true);
            //Fill query params
            for(int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            //Execute the query
            try (ResultSet resultSetItem = statement.executeQuery()) {
                while(resultSetItem.next()) {
                    currencies.add(buildCurrency(resultSetItem));
                }
                return Optional.of(currencies);
            } catch(SQLException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DatabaseConnectionException(e);
        }
    }
    private Currency buildCurrency(ResultSet resultSetItem) throws SQLException {
        return new Currency(resultSetItem.getLong("id"),
                resultSetItem.getString("code"),
                resultSetItem.getString("fullname"),
                resultSetItem.getString("sign"));
    }

}
