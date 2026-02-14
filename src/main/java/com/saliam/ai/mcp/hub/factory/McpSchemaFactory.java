package com.saliam.ai.mcp.hub.factory;

import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;

public class McpSchemaFactory {

    public static McpSchema.JsonSchema objectSchema(Map<String, Object> properties, List<String> required) {
        return new McpSchema.JsonSchema(
                "object",
                properties != null ? properties : Map.of(),
                required != null ? required : List.of(),
                false,
                null,  // $defs
                null   // definitions
        );
    }

    public static McpSchema.JsonSchema emptySchema() {
        return objectSchema(Map.of(), List.of());
    }
}
