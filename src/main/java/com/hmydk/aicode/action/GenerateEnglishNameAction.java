package com.hmydk.aicode.action;


import com.hmydk.aicode.service.AIBusinessService;
import com.hmydk.aicode.util.IdeaDialogUtil;
import com.hmydk.aicode.util.MarkdownToHtmlConverter;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * GenerateEnglishNameAction
 * <p>
 * An IntelliJ IDEA plugin action to suggest English names based on selected text.
 *
 * @author hmydk
 */
public class GenerateEnglishNameAction extends AnAction {

    private final AIBusinessService aiBusinessService = new AIBusinessService();


    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        //check api key
        if (!aiBusinessService.checkConfig()) {
            IdeaDialogUtil.showWarning(project, "Please set your API key first.", "No API Key Set");
            return;
        }

        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null || selectedText.trim().isEmpty()) {
            IdeaDialogUtil.showError(project, "Please select some text first.", "No Text Selected");
        }


        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Generating code name ...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    String codeSuggestion = aiBusinessService.generateAICodeName(selectedText.replace(' ', '_'));
                    codeSuggestion = MarkdownToHtmlConverter.convertToHtml(codeSuggestion);
                    String finalCodeSuggestion = codeSuggestion;
                    ApplicationManager.getApplication().invokeLater(() -> {
                        showSuggestionDialog(e.getProject(), finalCodeSuggestion);
                    });
                } catch (IllegalArgumentException ex) {
                    IdeaDialogUtil.showWarning(project, ex.getMessage(), "CodeNameSuggest Warning");
                } catch (Exception ex) {
                    IdeaDialogUtil.showError(project, "Error generating code name: " + ex.getMessage(), "Error");
                }
            }
        });
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(
                editor != null && editor.getSelectionModel().hasSelection()
        );
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    private void showSuggestionDialog(Project project, String suggestion) {
        SuggestionDialog dialog = new SuggestionDialog(project, suggestion);
        dialog.show();
    }

    private static class SuggestionDialog extends DialogWrapper {
        private final String suggestion;

        public SuggestionDialog(@Nullable Project project, String suggestion) {
            super(project);
            this.suggestion = suggestion;
            init();
            setTitle("AI CodeName Suggest");
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel dialogPanel = new JPanel(new BorderLayout());

            JEditorPane editorPane = new JEditorPane();
            editorPane.setContentType("text/html");
            editorPane.setText(suggestion);
            editorPane.setEditable(false);

            // Set up scrolling
            JBScrollPane scrollPane = new JBScrollPane(editorPane);
            scrollPane.setPreferredSize(new Dimension(900, 600));
            dialogPanel.add(scrollPane, BorderLayout.CENTER);

            return dialogPanel;
        }
    }
}