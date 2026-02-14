package com.saliam.ai.mcp.hub.tool.github;

import com.saliam.ai.mcp.hub.factory.McpSchemaFactory;
import io.modelcontextprotocol.server.McpAsyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CreateRepositoryTool extends GitHubMcpTool {

    public CreateRepositoryTool(WebClient.Builder builder, @Value("${github.token}") String token) {
        super(builder, token);
    }

    @Override
    public McpSchema.Tool getSpecification() {
        var props = Map.<String, Object>of(
                "name", Map.of("type", "string", "description", "The name of the repository"),
                "private", Map.of("type", "boolean", "description", "Whether the repository is private")
        );

        return McpSchema.Tool.builder()
                .name("create_repository")
                .description("Creates a new repository for the authenticated user")
                .inputSchema(McpSchemaFactory.objectSchema(props, List.of("name")))
                .build();
    }

    @Override
    public Mono<McpSchema.CallToolResult> execute(McpAsyncServerExchange exchange, Map<String, Object> arguments) {
        return webClient.post()
                .uri("/user/repos")
                .bodyValue(arguments)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    String htmlUrl = (String) response.get("html_url");
                    log.info("Repository created: {}", htmlUrl);
                    return McpSchema.CallToolResult.builder()
                            .content(List.of(new McpSchema.TextContent("Created repository at: " + htmlUrl)))
                            .build();
                })
                .onErrorResume(e -> Mono.just(McpSchema.CallToolResult.builder()
                        .content(List.of(new McpSchema.TextContent("Failed to create repo: " + e.getMessage())))
                        .isError(true).build()));
    }
}