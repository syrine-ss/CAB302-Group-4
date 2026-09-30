# Refactoring and Object-Oriented Design Patterns

CAB302 Group 4 | Volunteer Impact Coordinator | Week 11 checkpoint

This document is the evidence for the refactoring and design pattern
criterion. Each entry states the problem in the code, what changed, and the
commit that changed it, so the work can be traced in the repository rather
than taken on trust.

Every refactor below was made with the test suite green before and after.
That is the point of a refactor: the behaviour does not change, only the
structure. The suite grew from 36 tests to 80 over this sprint, but no
existing test had to be rewritten to accommodate a refactor.

---

## Summary

| Pattern | Where | Why it is there |
|---|---|---|
| DAO | `dao/UserDAO`, `EventDAO`, `SignupDAO`, `HoursDAO` | Business logic never sees SQL |
| MVC | `view/*.fxml`, `controller/*`, `model/*` | Screens, coordination and data kept apart |
| Singleton | `DatabaseManager`, `SessionManager`, `ServiceFactory` | One shared connection, session and object graph |
| **Factory** | `service/ServiceFactory` | **Refactor: controllers stopped constructing SQLite DAOs** |
| **Builder** | `model/Event.Builder` | **Refactor: replaced an error-prone 8-argument constructor** |
| Test double (fake) | `test/.../Fake*DAO` | Services tested without a database |

The two in bold are new this sprint and are the refactoring evidence. The
others were already in place and are described briefly at the end.

---

## Refactor 1: Factory pattern, `ServiceFactory`

**Commit:** `e8dd871` refactor: introduce ServiceFactory so controllers stop building DAOs

### The problem

Every controller built its own dependency chain in a field initialiser:

```java
// LoginController, before
private final AuthService authService =
        new AuthService(new SqliteUserDAO(DatabaseManager.getInstance()));
```

```java
// CoordinatorDashboardController, before
private final EventService eventService =
        new EventService(new SqliteEventDAO(DatabaseManager.getInstance()));
```

The same shape appeared in five controllers. Three things were wrong with it.

**It duplicated wiring.** Adding a dependency to `EventService` would mean
editing every controller that used it.

**It defeated the DAO interfaces.** We introduced `UserDAO` and `EventDAO`
as interfaces specifically so the rest of the application would not know
which database sits behind them. Writing `new SqliteUserDAO(...)` inside a
controller put that knowledge straight back into the UI layer. Our own
class diagram flagged this: the arrow labelled *constructs concrete DAO*
running from the controller package into the DAO package was the one
dependency in the diagram pointing the wrong way.

**It made the controllers untestable.** Because the dependency was created
in a field initialiser, there was no seam. No test could give a controller
an in-memory DAO.

### The change

One class now owns the wiring:

```java
public class ServiceFactory {

    public ServiceFactory(UserDAO userDAO, EventDAO eventDAO,
                          SignupDAO signupDAO, HoursDAO hoursDAO) {
        this.authService   = new AuthService(userDAO);
        this.eventService  = new EventService(eventDAO);
        this.signupService = new SignupService(signupDAO, eventDAO);
        this.hoursService  = new HoursService(hoursDAO, signupDAO, eventDAO);
    }

    public static ServiceFactory createSqliteBacked(DatabaseManager db) {
        return new ServiceFactory(
                new SqliteUserDAO(db),   new SqliteEventDAO(db),
                new SqliteSignupDAO(db), new SqliteHoursDAO(db));
    }
}
```

Controllers became one line each:

```java
// LoginController, after
private final AuthService authService = ServiceFactory.getInstance().auth();
```

### What it bought us

**No controller names a database technology any more.** This is checkable:

```bash
$ grep -rn "Sqlite\|DatabaseManager" src/main/java/com/cab302/vic/controller/
$ # no matches
```

**The whole object graph can be swapped.** `ServiceFactory.setInstance()`
takes a factory built over fakes, which `ServiceFactoryTest` uses to prove
a registration can run end to end without touching a database.

**Adding the two new features touched one file, not five.** `SignupService`
and `HoursService` were wired in by editing `ServiceFactory` alone.

### Trade-off we accepted

`getInstance()` is a static singleton, which is global state. Constructor
injection into controllers would be cleaner, but JavaFX instantiates
controllers itself from the FXML loader, so passing constructor arguments
means installing a custom controller factory on every `FXMLLoader`. For a
project this size the static accessor with a `setInstance` seam gives most
of the benefit for a fraction of the wiring. The seam is what matters: the
dependency is now injectable, where before it was hard-coded.

---

