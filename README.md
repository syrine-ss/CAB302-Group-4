# Volunteer Impact Coordinator

A JavaFX desktop application that helps community organisations coordinate volunteers,
schedule events, log hours, and generate impact reports.

Built for CAB302 Semester 2, 2026 under the theme "Technology for Sustainable Futures".

---

## Where to find things

A map of the Submission 2 criteria to the evidence. Every path below is relative to the
root of the submission zip.

### Files added to the submission zip (not in the repository)

| File | What it is |
|---|---|
| `git-shortlog.txt` | Commits per team member (`git shortlog --summary --numbered --all --no-merges`) |
| `git-log.txt` | Full commit history with author, date and message |
| Trello export | Project management board export |
| `docs/Javadoc/index.html` | Generated Javadoc pages (`mvn javadoc:javadoc`), also downloadable as the `javadoc` artifact from every CI run |

### Object-oriented design

| Evidence | Path |
|---|---|
| Patterns introduced by refactoring, with before and after | `docs/refactoring-and-patterns.md` |
| One write-up per later refactor (Clock injection, HoursEntry encapsulation, extract method) | `docs/Refactors/` |
| Factory (held as a Singleton) | `src/main/java/com/cab302/vic/service/ServiceFactory.java` |
| Builder | `src/main/java/com/cab302/vic/model/Event.java` (`Event.Builder`) |
| Dependency Injection (constructor) | `src/main/java/com/cab302/vic/service/EventService.java` (`Clock`), and every service taking its DAOs |
| DAO interfaces and SQLite implementations | `src/main/java/com/cab302/vic/dao/` |
| MVC: FXML views and controllers | `src/main/resources/com/cab302/vic/view/`, `src/main/java/com/cab302/vic/controller/` |
| Class diagram and design rationale | `docs/design/class-diagram.png`, `docs/design/oo-design-notes.md` |

### Continuous integration and build

| Evidence | Path |
|---|---|
| GitHub Actions workflow: compile, test, coverage, JAR with the build script, launch test, Javadoc | `.github/workflows/ci.yml` |
| Build scripts | `build.sh`, `build.ps1`, `docs/build-and-run.md` |
| Javadoc configuration | `pom.xml` (`maven-javadoc-plugin`) |
| Branching workflow: feature branches, reviewed pull requests into `main` | `git-log.txt`, and the Workflow section below |
| Change log by sprint | `CHANGELOG.md` |

### Testing

| Evidence | Path |
|---|---|
| Service, model and utility tests | `src/test/java/com/cab302/vic/` |
| Test doubles: in-memory DAOs that isolate the database | `src/test/java/com/cab302/vic/dao/` |
| End-to-end volunteer journey | `src/test/java/com/cab302/vic/service/VolunteerJourneyTest.java` |
| Coverage | JaCoCo report, downloadable as the `jacoco-coverage-report` artifact from every CI run |

### Feedback-driven improvement and UX validation

| Evidence | Path |
|---|---|
| Feedback log: every finding linked to its decision and the PR that changed it | `docs/feedback-log.md` |
| Usability and accessibility overview, with before and after numbers | `docs/usability/README.md` |
| Heuristic review (16 findings, severity, fix) | `docs/usability/heuristic-review.md` |
| Usability test plan, SUS form and round 2 results | `docs/usability/usability-test-plan.md`, `docs/usability/sus-questionnaire.md`, `docs/usability/results-round-2.md` |
| Accessibility checklist (WCAG 2.1 AA), 11 to 19 of 22 | `docs/usability/accessibility-checklist.md` |

### Agile planning

| Evidence | Path |
|---|---|
| Product backlog: user stories, acceptance criteria, MoSCoW priorities | `docs/Project backlog/User stories - All.md` |
| Sprint and release plan, with the Week 9 revision | `docs/planning/sprint-and-release-plan.md` |
| Final sprint plan and task split | `docs/sprint-week-13.md` |
| Project brief and requirements | `docs/project-brief.md`, `docs/requirements.md` |
| Meeting minutes | `docs/Minutes & Notes/` |

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
└── Minutes & Notes/           Meeting minutes
```

## Workflow

- Work on feature branches off `main`
- Open a pull request when a feature is ready
- At least one other team member reviews before merging
- Do not push directly to `main`
- CI runs the build and full test suite on every pull request; merges wait for a green check
