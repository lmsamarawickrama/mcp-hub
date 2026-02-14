package com.saliam.ai.mcp.hub.prompt.github;

import com.saliam.ai.mcp.hub.prompt.McpPrompt;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class GitHubMcpPrompt implements McpPrompt {

    protected final WebClient webClient;

    protected GitHubMcpPrompt(WebClient.Builder builder, String token) {
        this.webClient = builder.baseUrl("https://api.github.com").build();
    }
}
