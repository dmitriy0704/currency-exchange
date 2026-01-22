package dev.folomkin.backend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.sql.*;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    private static final String DB_URL = "jdbc:sqlite:/WEB-INF/data/app.db";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            String dbDirPath = sce.getServletContext().getRealPath("/WEB-INF/data");
            System.out.println("=== ИНИЦИАЛИЗАЦИЯ БД ===");
            System.out.println("getRealPath вернул: " + dbDirPath);

            if (dbDirPath == null) {
                throw new RuntimeException("getRealPath вернулся null — контейнер не поддерживает запись (например, WAR без распаковки)");
            }

            File dbDir = new File(dbDirPath);
            System.out.println("Папка существует? " + dbDir.exists());
            System.out.println("Папка — директория? " + dbDir.isDirectory());
            System.out.println("Можно читать? " + dbDir.canRead());
            System.out.println("Можно писать? " + dbDir.canWrite());

            if (!dbDir.exists()) {
                boolean created = dbDir.mkdirs();
                System.out.println("Попытка создать папку: " + created);
                if (!created) {
                    throw new RuntimeException("Не удалось создать папку " + dbDirPath);
                }
            }

            String dbPath = dbDirPath + "/app.db";
            String url = "jdbc:sqlite:" + dbPath;
            System.out.println("URL для подключения: " + url);

            sce.getServletContext().setAttribute("DB_URL", url);

            // Подключаемся — здесь SQLite должен создать файл
            System.out.println("Пытаемся подключиться к БД...");
            Class.forName("org.sqlite.JDBC");  // Ручная регистрация драйвера
            try (Connection conn = DriverManager.getConnection(url)) {
                System.out.println("Подключение успешно! Файл БД должен быть создан.");

//                try (Statement stmt = conn.createStatement()) {
//                    stmt.execute("""
//                            CREATE TABLE IF NOT EXISTS users (
//                                id INTEGER PRIMARY KEY AUTOINCREMENT,
//                                name TEXT NOT NULL,
//                                email TEXT UNIQUE NOT NULL
//                            )
//                            """);
//                    System.out.println("Таблица users создана/проверена.");
//                }

//                try (Statement stmt = conn.createStatement()) {
//                    stmt.execute("""
//                            CREATE TABLE IF NOT EXISTS currency_rates
//                            (
//                                code     TEXT PRIMARY KEY,
//                                id       TEXT,
//                                num_code TEXT,
//                                nominal  INTEGER,
//                                name     TEXT,
//                                value    REAL,
//                                previous REAL
//                            );
//                            """);
//                    System.out.println("Таблица currency_rates создана/проверена.");
//                }

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("""
                            CREATE TABLE IF NOT EXISTS currencies
                             (
                                 id        integer PRIMARY KEY AUTOINCREMENT,
                                 code      VARCHAR(50) NOT NULL UNIQUE,
                                 name      VARCHAR(50) NOT NULL,
                                 rub_rate  DECIMAL(10, 6) NOT NULL,
                                 sign      VARCHAR(50)
                             );
                            """);
                    System.out.println("Таблица currencies создана/проверена.");
                }
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("""
                            CREATE TABLE IF NOT EXISTS exchange_rates
                               (
                                   id                 INTEGER PRIMARY KEY AUTOINCREMENT,
                                   base_currency_id   INT            NOT NULL,
                                   target_currency_id INT            NOT NULL,
                                   rate               DECIMAL(10, 6) NOT NULL CHECK (rate >= 0),

                                   -- Внешний ключ, связывающий exchangeRates с currencies
                                   CONSTRAINT base_currency
                                       FOREIGN KEY (base_currency_id)
                                           REFERENCES currencies (id)
                                           ON DELETE RESTRICT
                                           ON UPDATE CASCADE,

                                   CONSTRAINT target_currency
                                       FOREIGN KEY (target_currency_id)
                                           REFERENCES currencies (id)
                                           ON DELETE RESTRICT
                                           ON UPDATE CASCADE


                               );
                            """);
                    System.out.println("Таблица exchange_rates создана/проверена.");
                    loadInitialCurrencies(conn);
                }
            }

            // Проверяем наличие файла после подключения
            File dbFile = new File(dbPath);
            System.out.println("Файл БД существует после подключения? " + dbFile.exists());
            if (dbFile.exists()) {
                System.out.println("Размер файла: " + dbFile.length() + " байт");
            }
            System.out.println("=== ИНИЦИАЛИЗАЦИЯ БД ЗАВЕРШЕНА УСПЕШНО ===");
        } catch (Exception e) {
            System.err.println("=== ОШИБКА ИНИЦИАЛИЗАЦИИ БД ===");
            e.printStackTrace();
            throw new RuntimeException("Критическая ошибка инициализации БД", e);
        }
    }


    private void loadInitialCurrencies(Connection conn) throws Exception {
        // Пример: API ЦБ РФ за сегодня
        String apiUrl = "https://www.cbr-xml-daily.ru/daily_json.js";

        // Делаем HTTP-запрос
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Не удалось загрузить курсы: " + response.code());
                return;
            }

            String json = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode valute = root.path("Valute");
            String date = root.path("Date").asText();

            String sql = """
                    INSERT INTO currencies (code, name, rub_rate, sign)
                    VALUES (?, ?, ?, ?)
                    ON CONFLICT(code) DO UPDATE SET
                        name = excluded.name,
                        rub_rate = excluded.rub_rate,
                        sign = excluded.sign
                    """;

            String sqlInsert = """
                    INSERT OR REPLACE INTO currencies (code, name, rub_rate, sign) VALUES (?, ?, ?, ?)
                    """;

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                // Примеры популярных валют
                String[] codes = {"USD", "EUR", "GBP", "CNY", "JPY", "AUD", "AZN", "BYN", "BGN", "DKK", "AED", "INR", "IRR", "KZT", "CAD", "NOK", "PLN", "TRY"};
                String[] signs = {"$", "€", "£", "¥", "¥", "AUD", "AZN", "BYN", "BGN", "DKK", "AED", "INR", "IRR", "KZT", "CAD", "NOK", "PLN", "TRY"};

                for (int i = 0; i < codes.length; i++) {
                    String code = codes[i];
                    JsonNode currency = valute.path(code);
                    if (currency.isMissingNode()) continue;

                    String name = currency.path("Name").asText();
                    double rub_rate = currency.path("Value").asDouble();
                    String sign = signs[i];

                    pstmt.setString(1, code);
                    pstmt.setString(2, name);
                    pstmt.setDouble(3, rub_rate);
                    pstmt.setString(4, sign);
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                System.out.println("Курсы валют успешно загружены из " + apiUrl);
            }
        }
    }


    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}