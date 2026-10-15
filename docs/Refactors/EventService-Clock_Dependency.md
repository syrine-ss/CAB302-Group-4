# Refactor: Clock Dependency injection - EventService

Volunteer Impact Coordinator · CAB302 Group 4

## Summary

Refactored `EventService` to use an injectable `Clock` instead of accessing the system date directly. This makes date validation deterministic in unit tests while preserving existing application behaviour.

## The problem

Previously, `assertNotInPast()` called `LocalDate.now()` directly:

```java
if(date.isBefore(LocalDate.now())){
        throw new EventException("Event date cannot be in the past");
        };
```

This tightly coupled date validation to the system clock. Tests could only generate dates relative to the execution date rather than simulate specific dates and boundary conditions.

## The refactor

Introduced constructor-based dependency injection while retaining the original constructor for backwards compatibility:

```java
private final Clock clock;

public EventService(EventDAO eventDAO) {
    this(eventDAO, Clock.systemDefaultZone());
}

public EventService(EventDAO eventDAO, Clock clock) {
    this.eventDAO = eventDAO;
    this.clock = clock;
}
```

The validation method was changed from `static` to an instance method so it could access the injected clock.

The date comparison now uses:

```java
if (date.isBefore(LocalDate.now(clock))) {
    throw new EventException("Event date cannot be in the past");
}
```

Production continues using the system clock, while tests can inject a fixed clock.

**Design improvements:**

- **Dependency injection:** Removes the direct system-clock dependency from validation.
- **Loose coupling:** The service can work with different `Clock` implementations.
- **Encapsulation:** The clock reference and validation method remain private.
- **Backwards compatibility:** Existing callers, including `ServiceFactory`, require no changes.

**Trade-off:** The service now has an additional dependency and constructor. The injected clock is assumed to be non-null; constructor validation could be added if needed.

## Testing and verification

`EventServiceTest` was updated to use a fixed clock based on a constant reference date rather than the actual system date.

The existing `futureDate()` and `pastDate()` helpers were updated to use this same reference date.

A new test, `createAcceptsTodaysDate()`, verifies that an event scheduled exactly on the reference date is accepted.

| Date tested | Expected result |
| --- | --- |
| Day before today | Rejected |
| Today | Accepted |
| Future date | Accepted |

**Verification:** `mvn test` — 101 tests passed, 0 failures, 0 errors, 0 skipped.

The refactor preserves the existing business rule while making date-related tests repeatable and independent of the execution date.

**Refactoring technique**: Introduce Parameter / Constructor Dependency Injection

**Affected classes**: EventService.java, EventServiceTest.java
