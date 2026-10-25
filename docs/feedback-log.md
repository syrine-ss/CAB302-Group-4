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
| C6 | JaCoCo coverage report | `util` package at 50%: `SessionManager` has no tests | Add `SessionManagerTest`; exclude `SceneNavigator`, which needs a live JavaFX stage | Not done: the final sprint prioritised the remaining Must-have screens (US-05, US-16, profile). `util` did gain `EventDisplayTest` | Deferred |
| C7 | Sprint 2 planning | Controllers still call `ServiceFactory.getInstance()` directly, so they cannot be given test services | Dependency injection through a JavaFX controller factory | Partly done: constructor injection was applied at the service level (C11). Injecting services into the controllers is still open | Partly done |
| C8 | Sprint 2 planning | Dashboards only refresh after the user's own action, not when data changes | Observer pattern for signup and hours changes | Not done: each screen reloads its data when it opens, which covers a single-user desktop app; a refresh button was added to the event review page instead | Deferred |
| C9 | Refactoring task, Week 13 sprint plan | `HoursEntry` status was compared with enum values in several services and controllers | Encapsulate the checks in the model as predicates (`isApproved()`, `isRejected()`) | PR #47 (`docs/Refactors/HoursEntry-Status_Encapsulation.md`) | ✅ |
| C10 | Refactoring task, Week 13 sprint plan | The coordinator dashboard and event form repeated set-up code inside long methods | Extract methods: `openEventForm()` and the event form's configure methods | PRs #33, #34 (`docs/Refactors/Refactor_OpenEventForm.md`, `docs/Refactors/Refactor_initialiseMethod.md`) | ✅ |
| C11 | Testability of date rules | `EventService` read the system clock, so tests about past dates depended on the day they ran | Inject a `Clock` through the constructor | PR #39 (`docs/Refactors/EventService-Clock_Dependency.md`) | ✅ |
| C12 | Peer review of PR #51 | Clicking "Cancel selected" with no event selected threw a NullPointerException | Guard against no selection, as Edit and Review already did, and add tests | Commits `edfdbef`, `c8edef6` in PR #51 | ✅ |

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

We did not hold a formal retrospective meeting at the end of every sprint. Instead, the
team stopped to review how it was working at the points below, in our team chat and
meetings, and agreed a change. Each row records the trigger, what we changed, and where
the change can be seen.

| # | When | What prompted it | What we agreed | Evidence | Status |
|---|---|---|---|---|---|
| R1 | Week 9 | Two members withdrew in week 8, taking the team from seven to five, and Sprint 1 had run over | Redistribute their work rather than drop it, recover their finished mockups, and re-plan the remaining sprints for five people | "Week 9 revision" in `docs/planning/sprint-and-release-plan.md`; Team section of `README.md` | ✅ |
| R2 | After Submission 1 feedback | Tutor: most code went straight to `main`, and one member was carrying most of the development | Every feature on its own branch and pull request with a reviewer; remaining work split so each member owns their own pull requests | P1, P2 above; pull requests #20 onwards | ✅ |
| R3 | 7 Oct | The Week 11 features had landed as one large commit (#18) that could not be reviewed properly | Revert it and rebuild the same features as one reviewed pull request each | Revert commit `532df8a`; pull requests #20 to #28 | ✅ |
| R4 | 10 Oct | Review requests were being missed in the main chat | Post review requests in a `pr-review` channel and tag a named reviewer | P4 above | ✅ |
| R5 | 15 Oct | Work for the final sprint was unassigned and the deadline was close | A written sprint plan giving every remaining task an owner, with a Monday checkpoint to leave time for fixes | `docs/sprint-week-13.md` (PR #38) | ✅ |
| R6 | 17 Oct | Two open pull requests changed the same stylesheet (`app.css`) | Agree a merge order in the channel: the dashboard PR first, then the `list-card` rename on top | PR #37 merged 17 Oct; `list-card` rename followed in commit `7adea08` | ✅ |



**Looking back:** these changes were made when a problem had already slowed us down.
A short retrospective at the end of every sprint, rather than reacting mid-sprint, is the
main process change we would make on a future project.
