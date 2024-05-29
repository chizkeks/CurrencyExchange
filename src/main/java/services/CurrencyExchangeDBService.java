package services;

import model.Currency;
import model.ExchangeRate;
import servlets.CurrenciesServlet;

import javax.swing.text.html.Option;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyExchangeDBService {

    private String DATABASE_URL;

    public CurrencyExchangeDBService() {
        this.DATABASE_URL = "jdbc:sqlite:" + CurrenciesServlet.class.getClassLoader().getResource("db/currency_exchange_db");
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //DONE
    public Optional<List<Currency>> getAllCurrencies() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)){

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from currencies");
            List<Currency> currencyList = new ArrayList<>();
            while (rs.next()) {
                currencyList.add(new Currency(rs.getLong("id"), rs.getString("code"), rs.getString("fullname"), rs.getString("sign")));
            }
            return Optional.of(currencyList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    //DONE
    public Optional<Currency> getCurrencyByCode(String code) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)){

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM currencies WHERE code = ?");
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return Optional.of(new Currency(rs.getLong("id"), rs.getString("code"), rs.getString("fullname"), rs.getString("sign")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    //DONE
    public boolean addNewCurrency(Currency currency) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)){

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO currencies VALUES(NULL, ?, ?, ?)");
            stmt.setString(1, currency.getCode());
            stmt.setString(2, currency.getFullName());
            stmt.setString(3, currency.getSign());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Optional<List<ExchangeRate>> getAllExchangeRates() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT er.id as exchange_rate_id, c1.id as basecur_id, c1.fullname as basecur_fullname," +
                    " c1.code as basecur_code, c1.sign as basecur_sign," +
                    "c2.id as targetcur_id, c2.fullname as targetcur_fullname, " +
                    "c2.code as targetcur_code, c2.sign as targetcur_sign, er.rate " +
                    "FROM exchange_rates er INNER JOIN currencies c1 on er.basecurrencyid = c1.id " +
                    "INNER JOIN currencies c2 on er.targetcurrencyid = c2.id;");

            List<ExchangeRate> exchangeRatesList = new ArrayList<>();
            while (rs.next()) {
                exchangeRatesList.add(new ExchangeRate(rs.getLong("exchange_rate_id"),
                        new Currency(rs.getLong("basecur_id"), rs.getString("basecur_fullname"), rs.getString("basecur_code"), rs.getString("basecur_sign")),
                        new Currency(rs.getLong("targetcur_id"), rs.getString("targetcur_fullname"), rs.getString("targetcur_code"), rs.getString("targetcur_sign")),
                        rs.getDouble("rate")));
            }
            return Optional.of(exchangeRatesList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<ExchangeRate> getExchangeRateByCurrencyPair (String curCode1, String curCode2) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)){
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM exchange_rates WHERE basecurrency = ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
