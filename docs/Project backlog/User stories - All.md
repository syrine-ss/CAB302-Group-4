# Project Backlog — User Stories

**Volunteer Impact Coordinator | CAB302 Group 4**

Story IDs match the cards on the team Trello board, so a story can be traced from the
backlog, to the board, to the commit that implements it.

Priority uses MoSCoW: **Must** (needed for a usable product), **Should** (important but
the product works without it), **Could** (desirable if time allows).

---

## US-01 — User Creates Account
**Priority:** Must · **Author:** Maia

As a user, I want to be able to create an account so I can use the application.

Acceptance criteria:
- Clearly visible sign-up button in a predictable location
- Account creation screen with role selection (coordinator, volunteer)
- Sign-up button replaced with username once the account is created / logged in
- Password requirements: at least 8 characters, 1 capital letter, 1 special character
- Success notification after successful account creation
- Account details inserted into the database

---

## US-02 — Volunteer Manages Profile
**Priority:** Should · **Author:** Maia

As a logged-in volunteer, I want to be able to manage my profile so that interested
coordinators can find me.

Acceptance criteria:
- Profile screen accessible by clicking the name in the upper right corner
- Separate boxes for skills, areas of interest, and contact information
- Input fields for each skill / area of interest to be listed
- Input field for contact information with an edit button
- Success notification on successful update of fields
- Modified fields updated in the database

---

## US-03 — Coordinator Creates Event
**Priority:** Must · **Author:** Maia

As a logged-in coordinator, I want to be able to create an event so I can acquire and
manage volunteers.

Acceptance criteria:
- Event creation button clearly visible in the upper right corner
- Input fields for each event detail: title, description, date, time, location, and
  number of volunteers needed
- Create event button below the input fields
- Success notification on successful event creation
- Event details inserted into the database

---

## US-04 — Volunteer Views History
**Priority:** Should · **Author:** Maia

As a volunteer, I would like to see previous events I volunteered for on my profile so I
can track my volunteer history.

Acceptance criteria:
- Profile page has a "Volunteer history" section with a clear heading
- Events are identified by their title
- Most recent event displayed first, oldest last
- Each event is interactive and leads to its event page

---

## US-05 — Volunteer Logs Hours
**Priority:** Must · **Author:** Maia

As a volunteer, I would like to log hours, or update logged hours, for a specific event
so I can help the community organisation keep track of each volunteer's hours.

Acceptance criteria:
- Each active event a volunteer is participating in is listed by title on the volunteer's
  profile under a "Current events" section
- For a logged-in participating volunteer, the event page displays "X hours volunteered"
  along with a clearly interactive "Log hours" / "Update hours" button
- Hours are submitted with a pending status and only count once approved (see US-16)
- Logging hours updates the hours in the database

---

## US-06 — User Browses Events
**Priority:** Must · **Author:** Maia

As a user, I would like a page dedicated to current public community events needing
volunteers so I can find events based on their details.

Acceptance criteria:
- A user does not need to be logged in to view events on the page
- The page displays events and a snapshot of their details (title, organisation name,
  time listed, event date, location, volunteers needed)
- Initially, or when filters are cleared, events are displayed by date posted, most
  recent first
- Each event displayed is interactive so a user can open its page
- The page can be filtered by event detail (location, event type, time posted, event date)

---

## US-07 — Coordinator Searches Volunteers
**Priority:** Should · **Author:** Maia

As a logged-in coordinator, I want the ability to search and filter volunteers by skills
or interests for specific events.

Acceptance criteria:
- Filtering options and a search bar for coordinator use
- Can filter by multiple skills at once
- Results update within the view as filtering is applied
- Results are interactive and allow a coordinator to view a volunteer's profile

---

## US-08 — Volunteer Withdraws Signup
**Priority:** Should · **Author:** Maia

As a volunteer, I want the ability to withdraw my sign-up from an event, in case I am
unable to attend.

Acceptance criteria:
- "Cancel sign-up" option available on the event page for volunteers signed up to it
- Cancelling frees up a slot for events with a set capacity
- The coordinator's list of signed-up volunteers updates to show the withdrawal
- Cancelling removes the event from the volunteer's current events

---

## US-09 — Volunteer Receives Event Reminder
**Priority:** Could · **Author:** Maia

As a logged-in volunteer, I want to receive an alert when an event I've signed up for
approaches, so I can remember to attend.

Acceptance criteria:
- Notification appears on the volunteer's profile a set period before the event starts
- Reminder includes the name of the event, date, time, and location
- Notification clears automatically after the set time passes, or can be cleared manually
  by the volunteer

---

