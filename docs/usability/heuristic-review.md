# Heuristic review: register, login and volunteer dashboard

A review of the three screens a volunteer uses most, carried out before the CSS and
accessibility pass. Each finding is rated, linked to the fix that addressed it, and
listed in the [feedback log](../feedback-log.md) (rows U1 to U13).

**Screens:** Register, Login, Volunteer dashboard
**Reviewed by:** Syrine (team), 12 to 14 Oct 2026
**App version reviewed:** `main` at `0970d4e`, plus the volunteer sign-up screen from PR #32

## Method

1. Walked through the volunteer journey (create an account, log in, sign up for an
   event, withdraw, log out) and noted every point where the screen was unclear,
   slow, or gave no feedback.
2. Checked each problem against Nielsen's 10 usability heuristics and the
   [accessibility checklist](accessibility-checklist.md) (WCAG 2.1 AA).
3. Rated severity on Nielsen's 0 to 4 scale:
   0 not a problem, 1 cosmetic, 2 minor, 3 major, 4 catastrophe.
4. Read the FXML, controllers and `app.css` to confirm the cause of each problem,
   so the fix could target it directly.

### Walkthrough with people outside the team

Between 12 and 14 Oct, three people from outside the team, **Bella, Maiyah and Steven**
(colleagues of Syrine's), each used the register, login and volunteer dashboard
screens and talked through what they found confusing. They raised the same problems
as the review below, in particular the missing field labels (R1, L1), the password
rules disappearing while typing (R2), errors appearing at the bottom of the form (R4)
and the volunteer dashboard issues (V1 to V7).

These were informal walkthroughs. Task times, completion counts, SUS scores and
individual comments were **not** recorded, so they are reported here as confirmation
of the findings rather than as measured results. Measured sessions on the fixed
version, following the [test plan](usability-test-plan.md), are in
[`results-round-2.md`](results-round-2.md).

## Findings

| # | Screen | Finding | Heuristic / WCAG | Sev. | Fix | Status |
|---|---|---|---|---|---|---|
| R1 | Register | No visible labels. Field names are only placeholder text, which disappears as soon as you type, so a half-filled form has unnamed boxes | H6 Recognition rather than recall; WCAG 3.3.2 | 3 | Visible label above every field, linked with `labelFor`, with Alt-key shortcuts | Fixed, PR #36 |
| R2 | Register | Password rules are only in the placeholder, so they vanish when typing starts and the user has to guess why the password is rejected | H5 Error prevention; H6 | 3 | Rules shown permanently under the field as a live checklist that ticks off each rule while typing | Fixed, PR #36 |
| R3 | Register | After a successful sign-up the screen stays on Register; the user has to click back to Login and type their username again | H7 Flexibility and efficiency of use | 2 | Go straight to Login with the username filled in and the cursor in the password field | Fixed, PR #36 |
| R4 | Register | Errors appear one at a time at the very bottom of the card, away from the field at fault; fixing one reveals the next | H9 Help users recognise, diagnose and recover from errors | 2 | Every problem shown at once, directly under its field, with a red border; focus moves to the first one | Fixed, PR #36 |
| R5 | Register | Email format is not checked, so `jsmith` or `jsmith@` is accepted | H5 Error prevention | 2 | Email checked in the form and in `AuthService.register`, with unit tests | Fixed, PR #36 |
| R6 | Register, Login, event form | Field labels are 11px, below the 12px minimum on the checklist (C1) | WCAG 1.4.4 | 1 | `.field-label` raised to 12px bold in a darker colour | Fixed, PR #36 |
| L1 | Login | No visible labels on username and password | H6; WCAG 3.3.2 | 2 | Same as R1 | Fixed, PR #36 |
| L2 | Login | Error message sits below the "Create one" link at the bottom of the card; after a failed login the wrong password stays in the field | H9 | 1 | Message moved directly above the Log In button; password cleared and focused after a failed attempt | Fixed, PR #36 |
| G1 | All | Success text `#1e8449` is 4.36:1 on the page background, below the 4.5:1 minimum (checklist A6) | WCAG 1.4.3 | 2 | Changed to `#196f3d`: 6.2:1 on white, 5.7:1 on the page background | Fixed, PR #36 |
| V1 | Volunteer dashboard | Dates shown as stored (`2026-10-20`) and the start time is not shown at all | H2 Match between system and the real world | 3 | Shown as "Tue 20 Oct 2026, 9:00 AM" by `EventDisplay.when`, with unit tests | Fixed, PR #37 |
| V2 | Volunteer dashboard | Past and upcoming events are mixed together, oldest first, so the next event is often below the fold | H8 Aesthetic and minimalist design; H2 | 2 | "Upcoming" section first (soonest first), then "Past" (most recent first) | Fixed, PR #37 |
| V3 | Volunteer dashboard | No way to see just the events you have signed up for | H7 | 2 | "Only show events I've signed up for" filter | Fixed, PR #37 |
| V4 | Volunteer dashboard | After Sign up, the only feedback is the button changing to Withdraw, which is easy to miss | H1 Visibility of system status | 2 | Confirmation banner ("You're signed up for Beach Clean on ...") and a "You're signed up" tag on the card; errors shown in the same banner | Fixed, PR #37 |
| V5 | Volunteer dashboard | Clicking a card highlights it as if selected, but selection does nothing | H4 Consistency and standards | 1 | Cards laid out in a plain scrolling list with no selection | Fixed, PR #37 |
| V6 | Volunteer dashboard | When there are no events the screen is just blank | H1; H10 Help and documentation | 1 | Empty-state message explaining why, for both "no events" and "no signups" | Fixed, PR #37 |
| V7 | Volunteer dashboard | Buttons inside `ListView` cells are hard to reach with the keyboard (Tab goes to the list, not the button) | WCAG 2.1.1 Keyboard | 2 | Cards are ordinary nodes, so Tab moves between the Sign up / Withdraw buttons; buttons have accessible text naming the event | Fixed, PR #37 (confirm by hand, checklist D5) |

**Totals:** 16 findings: 3 major, 9 minor, 4 cosmetic. All 16 addressed.

## How the fixes are tested

| Fix | Automated test |
|---|---|
| R4, R5 form validation | `RegistrationValidatorTest`: every problem reported at once, in form order, against the right field; bad email formats; weak and mismatched passwords |
| R2 password checklist | `RegistrationValidatorTest.checklistAgreesWithAuthServicePasswordRule`: the live checklist and the server rule can never disagree |
| R5 server-side email check | `AuthServiceTest.registerRejectsInvalidEmail`, `isEmailValidAcceptsNormalAddresses` |
| V1 readable date and time | `EventDisplayTest.whenShowsReadableDateAndStartTime` and the two fallback tests |
| V2 upcoming then past | `EventDisplayTest.upcomingListsOnlyFutureAndTodaysEventsSoonestFirst`, `pastListsOnlyFinishedEventsMostRecentFirst` |

The screen layout itself (labels, banner, focus) is checked by hand using the
[accessibility checklist](accessibility-checklist.md).

## Not changed

| Item | Reason |
|---|---|
| Secondary button text `#2f6fd1` is exactly 4.50:1 on the page background (checklist A4) | Passes the minimum; left as is to keep the brand colour. Revisit if the background changes |
| Placeholder text is light grey (checklist A7) | Placeholders now only show examples (e.g. `name@example.com`); the field name is in the visible label, so nothing depends on the placeholder |
| Coordinator dashboard and event form | Outside this review. They are being refactored in separate PRs; event form labels are not yet linked with `labelFor` (checklist B5) |
