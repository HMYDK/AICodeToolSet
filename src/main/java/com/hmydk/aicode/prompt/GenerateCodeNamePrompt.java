package com.hmydk.aicode.prompt;

import javax.swing.*;

/**
 * PromptDesign
 *
 * @author hmydk
 */
public class GenerateCodeNamePrompt {

    public static String getPrompt(String language, String elementType, String originalText) {
        return DEFAULT_PROMPT
                .replace("{LANGUAGE}", language)
                .replace("{ELEMENT_TYPE}", elementType)
                .replace("{ORIGINAL_TEXT}", originalText);
    }

    public static final String DEFAULT_PROMPT = """
            You are an expert programmer and naming consultant. Your task is to suggest appropriate names for code elements (variables, functions, classes, etc.) based on the provided context. Follow these guidelines:
                        
            Besides the provided code naming suggestions, please reply in the following language: {LANGUAGE}
            Type of element to name: {ELEMENT_TYPE}
            Original text or description: {ORIGINAL_TEXT}
                        
            Please provide naming suggestions following these rules:
                        
            - Adhere to the naming conventions of the specified programming language.
            - Ensure names are clear, concise, and descriptive.
            - Avoid abbreviations unless widely recognized in the field.
            - Consider the scope and purpose of the element when naming.
            - Use prefix or suffix conventions if applicable (e.g., 'is' for boolean variables).
            - Provide 2-3 alternative suggestions if appropriate.
                        
                        
            For each suggestion, provide:
                        
            Suggested Name: [Primary Suggestion]
            Explanation: [Brief explanation of why this name is appropriate]
                       
            Alternative Suggestions:
            - [Alternative 1]: [Brief explanation]
            - [Alternative 2]: [Brief explanation]
                        
            Additional Comments: [Any extra insights or recommendations]
                        
            Notes:
            - If the original text is not in English, translate the core concept to English before naming.
            - Indicate any additional context that would help provide an accurate suggestion.
            """;

    public static void validatePrompt(String prompt) {
        if (prompt.contains("{LANGUAGE}") && prompt.contains("{ELEMENT_TYPE}") && prompt.contains("{ORIGINAL_TEXT}")) {
            JOptionPane.showMessageDialog(null, "Prompt is valid!", "Validation Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Prompt is invalid. It must contain  {LANGUAGE} and {ELEMENT_TYPE} and {ORIGINAL_TEXT}.", "Validation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
