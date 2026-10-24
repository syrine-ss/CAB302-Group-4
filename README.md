# Volunteer Impact Coordinator

A JavaFX desktop application that helps community organisations coordinate volunteers,
schedule events, log hours, and generate impact reports.

Built for CAB302 Semester 2, 2026 under the theme "Technology for Sustainable Futures".

---

## Where to find things

A map of the assessment criteria to the evidence in this repository. The first three
sections follow the marking criteria; the last two hold the planning and version control
evidence that supports them.

### Object-oriented design

| Evidence | Path |
|---|---|
| Class diagram, layered and colour-coded by package | `docs/design/class-diagram.png` |
| Diagram source, so it can be regenerated when the code changes | `docs/design/class-diagram.dot` |
| Written design rationale: OO principles, patterns, known limitations | `docs/design/oo-design-notes.md` |
| Patterns and refactors, with before and after rationale | `docs/refactoring-and-patterns.md` |
| Individual refactor write-ups: Clock injection, status encapsulation, event form | `docs/Refactors/` |
| Factory: builds the services so controllers no longer construct their own DAOs | `src/main/java/com/cab302/vic/service/ServiceFactory.java` |
| Builder: replaces the eight-argument event constructor | `src/main/java/com/cab302/vic/model/Event.java` |
| Singletons: database connection and session state | `src/main/java/com/cab302/vic/dao/DatabaseManager.java`, `src/main/java/com/cab302/vic/util/SessionManager.java` |
| DAO interfaces and their SQLite implementations | `src/main/java/com/cab302/vic/dao/` |
| MVC controllers | `src/main/java/com/cab302/vic/controller/` |
| Business rules and validation | `src/main/java/com/cab302/vic/service/` |
| Domain model | `src/main/java/com/cab302/vic/model/` |

### Continuous integration and automated build

| Evidence | Path |
|---|---|
| GitHub Actions workflow: compile, test, coverage, package, smoke test, Javadoc | `.github/workflows/ci.yml` |
| Build scripts used by both developers and CI | `build.sh`, `build.ps1` |
| How to build and run the packaged application | `docs/build-and-run.md` |
| Generated Javadoc, complete HTML documentation set | `docs/javadoc/index.html` |
| Javadoc also published as a downloadable artifact from every CI run | GitHub Actions, artifact `javadoc` |
| JaCoCo coverage report, downloadable from every CI run | GitHub Actions, artifact `jacoco-coverage-report` |
| Service-level behaviour tests | `src/test/java/com/cab302/vic/service/` |
| Model tests | `src/test/java/com/cab302/vic/model/` |
| Password hashing and session tests | `src/test/java/com/cab302/vic/util/` |
| Test doubles: in-memory DAOs that isolate the database | `src/test/java/com/cab302/vic/dao/` |

### Feedback-driven improvement

| Evidence | Path |
|---|---|
| Feedback log: every finding traced to a decision, a change and a result | `docs/feedback-log.md` |
| Sprint retrospectives and the work deliberately carried forward | `docs/feedback-log.md` (Retrospectives) |
| Usability evaluation overview | `docs/usability/README.md` |
| Heuristic review of the volunteer, login and register screens | `docs/usability/heuristic-review.md` |
| Usability test plan and task script | `docs/usability/usability-test-plan.md` |
| Round 2 results after the fixes: tasks completed, errors, mean SUS 88.75 | `docs/usability/results-round-2.md` |
| SUS questionnaire used | `docs/usability/sus-questionnaire.md` |
| Accessibility checklist, 11 of 22 passed to 19 of 22 | `docs/usability/accessibility-checklist.md` |
| Change log: what landed each sprint, against user story IDs | `CHANGELOG.md` |
| Final sprint plan | `docs/sprint-week-13.md` |

### Planning and process

| Evidence | Path |
|---|---|
| Product backlog: 23 user stories, acceptance criteria, MoSCoW priorities | `docs/Project backlog/User stories - All.md` |
| How the backlog was built, and what changed between drafts and final | `docs/Project backlog/README.md` |
| Individual story drafts (iteration evidence) | `docs/Project backlog/User Stories - Ryan.txt`, `docs/Project backlog/User Stories - Aedan.txt` |
| Sprint 1 plan, release plan, risk register, and week 9 revision | `docs/planning/sprint-and-release-plan.md` |
| Project brief and requirements | `docs/project-brief.md`, `docs/requirements.md` |
| Meeting minutes | `docs/Minutes & Notes/` |
| Low-fidelity UI sketches | `docs/design/low-fidelity/` |
| Mid-fidelity UI mockups | `docs/design/mid-fidelity/` |
| Project management board | Trello (export included in the submission zip) |

### Version control

| Evidence | Where |
|---|---|
| Commit history and contributor summary | `git-shortlog.txt` and `git-log.txt` in the submission zip |
| Author identity mapping (several members committed from both the web UI and a terminal) | `.mailmap` |
| Feature branches and pull requests, with review comments | GitHub repository history |

---

## Team

| Name | Student ID |
|------|------------|
| Syrine Shraim | n12067733 |
| Maia Sherwin | n11249188 |
| Ryan Francis | n12468932 |
| Sam Turner | n11240016 |
| Peniel Desta | n12501735 |

The project began with seven members. Bailey Musgrave (n12314781) and Aedan Manche
(n11420472) withdrew from the unit in week 9; their work was redistributed
among the remaining five. Both appear in the commit history, and the redistribution is
recorded in `docs/planning/sprint-and-release-plan.md`.

---

## Tech stack

- Java 21
- JavaFX 21
- Maven
- SQLite (via `sqlite-jdbc`)
- JUnit 5

## Getting started

### Prerequisites

- JDK 21 or later
- Maven 3.8 or later
- IntelliJ IDEA (recommended)

### Clone and run

```bash
git clone https://github.com/syrine-ss/CAB302-Group-4.git
cd CAB302-Group-4
mvn clean javafx:run
```

Running `VolunteerImpactApp` directly from the IDE will fail with "JavaFX runtime
components are missing". Use the Maven `javafx:run` goal instead.

The SQLite database (`vic.db`) is created automatically on first run, so no setup is
needed. It is git-ignored, so each developer has their own local copy.

### Run tests

```bash
mvn test
```

## Project structure

```
src/main/java/com/cab302/vic/
├── VolunteerImpactApp.java    Application entry point
├── controller/                JavaFX controllers (MVC)
├── service/                   Business rules and validation
├── dao/                       Data access objects and interfaces
├── model/                     Domain entities
└── util/                      Session, navigation, password hashing

src/main/resources/com/cab302/vic/
├── view/                      FXML view files
└── styles/                    CSS stylesheets

src/test/java/com/cab302/vic/   JUnit 5 tests and in-memory test doubles

docs/
├── Project backlog/           User stories, individual drafts
├── planning/                  Sprint and release plan
├── design/                    Class diagram, design notes, UI fidelities
├── Refactors/                 Individual refactor write-ups
├── usability/                 Heuristic review, test plan, results, accessibility
├── javadoc/                   Generated API documentation (open index.html)
├── feedback-log.md            Findings traced to decisions, changes and results
└── Minutes & Notes/           Meeting minutes
```

## Workflow

- Work on feature branches off `main`
- Open a pull request when a feature is ready
- At least one other team member reviews before merging
- Do not push directly to `main`
- CI runs the build and full test suite on every pull request; merges wait for a green check
