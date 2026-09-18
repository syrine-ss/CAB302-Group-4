# Sprint Plan and Release Plan

**Volunteer Impact Coordinator | CAB302 Group 4**
Authors: Syrine Shraim (n12067733) and Sam Turner (n11240016)

> This document was written in week 7. A dated revision recording what actually happened
> in Sprint 1, and the changes forced by two members withdrawing, is at the end under
> [Revision history](#revision-history). The original plan is left intact above it so the
> two can be compared.

---

## 1. Sprint 1 Plan (Weeks 5 to 7)

A sprint is a fixed stretch of time — two weeks for CAB302 — in which the team works
towards a sprint goal. User stories are pulled from the product backlog into the sprint
backlog at the start of the sprint.

### Sprint 1 goal

Get the foundations solid: user registration, login and logout working, coordinators can
create and edit events, volunteers can browse the public events page, and the UI design
for the core screens is locked in.

### Sprint 1 backlog

| Sprint ID | Backlog ID | Story | Owner | Pts | Status (at planning) |
|---|---|---|---|---|---|
| US-01 | US-18 | User logs in | Syrine | 3 | To Do |
| US-02 | US-19 | Coordinator edits an event | Syrine | 3 | To Do |
| US-03 | US-20 | User logs out | Syrine | 2 | To Do |
| US-04 | US-01 | Create user account (register) | Team | 5 | In Progress |
| US-05 | US-02 | Manage volunteer profile | Team | 3 | To Do |
| US-06 | US-03 | Create event (coordinator) | Team | 5 | To Do |
| US-07 | US-06 | Public events page (browse events) | Team | 5 | To Do |
| UI-01 | — | Login and register screens (low fidelity) | Aedan / Maia | 2 | In Progress |
| UI-02 | — | Coordinator dashboard (low fidelity) | Aedan / Maia | 3 | In Progress |
| UI-03 | — | Event creation and public events (mid fidelity) | Ryan / Peniel / Bailey | 3 | To Do |
| UI-04 | — | Volunteer profile page (mid fidelity) | Ryan / Peniel / Bailey | 3 | To Do |

**Total story points committed: 37**

The sprint was planned before the product backlog was numbered, so it used its own
sequence. The "Backlog ID" column above was added later to cross-reference
`docs/Project backlog/User stories - All.md`, where US-01 is account creation rather than
login.

"Team" as owner means the story existed in the product backlog but had not been claimed by
a specific member at planning time. Owners were to be finalised at the first standup.

### Sprint activities

| Activity | Time | Description |
|---|---|---|
| Sprint planning | Start of sprint (Mon week 5) | Team meeting to select stories from the product backlog. Story points estimated, owners assigned, sprint goal agreed. |
| Sprint implementation | Days 2 to 13 | Individual work on assigned stories. Weekly standups in the Wednesday practical and via Discord. Feature branches, pull requests, code review before merge. |
| Sprint review | End of sprint (Thu week 7 practical) | Demo completed stories to tutor and team. Incomplete stories move back to the product backlog for re-planning. |
| Sprint retrospective | End of sprint, after review | Team reflects on what went well, what did not, and one concrete change to try next sprint. |

---

## 2. Release Plan

The release plan is a strategic guide for the whole project, steering the team through
development to the final release at the end of week 13. It is deliberately lightweight —
the timeline is fixed by the teaching semester and the resources are the team members we
have — but covers all five required areas.

### 2.1 Scope

The release will deliver a functional JavaFX desktop application for community
organisations to coordinate volunteers. In scope:

- User authentication with coordinator and volunteer roles (register, login, logout)
- Volunteer profile management (skills, interests, contact)
- Event creation, editing, cancellation, and recurring events
- Public events browsing, sign-ups, and withdrawal
- Attendance tracking and hours logging with coordinator approval
- Reporting dashboard with engagement statistics and impact summaries
- CSV and PDF export of reports
- Persistent SQLite database

Out of scope for this release: mobile app, cloud sync, email notifications, multi-
organisation support.

### 2.2 Timeline

Four sprints of two weeks each, ending at week 13. Each sprint concludes on a CAB302
checkpoint week so progress can be presented to the tutor.

| Sprint | Dates | Theme | Deliverables |
|---|---|---|---|
| Sprint 1 | Weeks 5–7 | Foundation and auth | Register, log in, log out, profile management, event creation and editing, public events browsing, low-fi and mid-fi UI designs |
| Sprint 2 | Weeks 7–9 | Core volunteer flow | Event sign-ups, view signed-up volunteers, attendance marking, withdraw sign-up, event reminders, dashboard |
| Sprint 3 | Weeks 9–11 | Hours and reporting | Hours logging, approval workflow, impact summary, filter and search volunteers, event cancellation, statistics charts |
| Sprint 4 | Weeks 11–13 | Polish and CI | CSV and PDF export, recurring events, event templates, refactor to design patterns, automated build server, more tests, bug fixing, demo prep |

### 2.3 Resources

Team members (7 at time of writing):

| Member | Responsibilities |
|---|---|
| Syrine Shraim (n12067733) | Login, edit event, logout; PM and sprint/release plan |
| Sam Turner (n11240016) | PM and sprint/release plan; backend stories |
| Maia Sherwin (n11249188) | Low-fidelity UI; event and profile stories |
| Aedan Manche (n11420472) | Low-fidelity UI; coordinator dashboard stories |
| Ryan Francis (n12468932) | Mid-fidelity UI; reporting stories |
| Peniel Desta (n12501735) | Mid-fidelity UI |
| Bailey Musgrave (n12314781) | Mid-fidelity UI |

Tools and infrastructure:

- IntelliJ IDEA (IDE)
- Java 21 and JavaFX 21
- Maven (build tool)
- SQLite via `sqlite-jdbc` (persistent storage)
- JUnit 5 (testing)
- GitHub (source control)
- Trello (project management)
- Figma or Lucid (UI design)
- Discord (team communication)

### 2.4 Dependencies

- All team members complete Java Readiness quizzes before the week 5 practical
- GitHub repo access granted to all team members before Sprint 1 starts
- Trello board access shared with all team members
- Sprint 2 (event sign-ups, attendance) depends on Sprint 1 auth and event creation
- Sprint 3 (hours logging, reporting) depends on Sprint 2 sign-ups and attendance
- Sprint 4 (CSV/PDF export) depends on Sprint 3 reporting
- Weekly practical attendance for tutor checkpoints and sprint reviews

### 2.5 Risk assessment

| Risk | Likelihood | Mitigation |
|---|---|---|
| Team member falls behind or drops out | Medium | Fortnightly checkpoints catch this early. Redistribute stories at sprint planning if capacity drops. Seven members gives us buffer. |
| JavaFX or SQLite learning curve slows Sprint 1 | Medium | Java Readiness quizzes completed by all members before week 5. Pair up on the first tricky story. |
| Merge conflicts on shared FXML or database schema | Medium | Feature branches with mandatory PR review. One person owns schema changes per sprint. |
| Scope creep pushes deliverables out of the semester | High | Prioritise stories as Must, Should, Could. Cut Could items first if Sprint 3 or 4 slips. |
| Loss of local database during development | Low | Database file is git-ignored but the schema is in code. Anyone can regenerate a fresh database on startup. |

---

## 3. Project Management Tool: Trello

Trello was chosen because it is lightweight, visual, and the whole team can pick it up
without setup friction. The Kanban-style board makes it obvious at a glance which stories
are in flight and which are blocked.

How the board is used:

- Sam created the board and shared the link in Discord
- Columns follow the sprint workflow: Backlog, To Do (this sprint), In Progress, Review, Done
- Each user story is a card named `US-XX Story name`, matching the product backlog
- Each card holds the full story ("As a… I want… so that…") and its acceptance criteria in
  the description
- Cards are assigned to members using Trello's Members feature
- Labels are used for filtering (sprint-1, backend, frontend, ui, must-have)
- Cards move left to right as work progresses: Backlog → To Do at sprint planning →
  In Progress when work starts → Review when a pull request is open → Done when merged
- The board is reviewed as a team at the start of each Wednesday practical to check
  progress and unblock anything stuck

The board link is pinned in the team Discord. A board export is included in the submission
as evidence of active use.

---

## Revision history

### Week 9 revision

**Two members withdrew from the unit.** Bailey Musgrave and Aedan Manche both withdrew
during week 8, reducing the team from seven to five. This is the first risk listed in
section 2.5, and the mitigation was applied: their work was redistributed rather than
dropped.

- Aedan's object-oriented design documentation was picked up and delivered as
  `docs/design/class-diagram.png` and `docs/design/oo-design-notes.md`
- Bailey's continuous integration task was picked up and delivered as
  `.github/workflows/ci.yml`, which now runs the full test suite on every push and pull
  request
- Bailey's completed mid-fidelity mockups were recovered from his branch and committed to
  `docs/design/mid-fidelity/`

**Sprint 1 ran over.** The auth and event foundation (register, login, logout, dashboards,
event creation and editing) was completed, but landed in week 9 rather than week 7. The
UI design work finished on schedule. Sprint 2 work has therefore been compressed, and the
Sprint 2 to 4 themes in section 2.2 will need re-planning at the next sprint planning
session.

**Backlog numbering changed.** The product backlog was renumbered US-01 to US-23 in week 7
so stories could be referenced consistently from Trello cards and commit messages. The
Sprint 1 table above keeps its original IDs with a cross-reference column added, rather
than being rewritten, so the change is visible.

**Still to re-plan.** Sprint 2 and 3 scope against a five-person team, and owners for the
stories still marked "Team".
