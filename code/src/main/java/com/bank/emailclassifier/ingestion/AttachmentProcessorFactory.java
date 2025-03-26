package com.bank.emailclassifier.ingestion;

import java.util.HashMap;
import java.util.Map;

public class AttachmentProcessorFactory {
    private static final Map<String, AttachmentProcessor> processors = new HashMap<>();

    static {
        processors.put("text/plain", new TextAttachmentProcessor());
        processors.put("image/png", new ImageAttachmentProcessor());
        processors.put("image/jpeg", new ImageAttachmentProcessor());
        // Add processors for Word, PDF, etc., as needed
    }

    public static AttachmentProcessor getProcessor(String mimeType) {
        return processors.getOrDefault(mimeType, new DefaultAttachmentProcessor());
    }
}
