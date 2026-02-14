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
public class FetchRepositoriesTool extends GitHubMcpTool {

    public FetchRepositoriesTool(WebClient.Builder builder, @Value("${github.token}") String token) {
        super(builder, token);
    }

    @Override
    public McpSchema.Tool getSpecification() {

        return McpSchema.Tool.builder()
                .name("fetch_repositories")
                .description("Lists repositories for the authenticated user")
                .inputSchema(McpSchemaFactory.emptySchema())
                .build();
    }

    @Override
    public Mono<McpSchema.CallToolResult> execute(McpAsyncServerExchange exchange, Map<String, Object> arguments) {
        return webClient.get()
                .uri("/user/repos")
                .retrieve()
                .bodyToFlux(Map.class)
                .map(repo -> repo.get("full_name").toString())
                .collectList()
                .map(repos -> {
                    log.info("Total repositories fetched: {}", repos.size());
                    String resultText = "Recent Repositories: " + String.join(", ", repos);


                    return McpSchema.CallToolResult.builder()
                            .content(List.of(new McpSchema.TextContent(resultText)))
                            .isError(false)
                            .build();
                })
                .onErrorResume(e -> Mono.just(
                        McpSchema.CallToolResult.builder()
                                .content(List.of(new McpSchema.TextContent("GitHub Error: " + e.getMessage())))
                                .isError(true)
                                .build()
                ));
    }
}