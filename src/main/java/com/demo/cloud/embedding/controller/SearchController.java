package com.demo.cloud.embedding.controller;

import com.demo.cloud.embedding.dto.SearchRequest;
import com.demo.cloud.embedding.vo.ApiResult;
import com.demo.cloud.embedding.vo.SearchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "相似度搜索")
@RestController
@RequestMapping("/api")
public class SearchController {

    private final VectorStore vectorStore;

    public SearchController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Operation(summary = "自然语言相似度搜索")
    @PostMapping("/search")
    public ApiResult<List<SearchVO>> search(@Valid @RequestBody SearchRequest request) {
        // Build Spring AI SearchRequest — VectorStore auto-embeds the query
        var builder = org.springframework.ai.vectorstore.SearchRequest.builder()
                .query(request.getQuery())
                .topK(request.getTopK())
                .similarityThreshold(request.getThreshold());

        // Add filter expression if provided
        if (request.getFilterExpression() != null && !request.getFilterExpression().isBlank()) {
            builder.filterExpression(request.getFilterExpression());
        }

        // Execute similarity search
        List<Document> documents = vectorStore.similaritySearch(builder.build());

        // Convert to SearchVO
        List<SearchVO> results = documents.stream()
                .map(doc -> new SearchVO(
                        doc.getId(),
                        doc.getText(),
                        doc.getScore(),
                        doc.getMetadata()))
                .toList();

        return ApiResult.success(results);
    }
}
