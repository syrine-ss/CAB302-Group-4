# Initial Requirements

## Functional Requirements

1. The system shall allow users to register an account with either a coordinator or volunteer role.
2. The system shall authenticate users via a secure login screen before granting access to features.
3. The system shall allow volunteers to create and edit a personal profile, including contact details, skills, and areas of interest.
4. The system shall allow coordinators to create, edit, and delete events and activities.
5. The system shall allow coordinators to set details for each event, including title, description, date, time, location, and number of volunteers needed.
6. The system shall allow volunteers to browse a list of upcoming events and sign up for those they wish to attend.
7. The system shall allow coordinators to view the list of volunteers signed up for each event.
8. The system shall allow volunteers to log hours contributed to events they attended.
9. The system shall allow coordinators to review and approve or reject logged hours.
10. The system shall generate reports showing total volunteer hours contributed over a chosen date range.
11. The system shall generate reports showing engagement statistics per event, such as number of volunteers, hours contributed, and attendance rate.
12. The system shall display a dashboard summarising recent activity and impact metrics on login.
13. The system shall persist all data (users, events, sign-ups, hours) in a local SQLite database.
14. The system shall allow coordinators to export reports to a shareable format such as CSV or PDF.

## Non-Functional Requirements

15. The application shall run on Windows, macOS, and Linux desktops with Java 21 or later installed.
16. The user interface shall follow a consistent JavaFX design across all screens.
17. Passwords shall be stored using a secure hashing algorithm rather than in plain text.
18. The application shall respond to user actions within two seconds under normal load.
19. The database schema shall support at least 500 volunteers, 200 events, and 5000 hour log entries without performance degradation.
20. The source code shall follow object-oriented design principles and be organised using the MVC pattern.
21. The project shall include automated tests written with JUnit 5, covering the core business logic.
