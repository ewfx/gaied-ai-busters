package com.bank.emailclassifier.model;

public record EmailMetadata(String messageId, String inReplyTo, String references, String bodyHash) {
}