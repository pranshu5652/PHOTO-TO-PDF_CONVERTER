package com.pranshu.phototopdf.controller;

import com.pranshu.phototopdf.service.PdfConverterService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfConverterService pdfConverterService;

    public PdfController(PdfConverterService pdfConverterService) {
        this.pdfConverterService = pdfConverterService;
    }

    /**
     * Upload ONE photo -> get back a one-page PDF.
     * Test with curl:
     * curl -F "file=@photo.jpg" http://localhost:8080/api/pdf/convert -o output.pdf
     */
    @PostMapping(value = "/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> convertSingle(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a photo to upload.");
        }
        try {
            byte[] pdfBytes = pdfConverterService.convertSingleImageToPdf(file);
            return buildPdfResponse(pdfBytes, stripExtension(file.getOriginalFilename()) + ".pdf");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Conversion failed: " + e.getMessage());
        }
    }

    /**
     * Upload MULTIPLE photos -> get back one multi-page PDF (one photo per page).
     * Test with curl:
     * curl -F "files=@a.jpg" -F "files=@b.jpg" http://localhost:8080/api/pdf/convert-multiple -o output.pdf
     */
    @PostMapping(value = "/convert-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> convertMultiple(@RequestParam("files") MultipartFile[] files) {
        if (files.length == 0) {
            return ResponseEntity.badRequest().body("Please select at least one photo.");
        }
        try {
            byte[] pdfBytes = pdfConverterService.convertMultipleImagesToPdf(files);
            return buildPdfResponse(pdfBytes, "photos-merged.pdf");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Conversion failed: " + e.getMessage());
        }
    }

    private ResponseEntity<byte[]> buildPdfResponse(byte[] pdfBytes, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    private String stripExtension(String filename) {
        if (filename == null) return "photo";
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex == -1 ? filename : filename.substring(0, dotIndex);
    }
}
