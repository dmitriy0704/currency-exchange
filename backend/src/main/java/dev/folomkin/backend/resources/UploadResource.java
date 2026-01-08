package dev.folomkin.backend.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.folomkin.backend.repository.UserRepository;
import dev.folomkin.backend.service.UserService;
import dev.folomkin.backend.util.DatabaseUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Path("/currencies-upload")
public class UploadResource {

    @Context
    private ServletContext context;
    private final OkHttpClient client = new OkHttpClient();

    @GET
    @Produces("application/json")
    public void getCurrencies() throws SQLException, IOException, InterruptedException, URISyntaxException {
        try {
            String json = fetchJson("https://www.cbr-xml-daily.ru/daily_json.js");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode valuteNode = root.path("Valute");
            valuteNode.fields().forEachRemaining(entry -> {
                String code = entry.getKey(); // "USD", "EUR"
                JsonNode currencyNode = entry.getValue();
                try {
                    saveCurrencyFromNode(currencyNode, code);
                    System.out.println("Сохранена валюта: " + code);
                } catch (SQLException e) {
                    System.err.println("Ошибка при сохранении " + code + ": " + e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String fetchJson(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new RuntimeException("HTTP error: " + response.code());
            }
        }
    }

    public void saveCurrencyFromNode(JsonNode currencyNode, String code) throws SQLException {
        String sql = """
                INSERT INTO currencies (code, name, rub_curr) VALUES (?, ?, ?)
                """;
        try (Connection conn = DatabaseUtil.getConnection(context);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, code); // CharCode (USD, EUR)
            pstmt.setString(2, currencyNode.path("Name").asText());
            pstmt.setString(3, String.valueOf(currencyNode.path("Value").asDouble()));
            pstmt.executeUpdate();
        }
    }
}