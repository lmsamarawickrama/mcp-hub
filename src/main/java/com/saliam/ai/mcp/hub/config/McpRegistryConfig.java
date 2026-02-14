package com.saliam.ai.mcp.hub.config;


import com.saliam.ai.mcp.hub.tool.McpTool;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class McpRegistryConfig {

    @Bean
    public ApplicationRunner registerTools(
            McpAsyncServer server,
            List<McpTool> toolComponents) {

        return args -> {

            for (McpTool tool : toolComponents) {

                McpServerFeatures.AsyncToolSpecification spec =
                        McpServerFeatures.AsyncToolSpecification
                                .builder()
                                .tool(tool.getSpecification())
                                .callHandler((exchange, request) ->
                                        tool.execute(exchange, request.arguments()))
                                .build();

                server.addTool(spec).block();
                log.info("Registered tool: {}", tool.getSpecification().name());
            }

            log.info("Total tools registered: {}", toolComponents.size());
        };
    }
}