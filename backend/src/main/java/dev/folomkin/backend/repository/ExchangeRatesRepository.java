package dev.folomkin.backend.repository;

import dev.folomkin.backend.exception.AlreadyExistsException;
import dev.folomkin.backend.exception.NotFoundException;
import dev.folomkin.backend.model.ConversionResultDto;
import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.model.ExchangeRate;
import dev.folomkin.backend.util.DatabaseUtil;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
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


    public ExchangeRate findByCodes(String baseCode, String targetCode) {
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
                WHERE base.code = ? AND target.code = ?
                """;

        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, baseCode);
            pstmt.setString(2, targetCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    // Возвращаем один объект (не массив!)
                    return mapToExchangeRate(rs);
                } else {
                    throw new NotFoundException("Обменный курс с валютной парой " + baseCode + "/" + targetCode + " не найден");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public ExchangeRate save(String baseCode, String targetCode) {
        System.out.printf("BASECODE:  " + baseCode);
        baseCode = baseCode.toUpperCase();
        targetCode = targetCode.toUpperCase();

        String sqlFindCurrencies = """
                SELECT id, name, code, rub_rate, sign FROM currencies
                WHERE code IN (?, ?)
                ORDER BY code = ? DESC, code = ? DESC
                """;

        String sqlInsertRate = """
                INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = DatabaseUtil.getConnection(context)) {
            // Ищем обе валюты по коду
            Long baseId = null, targetId = null;
            Currency baseCurrency = null, targetCurrency = null;

            try (PreparedStatement pstmt = conn.prepareStatement(sqlFindCurrencies)) {

                pstmt.setString(1, baseCode);
                pstmt.setString(2, targetCode);
                pstmt.setString(3, baseCode);
                pstmt.setString(4, targetCode);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String code = rs.getString("code");
                        if (code.equals(baseCode)) {
                            baseId = rs.getLong("id");
                            baseCurrency = new Currency(
                                    baseId,
                                    rs.getString("name"),
                                    code,
                                    rs.getBigDecimal("rub_rate"),
                                    rs.getString("sign")
                            );
                        } else if (code.equals(targetCode)) {
                            targetId = rs.getLong("id");
                            targetCurrency = new Currency(
                                    targetId,
                                    rs.getString("name"),
                                    code,
                                    rs.getBigDecimal("rub_rate"),
                                    rs.getString("sign")
                            );
                        }
                    }
                }
            }

//            // Проверяем, найдены ли обе валюты
            if (baseCurrency == null) {
                throw new NotFoundException("Базовая валюта с кодом " + baseCode + " не найдена");
            }
            if (targetCurrency == null) {
                throw new NotFoundException("Целевая валюта с кодом " + targetCode + " не найдена");
            }
// Проверяем, нет ли уже такой пары (опционально, но рекомендуется)
            String checkDuplicate = """
                    SELECT 1 FROM exchange_rates
                    WHERE base_currency_id = ? AND target_currency_id = ?
                    """;
            try (PreparedStatement check = conn.prepareStatement(checkDuplicate)) {
                check.setLong(1, baseId);
                check.setLong(2, targetId);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        throw new AlreadyExistsException("Пара " + baseCode + "/" + targetCode + " уже существует");
                    }
                }
            }

            // Вставляем новую пару
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsertRate, Statement.RETURN_GENERATED_KEYS)) {

                assert baseCurrency != null;
                assert targetCurrency != null;
                BigDecimal calculateRates = calculateRates(
                        baseCurrency.getRub_rate(),
                        targetCurrency.getRub_rate()
                );

                pstmt.setLong(1, baseId);
                pstmt.setLong(2, targetId);
                pstmt.setBigDecimal(3, calculateRates);
                pstmt.executeUpdate();

                // Получаем сгенерированный ID
                long generatedId;
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedId = keys.getLong(1);
                    } else {
                        throw new SQLException("Не удалось получить ID новой записи");
                    }
                }
                // Формируем ответ
                ExchangeRate exchangeRate = new ExchangeRate(
                        generatedId,
                        baseCurrency,
                        targetCurrency,
                        calculateRates
                );
                return exchangeRate;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка SQL");
        }
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


    /// -> Подсчет кросскурса обмена валют

    public BigDecimal calculateRates(BigDecimal baseCurrencyRate, BigDecimal targetCurrencyRate) throws SQLException {

        if (baseCurrencyRate == null || targetCurrencyRate == null) {
            throw new IllegalArgumentException("Значение валют не должны быть равны нулю");
        }

//      Расчет на примере доллара и евро:
//      Для каждой валюты известно отношение к рублю. Рассчитываем кросс-курс
//        USD/EUR = USD/RUB * RUB/EUR -> Базовая формула: AC = A/B * B/C
//        USD/RUB = 79.7296 RUB -> baseCurrencyRate
//        EUR/RUB = 93.5626 RUB -> targetCurrencyRate
//        RUB/USD = 1/79.7296 = 0.0125
//        RUB/EUR = 1/93.5626 = 0.0107
//        EUR/USD = 1.16
//        USD/EUR = 79.7296 * 0.0107 = 0.8531

        BigDecimal rub = new BigDecimal(1);
        BigDecimal roundedResult = rub.divide(targetCurrencyRate, 4, RoundingMode.HALF_UP);

        return baseCurrencyRate.multiply(roundedResult);
    }


    /// -> Конвертация валют
    public ConversionResultDto convertCurrency(String fromCode, String toCode, BigDecimal amount) {

        fromCode = fromCode.trim().toUpperCase();
        toCode = toCode.trim().toUpperCase();

//        findByCodes(fromCode, toCode);
//        findByCodes(toCode, fromCode);


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
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка базы данных");
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
                    throw new NotFoundException("Курс для пары " + fromCode + "/" + toCode + " не найден");
//                    return Response.status(Response.Status.NOT_FOUND)
//                            .entity("{\"error\": \"Курс для пары " + fromCode + "/" + toCode + " не найден\"}")
//                            .build();
                }
            }
        }

    }
}
