package com.hmydk.aicode.service;


import com.hmydk.aicode.config.AICodeToolSetSettings;
import com.hmydk.aicode.config.ConfigConstant;
import com.hmydk.aicode.prompt.GenerateCodeNamePrompt;
import com.hmydk.aicode.prompt.GenerateGItMessagePrompt;
import com.hmydk.aicode.service.impl.GeminiService;

import java.util.List;

public class AIBusinessService {
    private final GeminiService aiService;

    public AIBusinessService() {
        this.aiService = new GeminiService();
    }

    public boolean checkConfig() {
        return aiService.checkConfig();
    }

    public boolean validateConfig(String model, String apiKey) {
        return aiService.validateConfig(model, apiKey);
    }

    public String generateCommitMessage(String branch, String diff, List<String> gitHistoryMsg) {
        String prompt = GenerateGItMessagePrompt.constructPrompt(diff, branch, gitHistoryMsg);
        return aiService.getPromptResult(prompt);
    }

    public String generateAICodeName(String input) {
        input = GenerateCodeNamePrompt.getPrompt(AICodeToolSetSettings.getInstance().getToolConfig(ConfigConstant.CODE_NAME_SUGGEST).getLanguage(), "Variable", input);
        return aiService.getPromptResult(input);
    }

}
