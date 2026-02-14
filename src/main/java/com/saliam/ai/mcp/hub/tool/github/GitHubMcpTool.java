package com.saliam.ai.mcp.hub.tool.github;

import com.saliam.ai.mcp.hub.tool.McpTool;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class GitHubMcpTool implements McpTool {

    protected final WebClient webClient;

    protected GitHubMcpTool(WebClient.Builder builder, String apiToken) {
        this.webClient = builder
                .baseUrl("https://api.github.com")
                .defaultHeader("Authorization", "Bearer " + apiToken)
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
    }
}
