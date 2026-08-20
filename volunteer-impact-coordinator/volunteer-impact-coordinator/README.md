# Volunteer Impact Coordinator

A JavaFX desktop application that helps community organisations coordinate volunteers, schedule events, log hours, and generate impact reports.

Built for CAB302 Semester 2, 2026 under the theme "Technology for Sustainable Futures".

## Team

| Name | Student ID |
|------|------------|
| Syrine Shraim | n12067733 |
| Maia Sherwin | n11249188 |
| Aedan Manche | n11420472 |
| Ryan Francis | n12468932 |
| Sam Turner | n11240016 |

## Tech Stack

- Java 21
- JavaFX 21
- Maven
- SQLite (via `sqlite-jdbc`)
- JUnit 5

## Getting Started

### Prerequisites
- JDK 21 or later
- Maven 3.8 or later
- IntelliJ IDEA (recommended)

### Clone and run
```bash
git clone <repo-url>
cd volunteer-impact-coordinator
mvn clean javafx:run
```

### Run tests
```bash
mvn test
```

## Project Structure

```
src/main/java/com/cab302/vic/
├── VolunteerImpactApp.java   Application entry point
├── controller/                JavaFX controllers (MVC)
├── model/                     Data model classes
├── dao/                       Data access objects (SQLite)
└── util/                      Helper utilities

src/main/resources/com/cab302/vic/
├── view/                      FXML view files
└── styles/                    CSS stylesheets
```

## Workflow

- Work on feature branches off `main`
- Open a pull request when a feature is ready
- At least one other team member reviews before merging
- Do not push directly to `main`

## Documentation

See the `docs/` folder for the project brief, requirements, and meeting notes.
