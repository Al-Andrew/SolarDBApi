# SolarDB API (Java)

## Build (Gradle)

This repo includes `build.gradle`, but **does not include the Gradle wrapper** (`./gradlew`) to avoid committing binary wrapper JARs.

- **Build**:

```bash
cd /home/aaldea/Work/dev/SolarDB/solardb-api
gradle build
```

- **Run**:

```bash
cd /home/aaldea/Work/dev/SolarDB/solardb-api
gradle bootRun
```

## Database

Configure Postgres in `src/main/resources/application.yml`:

- DB: `solardb`
- user: `solardb`
- pass: `solardb`

Flyway will create tables on startup.

## Ingest API

- **POST** `/api/v1/readings`
- **Body**: JSON shaped like `../example.json`
- **Response**: `201 Created` with `{ "readingId": <id> }`

