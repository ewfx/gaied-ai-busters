package com.bank.emailclassifier.ingestion;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.bank.emailclassifier.model.EmailInput;

public class FileEmailSource implements EmailSource {
    private final String directoryPath;
    private final EmailParser parser;

    public FileEmailSource(String directoryPath) {
        this.directoryPath = directoryPath;
        this.parser = new EmailParser();
    }

    @Override
    public List<EmailInput> getEmails() throws Exception {
        List<EmailInput> emails = new ArrayList<>();
        File directory = new File(directoryPath);
        for (File file : directory.listFiles((dir, name) -> name.endsWith(".eml"))) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Session session = Session.getInstance(new Properties());
                MimeMessage message = new MimeMessage(session, fis);
                emails.add(parser.parse(message));
            }
        }
        return emails;
    }
}