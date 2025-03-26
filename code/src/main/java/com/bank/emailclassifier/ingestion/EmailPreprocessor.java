package com.bank.emailclassifier.ingestion;

import com.bank.emailclassifier.model.EmailInput;

public class EmailPreprocessor {
    public EmailInput preprocess(EmailInput email) {
        String cleanedBody = email.body().trim().replaceAll("\\s+", " ");
        return new EmailInput(email.subject(), cleanedBody, email.attachments(), email.metadata());
    }
}