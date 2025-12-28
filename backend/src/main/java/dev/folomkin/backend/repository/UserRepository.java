package dev.folomkin.backend.repository;

import dev.folomkin.backend.model.User;
import dev.folomkin.backend.util.DatabaseUtil;
import jakarta.servlet.ServletContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final ServletContext context;

    public UserRepository(ServletContext context) {
        this.context = context;
    }

    public User save(String name, String email) throws SQLException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name.trim());
            pstmt.setString(2, email.trim().toLowerCase());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    User user = new User();
                    user.setId(keys.getLong(1));
                    user.setName(name.trim());
                    user.setEmail(email.trim().toLowerCase());
                    return user;
                }
            }
        }
        throw new SQLException("Не удалось создать пользователя");
    }


    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email FROM users WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.toLowerCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email FROM users";

        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        }
        return users;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        return user;
    }
}
