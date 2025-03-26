package com.bank.emailclassifier.service;

import com.bank.emailclassifier.config.FieldMappingConfig;
import com.bank.emailclassifier.config.PromptConfig;
import com.bank.emailclassifier.config.RequestTypeConfig;
import com.bank.emailclassifier.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailClassifierServiceTest {

    @Mock
    private PromptConfig promptConfig;
    @Mock
    private RequestTypeConfig requestTypeConfig;
    @Mock
    private FieldMappingConfig fieldMappingConfig;
    @Mock
    private ExtractionService extractionService;
    @Mock
    private DuplicateDetector duplicateDetector;
    @Mock
    private LLMClient llmClient;
    @InjectMocks
    private EmailClassifierService emailClassifierService;

    @Test
    void testClassifyEmail_duplicate() throws Exception {
        // Arrange
        EmailInput email = new EmailInput("subject", "body", Collections.emptyList(), new EmailMetadata());
        DuplicateDetectionResult duplicateResult = new DuplicateDetectionResult(true, "Reason");
        when(duplicateDetector.isDuplicate(any(EmailMetadata.class))).thenReturn(duplicateResult);

        // Act
        ClassifiedEmail result = emailClassifierService.classifyEmail(email);

        // Assert
        assertTrue(result.isDuplicate());
        assertEquals("Reason", result.duplicateReason());
        verify(extractionService, never()).extractFields(any(EmailInput.class));
        verify(llmClient, never()).classifyEmail(anyString());
    }

    @Test
    void testClassifyEmail_noDuplicate_successfulClassification() throws Exception {
        // Arrange
        EmailInput email = new EmailInput("subject", "body", Collections.emptyList(), new EmailMetadata());
        when(duplicateDetector.isDuplicate(any(EmailMetadata.class)))
                .thenReturn(new DuplicateDetectionResult(false, ""));
        Map<String, ExtractedField> extractedFields = Map.of("key1", new ExtractedField("value1", "source1"));
        when(extractionService.extractFields(any(EmailInput.class))).thenReturn(extractedFields);
        String expectedJson = "{\"classification\": \"type1\", \"fields\": {\"field1\": \"value1\"}}";
        when(llmClient.classifyEmail(anyString())).thenReturn(expectedJson);

        // Act
        ClassifiedEmail result = emailClassifierService.classifyEmail(email);

        // Assert
        assertFalse(result.isDuplicate());
        assertNotNull(result.classification()); // Check that classification is not null
        verify(extractionService, times(1)).extractFields(any(EmailInput.class));
        verify(llmClient, times(1)).classifyEmail(anyString());
    }

    @Test
    void testClassifyEmail_noDuplicate_exceptionDuringClassification() throws Exception {
        // Arrange
        EmailInput email = new EmailInput("subject", "body", Collections.emptyList(), new EmailMetadata());
        when(duplicateDetector.isDuplicate(any(EmailMetadata.class)))
                .thenReturn(new DuplicateDetectionResult(false, ""));
        when(extractionService.extractFields(any(EmailInput.class))).thenReturn(Map.of());
        when(llmClient.classifyEmail(anyString())).thenThrow(new Exception("LLM Error"));

        // Act & Assert
        assertThrows(Exception.class, () -> emailClassifierService.classifyEmail(email));
    }

    // Add more tests for different scenarios and edge cases as needed. For example:
    // - Test with different prompt configurations
    // - Test with different extracted fields
    // - Test with empty email subject and body
    // - Test with attachments
    // - Test error handling in ResponseParser (if you have one)

}
