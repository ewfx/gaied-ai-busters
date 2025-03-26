package com.bank.emailclassifier.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExtractionRuleConfig {
    private final Map<String, Map<String, List<String>>> extractionRules;

    @SuppressWarnings("unchecked")
    public ExtractionRuleConfig(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(Files.readAllBytes(Paths.get(filePath)));
        this.extractionRules = mapper.convertValue(root, Map.class);
    }

    public Map<String, List<String>> getRulesForRequestType(String requestType) {
        return extractionRules.getOrDefault(requestType, Map.of());
    }
}