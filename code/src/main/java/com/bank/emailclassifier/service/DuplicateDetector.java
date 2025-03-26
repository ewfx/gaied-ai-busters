package com.bank.emailclassifier.service;

import java.util.HashSet;
import java.util.Set;

import com.bank.emailclassifier.model.EmailMetadata;

public class DuplicateDetector {
    private final Set<String> processedMessageIds = new HashSet<>();
    private final Set<String> processedThreads = new HashSet<>();
    private final Set<String> processedBodyHashes = new HashSet<>();

    public DuplicateDetectionResult isDuplicate(EmailMetadata metadata) {
        if (processedMessageIds.contains(metadata.messageId())) {
            return new DuplicateDetectionResult(true, "Duplicate Message-ID: " + metadata.messageId());
        }
        if (metadata.inReplyTo() != null && processedThreads.contains(metadata.inReplyTo())) {
            return new DuplicateDetectionResult(true, "Part of thread, In-Reply-To: " + metadata.inReplyTo());
        }
        if (metadata.references() != null) {
            String[] refs = metadata.references().split("\\s+");
            for (String ref : refs) {
                if (processedThreads.contains(ref)) {
                    return new DuplicateDetectionResult(true, "Part of thread, References: " + ref);
                }
            }
        }
        if (processedBodyHashes.contains(metadata.bodyHash())) {
            return new DuplicateDetectionResult(true, "Similar content detected");
        }
        processedMessageIds.add(metadata.messageId());
        if (metadata.inReplyTo() != null)
            processedThreads.add(metadata.inReplyTo());
        if (metadata.references() != null) {
            String[] refs = metadata.references().split("\\s+");
            for (String ref : refs)
                processedThreads.add(ref);
        }
        processedBodyHashes.add(metadata.bodyHash());
        return new DuplicateDetectionResult(false, null);
    }
}

record DuplicateDetectionResult(boolean isDuplicate, String reason) {
}