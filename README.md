# MCP Hub Server

A central Model Context Protocol (MCP) hub server built with Spring Boot that registers tools and prompts, and exposes an HTTP MCP endpoint for organization-wide use.

## Features
- HTTP MCP endpoint at `/mcp` for tool and prompt calls
- GitHub tools (e.g., `fetch_repositories`) currently use a token from application properties
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

## Authentication (Current Behavior)
Currently, the GitHub token is read from application properties (`github.token`) defined in `application.yaml`. You can override it via an environment variable before starting the server:

Windows PowerShell:
```
$env:GITHUB_TOKEN = "<your_personal_access_token>"; java -jar target/mcp-hub-*.jar
```

application.yaml snippet:
```
github:
  token: ${GITHUB_TOKEN:ghp_example_default}
```

## Planned Authentication via Custom Header
Header-based per-request authentication (e.g., `X-GITHUB-TOKEN`) is planned so different users can pass their own tokens through the MCP client (like Copilot). Once implemented, tools will extract the token from the MCP transport context. Until then, use the application property as described above.

## MCP Client Configuration (Example)
You may configure your MCP client (e.g., Copilot) to call the hub, but note that as of now the server does not consume `X-GITHUB-TOKEN` for tool calls:
```
{
  "servers": {
    "mcp-hub": {
      "type": "http",
      "url": "http://localhost:8081/mcp"
      // headers like X-GITHUB-TOKEN will be supported in a future update
    }
  }
}
```

## Tools
- `fetch_repositories`: Lists repositories for the authenticated user
  - Path: `com.saliam.ai.mcp.hub.tool.github.FetchRepositoriesTool`
  - Uses `github.token` from application properties at runtime

Add more tools under `src/main/java/com/saliam/ai/mcp/hub/tool/**`.

## Prompts
- Prompts can be added under `src/main/java/com/saliam/ai/mcp/hub/prompt/**` and are registered in `McpRegistryConfig`.

## Development Notes
- Tool and prompt registration happens in `McpRegistryConfig` using `McpAsyncServer` and `McpServerFeatures`.
- GitHub authentication currently reads from `github.token` in `application.yaml`.

## Troubleshooting
- If MCP requests time out or return 500:
  - Ensure the server is running on port 8081 and `/mcp` is reachable
  - Verify the `GITHUB_TOKEN` environment variable or `application.yaml` value is set correctly
  - Check server logs for exceptions during tool dispatch
- Use PowerShell to validate your token:
```
Invoke-RestMethod -Headers @{ Authorization = "Bearer <token>"; Accept = "application/vnd.github+json" } -Uri "https://api.github.com/user" -Method Get
Invoke-RestMethod -Headers @{ Authorization = "Bearer <token>"; Accept = "application/vnd.github+json" } -Uri "https://api.github.com/user/repos?per_page=100&sort=updated" -Method Get
```

## License
Proprietary – internal use within the organization.
