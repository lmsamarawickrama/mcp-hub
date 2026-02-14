package com.saliam.ai.mcp.hub.prompt.github;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class CommitMessagePrompt extends GitHubMcpPrompt {

    public CommitMessagePrompt(WebClient.Builder builder, @Value("${github.token}") String token) {
        super(builder, token);
    }

    @Override
    public McpSchema.Prompt getSpecification() {
        return new McpSchema.Prompt(
                "generate-commit",
                "Suggests a professional commit message based on a description",
                List.of(new McpSchema.PromptArgument("description", "What was changed?", true))
        );
    }

    @Override
    public Mono<McpSchema.GetPromptResult> execute(McpSchema.GetPromptRequest request) {
        String description = (String) request.arguments().get("description");
        log.info("Generating commit message for: {}", description);

        // Return the prompt result with a system message and a user message
        return Mono.just(new McpSchema.GetPromptResult(
                """
                        You are a git expert. Write a concise, conventional commit message. Follow these rules:
                        - Keep the subject line under 50 characters.
                        - Capitalize the subject line.
                        - Do not end the subject line with a period.
                        - Use the imperative mood in the subject (e.g., "Fix bug").
                        - Wrap the body at 72 characters.
                        - Use the body to explain what and why, not how.
                        
                        Example:
                        Implement feature flag for new user registration
                        
                        Add a feature flag to control access to the new user registration functionality, allowing selective rollout and testing before full deployment.
                        
                        """,
                List.of(new McpSchema.PromptMessage(
                        McpSchema.Role.USER,
                        new McpSchema.TextContent("The change is: " + description)
                ))
        ));
    }
}
