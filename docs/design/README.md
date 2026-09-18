# UI Design

**Volunteer Impact Coordinator | CAB302 Group 4**

The interface was designed in two passes: hand-drawn low-fidelity sketches to agree the
screens and flow, then mid-fidelity mockups to settle layout and visual detail. Both are
kept here so the progression is visible.

## Low fidelity

`low-fidelity/`

| File | Author |
|---|---|
| `Initial Prototype - Maia.png` | Maia |
| `Low fidelity - Aedan.jpg` | Aedan |

Maia's sheet covers seven screens with annotations in red marking behaviour rather than
layout — which element replaces which after sign-up, what a hover dropdown contains, which
screens are restricted to organisations. Several of those annotations became acceptance
criteria in the backlog.

## Mid fidelity

`mid-fidelity/`

| File | Screen | Author |
|---|---|---|
| `home-page.png` | Landing page with event preview cards | Bailey |
| `sign-up.png` | Account creation with role selection (US-01) | Bailey |
| `user-settings.png` | Profile and availability settings (US-02) | Bailey |
| `volunteer-profile.png` | Volunteer profile with current and past events (US-04) | Bailey |
| `login.png` | Log in, including the error state (US-18) | Ryan |
| `events-list.png` | Public events page with filters (US-06) | Ryan |
| `event-detail.png` | Event page before sign-up | Ryan |
| `event-detail-signed-up.png` | Event page after sign-up (US-05) | Ryan |
| `analytics-dashboard.png` | Impact summary for coordinators (US-14) | Ryan |

Every screen in the low-fidelity sheet now has a mid-fidelity version.

## What changed between the two passes

**Sign-up button behaviour.** The sketch annotates that the "Interested?" button is
replaced by "X hours logged | Log hours" once a volunteer has signed up. The mid-fidelity
set makes this two separate frames — `event-detail.png` and `event-detail-signed-up.png` —
so both states are explicit rather than described.

**Hours approval made visible.** After US-05 and US-16 were reconciled to use a pending
status, the signed-up frame gained a "Pending coordinator approval" note, and the dashboard
gained a footnote stating only approved hours count. Neither appears in the sketch, because
the decision came later.

**Dashboard restructured.** The sketch shows a stacked list of events with volunteers and
hours per event. US-14 additionally asks for organisation-wide totals, so the mid-fidelity
version puts three summary cards above the per-event list rather than replacing it.

**Footer dropped.** The sketch notes a footer "might not be needed for desktop app". It was
left out.

**Login error wording.** US-18 requires that a failed login doesn't reveal whether the
username or the password was wrong. The mid-fidelity login screen shows that error state
explicitly, since it's a security decision rather than a visual one, and there is a unit
test covering it (`loginErrorMessageDoesNotRevealWhichPartIsWrong` in `AuthServiceTest`).

## Note on fidelity

These are mid-fidelity mockups, not a clickable prototype. They fix layout, hierarchy and
content, and deliberately leave interaction, real imagery and final copy open. The
implemented JavaFX screens follow them closely but are not pixel-for-pixel copies.
