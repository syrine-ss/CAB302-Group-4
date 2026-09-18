# Project Backlog

**Volunteer Impact Coordinator | CAB302 Group 4**

## What's in this folder

| File | What it is |
|---|---|
| `User stories - All.md` | The product backlog. 23 stories with acceptance criteria, MoSCoW priorities, and IDs that match the Trello board. This is the current, authoritative list. |
| `User Stories - Ryan.txt` | Individual draft, written before consolidation |
| `User Stories - Aedan.txt` | Individual draft, written before consolidation |

## How the backlog was built

Each team member wrote three to four stories individually, working from the project brief
(`docs/project-brief.md`). Members committed their drafts either as an individual file in
this folder or directly into the team's shared working document, so not every member has a
separate draft file here — the consolidated backlog is the complete set.

The drafts were then merged into a single backlog and revised as a team. The main changes
between the drafts and the final version:

- **Duplicates removed.** Three near-identical account-creation stories were written
  separately (coordinator, volunteer, and generic versions). These were merged into US-01,
  because role selection is a field on the sign-up form rather than a separate workflow.
- **IDs introduced.** Stories were originally unnumbered, which made them impossible to
  reference from the Trello board or from commit messages. US-01 to US-23 were assigned in
  week 7 and the Trello cards renamed to match.
- **Priorities added.** MoSCoW priorities were applied across the whole backlog so the
  release plan could be ordered by what the product actually needs first.
- **A conflict resolved.** US-05 (volunteer logs hours) and US-16 (coordinator approves
  hours) were written independently by different members and contradicted each other —
  one wrote hours straight to the database, the other assumed they awaited approval. The
  team settled on a pending status, and US-05 was rewritten to match.

Comparing an individual draft file against the same story in `User stories - All.md` shows
this progression.

## Traceability

A story can be followed through the project:

```
User stories - All.md   →   Trello card "US-03: Coordinator Creates Event"
                        →   commit "feat(ui): add dashboards, logout, and event form (US-02, US-03)"
                        →   src/main/java/com/cab302/vic/controller/EventFormController.java
```
