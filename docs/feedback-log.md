# Feedback log

A record of the feedback the team has sought and received, what we decided, and what
changed as a result. Each row links a finding to the decision and to the code, test or
UI change that followed, so the effect of feedback can be traced in the repository.

**Sources of feedback:** tutor (Submission 1 marking), peer code review on pull
requests, our own design notes, automated quality checks (CI and JaCoCo coverage),
usability testing with users ([`usability/`](usability/README.md)), and sprint
retrospectives.

Status key: ✅ done · 🔄 in progress · ⏳ planned

## Process and workflow

| # | Source | Finding | Decision | Change | Status |
|---|---|---|---|---|---|
| P1 | Tutor, Submission 1 (version control) | "One feature branch merged into main… the rest of the code is all done directly in the main branch." | Feature work goes through a feature branch and a pull request, with CI on every PR | Feature work since 6 Oct merged through pull requests #20 to #31 | ✅ |
| P2 | Tutor, Submission 1 (OO design) | One student was carrying most of the development; work should be distributed more fairly | Remaining features and refactors split across the team, each member owning PRs under their own name | Sprint 2 task split (see sprint plan) | 🔄 |
| P3 | Tutor, Submission 1 (TDD) | More evidence of Red–Green–Refactor needed | Remaining features committed as failing test, then implementation, then refactor, and merged without squashing so the commits stay visible | Applies to PRs from 13 Oct | 🔄 |
| P4 | Team discussion | PR reviews were being missed because requests were scattered | Review requests posted in the `pr-review` channel, tagging a specific reviewer | From 10 Oct | ✅ |

## Architecture and code quality

| # | Source | Finding | Decision | Change | Status |
|---|---|---|---|---|---|
| C1 | Design notes, "Known limitation" (`design/oo-design-notes.md`) | Every controller built its own service and SQLite DAO, so all five were tied to SQLite and the line was repeated five times | Introduce a Factory that builds the services once | `ServiceFactory`, PR #20; write-up in `refactoring-and-patterns.md` | ✅ |
| C2 | Internal code review of event creation | `Event` was built with an eight-argument constructor where date and time could be swapped without a compile error | Introduce a Builder | `Event.Builder`, PR #22 | ✅ |
| C3 | Peer review of PR #26 (JaCoCo) | `build.sh` runs `mvn clean`, which would delete the coverage report before it was uploaded; `Launcher` was counted in coverage although it only starts JavaFX | Upload coverage before building the JAR; exclude `Launcher` | Commits `079daa2`, `4ec8853` in PR #26 | ✅ |
| C4 | Peer review of PR #31 (Javadoc) | Private constructors added to `EventFormController` and `VolunteerDashboardController` would stop FXML creating them, crashing both screens; `SessionManager`'s private constructor had been removed | Remove the controller constructors and restore `SessionManager()` before merging | Commits `58935a7`, `ee52aca` in PR #31 | ✅ |
| C5 | CI failure on PR #29 | A private `VolunteerImpactApp` constructor stopped the app launching; the JAR smoke test caught it | Remove the constructor | Commit `ecbd697`, merged in PR #31 | ✅ |
| C6 | JaCoCo coverage report | `util` package at 50%: `SessionManager` has no tests | Add `SessionManagerTest`; exclude `SceneNavigator`, which needs a live JavaFX stage | Not implemented; see Carried forward under Retrospectives | ⏳ |
| C7 | Sprint 2 planning | Controllers still call `ServiceFactory.getInstance()` directly, so they cannot be given test services | Dependency injection through a JavaFX controller factory | Not implemented; see Carried forward under Retrospectives | ⏳ |
| C8 | Sprint 2 planning | Dashboards only refresh after the user's own action, not when data changes | Observer pattern for signup and hours changes | Not implemented; see Carried forward under Retrospectives | ⏳ |

## User interface and accessibility

From the [heuristic review](usability/heuristic-review.md) of the volunteer screens
(12 to 14 Oct), confirmed in walkthroughs with three people outside the team, the
[round 2 usability test](usability/results-round-2.md) on the fixed version (16 Oct), and the
[accessibility checklist](usability/accessibility-checklist.md). Finding IDs in
brackets match the review.

