# Refactor: OpenEventForm extract method

Coordinator Dashboard Controller · CAB302 Group 4

## Summary

Extracted `OpenEventForm` helper method from multiple methods to reduce repetition and complexity. This helps eliminate extra work if any bugs are found in the method, only requiring updates in one location. 

## The Problem


```java
try {
            SceneNavigator.switchTo(welcomeLabel, "event-form.fxml", "Create Event");
        } catch (IOException e) {
            showError("Could not open the event form: " + e.getMessage());
        }
```

This section of code was repeated multiple times in different methods, this can eventually lead to cluttered code that can eventually break if updated in one place and not another.  

## The Refactor

Extracting a separate helper method, OpenEventForm, with a variable for the name of the message allows the code to be kept in one method, decluttering code, making maintenance a lot easier and abiding by the Single Responsibility Principle.


```java
private void openEventForm(String windowTitle) {
        try {
            SceneNavigator.switchTo(welcomeLabel, "event-form.fxml", windowTitle);
        } catch (IOException e) {
            showError("Could not open the event form: " + e.getMessage());
        }
    }
```

## Testing and verification

Coordinator Dashboard Controller was already passing tests, it wasn’t disfunctional. After the refactor, mvn test was ran,

`Tests run: 110, Failures: 0, Errors: 0, Skipped: 0`

Pushing the refactor update to github, CI was run and no issues were found. Feedback was received from peers and attended to.
