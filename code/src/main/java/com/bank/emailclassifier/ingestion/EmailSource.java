package com.bank.emailclassifier.ingestion;

import java.util.List;

import com.bank.emailclassifier.model.EmailInput;

public interface EmailSource {
    List<EmailInput> getEmails() throws Exception;
}