## US-10 — Coordinator Matches Volunteers To Roles
**Priority:** Could · **Author:** Aedan

As a logged-in coordinator, I want to be able to match volunteer profiles with their
skill sets to predefined capabilities or requirements, so they can be matched
automatically or manually to relevant opportunities.

Acceptance criteria:
- Profile filtering that automatically shows whether a profile meets minimum criteria
- Profiles must be created with a set of required fields, so requirements can be checked
- Coordinators can match profiles, or recommend them to other coordinators
- Profiles can be filtered by specific criteria where a role requires it

---

## US-11 — Coordinator Assigns Volunteers To Events
**Priority:** Could · **Author:** Aedan

As a logged-in coordinator, I want to assign volunteers to specific scheduled events
based on their availability and preferences, so events are staffed adequately.

Acceptance criteria:
- Scheduled times, availability and preferences of volunteers can be viewed
- Event availability can be viewed alongside a user's schedule data for easy assignment
- The section has its own menu entry and landing page
- Click-and-drag or a clickable assignment button assigns users to events
- Events display how many people are already assigned, and a recommended number for an
  event of that size where historical data exists for a similar event

---

## US-12 — Coordinator Creates Recurring Events
**Priority:** Could · **Author:** Aedan

As a logged-in coordinator, I want to set up recurring events so I don't have to manually
re-enter events each time.

Acceptance criteria:
- Option to set specific events to recur at a chosen interval
- A prompt appears below the event page, or after event creation, asking whether the
  coordinator wants it to occur again
- Three ways to proceed: an "X" to dismiss, "Create recurring event" which opens the
  recurring event detail page, or "Remind me later" which prompts again after the event ends
- Recurring events can copy details from the previous event

---

## US-13 — Coordinator Creates Event Templates
**Priority:** Could · **Author:** Aedan

As a logged-in coordinator, I want to turn my own or other coordinators' previous events
into an event template, so I can create similar events easily.

Acceptance criteria:
- Access to events that other coordinators have marked public
- Event details can be cloned into a template form that excludes private information
- Personal events displayed in the first tab
- Public coordinator events displayed in the second tab, with a search bar and criteria list

---

## US-14 — Coordinator Views Impact Summary
**Priority:** Should · **Author:** Ryan

As a logged-in coordinator, I want to view a summary of volunteer hours and event
participation, so I can report our organisation's community impact.

Acceptance criteria:
- Impact summary accessible from the coordinator's main navigation
- Displays total volunteer hours, the number of events run, and the number of unique
  volunteers
- Figures are calculated from logged hours held in the database, not entered manually
- Events the coordinator did not create are excluded from the totals
- A message is shown in place of figures when the coordinator has no completed events

---

## US-15 — Coordinator Cancels Event
**Priority:** Must · **Author:** Ryan

As a logged-in coordinator, I want to cancel an event I created, so volunteers aren't left
expecting an event that isn't going ahead.

Acceptance criteria:
- Cancel event option on the event page, visible only to the coordinator who created it
- Confirmation dialogue before cancelling, stating how many volunteers are signed up
- Cancelled event no longer appears on the public events page
- Event moves to a clearly labelled "Cancelled" section on signed-up volunteers' profiles
- Event status set to cancelled in the database; the record is not deleted

---

## US-16 — Coordinator Approves Logged Hours
**Priority:** Should · **Author:** Ryan

As a logged-in coordinator, I want to approve or reject hours volunteers log against my
events, so recorded totals are accurate.

Acceptance criteria:
- Hours submitted by volunteers are stored with a pending status
- Pending submissions listed on the event page for the coordinator, showing volunteer
  name, hours claimed, and date submitted
- Approve and reject options available for each submission
- Only approved hours count towards the totals in the impact summary (US-14)
- The volunteer can see the status of their submission on their profile

---

## US-17 — Volunteer Signs Up For Event
**Priority:** Must · **Author:** Syrine

As a logged-in volunteer, I want to sign up for an upcoming event from the public events
page, so that the coordinator knows I plan to attend and reserves a spot for me.

Acceptance criteria:
- A "Sign up" button is visible on the event page for any logged-in volunteer who is not
  already signed up
- The button is hidden or replaced with "Already signed up" if the volunteer is already
  registered
- The button is labelled "Full" and disabled when an event has reached its volunteer target
- A confirmation message appears after successful sign-up
- Sign-up is recorded in the database with volunteer ID, event ID, and timestamp

---

## US-18 — User Logs In
**Priority:** Must · **Author:** Syrine

As a registered user, I want to log in with my username and password, so that I can access
my account and use features specific to my role.

