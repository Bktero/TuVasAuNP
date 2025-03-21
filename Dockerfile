# Stage 1: Build the application
FROM gradle:jdk23 AS builder
WORKDIR /home/gradle/
COPY --chown=gradle:gradle . .
RUN gradle installDist --no-daemon

# Stage 2: Run the application
FROM openjdk:23-jdk-slim

# Install tools to ease debugging and administration
RUN apt-get update && apt-get install -y --no-install-recommends tree vim

# Import and run the Kotlin app
WORKDIR /app
COPY --from=builder /home/gradle/build/install/TuVasAuNpBot .
ENTRYPOINT ["/app/bin/TuVasAuNpBot" ]
