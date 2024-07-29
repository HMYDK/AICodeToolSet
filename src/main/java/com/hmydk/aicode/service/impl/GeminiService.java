package com.hmydk.aicode.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmydk.aicode.config.ApiKeySettings;
import com.hmydk.aicode.service.AIService;
import com.hmydk.aicode.util.AIRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

/**
 * OpenAIService
 *
 * @author hmydk
 */
public class GeminiService implements AIService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    @Override
    public String getPromptResult(String fullPrompt) {
        String aiResponse;
        try {
            aiResponse = getAIResponse(ApiKeySettings.getInstance().getApiKey(), fullPrompt);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        log.info("aiResponse is  :\n{}", aiResponse);
        return aiResponse.replaceAll("```", "");
    }

    @Override
    public boolean checkConfig() {
        return !ApiKeySettings.getInstance().getApiKey().isEmpty();
    }

    @Override
    public boolean validateConfig(String model, String apiKey, String language) {
        int statusCode;
        try {
            HttpURLConnection connection = AIRequestUtil.Gemini.getConnection(apiKey, "hi");
            statusCode = connection.getResponseCode();
        } catch (IOException e) {
            return false;
        }
        // 打印状态码
        System.out.println("HTTP Status Code: " + statusCode);
        return statusCode == 200;
    }

    public static String getAIResponse(String apiKey, String textContent) throws Exception {
        HttpURLConnection connection = AIRequestUtil.Gemini.getConnection(apiKey, textContent);

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
}
