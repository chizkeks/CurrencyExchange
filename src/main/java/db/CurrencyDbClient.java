package db;

import exceptions.DatabaseConnectionException;
import model.Currency;
import org.sqlite.SQLiteException;
import utils.DBConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDbClient {
    public CurrencyDbClient() {}
    public void run(String query) throws SQLException, DatabaseConnectionException, SQLiteException
    {
        try(Connection connection = DBConnectionManager.getConnection()) {
            try(Statement statement = connection.createStatement()) {
                connection.setAutoCommit(true);
                statement.execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                throw new SQLException(e);
            }
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new DatabaseConnectionException(e);

        }
    }

    public Optional<List<Currency>> selectForList(String query) throws SQLException, DatabaseConnectionException
    {
        List<Currency> currencies = new ArrayList<>();
        try(Connection connection = DBConnectionManager.getConnection()) {
            try (Statement statement = connection.createStatement();
                 ResultSet resultSetItem = statement.executeQuery(query)) {
                connection.setAutoCommit(true);
                while(resultSetItem.next()) {
                    currencies.add(new Currency(resultSetItem.getLong("id"),
                            resultSetItem.getString("code"),
                            resultSetItem.getString("fullname"),
                            resultSetItem.getString("sign")));
                }
                return Optional.of(currencies);
            } catch(SQLException e) {
                //throw new SQLException(e);
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new DatabaseConnectionException(e);
        }
        return Optional.empty();
    }
}
