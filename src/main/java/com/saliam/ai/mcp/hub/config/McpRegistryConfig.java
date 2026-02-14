package com.saliam.ai.mcp.hub.config;


import com.saliam.ai.mcp.hub.prompt.McpPrompt;
import com.saliam.ai.mcp.hub.tool.McpTool;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class McpRegistryConfig {

    @Bean
    public SmartInitializingSingleton registerMcpComponents(
            McpAsyncServer server,
            List<McpTool> toolComponents,
            List<McpPrompt> promptComponents) {
        return () -> {
            toolComponents.forEach(tool -> {
                McpServerFeatures.AsyncToolSpecification spec =
                        McpServerFeatures.AsyncToolSpecification
                                .builder()
                                .tool(tool.getSpecification())
                                .callHandler((exchange, request) ->
                                        tool.execute(exchange, request.arguments()))
                                .build();
                server.addTool(spec).block();
                log.info("Registered tool: {}", tool.getSpecification().name());
            });
            log.info("Total tools registered: {}", toolComponents.size());

            promptComponents.forEach(prompt -> {
                var spec = new McpServerFeatures.AsyncPromptSpecification(
                        prompt.getSpecification(),
                        (exchange, request) -> prompt.execute(request)
                );
                server.addPrompt(spec).block();
                log.info("Registered prompt: {}", prompt.getSpecification().name());

            });

            log.info("MCP registration complete. Tools: {}, Prompts: {}",
                    toolComponents.size(), promptComponents.size());
        };
    }
}