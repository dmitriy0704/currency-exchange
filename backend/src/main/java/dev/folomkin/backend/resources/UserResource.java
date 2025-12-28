// src/main/java/dev/folomkin/backend/resources/UserResource.java
package dev.folomkin.backend.resources;

import dev.folomkin.backend.util.DatabaseUtil;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import dev.folomkin.backend.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static dev.folomkin.backend.util.DatabaseUtil.*;

@Path("/users")
public class UserResource {

    @Context
    ServletContext context;  // Автоматически注入 Jersey

    String INSERT_USER_QUERY = "INSERT INTO users (name, email) VALUES (?, ?)";
    String GET_USER_BY_EMAIL = "SELECT id FROM users WHERE email = ?";

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@FormParam("name") String name,
                               @FormParam("email") String email) {

        // Валидация входных данных
        if (name == null || name.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Поле 'name' обязательно\"}")
                    .build();
        }
        if (email == null || email.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Поле 'email' обязательно\"}")
                    .build();
        }

        name = name.trim();
        email = email.trim().toLowerCase();  // рекомендуется хранить email в нижнем регистре

        String checkSql = "SELECT id FROM users WHERE email = ?";
        String insertSql = "INSERT INTO users (name, email) VALUES (?, ?)";


        try (Connection conn = getConnection(context)) {
            // 1. Проверяем, существует ли уже пользователь с таким email

            // 1. Проверка на существование пользователя с таким email
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        return Response.status(Response.Status.CONFLICT)  // 409
                                .entity("{\"error\": \"Пользователь с email '" + email + "' уже существует\"}")
                                .build();
                    }
                }
            }


            // 2. Если не существует — вставляем
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_USER_QUERY,
                    Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("{\"error\": \"Не удалось создать пользователя\"}")
                            .build();
                }


                long generatedId = 0;
                // Получаем сгенерированный ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getLong(1);
                    }
                }

                User createdUser = new User();
                createdUser.setId(generatedId);
                createdUser.setName(name);
                createdUser.setEmail(email);
                return Response.status(201).entity(createdUser).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Только реальные ошибки БД — 500
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Ошибка базы данных\"}")
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        String sql = "SELECT * FROM users";
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

    @GET
    @Path("/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByEmail(@PathParam("email") String email) {
        String sql = "SELECT id, name, email FROM users  WHERE email = ?";

        // Простая валидация (опционально)
        if (email == null || email.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Email не может быть пустым\"}")
                    .build();
        }

        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Пользователь найден
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    // user.setCreatedAt(rs.getTimestamp("created_at")); // если нужно

                    return Response.ok(user).build();  // 200 OK + JSON с пользователем
                } else {
                    // Не найден
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("{\"error\": \"Пользователь с email '" + email + "' не найден\"}")
                            .build();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Ошибка базы данных\"}")
                    .build();
        }
    }
}