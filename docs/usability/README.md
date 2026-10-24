# Usability and accessibility evaluation

How the team gathers user feedback on the interface, turns it into changes, and
measures whether those changes worked. This is the UX half of our feedback-driven
improvement process; the overall record of findings, decisions and changes is in
[`../feedback-log.md`](../feedback-log.md).

## The loop

```
Round 1: heuristic review  ──►  findings  ──►  fixes in PRs  ──►  Round 2: usability test
 + walkthroughs with 3 users     (severity)     (#36, #37)          (tasks, times, errors, SUS)
```

1. **Round 1 (12 to 14 Oct).** A heuristic review of register, login and the volunteer
   dashboard found 16 problems. Walkthroughs with three people outside the team raised
   the same problems.
2. **Fixes.** All 16 were fixed in PR #36 (register and login) and PR #37 (volunteer
   dashboard), each with tests for the logic behind it.
3. **Round 2 (16 Oct).** Two participants ran the volunteer tasks on the fixed version
   while we recorded completion, time, errors and SUS.

| Result | Value |
|---|---|
| Heuristic review findings fixed | 16 / 16 |
| Accessibility checks passed | 11 / 22 before, 19 / 22 after, 0 failing |
| Success text contrast | 4.36:1 before, 5.7:1 after |
| Round 2 tasks completed without help | 8 / 8 |
| Round 2 mean SUS | 88.75 (average is about 68) |

## Files

| File | What it is |
|---|---|
| [`heuristic-review.md`](heuristic-review.md) | Round 1: heuristic review of register, login and the volunteer dashboard. 16 findings, severity, and the fix for each |
| [`usability-test-plan.md`](usability-test-plan.md) | Who we test with, how a session runs, the facilitator script and the six tasks |
| [`sus-questionnaire.md`](sus-questionnaire.md) | System Usability Scale form given after each session, with scoring |
| [`accessibility-checklist.md`](accessibility-checklist.md) | Accessibility checks against WCAG 2.1 AA, with a baseline and an after column |
| [`results-round-2.md`](results-round-2.md) | Round 2: usability test of the fixed version, with task results, SUS scores and what happened to each round 1 finding |

## Measures

| Measure | How it is recorded | Why |
|---|---|---|
| Task completion | Completed without help / with a hint / not completed | Can people actually do the main jobs? |
| Time on task | Seconds, from reading the task to the success condition | Efficiency |
| Errors | Wrong clicks, wrong screens, rejected submissions | Where the interface misleads |
| SUS score | 0 to 100 from the questionnaire | One overall usability number to compare between rounds |
| Accessibility checks passed | Count out of the checklist total | Measurable accessibility improvement |
