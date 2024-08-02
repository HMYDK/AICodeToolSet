package com.hmydk.aicode.config;

import com.hmydk.aicode.prompt.GenerateCodeNamePrompt;
import com.hmydk.aicode.prompt.GenerateGItMessagePrompt;
import com.hmydk.aicode.service.AIBusinessService;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class AICodeToolSetConfigurable implements Configurable {
    private final AIBusinessService AIBusinessService = new AIBusinessService();

    // Common configuration components
    private ComboBox<String> modelComboBox;
    private JBPasswordField apiKeyField;
    private JCheckBox showPasswordCheckBox;
    //校验配置的模型是否可以访问
    private JButton verifyModelConfigButton;

    // Git commit message tool specific components
    private ComboBox<String> gitCommitMessageLanguageComboBox;
    private JBTextArea gitCommitMessageCustomPromptArea;
    private JButton gitCommitMessageResetPromptButton;
    private JButton gitCommitMessageValidatePromptButton;

    // Code name suggest tool specific components
    private ComboBox<String> codeNameSuggestLanguageComboBox;
    private JBTextArea codeNameSuggestCustomPromptArea;
    private JButton codeNameSuggestResetPromptButton;
    private JButton codeNameSuggestValidatePromptButton;

    @Nullable
    @Override
    public JComponent createComponent() {
        initializeComponents();

        JPanel mainPanel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Common Settings", createCommonSettingsPanel());
        tabbedPane.addTab("Git Commit Message", createGitCommitMessagePanel());
        tabbedPane.addTab("Code Name Suggest", createCodeNameSuggestPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
//        mainPanel.add(createHintLabel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    private void initializeComponents() {
        modelComboBox = new ComboBox<>(new String[]{"Gemini"});
        apiKeyField = new JBPasswordField();
        showPasswordCheckBox = new JCheckBox("Show Key");
        verifyModelConfigButton = new JButton("Check Connection");

        gitCommitMessageLanguageComboBox = new ComboBox<>(ConfigConstant.SUPPORTED_LANGUAGES);
        codeNameSuggestLanguageComboBox = new ComboBox<>(ConfigConstant.SUPPORTED_LANGUAGES);
        gitCommitMessageCustomPromptArea = new JBTextArea(5, 30);
        codeNameSuggestCustomPromptArea = new JBTextArea(5, 30);
        gitCommitMessageResetPromptButton = new JButton("Reset Prompt");
        codeNameSuggestResetPromptButton = new JButton("Reset Prompt");
        gitCommitMessageValidatePromptButton = new JButton("Validate Prompt");
        codeNameSuggestValidatePromptButton = new JButton("Validate Prompt");

        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());
        verifyModelConfigButton.addActionListener(e -> verifyConfig(true));
        gitCommitMessageResetPromptButton.addActionListener(e -> gitCommitMessageCustomPromptArea.setText(GenerateGItMessagePrompt.DEFAULT_PROMPT));
        codeNameSuggestResetPromptButton.addActionListener(e -> codeNameSuggestCustomPromptArea.setText(GenerateCodeNamePrompt.DEFAULT_PROMPT));
        gitCommitMessageValidatePromptButton.addActionListener(e -> GenerateGItMessagePrompt.validatePrompt(gitCommitMessageCustomPromptArea.getText()));
        codeNameSuggestValidatePromptButton.addActionListener(e -> GenerateCodeNamePrompt.validatePrompt(codeNameSuggestCustomPromptArea.getText()));
    }

    private JPanel createCommonSettingsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Add AI model selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JBLabel("LLM Client"), gbc);
        gbc.gridx = 1;
        formPanel.add(createSizedComboBox(modelComboBox), gbc);

        // Add API Key section
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JBLabel("API Key"), gbc);
        gbc.gridx = 1;
        formPanel.add(createApiKeyPanel(), gbc);

        // Add verify button (now in a separate panel)
        JPanel verifyButtonPanel = new JPanel(new BorderLayout());
        verifyModelConfigButton.setPreferredSize(new Dimension(130, 30)); // Set a preferred size
        verifyButtonPanel.add(verifyModelConfigButton, BorderLayout.EAST);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(verifyButtonPanel, gbc);

        // Add hint label
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(createHintLabel(), gbc);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        return mainPanel;
    }

    private JPanel createGitCommitMessagePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addFormComponent(panel, gbc, "Language", createSizedComboBox(gitCommitMessageLanguageComboBox));

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(createCustomPromptPanel(gitCommitMessageCustomPromptArea), gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        panel.add(createButtonsPanel(gitCommitMessageResetPromptButton, gitCommitMessageValidatePromptButton), gbc);

        return panel;
    }

    private JPanel createCodeNameSuggestPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addFormComponent(panel, gbc, "Language", createSizedComboBox(codeNameSuggestLanguageComboBox));

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(createCustomPromptPanel(codeNameSuggestCustomPromptArea), gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        panel.add(createButtonsPanel(codeNameSuggestResetPromptButton, codeNameSuggestValidatePromptButton), gbc);

        return panel;
    }

    private void addFormComponent(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JBLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
    }


    /**
     * 创建一个自定义提示面板，包含一个文本区域。
     *
     * @param jbTextArea 用于显示和编辑文本的JBTextArea对象。
     * @return 包含JBTextArea的JPanel面板，设置有边框和滚动条。
     */
    private JPanel createCustomPromptPanel(JBTextArea jbTextArea) {
        // 创建一个使用BorderLayout布局的JPanel面板
        JPanel panel = new JPanel(new BorderLayout());
        // 为面板设置标题边框
        panel.setBorder(BorderFactory.createTitledBorder("Custom Prompt"));

        // 设置文本区域的行包装和单词包装，以便文本可以在行内换行
        jbTextArea.setLineWrap(true);
        jbTextArea.setWrapStyleWord(true);
        // 为文本区域设置内边距，提供更好的阅读体验
        jbTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 将文本区域添加到滚动面板中，并将其添加到面板的中心位置
        panel.add(new JScrollPane(jbTextArea), BorderLayout.CENTER);

        return panel;
    }


    /**
     * 创建一个包含重置和验证按钮的面板。
     *
     * @param resetPromptButton    用于重置操作的按钮，点击后触发重置行为。
     * @param validatePromptButton 用于验证操作的按钮，点击后触发验证行为。
     * @return 返回一个包含重置和验证按钮的面板，按钮从右到左排列。
     */
    private JPanel createButtonsPanel(JButton resetPromptButton, JButton validatePromptButton) {
        // 创建一个面板，使用FlowLayout布局管理器，按钮将从右到左排列。
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // 在面板上添加重置按钮。
        panel.add(resetPromptButton);
        // 在面板上添加验证按钮。
        panel.add(validatePromptButton);
        // 返回包含按钮的面板。
        return panel;
    }


    private JLabel createHintLabel() {
        JLabel label = new JLabel("<html><li><a href=\"https://aistudio.google.com/app/apikey\">Visit AI Studio API Key Page to get <strong>gemini</strong> api key</a></li></html>");
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BrowserUtil.browse("https://aistudio.google.com/app/apikey");
            }
        });
        return label;
    }

    private JPanel createApiKeyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(apiKeyField, BorderLayout.CENTER);
        panel.add(showPasswordCheckBox, BorderLayout.EAST);
        return panel;
    }

    private void togglePasswordVisibility() {
        apiKeyField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '•');
    }

    private JComboBox<String> createSizedComboBox(JComboBox<String> comboBox) {
        Dimension dimension = new Dimension(50, comboBox.getPreferredSize().height);
        comboBox.setPreferredSize(dimension);
        comboBox.setMaximumSize(dimension);
        comboBox.setMinimumSize(dimension);
        return comboBox;
    }

    private void verifyConfig(boolean showSuccess) {
        String model = (String) modelComboBox.getSelectedItem();
        String apiKey = String.valueOf(apiKeyField.getPassword());

        // 创建一个模态对话框来显示加载消息
        Window parentWindow = SwingUtilities.getWindowAncestor(verifyModelConfigButton);
        JDialog loadingDialog = new JDialog(parentWindow, "Verifying", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Checking configuration..."), BorderLayout.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(progressBar, BorderLayout.SOUTH);
        loadingDialog.add(panel);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(parentWindow);

        // 在新线程中执行验证，以避免UI冻结
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return AIBusinessService.validateConfig(model, apiKey);
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    boolean isValid = get();
                    if (isValid) {
                        if (showSuccess) {
                            JOptionPane.showMessageDialog(parentWindow, "Configuration is valid!", "Verification Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(parentWindow, "Configuration is invalid. Please check your settings.", "Verification Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(parentWindow, "An error occurred during verification: " + e.getMessage(), "Verification Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
        loadingDialog.setVisible(true);
    }

    @Override
    public boolean isModified() {
        AICodeToolSetSettings settings = AICodeToolSetSettings.getInstance();
        AICodeToolSetSettings.ToolConfig gitConfig = settings.getToolConfig(ConfigConstant.GIT_COMMIT_MESSAGE);
        AICodeToolSetSettings.ToolConfig codeNameSuggestConfig = settings.getToolConfig(ConfigConstant.CODE_NAME_SUGGEST);

        return !Objects.equals(modelComboBox.getSelectedItem(), settings.getAiModel())
                || !String.valueOf(apiKeyField.getPassword()).equals(settings.getApiKey())
                || !Objects.equals(gitCommitMessageLanguageComboBox.getSelectedItem(), gitConfig.getLanguage())
                || !Objects.equals(gitCommitMessageCustomPromptArea.getText(), gitConfig.getCustomPrompt())
                || !Objects.equals(codeNameSuggestLanguageComboBox.getSelectedItem(), codeNameSuggestConfig.getLanguage())
                || !Objects.equals(codeNameSuggestCustomPromptArea.getText(), codeNameSuggestConfig.getCustomPrompt());
    }

    @Override
    public void apply() throws ConfigurationException {
        // 验证api配置
        verifyConfig(false);
        //todo 验证各个工具自己的配置

        AICodeToolSetSettings settings = AICodeToolSetSettings.getInstance();
        settings.setAiModel((String) modelComboBox.getSelectedItem());
        settings.setApiKey(String.valueOf(apiKeyField.getPassword()));

        //git commit message
        AICodeToolSetSettings.ToolConfig gitConfig = settings.getToolConfig(ConfigConstant.GIT_COMMIT_MESSAGE);
        gitConfig.setLanguage((String) gitCommitMessageLanguageComboBox.getSelectedItem());
        gitConfig.setCustomPrompt(gitCommitMessageCustomPromptArea.getText());
        settings.setToolConfig(ConfigConstant.GIT_COMMIT_MESSAGE, gitConfig);

        //code name suggest
        AICodeToolSetSettings.ToolConfig codeNameSuggestConfig = settings.getToolConfig(ConfigConstant.CODE_NAME_SUGGEST);
        codeNameSuggestConfig.setLanguage((String) codeNameSuggestLanguageComboBox.getSelectedItem());
        codeNameSuggestConfig.setCustomPrompt(codeNameSuggestCustomPromptArea.getText());
        settings.setToolConfig(ConfigConstant.CODE_NAME_SUGGEST, codeNameSuggestConfig);
    }

    @Override
    public void reset() {
        AICodeToolSetSettings settings = AICodeToolSetSettings.getInstance();
        AICodeToolSetSettings.ToolConfig gitConfig = settings.getToolConfig(ConfigConstant.GIT_COMMIT_MESSAGE);
        AICodeToolSetSettings.ToolConfig codeNameSuggestConfig = settings.getToolConfig(ConfigConstant.CODE_NAME_SUGGEST);

        modelComboBox.setSelectedItem(settings.getAiModel());
        apiKeyField.setText(settings.getApiKey());
        showPasswordCheckBox.setSelected(false);
        apiKeyField.setEchoChar('•');

        gitCommitMessageLanguageComboBox.setSelectedItem(gitConfig.getLanguage());
        codeNameSuggestLanguageComboBox.setSelectedItem(codeNameSuggestConfig.getLanguage());

        gitCommitMessageCustomPromptArea.setText(GenerateGItMessagePrompt.DEFAULT_PROMPT);
        codeNameSuggestCustomPromptArea.setText(GenerateCodeNamePrompt.DEFAULT_PROMPT);
    }


    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "AI Code Tool Set";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "preference.AICodeToolSetConfigurable";
    }

}