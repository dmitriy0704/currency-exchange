// src/main/java/dev/folomkin/backend/resources/UserResource.java
package dev.folomkin.backend.resources;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import dev.folomkin.backend.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static dev.folomkin.backend.util.DatabaseUtil.*;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Context
    ServletContext context;  // Автоматически注入 Jersey

    String INSERT_USER_QUERY = "INSERT INTO users (name, email) VALUES (?, ?)";
    String CHECK_USER = "SELECT id FROM users WHERE email = ?";

    @POST
    public Response createUser(User user) {

        // Проверка обязательных полей (опционально)
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Email обязателен\"}")
                    .build();
        }


        try (Connection conn = getConnection(context)) {
            // 1. Проверяем, существует ли уже пользователь с таким email

            try (PreparedStatement checkStmt = conn.prepareStatement(CHECK_USER)) {
                checkStmt.setString(1, user.getEmail());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // Пользователь уже существует → 409 Conflict
                        return Response.status(Response.Status.CONFLICT)  // 409
                                .entity("{\"error\": \"Пользователь с email '" + user.getEmail() + "' уже существует\"}")
                                .build();
                    }
                }
            }


            // 2. Если не существует — вставляем
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_USER_QUERY,
                    Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, user.getName());
                pstmt.setString(2, user.getEmail());
                pstmt.executeUpdate();

                // Получаем сгенерированный ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    }
                }
            }

            // 3. Успешно создано
            return Response.status(Response.Status.CREATED)  // 201
                    .entity(user)
                    .build();


        } catch (SQLException e) {
            e.printStackTrace();
            // Только реальные ошибки БД — 500
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Ошибка базы данных\"}")
                    .build();
        }


    }

    @GET
    public Response getAllUsers() {
        String sql = "SELECT id, name, email FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }

            return Response.ok(users).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Ошибка чтения пользователей").build();
        }
    }
}