package com.bank.emailclassifier.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FieldMappingConfig {
    private final Map<String, List<String>> fieldMappings;

    public FieldMappingConfig(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(Files.readAllBytes(Paths.get(filePath)));
        this.fieldMappings = mapper.convertValue(root, new TypeReference<Map<String, List<String>>>() {
        });
    }

    public String getFormattedFieldMappings() {
        return fieldMappings.entrySet().stream()
                .map(entry -> "For " + entry.getKey() + ": " + String.join(", ", entry.getValue()))
                .collect(Collectors.joining("\n"));
    }
}
