package dev.folomkin.backend.repository;

import dev.folomkin.backend.exception.NotFoundException;
import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.util.DatabaseUtil;
import jakarta.servlet.ServletContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesRepository {

    private final ServletContext context;

    public CurrenciesRepository(ServletContext context) {
        this.context = context;
    }

    public List<Currency> findAll() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT id, code, name, rub_rate, sign FROM currencies";

        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                currencies.add(mapRowToCurrency(rs));
            }
        }
        return currencies;
    }


    public Currency findByCode(String code) throws SQLException {
        String sql = "SELECT id, code, name, rub_rate, sign FROM currencies WHERE code = ?";
        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCurrency(rs);
                } else {
                    throw new NotFoundException("Валюта с кодом " + code + " не найдена");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Ошибка БД");
        }
    }


    /**
     * -> Создание валюты
     */
    public Currency createCurrency(String name, String code, BigDecimal rub_rate, String sign) throws SQLException {
        String sql = "INSERT INTO currencies (name, code, rub_rate, sign) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name.trim());
            pstmt.setString(2, code.trim().toUpperCase());
            pstmt.setBigDecimal(3, rub_rate);
            pstmt.setString(4, sign.trim());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    Currency currency = new Currency();
                    currency.setId(keys.getLong(1));
                    currency.setName(name.trim());
                    currency.setCode(code.trim().toUpperCase());
                    currency.setRub_rate(rub_rate);
                    currency.setSign(sign.trim());
                    return currency;
                }
            }
        }
        throw new SQLException("Не удалось создать валюту");
    }


    /**
     * -> Маппер ResultSet в объект валюты
     */
    private Currency mapRowToCurrency(ResultSet rs) throws SQLException {
        BigDecimal rate = new BigDecimal(rs.getBigDecimal("rub_rate").doubleValue());
        BigDecimal rounded = rate.setScale(2, RoundingMode.HALF_UP);
        Currency currency = new Currency();
        currency.setId(rs.getLong("id"));
        currency.setCode(rs.getString("code"));
        currency.setName(rs.getString("name"));
        currency.setRub_rate(rounded);
        currency.setSign(rs.getString("sign"));
        return currency;
    }
}
