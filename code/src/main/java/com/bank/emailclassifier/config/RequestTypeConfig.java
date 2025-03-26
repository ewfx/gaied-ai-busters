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

public class RequestTypeConfig {
    private final Map<String, List<String>> requestTypes;

    public RequestTypeConfig(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(Files.readAllBytes(Paths.get(filePath)));
        this.requestTypes = mapper.convertValue(root.get("request_types"),
                new TypeReference<Map<String, List<String>>>() {
                });
    }

    public String getFormattedRequestTypes() {
        return requestTypes.entrySet().stream()
                .map(entry -> {
                    List<String> subTypesList = entry.getValue();
                    String subTypes = (subTypesList != null && !subTypesList.isEmpty())
                            ? String.join(", ", subTypesList)
                            : "";
                    return entry.getKey() + (subTypes.isEmpty() ? "" : ": " + subTypes);
                })
                .collect(Collectors.joining("\n"));
    }
}