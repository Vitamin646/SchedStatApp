# SchedStatApp

Desktop app (Java + JavaFX) for planning, logging and analyzing strength training
and related metrics. Built mainly for personal / small-circle use.

## Requirements

- JDK 25 (LTS)
- No separate Maven install needed: use the bundled wrapper (`./mvnw`, or `mvnw.cmd` on Windows)

## Run

```
./mvnw clean javafx:run
```

## Test

```
./mvnw verify
```

## Project layout

- `app` – entry point (`Main`), `AppContext` (composition root), `ViewManager`
- `controller` – FXML controllers (receive dependencies through constructors)
- `service` – application services (e.g. `NotificationService`)
- `model` – domain model
- `util` – settings and helpers (`AppSettings`, `ViewType`)
- `src/main/resources/.../fxml` – views

## Roadmap decisions

- Scope for now: strength training only; the data model will be extended later
- Storage: SQLite with Flyway migrations
- Body and sleep metrics: not planned yet
- No release/distribution phase for now