Acceptance criteria:
- A "Log in" element is clearly visible on the landing page in a predictable location
- The login form has fields for username and password with a "Log in" button
- Passwords entered are masked as they are typed
- The system checks the credentials against the users table and matches the hashed password
- Successful login routes coordinators to the coordinator dashboard and volunteers to the
  volunteer dashboard
- Invalid credentials show a clear error message without revealing whether the username or
  the password was wrong
- The "Log in" element is replaced with the user's name once they are signed in

---

## US-19 — Coordinator Edits Event
**Priority:** Must · **Author:** Syrine

As a logged-in coordinator, I want to edit the details of an event I created, so that I can
correct mistakes or update information such as date or location, without having to cancel
and create a new event.

Acceptance criteria:
- An "Edit event" button is visible on the event page for the coordinator who created it
- Clicking the button opens the event form pre-filled with the current details
- All event fields can be edited (title, description, date, time, location, volunteers needed)
- The form validates the same rules as event creation (no past dates, required fields not empty)
- A "Save changes" button updates the record in the database
- A "Cancel" button discards changes and returns to the event page
- Any volunteer already signed up sees the updated details on their profile
- A success message confirms the changes were saved

---

## US-20 — User Logs Out
**Priority:** Must · **Author:** Syrine

As a logged-in user, I want to be able to log out of my account, so that nobody else can
access my account if I am using a shared device.

Acceptance criteria:
- A "Log out" option is available in the user menu, accessible by clicking the name in the
  upper right corner
- A confirmation dialogue appears asking "Are you sure you want to log out?"
- Confirming ends the current session and returns the user to the landing page
- The user's name in the upper right is replaced with the "Log in" element again
- Attempting to access a logged-in-only page after logging out redirects back to the login page

---

## US-21 — Coordinator Sends Announcements
**Priority:** Should · **Author:** Peniel

As a logged-in coordinator, I want to send announcements to volunteers registered for my
event, so I can communicate important updates or instructions before the event.

Acceptance criteria:
- Event page contains a "Send announcement" option visible only to the coordinator who
  created the event
- Coordinator can enter an announcement title and message
- Before sending, the system displays how many registered volunteers will receive it
- Announcement is sent only to volunteers currently registered for the event
- Announcement is displayed in a notification area on each affected volunteer's account
- Announcement contains the event title and date so the volunteer can identify which event
  it relates to
- Sent announcements are stored against the event in the database
- Coordinator can view previously sent announcements

---

## US-22 — Coordinator Records Attendance
**Priority:** Must · **Author:** Peniel

As a logged-in coordinator, I want to record which registered volunteers attended an event,
so volunteer participation records accurately reflect actual attendance.

Acceptance criteria:
- Coordinator can access an "Attendance" section from an event they created
- All volunteers registered for the event are displayed
- Coordinator can mark each volunteer as Attended, Absent, or Not recorded
- Attendance information is saved to the database
- Attendance can be edited by the coordinator after the event
- Volunteers marked as attended have the event added to their volunteer history once the
  event is completed
- Volunteers who withdrew before the event are not included in the attendance list
- Attendance information is available for use in impact reporting

---

## US-23 — Coordinator Completes Event
**Priority:** Must · **Author:** Peniel

As a logged-in coordinator, I want to mark an event as completed after it finishes, so the
system can separate completed events from active events and finalise their participation
records.

Acceptance criteria:
- A "Complete event" option becomes available to the coordinator after the event's
  scheduled end time
- Completing an event requires confirmation
- Completed event status is stored in the database
- Completed events no longer appear as upcoming public events
- Completed events remain accessible to the coordinator for historical records
- Volunteers who attended have the event moved from Current events to Volunteer history
- Outstanding volunteer hour submissions remain accessible for approval or rejection
- Completed events contribute towards the coordinator's impact summary once relevant hours
  have been approved
- Completed events cannot accept new volunteer registrations

---

## Notes on revisions

**Account creation.** Three near-duplicate stories appeared in the first draft of this
backlog — separate coordinator, volunteer, and generic versions of creating an account.
These were consolidated into US-01, because role selection is a field on the sign-up form
rather than a separate workflow. The earlier drafts are preserved in the individual
per-member story files in this folder as evidence of iteration.

**Hours approval.** US-05 and US-16 were written independently and initially conflicted:
US-05 wrote hours straight to the database, while US-16 assumed hours awaited coordinator
approval. The team resolved this in favour of a pending status, and US-05 was updated to
match. US-22 and US-23 later built on the same record.

**Numbering.** Stories were unnumbered in the first draft, which made them hard to
reference in the project management board and in commit messages. IDs were introduced in
week 7 and now match the Trello card names.
