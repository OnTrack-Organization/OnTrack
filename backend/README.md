# OnTrack Backend
## Requirements
- Local installation of Docker/Docker-compose

## Setup Dev Environment
Run `docker compose up -d` to start the backend services.
The project is configured to automatically restart when changes are detected after building
in order to apply the new code changes as soon as possible.
When working in Intellij enable the following options:
- `Settings` > `Advanced Settings` > Enable `Allow auto-make to start even if application is currently running`
- `Settings` > `Build, Execution, Deployment` > `Compiler` > Enable `Build project automatically`

Now your code changes will apply after a few seconds automatically. 
If you need to apply them immediately, you can build the project manually:
- In the top navigation bar `Build` > `Build project` (or CMD + F9 shortcut on MacOS) 

### Setup Debugger
1. Setup Debug Listener
   - Create a new Run Configuration
   - Select "Remote JVM Debug"
   - Pick the "Attach to remote JVM" (default)
   - Host: `localhost` (default)
   - Port: 5005 (default)
   - CMD line arguments for remote JVM: <br> `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005` (default)
2. (optional) Run Debug Configuration to start debugging

### Make Browser trust DEV TLS/SSL Certificate
1. Install https://github.com/FiloSottile/mkcert
2. Run `mkcert --install`
3. Run `scripts/generate-dev-cert.sh`
4. Make sure you get the output
```
The local CA is now installed in the system/Firefox trust store!
```

### Running DB Migrations
This project uses Liquibase for managing DB migrations.
Run the `db/migrate.sh` script to update or rollback the DB schema.
For more infos run `db/migrate.sh help`.

