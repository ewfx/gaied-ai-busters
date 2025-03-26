package com.bank.emailclassifier.service;

import java.io.IOException;

public interface LLMClient {
    String classifyEmail(String prompt) throws IOException;

}