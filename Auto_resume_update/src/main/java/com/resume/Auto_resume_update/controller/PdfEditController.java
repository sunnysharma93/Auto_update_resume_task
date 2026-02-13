package com.resume.Auto_resume_update.controller;

import com.resume.Auto_resume_update.dto.EditRequest;
import com.resume.Auto_resume_update.service.PdfEditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "http://localhost:5173/")
public class PdfEditController {

    private static final Logger logger = LoggerFactory.getLogger(PdfEditController.class);

    @Autowired
    private PdfEditService pdfEditService;

    @PostMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> editPdf(@RequestPart("file") MultipartFile file, @RequestPart("request") EditRequest request) {
        try {
            // Validate input
            if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
                logger.warn("Invalid file uploaded: empty or not PDF");
                return ResponseEntity.badRequest().body("Invalid file: Must be a non-empty PDF".getBytes());
            }
            if (request == null) {
                logger.warn("EditRequest is null");
                return ResponseEntity.badRequest().body("EditRequest cannot be null".getBytes());
            }

            logger.info("Processing PDF edit request for file: {}", file.getOriginalFilename());

            // Process the PDF
            byte[] updatedPdf = pdfEditService.applyEdits(file.getInputStream(), request);

            // Return the updated PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "updated_resume.pdf");
            logger.info("PDF edit completed successfully for file: {}", file.getOriginalFilename());
            return ResponseEntity.ok().headers(headers).body(updatedPdf);

        } catch (Exception e) {
            logger.error("Error processing PDF edit for file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(500).body("Internal server error".getBytes());
        }
    }
}