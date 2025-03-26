package com.bank.emailclassifier.model;

import java.util.List;

public record EmailInput(String subject, String body, List<Attachment> attachments, EmailMetadata metadata) {
}