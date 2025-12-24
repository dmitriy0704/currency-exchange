// src/main/java/dev/folomkin/backend/resources/UserResource.java
package dev.folomkin.backend.resources;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import dev.folomkin.backend.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static dev.folomkin.backend.util.DatabaseUtil.*;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Context
    ServletContext context;  // Автоматически注入 Jersey

    @POST
    public Response createUser(User user) {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";

        try (Connection conn = getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    user.setId(generatedId);  // Обновляем ID в объекте
                }
            }

            return Response.status(Response.Status.CREATED).entity(user).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Ошибка сохранения пользователя").build();
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