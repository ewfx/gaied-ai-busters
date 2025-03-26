package com.bank.emailclassifier.model;

public record PossibleRequest(String requestType, String subRequestType, double confidence) {
}