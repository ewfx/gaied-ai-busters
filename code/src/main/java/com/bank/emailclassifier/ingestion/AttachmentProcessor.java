package com.bank.emailclassifier.ingestion;

import com.bank.emailclassifier.model.Attachment;

public interface AttachmentProcessor {
    String processAttachment(Attachment attachment) throws Exception;
}