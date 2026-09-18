# Object-Oriented Design Notes

**Volunteer Impact Coordinator — CAB302 Group 4**

Class diagram: `docs/design/class-diagram.png`
Diagram source (editable): `docs/design/class-diagram.dot`

---

## Architecture overview

The application is organised into four layers, each in its own package under
`src/main/java/com/cab302/vic/`:

| Layer | Package | Responsibility |
|---|---|---|
| View / Controller | `controller` + `resources/.../view/*.fxml` | JavaFX screens and the controllers that respond to user input |
| Service | `service` | Business rules, validation, and error handling |
| Persistence | `dao` | Reading and writing to the SQLite database |
| Domain | `model` | Plain data entities (`User`, `Event`) |
| Support | `util` | Session state, scene switching, password hashing |

Requests flow downward only: a controller calls a service, the service calls a DAO,
the DAO returns domain objects. No controller touches the database directly, and no
model class knows anything about JavaFX.

---

## How the requirements shaped the design

The user stories drove the structure directly:

- **US-01 (User creates account)** required a role choice at sign-up, so `User` carries a
  `Role` enum (`COORDINATOR` / `VOLUNTEER`) rather than two separate user classes. After
  login, `LoginController.routeToDashboard()` reads that role and sends the user to the
  matching dashboard.
- The password rules in US-01 ("at least 8 characters, 1 capital, 1 special character")
  live in one place, `AuthService.isPasswordStrong()`, so the rule can be unit-tested
  without a UI.
- **US-03 (Coordinator creates event)** listed the exact fields an event needs, which
  became the constructor and fields of `Event`.
- Because several stories describe events belonging to the coordinator who made them,
  `Event` holds a `createdBy` id and `EventDAO` exposes `findByCoordinator(int)`.

---

## OO principles demonstrated

**Encapsulation.** All fields in `User` and `Event` are private and reached through
getters and setters. `PasswordHasher` keeps `digest()` and `constantTimeEquals()` private
and exposes only `hash()` and `verify()`, so callers cannot misuse the internals.

**Interfaces and abstraction.** `UserDAO` and `EventDAO` are interfaces. `AuthService` and
`EventService` hold a reference to the *interface*, not to the SQLite class, and receive it
through their constructor. The services therefore have no knowledge of SQLite at all —
swapping in a different database would mean writing one new class and changing nothing
else.

**Inheritance.** `AuthException` and `EventException` extend `Exception`, giving each layer
its own meaningful error type instead of leaking raw `SQLException` upward. In the UI,
`EventCell` extends JavaFX's `ListCell<Event>` and overrides `updateItem()` to control how
each event is drawn in the list.

**Polymorphism.** `EventService` calls `eventDAO.findAll()` without knowing whether it is
talking to `SqliteEventDAO` in production or `FakeEventDAO` in the tests. The correct
implementation is chosen at runtime.

**Separation of concerns.** Validation lives in the services
(`EventService.validateFields()`, `assertNotInPast()`), SQL lives only in the `dao`
package, and the controllers do nothing but move data between the screen and a service.

---

## Architectural patterns

**MVC.** FXML files are the View, the `controller` package holds the Controllers, and
`model` holds the Model. JavaFX's FXML loader wires a view to its controller.

**DAO pattern.** `UserDAO` / `EventDAO` define the persistence contract;
`SqliteUserDAO` / `SqliteEventDAO` implement it. Row-to-object translation is isolated in
the private `mapRow()` methods.

**Singleton.** `DatabaseManager` and `SessionManager` both use a private constructor with a
static `getInstance()`, so there is exactly one database connection source and one record
of who is currently logged in.

**Dependency injection (constructor injection).** `SqliteUserDAO(DatabaseManager)`,
`AuthService(UserDAO)` and `EventService(EventDAO)` all receive their collaborators rather
than constructing them. This is what makes the services testable.

---

## Known limitation: where the injection stops

Injection currently reaches the service layer but not the controllers. Each controller
builds its own dependency chain, for example in `CoordinatorDashboardController`:

```java
private final EventService eventService =
        new EventService(new SqliteEventDAO(DatabaseManager.getInstance()));
```

So the controllers are hard-wired to the SQLite implementation (shown as the dotted red
"constructs" arrows on the diagram), even though the services below them are not.

The practical effect: swapping SQLite for another database would require no change to
`AuthService` or `EventService` at all, but would require editing the one construction line
in each of the five controllers. Every business rule stays test-covered either way, which is
why this was an acceptable trade-off for a prototype.

The tidier fix for a later sprint is a small factory or an application-level composition
root that builds the services once and hands them to the controllers, removing the five
duplicated lines.

---

## Why this design is testable

The interface boundary at the DAO layer is what allows the test suite to run without a
database. `FakeUserDAO` and `FakeEventDAO` (in `src/test/java/com/cab302/vic/dao/`)
implement the same interfaces with in-memory storage, so `AuthServiceTest` and
`EventServiceTest` exercise real business logic with no SQLite, no file I/O, and no JavaFX
window — fast, repeatable, and isolated.

This is the "isolating non-testable components" practice: the database and the GUI are the
two parts that are awkward to test, and both are held behind boundaries the tests can
substitute.
