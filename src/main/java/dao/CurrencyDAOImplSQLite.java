package dao;

import exceptions.CurrencyAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.Currency;
import org.sqlite.SQLiteException;
import utils.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAOImplSQLite implements CurrencyDAO{
    private static final String SELECT_ALL = "SELECT * FROM currencies";
    private static final String SELECT_BY_CODE = "SELECT * FROM currencies WHERE code = ?";
    private static final String INSERT_DATA = "INSERT INTO currencies VALUES(NULL, ?, ?, ?)";

    public static CurrencyDAOImplSQLite getInstance() {
        return CurrencyDAOImplSQLiteHelper.singletonObject;
    }

    private static class CurrencyDAOImplSQLiteHelper{
        public static CurrencyDAOImplSQLite singletonObject = new CurrencyDAOImplSQLite();
    }

    @Override
    public void add(Currency currency) throws SQLException, CurrencyAlreadyExistsException, DatabaseConnectionException {
        try(Connection connection = DBConnectionManager.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(INSERT_DATA)) {
                statement.setString(1, currency.getCode());
                statement.setString(2, currency.getFullName());
                statement.setString(3, currency.getSign());
                connection.setAutoCommit(true);
                statement.executeUpdate();
            } catch (SQLiteException e) {
                throw new CurrencyAlreadyExistsException("Валюта с таким кодом уже существует");
            }catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DatabaseConnectionException(e);

        }
    }
    @Override
    public Optional<List<Currency>> getList() throws SQLException, DatabaseConnectionException {
        //return this.dbClient.selectForList(SELECT_ALL);
        List<Currency> currencies = new ArrayList<>();
        try(Connection connection = DBConnectionManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
                 ResultSet resultSetItem = statement.executeQuery()) {

                connection.setAutoCommit(true);
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

    @Override
    public Optional<Currency> getByCode(String code) throws SQLException, DatabaseConnectionException{
        List<Currency> currencies = new ArrayList<>();
        try(Connection connection = DBConnectionManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_CODE)) {
                statement.setString(1, code);
                ResultSet resultSetItem = statement.executeQuery();
                connection.setAutoCommit(true);
                while(resultSetItem.next()) {
                    currencies.add(buildCurrency(resultSetItem));
                }
                resultSetItem.close();
                if(currencies.size() > 1) throw new SQLException("В таблице currencies найдено более одной записи по заданным параметрам: code = " + code);
                return currencies.isEmpty() ? Optional.empty() : Optional.of(currencies.get(0));
            } catch(SQLException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DatabaseConnectionException(e);
        }
    }
}
