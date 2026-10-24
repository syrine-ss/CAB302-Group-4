# Usability test results: round 2 (after the fixes)

Measured sessions run on the fixed version, to check whether the changes from the
[heuristic review](heuristic-review.md) worked for real users. Round 1 was the
heuristic review itself plus informal walkthroughs, so it has findings but no
measurements; this round is the first with recorded times, errors and SUS scores.

**Date:** 16 Oct 2026
**Facilitator and note-taker:** Syrine
**App version:** the fixes from PR #36 (`0eb513d`) and PR #37 (`f8526cf`) combined on a
local test branch. The volunteer and login screens in this build are identical to
`main` at `cd28951`, after both PRs were merged.
**Tasks:** T1 to T4 from the [test plan](usability-test-plan.md), the volunteer
journey these fixes covered. T5 and T6 (coordinator) were not run because the
coordinator screens were outside the review and are still being changed.

## Participants

| ID | Has used the app before? | Notes |
|---|---|---|
| P1 | Yes | Took part in the walkthrough of the old version, 12 to 14 Oct |
| P2 | Yes | Took part in the walkthrough of the old version, 12 to 14 Oct |

Both are from outside the team. Each session started from a fresh database with only
the `coord` account and the Beach Clean event.

## Task results

Completion: ✅ completed without help · 💡 with hint · ❌ not completed.
Times were taken by hand to the nearest second.

| Task | P1 | P2 |
|---|---|---|
| T1 Create account and log in | ✅ 60s, 2 errors | ✅ 60s, 0 errors |
| T2 Sign up for Beach Clean | ✅ 15s, 0 errors | ✅ 11s, 0 errors |
| T3 Withdraw from Beach Clean | ✅ 6s, 0 errors | ✅ 12s, 0 errors |
| T4 Log out | ✅ 10s, 0 errors | ✅ 4s, 0 errors |

P1's two errors in T1: the password was rejected once, and one wrong click.

## Summary per task

| Task | Completion without help | Mean time | Total errors |
|---|---|---|---|
| T1 | 2 / 2 | 60 s | 2 |
| T2 | 2 / 2 | 13 s | 0 |
| T3 | 2 / 2 | 9 s | 0 |
| T4 | 2 / 2 | 7 s | 0 |
| **All** | **8 / 8 (100%)** | | **2** |

No hints were needed.

## SUS scores

Scored as in [`sus-questionnaire.md`](sus-questionnaire.md).

| Statement | P1 | P2 |
|---|---|---|
| 1 Would like to use frequently | 4 | 5 |
| 2 Unnecessarily complex | 1 | 1 |
| 3 Easy to use | 5 | 5 |
| 4 Would need technical support | 1 | 1 |
| 5 Functions well integrated | 4 | 4 |
| 6 Too much inconsistency | 1 | 1 |
| 7 Most people would learn quickly | 4 | 4 |
| 8 Cumbersome to use | 1 | 1 |
| 9 Felt confident | 4 | 5 |
| 10 Needed to learn a lot first | 4 | 1 |
| **SUS score** | **82.5** | **95.0** |

**Mean SUS: 88.75.** The average SUS score across published studies is about 68, so
both participants rated the fixed screens well above average.

P1's answer to statement 10 (4, "needed to learn a lot first") is the only negative
answer from either participant and doesn't match P1's other answers. It is recorded
as given.

## Comments and answers to the three questions

| ID | Most confusing | Expected but couldn't | One change |
|---|---|---|---|
| P1 | None | None | None |
| P2 | None | None | None |

## What happened to the heuristic review findings

Every finding from the [heuristic review](heuristic-review.md) that the volunteer
tasks exercise, and whether it came up again in this round.

| Finding | Fixed in | Seen again in round 2? |
|---|---|---|
| R1, L1 No visible labels | PR #36 | No |
| R2 Password rules vanish while typing | PR #36 | Partly: P1 had one password rejected in T1 even with the checklist showing, then corrected it without help |
| R3 Must retype username after registering | PR #36 | No: both went straight from register to login |
| R4 Errors one at a time at the bottom | PR #36 | Not tested directly (only one form error occurred) |
| R5 Email not validated | PR #36 | No invalid emails entered |
| V1 ISO date, no start time | PR #37 | No |
| V2 Past and upcoming mixed | PR #37 | No: both found Beach Clean straight away (T2 mean 13 s) |
| V4 Little feedback after signing up | PR #37 | No |
| V5, V7 Card selection, keyboard reach | PR #37 | No (mouse used; keyboard checked separately in the accessibility checklist) |
| V6 Blank empty state | PR #37 | Not reached in these tasks |

## New findings

| # | Finding | Task | Participants affected | Severity (0 to 4) | Decision |
|---|---|---|---|---|---|
| F1 | A password was rejected once despite the live checklist | T1 | P1 | 1 cosmetic | No change for now: the checklist was visible and P1 fixed it on the next try. Watch for it in future testing |
| F2 | One wrong click during sign-up (target not recorded) | T1 | P1 | 1 cosmetic | No change: not repeated, and the target wasn't noted. Record the target in future sessions |

## Limitations

- Two participants. Enough to confirm the fixes didn't introduce new problems, not
  enough for reliable averages.
- Both had seen the old version in the walkthrough, so some speed may come from
  familiarity rather than the fixes.
- There is no measured "before" round to compare times and SUS against; the before
  state is described by the heuristic review findings instead.
- Times were taken by hand and rounded.
