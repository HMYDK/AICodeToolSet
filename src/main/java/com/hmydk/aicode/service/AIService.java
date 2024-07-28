package com.hmydk.aicode.service;

/**
 * AIService
 *
 * @author hmydk
 */
public interface AIService {

    String generateCommitMessage(String content);

    boolean checkApiKeyIsExists();


    boolean validateConfig(String model, String apiKey, String language);
}
