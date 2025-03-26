package com.bank.emailclassifier.ingestion;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;

import com.bank.emailclassifier.model.Attachment;
import com.bank.emailclassifier.model.EmailInput;
import com.bank.emailclassifier.model.EmailMetadata;

public class EmailParser {
    public EmailInput parse(Message message) throws Exception {
        String subject = message.getSubject();
        String body = extractBody(message);
        List<Attachment> attachments = extractAttachments(message);
        EmailMetadata metadata = extractMetadata(message);
        return new EmailInput(subject, body, attachments, metadata);
    }

    private String extractBody(Part part) throws Exception {
        if (part.isMimeType("text/plain"))
            return (String) part.getContent();
        else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                String body = extractBody(multipart.getBodyPart(i));
                if (body != null)
                    return body;
            }
        }
        return "";
    }

    private List<Attachment> extractAttachments(Part part) throws Exception {
        List<Attachment> attachments = new ArrayList<>();
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                Part bodyPart = multipart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) && bodyPart.getFileName() != null) {
                    String mimeType = bodyPart.getContentType().split(";")[0];
                    AttachmentProcessor processor = AttachmentProcessorFactory.getProcessor(mimeType);
                    String content = processor.processAttachment(
                            new Attachment(bodyPart.getFileName(), bodyPart.getContent().toString()));
                    attachments.add(new Attachment(bodyPart.getFileName(), content));
                } else if (bodyPart.isMimeType("multipart/*")) {
                    attachments.addAll(extractAttachments(bodyPart));
                }
            }
        }
        return attachments;
    }

    private EmailMetadata extractMetadata(Message message) throws Exception {
        String messageId = message.getHeader("Message-ID") != null ? message.getHeader("Message-ID")[0] : "";
        String inReplyTo = message.getHeader("In-Reply-To") != null ? message.getHeader("In-Reply-To")[0] : null;
        String references = message.getHeader("References") != null ? message.getHeader("References")[0] : null;
        String bodyHash = hashBody(message.getContent().toString());
        return new EmailMetadata(messageId, inReplyTo, references, bodyHash);
    }

    private String hashBody(String body) {
        return String.valueOf(body.hashCode()); // Simple hash; replace with MurmurHash3 if needed
    }
}