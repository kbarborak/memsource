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

## Configure Memsource REST API access

```
curl -d '{"username": "username", "password": "password"}' -H "Content-Type: application/json" -X POST http://localhost:8080/api/configurations 
```

## List of available projects

Configuration endpoint must be called first.

```
curl http://localhost:8080/api/projects
```
