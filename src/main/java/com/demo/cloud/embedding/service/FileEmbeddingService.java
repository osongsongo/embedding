package com.demo.cloud.embedding.service;

import com.demo.cloud.embedding.service.reader.CsvDocumentReader;
import com.demo.cloud.embedding.vo.FileUploadVO;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileEmbeddingService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter textSplitter;

    public FileEmbeddingService(VectorStore vectorStore, TokenTextSplitter textSplitter) {
        this.vectorStore = vectorStore;
        this.textSplitter = textSplitter;
    }

    public FileUploadVO embedFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        // 1. Read documents from file
        List<Document> documents = readDocuments(file, extension);

        // 2. Add source metadata to all documents
        for (Document doc : documents) {
            doc.getMetadata().put("source_type", extension);
            doc.getMetadata().put("source_name", filename);
        }

        // 3. Split documents into chunks
        List<Document> splitDocuments = textSplitter.apply(documents);

        // 4. Add to vector store (auto-embeds)
        vectorStore.add(splitDocuments);

        // 5. Build result
        return new FileUploadVO(splitDocuments.size(), splitDocuments.size(), List.of(filename));
    }

    private List<Document> readDocuments(MultipartFile file, String fileType) {
        var resource = file.getResource();
        return switch (fileType) {
            case "txt" -> new TextReader(resource).get();
            case "pdf" -> readPdf(resource);
            case "docx" -> new TikaDocumentReader(resource).get();
            case "csv" -> new CsvDocumentReader(resource).get();
            default -> throw new RuntimeException("不支持的文件类型: " + fileType);
        };
    }

    private List<Document> readPdf(org.springframework.core.io.Resource resource) {
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());
        List<Document> documents = pdfReader.get();

        // Scanned PDF check: if all document contents are blank/empty, throw error
        boolean allBlank = documents.stream()
                .allMatch(doc -> !StringUtils.hasText(doc.getText()));
        if (allBlank) {
            throw new RuntimeException("PDF无法提取文本，可能是扫描版PDF");
        }

        return documents;
    }
}
