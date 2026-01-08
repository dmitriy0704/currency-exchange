package dev.folomkin.backend.repository;

import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.model.ExchangeRate;
import dev.folomkin.backend.util.DatabaseUtil;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesRepository {

    private final ServletContext context;


    public ExchangeRatesRepository(ServletContext context) {
        this.context = context;
    }


    public List<ExchangeRate> findAll() throws SQLException {
        List<ExchangeRate> rates = new ArrayList<>();
        String sql = """
                SELECT
                    er.id AS rate_id,
                    er.rate,
                    base.id AS base_id,
                    base.name AS base_name,
                    base.code AS base_code,
                    base.rub_rate AS base_rub_rate,
                    base.sign AS base_sign,
                    target.id AS target_id,
                    target.name AS target_name,
                    target.code AS target_code,
                    target.rub_rate AS target_rub_rate,
                    target.sign AS target_sign
                FROM exchange_rates er
                JOIN currencies base ON er.base_currency_id = base.id
                JOIN currencies target ON er.target_currency_id = target.id
                """;
        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                rates.add(mapToExchangeRate(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // При ошибке можно бросить WebApplicationException или вернуть пустой список
            throw new WebApplicationException("Ошибка базы данных", Response.Status.INTERNAL_SERVER_ERROR);
        }

        return rates;
    }


    public ExchangeRate mapToExchangeRate(ResultSet rs) throws SQLException {
        Currency base = new Currency(
                rs.getLong("base_id"),
                rs.getString("base_name"),
                rs.getString("base_code"),
                rs.getBigDecimal("base_rub_rate"),
                rs.getString("base_sign")
        );

        Currency target = new Currency(
                rs.getLong("target_id"),
                rs.getString("target_name"),
                rs.getString("target_code"),
                rs.getBigDecimal("target_rub_rate"),
                rs.getString("target_sign")
        );

        ExchangeRate rate = new ExchangeRate(
                rs.getLong("rate_id"),
                base,
                target,
                rs.getBigDecimal("rate")
        );

        return rate;
    }
}
