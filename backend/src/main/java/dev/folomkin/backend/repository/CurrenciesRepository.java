package dev.folomkin.backend.repository;

import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.model.User;
import dev.folomkin.backend.util.DatabaseUtil;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesRepository {

    private final ServletContext context;

    public CurrenciesRepository(ServletContext context) {
        this.context = context;
    }


    public List<Currency> findAll() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT id, code, full_name, rub_rate, sign FROM currencies";

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
        String sql = "SELECT id, code, full_name, rub_rate, sign FROM currencies WHERE code = ?";

        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Валюта найдена — формируем объект

                    return mapRowToCurrency(rs);
//                    return Response.ok(currency).build();  // 200 OK + JSON
                } else {
                    // Не найдена
                    return null;
//                    return Response.status(Response.Status.NOT_FOUND)
//                            .entity("{\"error\": \"Валюта с кодом '" + code + "' не найдена\"}")
//                            .build();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity("{\"error\": \"Ошибка базы данных\"}")
//                    .build();
        }

        return null;

    }


    private Currency mapRowToCurrency(ResultSet rs) throws SQLException {

        Currency currency = new Currency();
        currency.setId(rs.getLong("id"));
        currency.setCode(rs.getString("code"));
        currency.setFull_name(rs.getString("full_name"));
        currency.setRub_rate(rs.getBigDecimal("rub_rate"));
        currency.setSign(rs.getString("sign"));

        return currency;
    }
}
