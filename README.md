# OTP Generation System

A Java-based one-time password (OTP) system built with Spring Boot. The OTP service generates and verifies codes, while a separate notification service consumes OTP events from Kafka and handles message delivery.

> **Development status:** SMS and email notifications are currently written to the notification service logs. No external SMS or email provider is configured.

## Features

- Generate and verify OTPs sent to an email address or phone number.
- Store users, OTP records, and audit events in PostgreSQL.
- Cache active OTP data and enforce rate limits with Redis.
- Publish OTP events to Kafka for asynchronous notification handling.
- Validate input and track OTP expiration and verification attempts.

## Architecture

```text
Client
  |
  v
OTP Service (Spring Boot REST API)
  |             |             |
  v             v             v
PostgreSQL     Redis         Kafka
                              |
                              v
                    Notification Service
                    (logs SMS/email messages)
```

The repository is a Maven multi-module project:

| Module | Purpose |
| --- | --- |
| `otp-service` | REST API for sending and verifying OTPs |
| `notification-service` | Consumes OTP events and logs SMS/email messages |
| `common` | Event and enum types shared by the services |

## Technology

- Java 17
- Spring Boot
- Maven
- PostgreSQL
- Redis
- Apache Kafka
- Docker Compose

## Prerequisites

- Java 17 or later
- Maven 3.9 or later (optional if you use the provided Docker workflow)
- Docker Engine and the Docker Compose plugin

## Run with Docker Compose

1. Create a `.env` file in the repository root:

   ```dotenv
   POSTGRES_PASSWORD=replace-with-a-strong-password
   OTP_HMAC_SECRET=replace-with-a-long-random-secret
   ```

   Do not commit `.env`. Docker Compose requires both variables. Use a unique, securely generated `OTP_HMAC_SECRET` outside local development.

2. Start the services:

   ```bash
   docker compose up --build
   ```

   The stack includes PostgreSQL, Redis, Kafka, the OTP service, and the notification service. PostgreSQL initializes its schema from `otp-service/src/main/resources/db`.

3. The REST API is available at `http://localhost:8080`.

To stop the containers, press `Ctrl+C` and run:

```bash
docker compose down
```

To also delete persisted PostgreSQL and Kafka data, run `docker compose down -v`. This permanently removes the data in the Compose-managed volumes.

## API

### Send an OTP

`POST /api/v1/otp/send`

```json
{
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "purpose": "SIGNUP"
}
```

Valid channels are `EMAIL` and `SMS`. Valid purposes are `LOGIN`, `SIGNUP`, `PASSWORD_RESET`, and `PAYMENT`.

Example:

```bash
curl -X POST http://localhost:8080/api/v1/otp/send \
  -H "Content-Type: application/json" \
  -d '{"channel":"EMAIL","recipient":"user@example.com","purpose":"SIGNUP"}'
```

A successful request returns HTTP `200` with a message indicating the code's expiry time. Requests exceeding the configured rate limit return HTTP `429`.

### Verify an OTP

`POST /api/v1/otp/verify`

```json
{
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "purpose": "SIGNUP",
  "otp": "123456"
}
```

Example:

```bash
curl -X POST http://localhost:8080/api/v1/otp/verify \
  -H "Content-Type: application/json" \
  -d '{"channel":"EMAIL","recipient":"user@example.com","purpose":"SIGNUP","otp":"123456"}'
```

The endpoint returns HTTP `200` when the code is verified, HTTP `400` for an invalid or expired code, and HTTP `429` when the maximum number of incorrect attempts is reached. The OTP defaults to six digits and expires after five minutes; rate limits and OTP behavior are configured in the OTP service's `application.yml`.

## Build

Build all modules from the repository root:

```bash
mvn clean package
```

## Configuration

The Docker Compose configuration reads these required values from the root `.env` file:

| Variable | Purpose |
| --- | --- |
| `POSTGRES_PASSWORD` | Password assigned to the PostgreSQL container |
| `OTP_HMAC_SECRET` | Secret used to HMAC OTP values |

The committed Spring configuration contains local development connection settings. For deployments, supply appropriate service URLs and credentials through environment-specific configuration, and never use development secrets.

## Security notes

- Do not commit `.env` files, real credentials, or production secrets.
- Replace the example values above with secure values.
- The notification service currently logs OTP message content. Treat its logs as sensitive and do not use this implementation to deliver production OTPs.
- Configure and integrate a production SMS/email provider before using this system for real users.
