package com.bank.emailclassifier.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bank.emailclassifier.config.ExtractionRuleConfig;
import com.bank.emailclassifier.model.EmailInput;
import com.bank.emailclassifier.model.ExtractedField;

public class ExtractionService {
    private final ExtractionRuleConfig ruleConfig;

    public ExtractionService(ExtractionRuleConfig ruleConfig) {
        this.ruleConfig = ruleConfig;
    }

    public Map<String, ExtractedField> extractFields(EmailInput email) {
        Map<String, ExtractedField> extractedFields = new HashMap<>();

        // For simplicity, assume initial extraction without request type; LLM refines
        // later
        Map<String, List<String>> rules = ruleConfig.getRulesForRequestType("default");
        for (Map.Entry<String, List<String>> rule : rules.entrySet()) {
            String field = rule.getKey();
            List<String> sources = rule.getValue(); // e.g., ["body", "attachments"]

            for (String source : sources) {
                String content = source.equals("body") ? email.body() : email.attachments().toString();
                String pattern = getPatternForField(field); // Define patterns elsewhere
                Matcher matcher = Pattern.compile(pattern).matcher(content);
                if (matcher.find()) {
                    extractedFields.put(field, new ExtractedField(matcher.group(1), source));
                    break; // Priority respected: stop after first match
                }
            }
        }
        return extractedFields;
    }

    private String getPatternForField(String field) {
        return switch (field) {
            case "deal_name" -> "Deal Name: (.*)";
            case "amount" -> "Amount: (\\d+\\.\\d{2})";
            case "expiration_date" -> "Expiration Date: (\\d{4}-\\d{2}-\\d{2})";
            default -> "";
        };
    }
}