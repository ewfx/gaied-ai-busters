package com.bank.emailclassifier.model;

import java.util.List;
import java.util.Map;

public record ClassifiedEmail(
                List<PossibleRequest> possibleRequests,
                Request primaryRequest,
                Map<String, ExtractedField> extractedFields,
                boolean isDuplicate,
                String duplicateReason) {
}