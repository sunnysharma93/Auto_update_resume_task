package com.resume.Auto_resume_update.service;

import com.resume.Auto_resume_update.dto.EditRequest;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.awt.Color; // For colors

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfEditService {

    private static final Logger logger = LoggerFactory.getLogger(PdfEditService.class);

    // Helper method to sanitize text: remove non-ASCII characters to avoid font encoding issues
    private String sanitizeText(String text) {
        if (text == null) return "";
        // Remove non-ASCII characters (anything above U+007F) and also strip newlines/tabs as before
        return text.replaceAll("[^\\x00-\\x7F]", "").replaceAll("[\\r\\n\\t]", "");
    }

    public byte[] applyEdits(InputStream pdfInput, EditRequest request) throws IOException {
        PDDocument document = null;

        try {
            document = PDDocument.load(pdfInput);
            logger.info("Loaded PDF with {} pages", document.getNumberOfPages());

            // Process each page (assume single page for resume)
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                PDPage page = document.getPage(pageIndex);

                // Get page dimensions
                float pageHeight = page.getMediaBox().getHeight();
                float pageWidth = page.getMediaBox().getWidth();

                // Use a standard font
                PDFont font = PDType1Font.HELVETICA;
                float fontSize = 12;
                float xOffset = 50; // Left margin
                float lineSpacing = 14;

                // Now open the content stream
                PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);

                try {
                    // Step 1: Blank the entire page
                    contentStream.setNonStrokingColor(Color.WHITE);
                    float margin = 20;
                    contentStream.addRect(margin, margin, pageWidth - 2 * margin, pageHeight - 2 * margin);
                    contentStream.fill();
                    contentStream.setNonStrokingColor(Color.BLACK);

                    // Step 2: Overlay sections in ATS-friendly order with fixed positions
                    float currentY = pageHeight - 50; // Start from top

                    // Name (bold, larger font)
                    if (request.getName() != null && !request.getName().isEmpty()) {
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16); // Bold and larger
                        contentStream.newLineAtOffset(xOffset, currentY);
                        contentStream.showText(sanitizeText(request.getName()));
                        contentStream.endText();
                        currentY -= lineSpacing * 2; // Extra space
                    }

                    // Gmail and Number (side by side)
                    if ((request.getGmail() != null && !request.getGmail().isEmpty()) || (request.getNumber() != null && !request.getNumber().isEmpty())) {
                        contentStream.beginText();
                        contentStream.setFont(font, fontSize);
                        contentStream.newLineAtOffset(xOffset, currentY);
                        String contact = sanitizeText(request.getGmail() != null ? request.getGmail() : "") +
                                (request.getNumber() != null ? " | " + sanitizeText(request.getNumber()) : "");
                        contentStream.showText(contact);
                        contentStream.endText();
                        currentY -= lineSpacing;
                    }

                    // Summary
                    if (request.getSummary() != null && !request.getSummary().isEmpty()) {
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                        contentStream.newLineAtOffset(xOffset, currentY);
                        contentStream.showText("Summary");
                        contentStream.endText();
                        currentY -= lineSpacing;
                        String[] summaryLines = request.getSummary().split("\n");
                        for (String line : summaryLines) {
                            contentStream.beginText();
                            contentStream.setFont(font, fontSize);
                            contentStream.newLineAtOffset(xOffset, currentY);
                            contentStream.showText(sanitizeText(line));
                            contentStream.endText();
                            currentY -= lineSpacing;
                        }
                    }

                    // Education
                    if (request.getEducation() != null && !request.getEducation().isEmpty()) {
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                        contentStream.newLineAtOffset(xOffset, currentY);
                        contentStream.showText("Education");
                        contentStream.endText();
                        currentY -= lineSpacing;
                        String[] eduLines = request.getEducation().split("\n");
                        for (String line : eduLines) {
                            contentStream.beginText();
                            contentStream.setFont(font, fontSize);
                            contentStream.newLineAtOffset(xOffset, currentY);
                            contentStream.showText(sanitizeText(line));
                            contentStream.endText();
                            currentY -= lineSpacing;
                        }
                    }

                    // Skills
                    if (request.getModifiedSkill() != null && !request.getModifiedSkill().isEmpty()) {
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                        contentStream.newLineAtOffset(xOffset, currentY);
                        contentStream.showText("Skills");
                        contentStream.endText();
                        currentY -= lineSpacing;
                        contentStream.beginText();
                        contentStream.setFont(font, fontSize);
                        contentStream.newLineAtOffset(xOffset, currentY);
                        contentStream.showText(sanitizeText(request.getModifiedSkill()));
                        contentStream.endText();
                        currentY -= lineSpacing;
                    }

                    // Projects
                    if (request.getProjects() != null && !request.getProjects().isEmpty()) {
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                        contentStream.newLineAtOffset(xOffset, currentY);
                        contentStream.showText("Projects");
                        contentStream.endText();
                        currentY -= lineSpacing;
                        String[] projectLines = request.getProjects().split("\n");
                        for (String line : projectLines) {
                            contentStream.beginText();
                            contentStream.setFont(font, fontSize);
                            contentStream.newLineAtOffset(xOffset, currentY);
                            contentStream.showText(sanitizeText(line));
                            contentStream.endText();
                            currentY -= lineSpacing;
                        }
                    }

                    // Experience
                    if (request.getNewExperience() != null && !request.getNewExperience().isEmpty()) {
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                        contentStream.newLineAtOffset(xOffset, currentY);
                        contentStream.showText("Experience");
                        contentStream.endText();
                        currentY -= lineSpacing;
                        String[] expLines = request.getNewExperience().split("\n");
                        for (String line : expLines) {
                            contentStream.beginText();
                            contentStream.setFont(font, fontSize);
                            contentStream.newLineAtOffset(xOffset, currentY);
                            contentStream.showText(sanitizeText(line));
                            contentStream.endText();
                            currentY -= lineSpacing;
                        }
                    }

                    // Certificates
                    if (request.getNewCertification() != null && !request.getNewCertification().isEmpty()) {
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                        contentStream.newLineAtOffset(xOffset, currentY);
                        contentStream.showText("Certificates");
                        contentStream.endText();
                        currentY -= lineSpacing;
                        contentStream.beginText();
                        contentStream.setFont(font, fontSize);
                        contentStream.newLineAtOffset(xOffset, currentY);
                        contentStream.showText(sanitizeText(request.getNewCertification()));
                        contentStream.endText();
                        currentY -= lineSpacing;
                    }

                    // Languages
                    if (request.getLanguages() != null && !request.getLanguages().isEmpty()) {
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                        contentStream.newLineAtOffset(xOffset, currentY);
                        contentStream.showText("Languages");
                        contentStream.endText();
                        currentY -= lineSpacing;
                        contentStream.beginText();
                        contentStream.setFont(font, fontSize);
                        contentStream.newLineAtOffset(xOffset, currentY);
                        contentStream.showText(sanitizeText(request.getLanguages()));
                        contentStream.endText();
                    }

                } finally {
                    contentStream.close();
                }
            }

            // Save updated PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            logger.info("PDF updated successfully");
            return outputStream.toByteArray();

        } catch (Exception e) {
            logger.error("Error applying edits to PDF", e);
            throw new IOException("Failed to edit PDF", e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    logger.warn("Error closing document", e);
                }
            }
        }
    }

    // Helper methods (unchanged)
    private float findSectionY(String text, String sectionRegex, float pageHeight) {
        Pattern pattern = Pattern.compile(sectionRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return pageHeight - 100;
        }
        return pageHeight - 50;
    }

    private PDFont getOriginalFont(PDPage page) {
        try {
            PDResources resources = page.getResources();
            for (COSName fontName : resources.getFontNames()) {
                PDFont font = resources.getFont(fontName);
                if (font != null) {
                    return font;
                }
            }
        } catch (Exception e) {
            logger.warn("Could not extract original font", e);
        }
        return null;
    }
}