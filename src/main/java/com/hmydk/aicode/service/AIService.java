package com.hmydk.aicode.service;

/**
 * AIService
 *
 * @author hmydk
 */
public interface AIService {

    /**
     * @param fullPrompt 拼装好的prompt
     * @return git message
     */
    String getPromptResult(String fullPrompt);

    /**
     * @return 检查结果
     */
    boolean checkConfig();

    /**
     * 配置页面：校验配置是否正确
     *
     * @param model
     * @param apiKey
     * @param language
     * @return
     */
    boolean validateConfig(String model, String apiKey, String language);
}
