package com.bank.emailclassifier.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bank.emailclassifier.config.FieldMappingConfig;
import com.bank.emailclassifier.config.PromptConfig;
import com.bank.emailclassifier.config.RequestTypeConfig;
import com.bank.emailclassifier.model.ClassifiedEmail;
import com.bank.emailclassifier.model.EmailInput;
import com.bank.emailclassifier.model.ExtractedField;

public class EmailClassifierService {
    private final PromptConfig promptConfig;
    private final RequestTypeConfig requestTypeConfig;
    private final FieldMappingConfig fieldMappingConfig;
    private final ExtractionService extractionService;
    private final DuplicateDetector duplicateDetector;
    private final LLMClient llmClient;
    private static final Logger log = LoggerFactory.getLogger(EmailClassifierService.class);

    public EmailClassifierService(PromptConfig promptConfig, RequestTypeConfig requestTypeConfig,
            FieldMappingConfig fieldMappingConfig, ExtractionService extractionService,
            DuplicateDetector duplicateDetector, LLMClient llmClient) {
        this.promptConfig = promptConfig;
        this.requestTypeConfig = requestTypeConfig;
        this.fieldMappingConfig = fieldMappingConfig;
        this.extractionService = extractionService;
        this.duplicateDetector = duplicateDetector;
        this.llmClient = llmClient;
    }

    public ClassifiedEmail classifyEmail(EmailInput email) throws Exception {
        // Check for duplicates
        DuplicateDetectionResult duplicateResult = duplicateDetector.isDuplicate(email.metadata());
        if (duplicateResult.isDuplicate()) {
            return new ClassifiedEmail(List.of(), null, Map.of(), true, duplicateResult.reason());
        }

        // Extract fields based on rules
        Map<String, ExtractedField> extractedFields = extractionService.extractFields(email);

        // Construct prompt with extracted fields
        String extractedFieldsStr = extractedFields.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue().value() + " (from " + e.getValue().source() + ")")
                .collect(Collectors.joining("\n"));

        String prompt = promptConfig.getPrompt(
                requestTypeConfig.getFormattedRequestTypes(),
                fieldMappingConfig.getFormattedFieldMappings(),
                email.subject(),
                email.body(),
                email.attachments().toString(),
                extractedFieldsStr);
        log.info("****************************************");
        log.info("Prompt: {}", prompt);
        String jsonResponse = llmClient.classifyEmail(prompt);
        return ResponseParser.parse(jsonResponse, extractedFields);
    }
}