| # | Source | Finding | Decision | Change | Measured result | Status |
|---|---|---|---|---|---|---|
| U1 | Heuristic review (R1, L1); checklist B1, B2, B5 | Login and register fields have no visible labels, only placeholder text | Visible label above every field, linked with `labelFor` | PR #36 | Checklist B1, B2 ❌ → ✅; B5 ❌ → ⚠️ | ✅ |
| U2 | Heuristic review (R2); checklist B4 | Password rules vanish once the user starts typing | Permanent live checklist under the password field | `RegistrationValidator.passwordChecklist`, PR #36 | Checklist B4 ❌ → ✅; test keeps checklist and server rule in step | ✅ |
| U3 | Heuristic review (R3) | After registering, the user must go back to Login and retype their username | Go to Login with the username filled in | `LoginController.prefillAfterRegistration`, PR #36 | One screen and one field fewer to complete T1 | ✅ |
| U4 | Heuristic review (R4) | Register errors appear one at a time at the bottom of the form | Show every error at once under its own field | `RegistrationValidator`, PR #36 | 8 new tests in `RegistrationValidatorTest` | ✅ |
| U5 | Heuristic review (R5) | Any text is accepted as an email | Check the format in the form and in `AuthService` | PR #36 | 2 new tests in `AuthServiceTest` | ✅ |
| U6 | Heuristic review (R6, L2); checklist C1 | Labels are 11px; login error shown below the link at the bottom of the card | 12px labels; message above the Log In button | PR #36 | Checklist C1 ❌ → ✅ | ✅ |
| U7 | Heuristic review (G1); checklist A6 | Success text is 4.36:1 on the page background, below 4.5:1 | Darken the success colour | `.success` `#1e8449` → `#196f3d`, PR #36 | 4.36:1 → 5.7:1 | ✅ |
| U8 | Heuristic review (V1) | Event dates shown as `2026-10-20` with no start time | Readable date with start time | `EventDisplay.when`, PR #37 | 3 new tests in `EventDisplayTest` | ✅ |
| U9 | Heuristic review (V2) | Past and upcoming events mixed, oldest first | Upcoming first, soonest first; past underneath | `EventDisplay.upcoming` / `past`, PR #37 | 3 new tests in `EventDisplayTest` | ✅ |
| U10 | Heuristic review (V3) | No way to see only your own events | Add a "my events" filter | PR #37 | | ✅ |
| U11 | Heuristic review (V4) | Little feedback after signing up | Confirmation banner and "You're signed up" tag | PR #37 | | ✅ |
| U12 | Heuristic review (V5, V7); checklist D5 | Cards could be selected for no reason; card buttons hard to reach by keyboard | Replace the `ListView` with a plain scrolling list of cards | PR #37 | Checklist D5 checked by hand | ✅ |
| U13 | Heuristic review (V6) | Empty dashboard is just blank | Explain why it is empty | PR #37 | | ✅ |
| U14 | Usability test, round 2 (2 participants, 16 Oct) | Check the fixes with real users on tasks T1 to T4 | Run the test plan tasks on the fixed version | [`results-round-2.md`](usability/results-round-2.md) | 8 / 8 tasks completed without help; 2 errors, both in T1; mean SUS 88.75 (average is about 68) | ✅ |
| U15 | Usability test, round 2 (F1, F2) | One rejected password and one wrong click in T1, both from one participant | Severity 1: no change, but record the target of wrong clicks in future sessions | Noted in `results-round-2.md` | | ✅ |
| U16 | Accessibility checklist (D3), keyboard check | Focused buttons show no outline: the custom button colours replace JavaFX's focus ring, so keyboard users can't see where they are | Draw a visible outline on focused buttons | `.primary:focused`, `.secondary:focused`, `.danger:focused` in `app.css`, branch `ux/focus-visible` | Checklist D3 ❌ → ✅ | ✅ |

Overall: the accessibility checklist went from 11 of 22 passed (9 failed) to
19 of 22 passed with none failing (see the checklist summary), and in round 2
both participants completed every volunteer task without help, with a mean SUS of 88.75.

## Retrospectives

Held at the end of each sprint, after the checkpoint for that sprint. Each row records
what the team agreed to change about how it worked, and where that change can be seen
in the repository.

| # | Retrospective | What went well | What did not | Action agreed | Followed up in | Status |
|---|---|---|---|---|---|---|
| R1 | Sprint 2, after the week 9 submission | A working prototype with authentication, events and both dashboards; CI running on every push | Most development was carried by one member, and much of the code went straight to `main` rather than through review | Every feature goes through a feature branch and a pull request with a tagged reviewer; remaining features and refactors split so each member owns pull requests under their own name | P1, P2 and P4 above; pull requests #20 onwards | ✅ |
| R2 | Sprint 3, 7 Oct, after the week 11 branch was reverted | The features themselves worked | The sprint's work landed as one large commit, which could not be reviewed properly and did not show who had written what | Revert and rebuild the same features as one pull request each, merged without squashing so the test-then-implement commits stay visible | Pull requests #21 to #28; P3 above; the Removed note in `CHANGELOG.md` | ✅ |
| R3 | Sprint 4, after the week 13 checkpoint | Usability testing produced measurable improvement: mean SUS 88.75, accessibility checklist from 11 of 22 passed to 19 of 22 | Three architecture improvements were identified but not implemented, and two members withdrew in week 8 without their tasks being handed over | Record unimplemented work explicitly rather than dropping it, so it is visible as known future work rather than an omission | C6, C7 and C8 above | ✅ |

### Carried forward

Three items were identified and deliberately not implemented before submission. They are
recorded here rather than removed, so the decision is visible:

- **C6** — tests for `SessionManager`, and a coverage exclusion for `SceneNavigator`
- **C7** — dependency injection into the controllers through a JavaFX controller factory
- **C8** — Observer pattern so dashboards refresh when data changes rather than only after
  the user's own action
