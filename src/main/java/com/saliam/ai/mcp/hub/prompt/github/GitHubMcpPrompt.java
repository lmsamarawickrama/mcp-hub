package com.saliam.ai.mcp.hub.prompt;

public abstract class GitHubMcpPrompt implements McpPrompt{

    public CommitMessagePrompt(WebClient.Builder builder, @Value("${github.token}") String token) {
        super(builder, token);
    }

}
