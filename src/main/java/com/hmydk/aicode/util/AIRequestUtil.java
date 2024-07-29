package com.hmydk.aicode.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmydk.aicode.pojo.GeminiRequestBO;
import com.hmydk.aicode.prompt.GenerateCodeNamePrompt;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * AIRequestUtil
 *
 * @author hmydk
 */
public class AIRequestUtil {

    public static class Gemini {

        public static String getAIResponse(String apiKey, String language, String textContent) throws Exception {
            textContent = GenerateCodeNamePrompt.getPrompt(language, "Variable", textContent);

            HttpURLConnection connection = getConnection(apiKey, textContent);
            // 读取响应并打印
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // 使用Jackson解析JSON响应
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.toString());
            JsonNode candidates = jsonResponse.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode content = firstCandidate.path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    JsonNode firstPart = parts.get(0);
                    return firstPart.path("text").asText();
                }
            }

            return "";
        }

        public static @NotNull HttpURLConnection getConnection(String apiKey, String textContent) throws IOException {
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
}
