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
public class CreateCommitTool extends GitHubMcpTool {

    public CreateCommitTool(WebClient.Builder builder, @Value("${github.token}") String token) {
        super(builder, token);
    }

    @Override
    public McpSchema.Tool getSpecification() {
        var props = Map.<String, Object>of(
                "owner", Map.of("type", "string", "description", "Repository owner"),
                "repo", Map.of("type", "string", "description", "Repository name"),
                "path", Map.of("type", "string", "description", "File path (e.g. README.md)"),
                "message", Map.of("type", "string", "description", "Commit message"),
                "content", Map.of("type", "string", "description", "Base64 encoded content")
        );

        return McpSchema.Tool.builder()
                .name("create_commit")
                .description("Creates or updates a file in a repository (commits changes)")
                .inputSchema(McpSchemaFactory.objectSchema(props, List.of("owner", "repo", "path", "message", "content")))
                .build();
    }

    @Override
    public Mono<McpSchema.CallToolResult> execute(McpAsyncServerExchange exchange, Map<String, Object> arguments) {
        String owner = (String) arguments.get("owner");
        String repo = (String) arguments.get("repo");
        String path = (String) arguments.get("path");

        return webClient.put() // GitHub uses PUT for file content creation
                .uri("/repos/{owner}/{repo}/contents/{path}", owner, repo, path)
                .bodyValue(Map.of(
                        "message", arguments.get("message"),
                        "content", arguments.get("content")
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> {
                    log.info("File committed successfully to {}/{}", owner, repo);
                    return McpSchema.CallToolResult.builder()
                            .content(List.of(new McpSchema.TextContent("Successfully committed to " + path)))
                            .build();
                })
                .onErrorResume(e -> Mono.just(McpSchema.CallToolResult.builder()
                        .content(List.of(new McpSchema.TextContent("Commit failed: " + e.getMessage())))
                        .isError(true).build()));
    }
}