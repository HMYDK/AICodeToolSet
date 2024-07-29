package com.hmydk.aicode.prompt;

import com.hmydk.aicode.config.ApiKeySettings;

import java.util.List;

/**
 * PromptUtil
 *
 * @author hmydk
 */
public class GenerateGItMessagePrompt {

    public static final String DEFAULT_PROMPT = generatePrompt4();

    public static String constructPrompt(String diff, String branch, List<String> historyMsg) {
        String content = ApiKeySettings.getInstance().getCustomPrompt();
        content = content.replace("{branch}", branch);
        if (content.contains("{history}") && historyMsg != null) {
            content = content.replace("{history}", String.join("\n", historyMsg));
        }

        if (content.contains("{diff}")) {
            content = content.replace("{diff}", diff);
        } else {
            content = content + "\n" + diff;
        }

        if (content.contains("{local}")) {
            content = content.replace("{local}", ApiKeySettings.getInstance().getLanguage());
        }

        return content;
    }

    private static String generatePrompt4() {
        return """
                Generate a commit message using the Angular Conventional Commit Convention.
                Constraints:
                - Summarize changes with specificity
                - Optionally include benefits in the body
                - Keep lines within 72 characters
                - Use {local} language
                - Infer the scope from the context of the diff
                Structure:
                <type>[optional scope]: <description>
                [optional body]
                Example:
                   feat(api): add endpoint for user authentication
                Possible scopes (examples, infer from diff context):
                - api: app API-related code
                - ui: user interface changes
                - db: database-related changes
                - etc.
                Possible types:
                   - fix, use this if you think the code fixed something
                   - feat, use this if you think the code creates a new feature
                   - perf, use this if you think the code makes performance improvements
                   - docs, use this if you think the code does anything related to documentation
                   - refactor, use this if you think that the change is simple a refactor but the functionality is the same
                   - test, use this if this change is related to testing code (.spec, .test, etc)
                   - chore, use this for code related to maintenance tasks, build processes, or other non-user-facing changes. It typically includes tasks that don't directly impact the functionality but are necessary for the project's development and maintenance.
                   - ci, use this if this change is for CI related stuff
                   - revert, use this if im reverting something
                Diff:
                    {diff}
                """;
    }
}
