package com.hmydk.aicode.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@State(
        name = "com.hmydk.aicode.config.AICodeToolSetSettings",
        storages = {@Storage("AICodeToolSetSettings.xml")}
)
public class AICodeToolSetSettings implements PersistentStateComponent<AICodeToolSetSettings> {
    // 通用配置
    private String aiModel = "Gemini";
    private String apiKey = "";

    // 特定工具配置
    private final Map<String, ToolConfig> toolConfigs = new HashMap<>();

    public static AICodeToolSetSettings getInstance() {
        return ApplicationManager.getApplication().getService(AICodeToolSetSettings.class);
    }

    @Nullable
    @Override
    public AICodeToolSetSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AICodeToolSetSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    // Getters and setters for common configurations
    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    // Methods for managing tool-specific configurations
    public ToolConfig getToolConfig(String toolName) {
        return toolConfigs.computeIfAbsent(toolName, k -> new ToolConfig());
    }

    public void setToolConfig(String toolName, ToolConfig config) {
        toolConfigs.put(toolName, config);
    }

    // Inner class for tool-specific configurations
    public static class ToolConfig {
        private String language = "English";
        private String customPrompt = "";

        // Getters and setters for ToolConfig
        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getCustomPrompt() {
            return customPrompt;
        }

        public void setCustomPrompt(String customPrompt) {
            this.customPrompt = customPrompt;
        }

        @Override
        public String toString() {
            return "ToolConfig{" +
                    "customPrompt='" + customPrompt + '\'' +
                    ", language='" + language + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AICodeToolSetSettings{" +
                "aiModel='" + aiModel + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", toolConfigs=" + toolConfigs +
                '}';
    }
}