package dev.folomkin.backend.repository;

import dev.folomkin.backend.model.ConversionResultDto;
import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.util.DatabaseUtil;
import jakarta.servlet.ServletContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;

import java.sql.PreparedStatement;

public class CurrencyExchangeRepository {

    private final ServletContext context;


    public CurrencyExchangeRepository(ServletContext context) {
        this.context = context;
    }

    public ConversionResultDto calcCurrencyExchange(String fromCode, String toCode, BigDecimal amount) {

        fromCode = fromCode.toUpperCase();
        toCode = toCode.toUpperCase();



        String sql = """
                SELECT
                    er.rate,
                    base.id AS base_id,
                    base.name AS base_name,
                    base.code AS base_code,
                    base.rub_rate as base_rub_rate,
                    base.sign AS base_sign,
                    target.id AS target_id,
                    target.name AS target_name,
                    target.code AS target_code,
                    target.rub_rate as target_rub_rate,
                    target.sign AS target_sign
                FROM exchange_rates er
                JOIN currencies base ON er.base_currency_id = base.id
                JOIN currencies target ON er.target_currency_id = target.id
                WHERE base.code = ? AND target.code = ?
                """;

        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fromCode);
            pstmt.setString(2, toCode);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    BigDecimal rate = rs.getBigDecimal("rate");

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

                    BigDecimal convertedAmount = amount.multiply(rate);

                    // Формируем ответ
                    ConversionResultDto result = new ConversionResultDto(
                            base,
                            target,
                            rate,
                            amount,
                            convertedAmount
                    );

//                    return Response.ok(result).build();
                    return result;

                } else {
                    // Прямой курс не найден — ищем обратный и рассчитываем
                    return handleReverseRate(fromCode, toCode, amount);
//                    return handleReverseRate(fromCode, toCode, amount);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка базы данных");
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity("{\"error\": \"Ошибка базы данных\"}")
//                    .build();
        }
    }

    private ConversionResultDto handleReverseRate(String fromCode, String toCode, BigDecimal amount) throws SQLException {

        String reverseSql = """
                SELECT er.rate,
                       base.id AS base_id,
                       base.name AS base_name,
                       base.code AS base_code,
                       base.rub_rate as base_rub_rate,
                       base.sign AS base_sign,
                       target.id AS target_id,
                       target.name AS target_name,
                       target.code AS target_code,
                       target.rub_rate as target_rub_rate,
                       target.sign AS target_sign
                FROM exchange_rates er
                JOIN currencies base ON er.base_currency_id = base.id
                JOIN currencies target ON er.target_currency_id = target.id
                WHERE base.code = ? AND target.code = ?
                """;

        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(reverseSql)) {

            pstmt.setString(1, toCode);   // ищем пару to → from
            pstmt.setString(2, fromCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    BigDecimal reverseRate = rs.getBigDecimal("rate");
                    BigDecimal directRate = BigDecimal.ONE.divide(reverseRate, 10, RoundingMode.HALF_UP);

                    Currency base = new Currency(
                            rs.getLong("target_id"),  // теперь from — это бывшая target
                            rs.getString("target_name"),
                            rs.getString("target_code"),
                            rs.getBigDecimal("target_rub_rate"),
                            rs.getString("target_sign")
                    );

                    Currency target = new Currency(
                            rs.getLong("base_id"),
                            rs.getString("base_name"),
                            rs.getString("base_code"),
                            rs.getBigDecimal("base_rub_rate"),
                            rs.getString("base_sign")
                    );

                    BigDecimal convertedAmount = amount.multiply(directRate);

                    ConversionResultDto result = new ConversionResultDto(
                            base,
                            target,
                            directRate,
                            amount,
                            convertedAmount
                    );

                    return result;
//                    return Response.ok(result).build();


                } else {
                    throw new RuntimeException("Курс для пары " + fromCode + "/" + toCode + " не найден");
//                    return Response.status(Response.Status.NOT_FOUND)
//                            .entity("{\"error\": \"Курс для пары " + fromCode + "/" + toCode + " не найден\"}")
//                            .build();
                }


            }
        }

    }
}
