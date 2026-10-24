# Changelog

All notable changes to Volunteer Impact Coordinator, grouped by sprint.

The project runs four two-week sprints, each ending on a CAB302 checkpoint. Dates are
the dates the work landed on `main`. Entries reference user story IDs from
`docs/Project backlog/User stories - All.md` where the work implements one.

---

## Sprint 4 — Weeks 11 to 13 (1 October to 25 October 2026)

**Theme:** rebuild the week 11 feature set, finish the remaining Must-have screens, polish,
accessibility, release tooling and submission packaging.

### Added

- Volunteer sign-up with capacity limits, withdrawal and attendance rules (US-17, US-08)
- Hours logging with coordinator approval and rejection (US-05, US-16)
- Sign up and withdraw controls on the volunteer dashboard
- `ServiceFactory`, so controllers no longer construct their own SQLite DAOs
- `Event.Builder` for event creation, replacing a long constructor
- Runnable JAR packaging: shade plugin, `Launcher`, `build.sh`, `build.ps1`, and
  `docs/build-and-run.md`
- CI now packages the JAR with the build script and smoke-tests it on a virtual display
- JaCoCo coverage reporting, published as a downloadable artifact from every CI run
- End-to-end test of the volunteer journey
- `RegistrationValidator`, extracted from `RegisterController`
- `EventDisplay` utility, extracted from the dashboard controllers
- `Clock` injected into `EventService` so date-based rules are deterministic under test
- Javadoc across the public classes, plus generation in the build
- `docs/refactoring-and-patterns.md` — written rationale for the refactors and the
  patterns used
- `docs/sprint-week-13.md` — sprint plan for the final two weeks
- Volunteer hours logging screen (US-05), PR #46
- Coordinator attendance register and hours review screen (US-16, US-22), PRs #45 and #50
- Profile screens for both roles, PR #49
- Cancel event on the coordinator dashboard, with a confirmation step, a guard against
  cancelling events that have already passed, and two `EventService` tests, PR #51
- Usability evaluation set in `docs/usability/`, PR #40: heuristic review, usability test
  plan, SUS questionnaire, round 2 results and an accessibility checklist
- `docs/feedback-log.md`, tying each finding to the decision taken and the commit that
  followed, PR #40
- `docs/Refactors/` — individual write-ups for the `Clock` injection, the `HoursEntry`
  status encapsulation and the event form extraction
- Mid-fidelity mockup for the event review screen
- Javadoc published as a downloadable artifact from every CI run, and the generated HTML
  committed to `docs/javadoc/` so the documentation ships with the submission

### Changed

- Volunteer dashboard reworked against the heuristic review (findings V1 to V7)
- Register and login screens reworked against the heuristic review (R1 to R6, L1, L2, G1)
- Custom buttons now show a focus outline for keyboard users (WCAG 2.4.7)
- `CoordinatorDashboardController` — configuration extracted out of `initialise`, and the
  two event-form entry points collapsed into one `openEventForm` method
- `EventFormController` tidied along the same lines
- Class diagram updated to cover the new DAOs, the factory and the builder
- `HoursEntry` status checks encapsulated behind `isApproved()` and `isRejected()`, so
  controllers and services no longer compare the enum directly, PR #47
- Coordinator dashboard lists events newest first and uses `EventDisplay.when` for
  readable dates, PR #51
- README evidence map restructured around the three marking criteria, with rows added for
  the usability evidence, the feedback log, the change log and the Javadoc
- `.mailmap` extended to cover the remaining author identities, PR #48

### Removed

- The week 11 feature branch was reverted on 7 October and the work rebuilt as a series of
  smaller, individually reviewed pull requests (#21 to #28). The original branch had
  landed as one large commit, which made it hard to review and hard to attribute.

---

## Sprint 3 — Weeks 9 to 11 (20 September to 1 October 2026)

**Theme:** signups, hours logging and the first refactors.

### Added

- Signups, hours logging, Factory and Builder refactors and a runnable JAR, landed as a
  single week 11 branch (#18)
- Additional tests covering edge cases in the service layer

> This sprint's work was later reverted and rebuilt in Sprint 4. See the Removed note above.

---

## Sprint 2 — Weeks 7 to 9 (5 September to 20 September 2026)

**Theme:** authentication, events, and the first working prototype. Ended with the week 9
submission.

### Added

- Authentication service with salted password hashing, behind a DAO interface
- Login and register screens (US-01, US-18)
- Event model, DAO and service, with validation for past dates and empty fields (US-03)
- Coordinator and volunteer dashboards, event form, and logout (US-19, US-20)
- Database layer querying users and events
- GitHub Actions workflow: builds and runs the full test suite on every push and pull
  request
- Class diagram and `docs/design/oo-design-notes.md`, covering the layering, the DAO
  pattern, the singletons and one known limitation
- Mid-fidelity mockups for every screen in the low-fidelity prototype
- `docs/planning/sprint-and-release-plan.md`, with a week 9 revision recording the two
  member withdrawals
- Evidence map in the README, pointing each assessment criterion at its path in the repo
- `.mailmap`, so `git shortlog` reports one entry per person rather than one per identity

### Changed

- Project structure flattened so `pom.xml` sits at the repository root
- Product backlog consolidated from the individual drafts into a single numbered list of
  23 stories with MoSCoW priorities, matching the Trello card names

### Fixed

- Clone instructions in the README, which still referenced the pre-flatten directory
- Two documentation files committed without file extensions

---

## Sprint 1 — Weeks 5 to 7 (6 August to 3 September 2026)

**Theme:** team formation and planning.

### Added

- Repository, Maven project skeleton and initial README
- Team formation, Discord channel and the Trello board
- Individual user story drafts from each member, later consolidated
- Low-fidelity UI sketches covering seven screens, with behaviour annotations
- First mid-fidelity mockups: home page, sign-up, user settings, volunteer profile
- Week 5, 6 and 7 meeting minutes
- Project brief and requirements

---

## Notes on this changelog

Entries are grouped by sprint rather than by release, because the project has no versioned
releases — each sprint ends at a checkpoint instead. Commit-level detail is in
`git-log.txt` in the submission, and the per-person contribution summary is in
`git-shortlog.txt`.
