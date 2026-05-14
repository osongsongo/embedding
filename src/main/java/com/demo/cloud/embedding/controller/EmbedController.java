package com.demo.cloud.embedding.controller;

import com.demo.cloud.embedding.dto.EmbedRequest;
import com.demo.cloud.embedding.service.FileEmbeddingService;
import com.demo.cloud.embedding.vo.ApiResult;
import com.demo.cloud.embedding.vo.EmbedVO;
import com.demo.cloud.embedding.vo.FileUploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "文本嵌入")
@RestController
@RequestMapping("/api")
public class EmbedController {

    private final VectorStore vectorStore;
    private final FileEmbeddingService fileEmbeddingService;

    public EmbedController(VectorStore vectorStore, FileEmbeddingService fileEmbeddingService) {
        this.vectorStore = vectorStore;
        this.fileEmbeddingService = fileEmbeddingService;
    }

    @Operation(summary = "单文本嵌入")
    @PostMapping("/embed")
    public ApiResult<EmbedVO> embed(@Valid @RequestBody EmbedRequest request) {
        // Create Document with metadata
        Document document = new Document(request.getContent(),
                Map.of("source_type", "manual", "source_name", "manual"));

        // VectorStore.add() auto-embeds and stores
        vectorStore.add(List.of(document));

        // Build response
        EmbedVO vo = new EmbedVO(document.getId(), document.getText(), document.getMetadata());
        return ApiResult.success(vo);
    }

    @Operation(summary = "文件上传嵌入")
    @PostMapping("/embed/file")
    public ApiResult<FileUploadVO> embedFile(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            return ApiResult.fail(400, "无效的文件名");
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!List.of("txt", "csv", "pdf", "docx").contains(extension)) {
            return ApiResult.fail(400, "不支持的文件类型: " + extension);
        }

        FileUploadVO result = fileEmbeddingService.embedFile(file);
        return ApiResult.success(result);
    }
}
