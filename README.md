# Memsource Assignment

## Build

Globally installed JDK (11+) is needed for running and building with Gradle.

```
./gradlew build
```

## Run

```
java -jar build\libs\memsource-assignment-0.0.1-SNAPSHOT.jar
```

## Setup Memsource account credentials

```
curl -d '{"username": "username", "password": "password"}' -H "Content-Type: application/json" -X POST http://localhost:8080/api/credentials 
```

## List of available projects

```
curl http://localhost:8080/api/projects
```

Done!
