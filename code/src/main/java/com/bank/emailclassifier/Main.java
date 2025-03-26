package com.bank.emailclassifier;

import java.util.List;

import com.bank.emailclassifier.config.ExtractionRuleConfig;
import com.bank.emailclassifier.config.FieldMappingConfig;
import com.bank.emailclassifier.config.PromptConfig;
import com.bank.emailclassifier.config.RequestTypeConfig;
import com.bank.emailclassifier.ingestion.EmailPreprocessor;
import com.bank.emailclassifier.ingestion.EmailSource;
import com.bank.emailclassifier.ingestion.FileEmailSource;
import com.bank.emailclassifier.model.ClassifiedEmail;
import com.bank.emailclassifier.model.EmailInput;
import com.bank.emailclassifier.service.DuplicateDetector;
import com.bank.emailclassifier.service.EmailClassifierService;
import com.bank.emailclassifier.service.ExtractionService;
import com.bank.emailclassifier.service.LLMClient;
import com.bank.emailclassifier.service.OpenAIClient;

import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    public static void main(String[] args) throws Exception {

        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("OPENAI_API_KEY not found in .env file");
        }

        // Initialize configurations
        PromptConfig promptConfig = new PromptConfig("src/main/resources/prompt-template.txt");
        RequestTypeConfig requestTypeConfig = new RequestTypeConfig("src/main/resources/request-types.json");
        FieldMappingConfig fieldMappingConfig = new FieldMappingConfig("src/main/resources/field-mappings.json");
        ExtractionRuleConfig extractionRuleConfig = new ExtractionRuleConfig(
                "src/main/resources/extraction-rules.json");

        // Initialize services
        DuplicateDetector duplicateDetector = new DuplicateDetector();
        LLMClient llmClient = new OpenAIClient(apiKey);
        ExtractionService extractionService = new ExtractionService(extractionRuleConfig);
        EmailClassifierService classifier = new EmailClassifierService(
                promptConfig, requestTypeConfig, fieldMappingConfig, extractionService, duplicateDetector, llmClient);

        // Initialize email source
        EmailSource emailSource = new FileEmailSource("emails");
        EmailPreprocessor preprocessor = new EmailPreprocessor();
        List<EmailInput> emails = emailSource.getEmails().stream()
                .map(preprocessor::preprocess)
                .toList();

        // Process emails
        for (EmailInput email : emails) {
            ClassifiedEmail result = classifier.classifyEmail(email);
            System.out.println("Email Subject: " + email.subject());
            if (result.isDuplicate()) {
                System.out.println("Duplicate: " + result.duplicateReason());
            } else {
                System.out.println("Primary Request: " + result.primaryRequest());
                System.out.println("Possible Requests: " + result.possibleRequests());
                System.out.println("Extracted Fields: " + result.extractedFields());
            }
            System.out.println("---");
        }
    }
}