package com.demo.cloud.embedding.controller;

import com.demo.cloud.embedding.dto.ChatRequest;
import com.demo.cloud.embedding.vo.ApiResult;
import com.demo.cloud.embedding.vo.ChatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "RAG智能问答")
@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Operation(summary = "基于知识库的智能问答")
    @PostMapping("/chat")
    public ApiResult<ChatVO> chat(@Valid @RequestBody ChatRequest request) {
        var spec = chatClient.prompt().user(request.getQuestion());

        // Apply filter expression at runtime if provided
        if (request.getFilterExpression() != null && !request.getFilterExpression().isBlank()) {
            spec.advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, request.getFilterExpression()));
        }

        String answer = spec.call().content();
        return ApiResult.success(new ChatVO(answer));
    }
}
