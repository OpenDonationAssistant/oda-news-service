# ODA News Service

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/OpenDonationAssistant/oda-news-service)

## Running with Docker

### Pull the image

```bash
docker pull ghcr.io/opendonationassistant/oda-news-service:latest
```

### Required Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `JDBC_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/postgres?currentSchema=news` |
| `JDBC_USER` | Database username | `postgres` |
| `JDBC_PASSWORD` | Database password | `postgres` |

### Running the container

```bash
docker run -d \
  --name oda-news-service \
  -e JDBC_URL="jdbc:postgresql://<db-host>:5432/<db-name>?currentSchema=news" \
  -e JDBC_USER="<db-username>" \
  -e JDBC_PASSWORD="<db-password>" \
  ghcr.io/opendonationassistant/oda-news-service:latest
```

Example with local PostgreSQL:

```bash
docker run -d \
  --name oda-news-service \
  -e JDBC_URL="jdbc:postgresql://host.docker.internal:5432/postgres?currentSchema=news" \
  -e JDBC_USER="postgres" \
  -e JDBC_PASSWORD="postgres" \
  ghcr.io/opendonationassistant/oda-news-service:latest
```
