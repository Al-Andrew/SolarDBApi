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

## Docker

- **Build image**:

```bash
docker build -t solardb-api:local .
```

- **Run container** (defaults to port 8080):

```bash
docker run --rm -p 8080:8080 solardb-api:local
```

- **Run with environment overrides** (example):

```bash
docker run --rm -p 8080:8080 \
  -e SERVER_PORT=8080 \
  -e JAVA_OPTS="-Xms256m -Xmx512m" \
  solardb-api:local
```

## Database

Configure Postgres in `src/main/resources/application.yml`:

- **DB**: defaults to `solardb`
- **user**: defaults to `solardb`
- **pass**: defaults to `solardb`

The app reads common PaaS environment variables:

- **`SPRING_DATASOURCE_URL`** (preferred)
- **`DB_URL`** is also supported (including `postgres://...`; normalized to JDBC at startup)
- Or compose from **`DB_HOST`**, **`DB_PORT`**, and **`DB_DATABASE`** (or `DB_NAME`)
- Username from **`SPRING_DATASOURCE_USERNAME`**, `DB_USERNAME` (or `DB_USER`)
- Password from **`SPRING_DATASOURCE_PASSWORD`** or `DB_PASSWORD`

Flyway will create tables on startup.

## Ingest API

- **POST** `/api/v1/readings`
- **Body**: JSON shaped like `../example.json`
- **Response**: `201 Created` with `{ "readingId": <id> }`