## Refactor 2: Builder pattern, `Event.Builder`

**Commit:** `1452076` refactor: replace Event's 8-argument constructor with a Builder

### The problem

```java
public Event(int id, String title, String description,
             String eventDate, String eventTime, String location,
             int volunteersNeeded, int createdBy)
```

Four `String` parameters sit consecutively. The compiler cannot tell them
apart, so this compiles cleanly and is wrong:

```java
// location and time transposed: compiles, fails silently at runtime
new Event(0, "Beach Clean-up", "Bring gloves",
          "2026-11-20", "Manly Beach", "09:00", 12, 3);
```

The call site also had to pass a placeholder `0` for the id on creation,
and callers were hand-rolling null checks on the optional text fields.

### The change

```java
Event event = Event.builder()
        .title(title.trim())
        .description(description)
        .date(eventDate)
        .time(eventTime)
        .location(location)
        .volunteersNeeded(volunteersNeeded)
        .createdBy(createdBy)
        .build();
```

Every value is named, the id defaults sensibly, and optional text defaults
to `""` instead of `null`. That last point fixed a real class of bug: the
list cells render these fields directly, so a null surfaced on screen as
the literal text `null`.

`Event.builderFrom(existing)` covers the edit flow, copying an event and
changing only the named fields.

### Keeping it a refactor rather than a rewrite

The original constructor was deliberately kept, so no existing caller or
test had to change. `EventBuilderTest` includes a test whose only job is to
assert the two routes produce the same object:

```java
@Test
void builderProducesTheSameObjectAsTheConstructor() { ... }
```

That test is what makes this safe to call a refactor.

---

## Patterns already in place

These were established in earlier sprints. They are listed for completeness
and because the two refactors above build directly on them.

### DAO

`UserDAO`, `EventDAO`, `SignupDAO` and `HoursDAO` are interfaces; each has
a `Sqlite*` implementation. All SQL is confined to those implementations,
so no service contains a query. `AuthService` never mentions SQLite, which
is what allows every service test to run against an in-memory fake.

### MVC

- **Model:** `model/` holds plain data types with no UI or database code.
- **View:** `resources/.../view/*.fxml` describes each screen declaratively,
  styled by a single `app.css`.
- **Controller:** `controller/` reacts to input and delegates decisions to
  a service. Controllers hold no business rules. For example
  `VolunteerDashboardController.toggleSignup` decides nothing itself, it
  calls `SignupService` and shows whatever error comes back.

### Singleton

`DatabaseManager`, `SessionManager` and `ServiceFactory` each expose a
shared instance. `DatabaseManager` also keeps a public constructor taking
a JDBC URL, which is how the integration check runs against a throwaway
database file instead of the application one.

### Test doubles

`FakeUserDAO`, `FakeEventDAO`, `FakeSignupDAO` and `FakeHoursDAO` implement
the DAO interfaces with in-memory maps. We wrote these by hand rather than
using a mocking framework, for three reasons: tests run with no database,
each test starts from a clean state, and the behaviour of the double is
readable in the repository instead of being assembled by a library.

`FakeHoursDAO` is worth a look. Two of the real queries join against the
events table to scope results to one coordinator, so the fake is given the
`FakeEventDAO` and reproduces that scoping. Without it the fake would be
more permissive than the real SQL and tests would pass against behaviour
the database does not have.

---

## How the refactors were verified

1. **Full suite green before and after.** 80 tests, no existing test
   modified to accommodate either change.
2. **Integration check against a real SQLite file.** The unit tests use
   fakes, so a separate check exercises the actual SQL, including the
   coordinator-scoping joins, and confirms data survives a reconnect.
3. **A grep that proves the Factory's claim.** No match for `Sqlite` in the
   controller package.
4. **CI.** Every push compiles, runs the suite, packages the JAR and
   launches it under a virtual display to confirm it starts.

---

## Where to find everything

| Item | Path |
|---|---|
| Factory | `src/main/java/com/cab302/vic/service/ServiceFactory.java` |
| Factory tests | `src/test/java/com/cab302/vic/service/ServiceFactoryTest.java` |
| Builder | `src/main/java/com/cab302/vic/model/Event.java` |
| Builder tests | `src/test/java/com/cab302/vic/model/EventBuilderTest.java` |
| DAO interfaces | `src/main/java/com/cab302/vic/dao/*DAO.java` |
| Test doubles | `src/test/java/com/cab302/vic/dao/Fake*DAO.java` |
| Build script | `pom.xml`, `build.ps1`, `build.sh` |
| CI pipeline | `.github/workflows/ci.yml` |
