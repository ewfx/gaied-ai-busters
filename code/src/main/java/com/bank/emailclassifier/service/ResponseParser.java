package com.bank.emailclassifier.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bank.emailclassifier.model.ClassifiedEmail;
import com.bank.emailclassifier.model.ExtractedField;
import com.bank.emailclassifier.model.PossibleRequest;
import com.bank.emailclassifier.model.Request;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class ResponseParser {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(ResponseParser.class);

    public static ClassifiedEmail parse(String jsonResponse, Map<String, ExtractedField> preExtractedFields)
            throws Exception {
        try {
            JsonNode root = mapper.readTree(jsonResponse);

            List<PossibleRequest> possibleRequests = new ArrayList<>();
            JsonNode possibleRequestsNode = root.path("possible_requests");
            if (possibleRequestsNode.isArray()) {
                for (JsonNode node : possibleRequestsNode) {
                    possibleRequests.add(new PossibleRequest(
                            node.path("request_type").asText(),
                            node.path("sub_request_type").asText(),
                            node.path("confidence").asDouble()));
                }
            } else {
                log.warn("possible_requests is not an array in the response: {}", jsonResponse);
            }

            JsonNode primaryNode = root.path("primary_request");
            Request primaryRequest = null;
            if (!primaryNode.isMissingNode()) {
                primaryRequest = new Request(
                        primaryNode.path("request_type").asText(),
                        primaryNode.path("sub_request_type").asText());
            } else {
                log.warn("primary_request is missing in the response: {}", jsonResponse);
            }

            Map<String, ExtractedField> extractedFields = new java.util.HashMap<>(preExtractedFields);
            JsonNode fieldsNode = root.path("extracted_fields");
            if (fieldsNode.isObject()) {
                fieldsNode.fields().forEachRemaining(entry -> extractedFields.put(entry.getKey(),
                        new ExtractedField(entry.getValue().path("value").asText(),
                                entry.getValue().path("source").asText())));
            } else {
                log.warn("extracted_fields is not an object in the response: {}", jsonResponse);
            }

            return new ClassifiedEmail(possibleRequests, primaryRequest, extractedFields, false, null);
        } catch (MismatchedInputException | JsonParseException e) {
            log.error("Error parsing JSON response: {}", jsonResponse, e);
            throw new RuntimeException("Invalid JSON format received from LLM", e);
        } catch (Exception e) {
            log.error("Unexpected error during JSON parsing: {}", jsonResponse, e);
            throw new RuntimeException("Unexpected error during JSON parsing", e);
        }
    }
}
