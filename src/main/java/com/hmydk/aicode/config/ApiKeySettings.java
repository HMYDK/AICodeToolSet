package com.hmydk.aicode.config;

import com.hmydk.aicode.prompt.GenerateGItMessagePrompt;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "com.hmydk.aicode.config.ApiKeySettings",
        storages = {@Storage("AICodeToolSetSettings.xml")}
)
public class ApiKeySettings implements PersistentStateComponent<ApiKeySettings> {
    private String aiModel = "Gemini";
    private String apiKey = "";
    private String language = "English";

    public static ApiKeySettings getInstance() {
        return ApplicationManager.getApplication().getService(ApiKeySettings.class);
    }

    private String customPrompt = GenerateGItMessagePrompt.DEFAULT_PROMPT;

    public String getCustomPrompt() {
        return customPrompt;
    }

    public void setCustomPrompt(String customPrompt) {
        this.customPrompt = customPrompt;
    }

    @Nullable
    @Override
    public ApiKeySettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ApiKeySettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}