# Usability test plan

## Goal

Find out whether a first-time user can complete the main volunteer and coordinator
tasks without help, and where the interface slows them down or misleads them.

## Participants

- 3 to 5 people per round where possible. Five users typically uncover most of the
  significant usability problems in an interface. Round 2 ran with two, which is
  noted as a limitation in its results.
- Ideally people who have not seen the app before. Teammates can stand in if needed,
  but note it in the results.
- Each participant is recorded only as P1, P2, and so on. No names or personal details
  are written down.

## Roles

| Role | Job |
|---|---|
| Facilitator | Reads the script and the tasks, does not help unless the participant is stuck for 2 minutes |
| Note-taker | Records time, errors and comments in the results file |

One person can do both if needed.

## Setup before each session

1. Run the app from the version being tested (`main`, or the release branch). Note the
   commit hash in the results file.
2. Start from a fresh database: close the app and delete `vic.db`, then start it again.
3. Create the starting data:
   - Coordinator account: username `coord`, password `Test1234!`
   - As `coord`, create the event **Beach Clean**, a date next week, 09:00,
     location **Local beach**, 5 volunteers needed. Log out.
4. Leave the app on the login screen.

## Facilitator script

Read this out at the start:

> "Thanks for helping. We're testing the app, not you, so there are no wrong answers.
> I'll give you some short tasks. Please think out loud as you go: say what you're
> looking for and what you expect to happen. I won't help unless you get really stuck.
> It takes about 15 minutes, and at the end there's a 10-question survey. Nothing that
> identifies you is recorded. Is that OK?"

Then give the tasks one at a time, read out exactly as written. Start timing when you
finish reading the task.

## Tasks

| # | Role | Task (read out) | Success condition |
|---|---|---|---|
| T1 | Volunteer | "Create a volunteer account for yourself and log in." | The volunteer dashboard is showing |
| T2 | Volunteer | "Find the Beach Clean event and sign up for it." | The Beach Clean card shows Withdraw and one fewer spot |
| T3 | Volunteer | "You can't make it after all. Remove yourself from Beach Clean." | The card shows Sign up again |
| T4 | Volunteer | "Log out." | The login screen is showing |
| T5 | Coordinator | "Log in as username coord, password Test1234!, and create an event called Tree Planting next Saturday at 9am at City Park, needing 10 volunteers." | Tree Planting appears in Your Events with those details |
| T6 | Coordinator | "The location has changed. Update Tree Planting so it's at the Botanic Gardens." | Tree Planting shows Botanic Gardens |

If a participant is stuck for 2 minutes, give one hint and mark the task "with hint".
If still stuck after another minute, mark it "not completed" and move on.

## What to record for each task

- Completion: completed / with hint / not completed
- Time in seconds
- Errors: count, plus a short note of each (e.g. "clicked Edit selected with nothing
  selected", "password rejected twice")
- Comments: anything the participant says that shows confusion or a suggestion

## After the tasks

1. Hand over the [SUS questionnaire](sus-questionnaire.md).
2. Ask three questions and write down the answers:
   - "What was the most confusing part?"
   - "Was there anything you expected to be able to do but couldn't?"
   - "If you could change one thing, what would it be?"

## After all sessions in a round

1. Fill in the summary tables in the results file.
2. List each problem as a finding with a severity (see below), and how many
   participants hit it.
3. Create a GitHub issue for each finding of severity 2 or above, labelled `usability`
   or `accessibility`, and link it in the results file.
4. Decide for each: fix now, or leave with a reason. Record the decision in
   [`../feedback-log.md`](../feedback-log.md).

## Severity scale

| Severity | Meaning |
|---|---|
| 0 | Not a problem |
| 1 | Cosmetic: fix if time allows |
| 2 | Minor: slows people down, low priority |
| 3 | Major: causes errors or confusion, high priority |
| 4 | Critical: stops the task being completed, must fix |
