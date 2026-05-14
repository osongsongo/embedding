package com.demo.cloud.embedding.service.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.core.io.Resource;

public class CsvDocumentReader implements DocumentReader {

    private final Resource resource;

    public CsvDocumentReader(Resource resource) {
        this.resource = resource;
    }

    @Override
    public List<Document> get() {
        try {
            List<String> rows = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        if (line.startsWith("\uFEFF")) {
                            line = line.substring(1);
                        }
                        firstLine = false;
                    }
                    if (line.isBlank()) {
                        continue;
                    }
                    rows.add(parseLine(line));
                }
            }

            String content = String.join("\n", rows);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("source_type", "csv");
            metadata.put("source_name", resource.getFilename());

            Document document = new Document(content, metadata);
            return List.of(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV resource: " + resource.getFilename(), e);
        }
    }

    private String parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString().trim());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString().trim());

        return String.join(" ", fields);
    }
}
