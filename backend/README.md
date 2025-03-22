# OnTrack Backend
## Requirements
- Local installation of Docker/Docker-compose

## Setup Dev Environment
### Make Browser trust Dev TLS/SSL Certificate
1. Copy the `.env.dev` file as `.env`
2. Ask a maintainer for the Cloudflare credentials

### Running DB Migrations in Dev
This project uses Liquibase for managing DB migrations.
Run the `db/migrate.sh` script to update or rollback the DB schema.
For more infos run `db/migrate.sh help`.

### Run the application
Run `docker compose up -d` to start the backend services.

#### Automatic Build & Restart
The project is configured to automatically restart when changes are detected after building
in order to apply the new code changes as soon as possible.
When working in Intellij enable the following options:
- `Settings` > `Advanced Settings` > Enable `Allow auto-make to start even if application is currently running`
- `Settings` > `Build, Execution, Deployment` > `Compiler` > Enable `Build project automatically`

Now your code changes will apply after a few seconds automatically. 
If you need to apply them immediately, you can build the project manually:
- In the top navigation bar `Build` > `Build project` (or CMD + F9 shortcut on MacOS) 

### Setup Debugger
Since the application runs within Docker, we cannot debug directly. 
We have to attach to the Container explicitly: 
1. Setup Debug Config
   - Create a new Run Configuration
   - Select "Remote JVM Debug"
   - Pick the "Attach to remote JVM" (default)
   - Host: `localhost` (default)
   - Port: 5005 (default)
   - CMD line arguments for remote JVM: <br> `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005` (default)
2. Set a breakpoint
3. Run Debug Configuration to start debugging
4. Run a request against the breakpoint

