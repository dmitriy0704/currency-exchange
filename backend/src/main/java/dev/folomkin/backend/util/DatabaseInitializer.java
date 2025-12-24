package dev.folomkin.backend.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        email TEXT UNIQUE NOT NULL
                    )
                    """);
                    System.out.println("Таблица users создана/проверена.");
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
            e.printStackTrace();  // Полный стек в консоль
            throw new RuntimeException("Критическая ошибка инициализации БД", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Ничего особенного не нужно — SQLite сам закроет файл
    }
}