# MCP Hub Server

A central Model Context Protocol (MCP) hub server built with Spring Boot that registers tools and prompts, and exposes an HTTP MCP endpoint for organization-wide use.

## Features
- HTTP MCP endpoint at `/mcp` for tool and prompt calls
- GitHub tools (e.g., `fetch_repositories`) using per-request token via custom header
- Prompt registration (e.g., commit message prompt)
- Extensible tool/prompt registry via Spring beans

## Requirements
- Java 17+
- Maven 3.8+

## Build and Run

1. Build the project:
```
./mvnw clean package
```
On Windows PowerShell:
```
./mvnw.cmd clean package
```

2. Run the server:
```
java -jar target/mcp-hub-*.jar
```

The server starts on port 8081 by default (check `src/main/resources/application.yaml`).

## MCP Client Configuration
Configure your MCP client (e.g., GitHub Copilot) to use the hub:

```
{
  "servers": {
    "mcp-hub": {
      "type": "http",
      "url": "http://localhost:8081/mcp",
      "headers": {
        "X-GITHUB-TOKEN": "<your_personal_access_token>"
      }
    }
  }
}
```

## Authentication via Custom Header
- The server expects a GitHub token in the `X-GITHUB-TOKEN` header for GitHub-related tools.
- Tools read the token from the MCP exchange transport context and call GitHub APIs.
- If the header is missing, tools return a clear error message.

## Tools
- `fetch_repositories`: Lists repositories for the authenticated user
  - Path: `com.saliam.ai.mcp.hub.tool.github.FetchRepositoriesTool`
  - Requires `X-GITHUB-TOKEN`

Add more tools under `src/main/java/com/saliam/ai/mcp/hub/tool/**`.

## Prompts
- Prompts can be added under `src/main/java/com/saliam/ai/mcp/hub/prompt/**` and are registered in `McpRegistryConfig`.

## Development Notes
- Tool and prompt registration happens in `McpRegistryConfig` using `McpAsyncServer` and `McpServerFeatures`.
- Tokens are extracted from the MCP transport context; ensure your client sends headers as shown above.

## Troubleshooting
- If MCP requests time out or return 500:
  - Ensure the server is running on port 8081 and `/mcp` is reachable
  - Verify your MCP client includes `X-GITHUB-TOKEN`
  - Check server logs for exceptions during tool dispatch
- Use PowerShell to validate your token:
```
Invoke-RestMethod -Headers @{ Authorization = "Bearer <token>"; Accept = "application/vnd.github+json" } -Uri "https://api.github.com/user" -Method Get
Invoke-RestMethod -Headers @{ Authorization = "Bearer <token>"; Accept = "application/vnd.github+json" } -Uri "https://api.github.com/user/repos?per_page=100&sort=updated" -Method Get
```

## License
Proprietary – internal use within the organization.
