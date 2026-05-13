package com.example.agent.rag;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Files;

@Service
public class DocumentParseService {

    // 解析 PDF
    public String parsePdf(File file) throws Exception {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    // 解析 Markdown / Wiki
    public String parseMarkdown(File file) throws Exception {
        return Files.readString(file.toPath());
    }
}