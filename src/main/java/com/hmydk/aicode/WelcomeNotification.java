package com.hmydk.aicode;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WelcomeNotification implements ProjectActivity {

    private static final String NOTIFICATION_GROUP_ID = "AICodeToolSet Notifications";
    private static final String PLUGIN_NAME = "AICodeToolSet";
    private static final String WELCOME_TITLE = "Welcome to AICodeToolSet!";
    private static final String WELCOME_CONTENT = "Thank you for installing AICodeToolSet. " +
            "To get started, please configure your API key in the settings.";
    private static final String PLUGIN_VERSION_PROPERTY = "com.hmydk.aicode.version";

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        if (isNewOrUpdatedVersion()) {
            showWelcomeNotification(project);
            updateStoredVersion();
        }
        return Unit.INSTANCE;
    }

    private boolean isNewOrUpdatedVersion() {
        String storedVersion = PropertiesComponent.getInstance().getValue(PLUGIN_VERSION_PROPERTY);
        String currentVersion = getCurrentPluginVersion();
        return storedVersion == null || !storedVersion.equals(currentVersion);
    }

    private void updateStoredVersion() {
        PropertiesComponent.getInstance().setValue(PLUGIN_VERSION_PROPERTY, getCurrentPluginVersion());
    }

    private String getCurrentPluginVersion() {
        return PluginManagerCore.getPlugin(PluginId.getId("com.hmydk.aicode")).getVersion();
    }

    private void showWelcomeNotification(@NotNull Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(NOTIFICATION_GROUP_ID)
                .createNotification(WELCOME_TITLE, WELCOME_CONTENT, NotificationType.INFORMATION)
                .setIcon(null)  // You can set a custom icon here if you have one
                .addAction(new ConfigureAction())
                .notify(project);
    }

    private static class ConfigureAction extends com.intellij.openapi.actionSystem.AnAction {
        ConfigureAction() {
            super("Configure");
        }

        @Override
        public void actionPerformed(@NotNull com.intellij.openapi.actionSystem.AnActionEvent e) {
            com.intellij.openapi.options.ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), PLUGIN_NAME);
        }
    }
}