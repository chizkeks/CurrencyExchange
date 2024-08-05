package db;

import exceptions.DatabaseConnectionException;
import model.Currency;
import model.ExchangeRate;
import utils.DBConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDbClient {
    public ExchangeRateDbClient() {}
    public void run(String query) throws SQLException, DatabaseConnectionException
    {
        try(Connection connection = DBConnectionManager.getConnection();) {
            try (Statement statement = connection.createStatement();) {
                connection.setAutoCommit(true);
                statement.execute(query);
            } catch(SQLException e) {
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

    public Optional<List<ExchangeRate>> selectForList(String query) //throws SQLException, DatabaseConnectionException
    {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try(Connection connection = DBConnectionManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSetItem = statement.executeQuery(query);) {

            connection.setAutoCommit(true);
            while(resultSetItem.next()) {
                exchangeRates.add(new ExchangeRate(resultSetItem.getLong("exchange_rate_id"),
                        new Currency(resultSetItem.getLong("basecur_id"), resultSetItem.getString("basecur_fullname"), resultSetItem.getString("basecur_code"), resultSetItem.getString("basecur_sign")),
                        new Currency(resultSetItem.getLong("targetcur_id"), resultSetItem.getString("targetcur_fullname"), resultSetItem.getString("targetcur_code"), resultSetItem.getString("targetcur_sign")),
                        resultSetItem.getDouble("rate")));
            }
            return Optional.of(exchangeRates);
        } catch(SQLException e) {
           e.printStackTrace();
            System.out.println(e.getMessage());
           return Optional.empty();
        }
    }
}
