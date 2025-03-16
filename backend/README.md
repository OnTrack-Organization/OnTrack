# On Track Backend

## Setup Dev Environment
1. Run `docker compose up -d` to start the backend services
2. Setup Debug Listener
   - Create a new Run Configuration
   - Select "Remote JVM Debug"
   - Pick the "Attach to remote JVM" (default)
   - Host: `localhost` (default)
   - Port: 5005 (default)
   - CMD line arguments for remote JVM: <br> `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005` (default)
3. (optional) Run Debug Configuration to start debugging

