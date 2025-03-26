package com.bank.emailclassifier.ingestion;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.bank.emailclassifier.model.Attachment;

import net.sourceforge.tess4j.Tesseract;

public class ImageAttachmentProcessor implements AttachmentProcessor {
    private final Tesseract tesseract;

    public ImageAttachmentProcessor() {
        tesseract = new Tesseract();
        tesseract.setDatapath("/opt/homebrew/share/tessdata/");
        tesseract.setLanguage("eng"); // Set to English (default)
    }

    @Override
    public String processAttachment(Attachment attachment) throws Exception {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(attachment.content().getBytes()));
        return tesseract.doOCR(image);
    }
}