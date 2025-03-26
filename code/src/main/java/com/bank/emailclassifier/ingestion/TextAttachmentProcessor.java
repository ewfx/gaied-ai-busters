package com.bank.emailclassifier.ingestion;

import com.bank.emailclassifier.model.Attachment;

public class TextAttachmentProcessor implements AttachmentProcessor {
    @Override
    public String processAttachment(Attachment attachment) throws Exception {
        // Simply return the text content
        return attachment.content();
    }
}