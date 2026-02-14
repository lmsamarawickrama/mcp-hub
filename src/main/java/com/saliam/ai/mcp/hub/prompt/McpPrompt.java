package com.saliam.ai.mcp.hub.prompt;

import io.modelcontextprotocol.server.McpAsyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface McpPrompt {

    McpSchema.Prompt getSpecification();

    Mono<McpSchema.GetPromptResult> execute(McpSchema.GetPromptRequest request);
}
