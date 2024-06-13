package dao;

import model.Currency;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDbClient {
    private final DataSource dataSource;
    public CurrencyDbClient(DataSource dataSource) {this.dataSource = dataSource;}
    public boolean run(String query){
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();) {
            connection.setAutoCommit(true);
            statement.execute(query);
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public Optional<List<Currency>> selectForList(String query) {
        List<Currency> currencies = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSetItem = statement.executeQuery(query);) {

            connection.setAutoCommit(true);
            while(resultSetItem.next()) {
                currencies.add(new Currency(resultSetItem.getLong("id"), resultSetItem.getString("code"), resultSetItem.getString("fullname"), resultSetItem.getString("sign")));
            }
            return Optional.of(currencies);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
