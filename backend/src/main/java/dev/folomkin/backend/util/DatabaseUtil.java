package dev.folomkin.backend.util;

import jakarta.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    // Получаем соединение из контекста приложения
    public static Connection getConnection(ServletContext context) throws SQLException {
        String url = (String) context.getAttribute("DB_URL");
        if (url == null) {
            throw new IllegalStateException("DB_URL не инициализирован в ServletContext");
        }

        // Class.forName("org.sqlite.JDBC"); // не обязательно повторять каждый раз

        return DriverManager.getConnection(url);
    }

    // Закрываем ресурсы безопасно (удобный метод)
    public static void closeQuietly(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {}
            }
        }
    }
}