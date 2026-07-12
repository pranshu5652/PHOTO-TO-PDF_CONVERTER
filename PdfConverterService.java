package com.pranshu.phototopdf.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Core logic: takes one or more uploaded images and produces a single PDF.
 * Each image becomes one page, scaled to fit an A4 page while keeping
 * its aspect ratio (so the photo doesn't look stretched).
 */
@Service
public class PdfConverterService {

    private static final float PAGE_MARGIN = 20f; // points

    /**
     * Converts a single image file into a one-page PDF and returns the PDF bytes.
     */
    public byte[] convertSingleImageToPdf(MultipartFile imageFile) throws IOException {
        try (PDDocument document = new PDDocument()) {
            addImageAsPage(document, imageFile);
            return documentToBytes(document);
        }
    }

    /**
     * Converts multiple images into a single multi-page PDF (one image per page),
     * in the order they were uploaded.
     */
    public byte[] convertMultipleImagesToPdf(MultipartFile[] imageFiles) throws IOException {
        try (PDDocument document = new PDDocument()) {
            for (MultipartFile imageFile : imageFiles) {
                if (!imageFile.isEmpty()) {
                    addImageAsPage(document, imageFile);
                }
            }
            return documentToBytes(document);
        }
    }

    private void addImageAsPage(PDDocument document, MultipartFile imageFile) throws IOException {
        // Read raw bytes once so we can both inspect dimensions and load into PDFBox
        byte[] imageBytes = imageFile.getBytes();

        BufferedImage bufferedImage;
        try (InputStream in = imageFile.getInputStream()) {
            bufferedImage = ImageIO.read(in);
        }
        if (bufferedImage == null) {
            throw new IOException("File '" + imageFile.getOriginalFilename()
                    + "' is not a readable image (only JPG/PNG/BMP/GIF supported).");
        }

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDImageXObject pdImage = PDImageXObject.createFromByteArray(
                document, imageBytes, imageFile.getOriginalFilename());

        float pageWidth = page.getMediaBox().getWidth() - 2 * PAGE_MARGIN;
        float pageHeight = page.getMediaBox().getHeight() - 2 * PAGE_MARGIN;

        float imgWidth = bufferedImage.getWidth();
        float imgHeight = bufferedImage.getHeight();

        // Scale to fit within the page while preserving aspect ratio
        float scale = Math.min(pageWidth / imgWidth, pageHeight / imgHeight);
        float drawWidth = imgWidth * scale;
        float drawHeight = imgHeight * scale;

        // Center the image on the page
        float x = (page.getMediaBox().getWidth() - drawWidth) / 2;
        float y = (page.getMediaBox().getHeight() - drawHeight) / 2;

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.drawImage(pdImage, x, y, drawWidth, drawHeight);
        }
    }

    private byte[] documentToBytes(PDDocument document) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            document.save(out);
            return out.toByteArray();
        }
    }
}
