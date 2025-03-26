package com.bank.emailclassifier.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PromptConfig {
    private final String template;

    public PromptConfig(String templateFilePath) throws IOException {
        this.template = Files.readString(Paths.get(templateFilePath));
    }

    public String getPrompt(String requestTypes, String fieldMappings, String subject, String body,
            String attachments, String extractedFields) {
        return template
                .replace("{request_types}", requestTypes)
                .replace("{field_mappings}", fieldMappings)
                .replace("{subject}", subject)
                .replace("{body}", body)
                .replace("{attachments}", attachments)
                .replace("{extracted_fields}", extractedFields);
    }
}