# Refactoring and design patterns

Volunteer Impact Coordinator, CAB302 Group 4

This document covers the two refactors made during the Week 11 sprint, the
Factory and Builder patterns they introduced, and the patterns that were
already in place. For the overall architecture and OO principles, see
[`design/oo-design-notes.md`](design/oo-design-notes.md).

## Summary

| Refactor | Pattern | Problem it solved | Where |
|---|---|---|---|
| `ServiceFactory` | Factory (held as a Singleton) | Every controller built its own service and SQLite DAO, so all five were tied to SQLite | `service/ServiceFactory.java` |
| `Event.Builder` | Builder | `Event` was created through an eight-argument constructor that was easy to get wrong | `model/Event.java` |

Both were refactors in the strict sense: the app behaves exactly as before,
and the existing tests pass unchanged.

---

## Refactor 1: Factory pattern, `ServiceFactory`

### The problem

The "Known limitation" section of `oo-design-notes.md` described this before
the sprint. Dependency injection reached the services, but stopped at the
controllers. Each controller wired up its own chain:

```java
// EventFormController, before
private final EventService eventService =
        new EventService(new SqliteEventDAO(DatabaseManager.getInstance()));
```

The same line, or its `AuthService` equivalent, appeared in all five
controllers. That had three costs:

- **Duplication.** Five copies of the same construction.
- **Coupling.** Every controller imported and named `SqliteEventDAO` or
  `SqliteUserDAO`, even though the services above them only depend on the DAO
  interfaces. Switching storage would mean editing every controller.
- **Separate instances.** Each screen got its own service object rather than
  sharing one.

### The change

`ServiceFactory` builds each service once and hands the same instance to any
controller that asks:

```java
// EventFormController, after
private final EventService eventService = ServiceFactory.getInstance().events();
```

- **Factory:** `forDatabase(DatabaseManager)` decides which DAOs back the
  services. This is now the only place in the app that names a SQLite class.
- **Singleton:** `getInstance()` returns one shared factory, created on first
  use.
- **Injection is still possible:** the public constructor
  `ServiceFactory(UserDAO, EventDAO)` accepts any DAO implementation. The tests
  use it with the in-memory fakes.

### What it bought us

- Controllers no longer import anything from the `dao` package.
- Swapping SQLite for another database means changing `forDatabase()` and
  nothing else.
- New services, such as `SignupService` and `HoursService`, can be added in one
  place, and every screen can use them.

### Trade-off

A Singleton is global state, which can make tests depend on each other. We
limited that by keeping the constructor public, so tests build their own
factory from fake DAOs and never touch the shared instance's database. The
controllers themselves still call `getInstance()` rather than receiving the
factory as a parameter, because JavaFX creates controllers from FXML and passing
arguments in would mean a custom controller factory. That is a possible next
step, not something the prototype needs.

---

## Refactor 2: Builder pattern, `Event.Builder`

### The problem

An `Event` had one constructor with eight positional parameters, five of them
strings:

```java
// EventService, before
Event event = new Event(0, title.trim(), safe(description), eventDate,
        safe(eventTime), safe(location), volunteersNeeded, createdBy);
```

Swapping `eventDate` and `eventTime`, or `description` and `location`, still
compiles, and the mistake only shows up as wrong data on screen. The leading
`0` (no id yet) gives no hint of what it means, and every caller had to
remember to turn null text into empty strings.

### The change

`Event.builder()` names each field as it is set:

```java
// EventService, after
Event event = Event.builder()
        .title(title.trim())
        .description(safe(description))
        .date(eventDate)
        .time(safe(eventTime))
        .location(safe(location))
        .volunteersNeeded(volunteersNeeded)
        .createdBy(createdBy)
        .build();
```

`SqliteEventDAO.mapRow()` was changed the same way, so both places that create
events now use the builder.

The builder:

- **Defaults optional text to empty strings,** so nothing downstream has to
  null-check the description, time or location.
- **Leaves the id unset** for new events, instead of a bare `0`.
- **Refuses incomplete events.** `build()` throws if the title or date is
  missing.

### Keeping it a refactor, not a rewrite

- The eight-argument constructor was kept, so existing code and tests carry on
  working. Its Javadoc now points people to the builder.
- `build()` only checks that an event is complete enough to exist. Business
  rules, such as "an event can't be in the past", stay in `EventService`, so
  validation still lives in one layer.

---

## Patterns already in place

These were in the design before the sprint and are described in more detail in
`oo-design-notes.md`.

| Pattern | Where |
|---|---|
| **MVC** | FXML views, the `controller` package and the `model` package |
| **DAO** | `UserDAO`, `EventDAO`, `SignupDAO`, `HoursDAO`, each with a SQLite implementation |
| **Singleton** | `DatabaseManager`, `SessionManager`, and now `ServiceFactory` |
| **Dependency injection** | Services receive their DAOs through the constructor |
| **Test doubles** | `FakeUserDAO`, `FakeEventDAO`, `FakeSignupDAO` and `FakeHoursDAO` let the services be tested without a database |

The new signup and hours services follow the same shape as the original ones:
an interface, a SQLite DAO, a fake DAO for tests, and a service that depends
only on the interfaces. They also take a `Clock`, so tests can fix "today" and
the date rules don't depend on when the tests run.

---

## How the refactors were verified

- **Existing tests unchanged.** `EventServiceTest`, `AuthServiceTest` and
  `EventTest` passed before and after each refactor without edits, which shows
  behaviour didn't change.
- **New tests for each pattern.** `ServiceFactoryTest` checks that the factory
  returns shared services backed by the DAOs it was given. `EventBuilderTest`
  checks every field, the defaults, the required-field checks, and that the
  builder produces the same event as the old constructor.
- **CI.** Every pull request runs the full test suite, packages the runnable
  JAR and starts it on a virtual display, so a refactor that broke start-up
  would fail the build.

---

## Where to find everything

| What | Path |
|---|---|
| Factory | `src/main/java/com/cab302/vic/service/ServiceFactory.java` |
| Builder | `src/main/java/com/cab302/vic/model/Event.java` (`Event.Builder`) |
| Factory tests | `src/test/java/com/cab302/vic/service/ServiceFactoryTest.java` |
| Builder tests | `src/test/java/com/cab302/vic/model/EventBuilderTest.java` |
| Architecture and OO principles | `docs/design/oo-design-notes.md` |
