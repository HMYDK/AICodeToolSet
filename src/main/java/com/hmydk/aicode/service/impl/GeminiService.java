package com.hmydk.aicode.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmydk.aicode.config.ApiKeySettings;
import com.hmydk.aicode.pojo.GeminiRequestBO;
import com.hmydk.aicode.service.AIService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * OpenAIService
 *
 * @author hmydk
 */
public class GeminiService implements AIService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    @Override
    public String generateCommitMessage(String content) {
        String aiResponse;
        try {
            aiResponse = getAIResponse(ApiKeySettings.getInstance().getApiKey(), content);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        log.info("aiResponse is  :\n{}", aiResponse);
        return aiResponse.replaceAll("```", "");
    }

    @Override
    public boolean checkApiKeyIsExists() {
        return !ApiKeySettings.getInstance().getApiKey().isEmpty();
    }

    @Override
    public boolean validateConfig(String model, String apiKey, String language) {
        int statusCode;
        try {
            HttpURLConnection connection = getHttpURLConnection(apiKey, "hi");
            statusCode = connection.getResponseCode();
        } catch (IOException e) {
            return false;
        }
        // 打印状态码
        System.out.println("HTTP Status Code: " + statusCode);
        return statusCode == 200;
    }

    public static String getAIResponse(String apiKey, String textContent) throws Exception {
        HttpURLConnection connection = getHttpURLConnection(apiKey, textContent);

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.toString());
        JsonNode candidates = jsonResponse.path("candidates");
        if (candidates.isArray() && !candidates.isEmpty()) {
            JsonNode firstCandidate = candidates.get(0);
            JsonNode content = firstCandidate.path("content");
            JsonNode parts = content.path("parts");
            if (parts.isArray() && !parts.isEmpty()) {
                JsonNode firstPart = parts.get(0);
                return firstPart.path("text").asText();
            }
        }
        return "sth error when request ai api";
    }

    private static @NotNull HttpURLConnection getHttpURLConnection(String apiKey, String textContent) throws IOException {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;
        GeminiRequestBO geminiRequestBO = new GeminiRequestBO();
        geminiRequestBO.setContents(List.of(new GeminiRequestBO.Content(List.of(new GeminiRequestBO.Part(textContent)))));
        ObjectMapper objectMapper1 = new ObjectMapper();
        String jsonInputString = objectMapper1.writeValueAsString(geminiRequestBO);

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(10000); // 连接超时：10秒
        connection.setReadTimeout(10000); // 读取超时：10秒

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }
}
