package com.hmydk.aicode.action;

import com.hmydk.aicode.service.AIBusinessService;
import com.hmydk.aicode.util.GItCommitUtil;
import com.hmydk.aicode.util.IdeaDialogUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.util.List;

public class GenerateCommitMessageAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(GenerateCommitMessageAction.class);


    private final AIBusinessService aiBusinessService = new AIBusinessService();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        //check api key
        if (!aiBusinessService.checkConfig()) {
            Messages.showWarningDialog(project, "Please set your API key first.", "No API Key Set");
            return;
        }

        AbstractCommitWorkflowHandler<?, ?> commitWorkflowHandler = (AbstractCommitWorkflowHandler<?, ?>) e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER);
        if (commitWorkflowHandler == null) {
            IdeaDialogUtil.showWarning(project, "No changes selected. Please select files to commit.", "No Changes Selected");
            return;
        }

        List<Change> includedChanges = commitWorkflowHandler.getUi().getIncludedChanges();

        // Run the time-consuming operations in a background task
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Generating commit message", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    List<String> gitHistoryMsg = GItCommitUtil.computeGitHistoryMsg(project, 10);
                    String diff = GItCommitUtil.computeDiff(includedChanges, project);
                    String branch = GItCommitUtil.commonBranch(includedChanges, project);

                    String commitMessage = aiBusinessService.generateCommitMessage(branch, diff, gitHistoryMsg);

                    // Use invokeLater to update UI on EDT
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (showCommitMessageDialog(project, commitMessage)) {
                            copyToClipboard(commitMessage);
                            Messages.showInfoMessage(project, "Commit message has been copied to clipboard.", "Message Copied");
                        }
                    });
                } catch (IllegalArgumentException ex) {
                    IdeaDialogUtil.showWarning(project, ex.getMessage(), "AI Commit Message Warning");
                } catch (Exception ex) {
                    IdeaDialogUtil.showError(project, "Error generating commit message: " + ex.getMessage(), "Error");
                }
            }
        });
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        // Enable/disable the action based on whether there's an open project
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    private void copyToClipboard(String text) {
        CopyPasteManager.getInstance().setContents(new StringSelection(text));
    }

    private boolean showCommitMessageDialog(Project project, String commitMessage) {
        int result = Messages.showYesNoDialog(
                project,
                "Use the following AI-generated commit message?\n\n" + commitMessage,
                "AI Commit Message",
                Messages.getQuestionIcon()
        );
        return result == Messages.YES;
    }
}