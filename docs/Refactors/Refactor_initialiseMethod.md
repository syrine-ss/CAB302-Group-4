# Refactor: Initialise method refactor

Event Form Controller · CAB302 Group 4

## Summary

Extracted configureVolunteerSpinner(), configureDatePicker() and configureForm() from initialise() method, used final constants for spinner configuration to improve readability. This improves readability and abides by the Single Responsibility Principle by having each method strictly focused on one function.

## The Problem


```java
 @FXML
    public void initialize() {
        IntegerSpinnerValueFactory(1, 500, 5));
        
        volunteersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                DEFAULT_MIN_VOLUNTEERS, DEFAULT_MAX_VOLUNTEERS, DEFAULT_VOLUNTEERS)
        );

        // Prevent past dates in the date picker
        datePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f2f2f2;");
                }
            }
        });

        editingEvent = CoordinatorDashboardController.selectedEvent.get();
        if (editingEvent == null) {
            headingLabel.setText("Create New Event");

    }
```

This code is vague and has values that don't have a clear meaning. This is also a class containing multiple functions, cluttering an important method, initialise(), making readability worse.

## The Refactor

Extracting configureVolunteerSpinner(), configureDatePicker() and configureForm() cleans up the initialise() method, improving readability, isolates methods which ensures that if changes are made, an method as important as initialise() won't be entirely broken.


```java
public void initialize() {
        configureVolunteerSpinner();
        configureDatePicker();
        configureForm();
    }
```

## Testing and verification

Event Form Controller was already passing tests, it wasn’t dysfunctional. After the refactor, mvn test was ran,

`Tests run: 110, Failures: 0, Errors: 0, Skipped: 0`

Pushing the refactor update to github, CI was run and no issues were found. Feedback was received from peers and attended to.
