package com.bank.emailclassifier.ingestion;

import com.bank.emailclassifier.model.Attachment;

public class DefaultAttachmentProcessor implements AttachmentProcessor {
    @Override
    public String processAttachment(Attachment attachment) {
        return "Unsupported attachment type: " + attachment.fileName();
    }
}