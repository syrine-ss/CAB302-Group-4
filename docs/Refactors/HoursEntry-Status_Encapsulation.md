# Refactor: Status Encapsulation - HoursEntry

Volunteer Impact Coordinator · CAB302 Group 4

## Summary

Refactored `HoursEntry` status checks by introducing descriptive predicate methods instead of directly comparing enum values across services and controllers. This improves encapsulation, readability and consistency while preserving existing application behaviour.

## The problem

Previously, classes checked the status of logged hours through direct enum comparisons:

```
entry.getStatus() == HoursEntry.Status.APPROVED
entry.getStatus() == HoursEntry.Status.REJECTED
```

This exposed the status representation to multiple classes and repeated conditional logic throughout the application.

The existing `isPending()` method provided a consistent pattern for encapsulating these checks within the model.

## The refactor

Introduced two predicate methods in `HoursEntry`:

```
public boolean isApproved() {
    return status == Status.APPROVED;
}

public boolean isRejected() {
    return status == Status.REJECTED;
}
```

Replaced direct enum comparisons with these predicates.

**Before:**

```
if (entry.getStatus() == HoursEntry.Status.REJECTED)
```

**After:**

```
if (entry.isRejected())
```

Following the merge of PR #45, the refactor was extended to `EventReviewController` to encapsulate newly introduced status comparisons.

**OO design improvements:**

- **Encapsulation:** Status-checking logic is centralised within `HoursEntry`, reducing direct dependencies on its internal enum representation.
- **Abstraction:** Predicates such as `isApproved()` express intent without requiring callers to interpret the enum.
- **Single Responsibility (SRP):** Status interpretation belongs to the model, allowing services and controllers to focus on their respective responsibilities.
- **Maintainability:** Repeated comparisons are replaced with reusable methods, improving consistency and simplifying future changes.

**Trade-off:** Adds convenience methods to the model. The `getStatus()` accessor remains available for persistence operations in `SqliteHoursDAO`, so the underlying status representation is not completely hidden.

## Testing and verification

`HoursEntryTest` was extended to verify that the predicates return the expected results for the relevant status values.

The rejected-status predicate followed a test-first sequence:

1. **Red:** Added a failing test for `isRejected()` (`59a99e7`).
2. **Green:** Implemented the predicate (`c75e3da`).
3. **Refactor:** Replaced direct comparisons across the affected classes.

**Verification:** `mvn clean test` — 131 tests passed, 0 failures, 0 errors, 0 skipped.

The refactor preserves existing approval and rejection behaviour while improving code clarity and reducing duplicated status comparisons.

**Refactoring technique:** Encapsulate Conditional

### Affected files

| File | Change |
| --- | --- |
| `model/HoursEntry.java` | Added status predicates |
| `service/HoursService.java` | Replaced direct comparisons |
| `controller/EventReviewController.java` | Updated comparisons following PR #45 |
| `controller/VolunteerDashboardController.java` | Replaced approved-status comparison |
| `test/model/HoursEntryTest.java` | Added predicate tests |

*Paths are relative to `src/main/java/com/cab302/vic/`, except the test file, which is under `src/test/java/com/cab302/vic/`.*