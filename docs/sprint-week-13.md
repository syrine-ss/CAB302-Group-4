# Sprint Plan - Project Submission and Presentation 

**Volunteer Impact Coordinator | CAB302 Group 4** |
Author: Maia Sherwin (n11249188)

---

## Sprint Plan: Week 11 to 13 

### Sprint Overview
This will be the final sprint for the development this application. 

This encapsulates the tasks required and timeline for both the Project Progress and Performance submission 
and the Application Presentation.

### Sprint Goal
The final sprint's major goal is the application will be fully developed by the end of this sprint - including updated 
styling and accessibility, all 'must' and some 'should' User Stories has been implemented, and the app has undergone 
refactoring. The application will need final testing and bug fixes. It will also require documentation of any changes, 
refactoring and design elements, as well.

### Sprint Tasks and Assignments
#### 1: Finalising coverage reporting | Ryan              
If not finalised from previous checkpoint, finish the coverage reporting using JaCoCo. 

#### 2: Implementation of the final 'must' include User Stories | Maia
Currently, the main 3 User Stories to be implemented are:
1. Volunteer hours screen (volunteer logs hours, sees status) [US-05]
2. Hours review screen (coordinator approves/rejects) [US-22]
3. Volunteer manages profile [US-02]
4. Volunteer views history [US-04]
5. Coordinator cancels event [US-15]
6. Coordinator completes event [US-23]

However, there are other User Stories that are yet to be implemented - including some 'must' include and a lot
of 'should' or 'could' include. This can be found through Trello. Must have a pull request opened, 
with review from peers and testing before commiting.

#### 3: Application styling and accessibility | Syrine
Update the application styling using measurable UX enhancements (e.g., better usability, accessibility). Make sure to 
edit any new files as they are committed (e.g. Maia's tasks being committed).

#### 4: Remaining refactoring | Sam, Peniel
Refactoring the remaining files that haven't undergone refactoring. architectural refactoring (aligned with your OOD criterion) and
measurable UX enhancements (e.g., better usability, accessibility). Try document these for Ryan to make his documentaion/change logs
easier to write. 

#### 5: Change log and other documentation | Ryan
Final submission will require evidence of systematic improvements through documentation. Proof of architectural refactoring (aligned with your OOD criterion) and measurable UX enhancements (e.g., better usability, 
accessibility). A concise record (change log/design notes/refactoring) ties issues/findings to decisions to code/tests/UI updates. 

Refer to Syrine's `docs/refactoring-and-patterns.md` file for this. Feel free to rearrange the documents and remove/add files as needed 
files for the final submission. Improvements related to the feedback from Submission 1 is acknowledged (brief notes).

Evidence of Feedback‑Driven Improvement, Quality Assurance, Refactoring, UX Validation
- The source code is thoroughly tested and documented
- The UI considers usability, accessibility, and other UX quality metrics
- Quality improvements are systematic and documented

#### 6: Bug fixes | Sam, Peniel
Any bugs (or issues found during testing) will need to be fixed (and maybe documented). This is assigned to Sam and Peniel 
as it aligns with refactoring. 

#### 7: Application testing | All  
As the app is updated, it will need to be tested to find bugs (beyond the automatic build tests). Anyone is free to test and look for bugs,
and the full app will need to be tested for the application submission and presentation. 

When new parts of the application are inserted or updated, they will need testing files added.

#### 8: Presentation planning | Maia, All 
Preperation for the 5-minute presentation during Week 13. This will require a powerpoint. There are 5 mandatory topics to cover:
1. The Problem, the Users, and the Purpose
2. How You Worked as a Team
3. Your Development Approach and Key Decisions
4. Quality Across the System
5. Key Lessons and Professional Insights 

#### 9: Preparation for submission | All
Evidence of Quality, Refactoring, UX Validation
- Pass: Code is buildable and testable with passing unit tests for core behaviours;
  critical/known bugs are addressed by submission. Feedback is acknowledged
  (brief notes), but improvements are limited in scope.
- High Distinction: Evidence that the team seeks feedback to drive systematic
improvement: architectural refactoring (aligned with your OOD criterion) and
measurable UX enhancements (e.g., better usability, accessibility). A concise
record (change log/design notes) ties issues/findings to decisions to
code/tests/UI updates

### Timeline
The presentation takes place during the Week 13 tutorial, Thursday 22nd of October 4-6pm.

The final submission

| Date                     | Checkpoint              | Tasks due at Checkpoint                                                      |
|--------------------------|-------------------------|------------------------------------------------------------------------------|
| Monday Week 13 (19/10)   | Completion of all tasks | Everyone must be completed their tasks. Leaves a few days for final changes. |
| Thursday Week 13 (22/10) | Presentation            | Presentation powerpoint, evidence of work, progress report                   |
| Friday Week 13 (23/10)   | Final submission        | Completed application, video walkthrough                                     |

---
## Assessment Items
### Progress and Performance Submission 
Final Prototype: The Final Prototype delivers the minimal requirements:

a graphical user interface with windows for the main functionalities, in general based on JavaFX
an authentication system to sign-up/sign-in (GUI and models)
a persistency system to store/retrieve/update user data (GUI and models)
one or (more likely) more application windows in which the actual, useful part of the application is performed (again, with GUI and models).
All 'Must-Have' features have now been implemented as they were detailed in the user stories
Object Oriented Design: Advanced use of Object Oriented design patterns (e.g. Dependency Injection, Builder, Singleton, Factory, Observer) and principles (abstraction, encapsulation, inheritance, and polymorphism)
Continuous Integration/Automated Build Workflow

Build Script: Build and Tests are automated using a build script in Github
Automated Build Server: Github 'Actions' are in place to automated the Continuous Integration (CI) process
Documentation: Javadoc comments and generated pages.
Evidence of Feedback‑Driven Improvement, Quality Assurance, Refactoring, UX Validation

The source code is thoroughly tested and documented
The UI considers usability, accessibility, and other UX quality metrics
quality improvements are systematic and documented

### Final Presentation
Timing: Your team will have 5 minutes to present (excluding Q&A.) Note that the team must present as a group, no split presentations. Accurate timing is part of the marking criteria. If the presentation exceeds 5 minutes, we will need to cut you off to ensure that there is time for all groups to present.

Use the slides template to prepare your presentation. There are 5 mandatory topics to cover:

1. The Problem, the Users, and the Purpose
2. How You Worked as a Team
3. Your Development Approach and Key Decisions
4. Quality Across the System
5. Key Lessons and Professional Insights

It is up to the team to decide how to split the presentation, but we need to see clear evidence of contribution from all team member. Ideally, all team members will have their turn at presenting parts of the work. In all cases, the presentation MUST give solid evidence that all team members have contributed to the project and to the crafting of the presentation, what each team member was responsible for, and all team members must be ready to answer general questions about the project and detailed questions about their own contribution.

To ensure that a record of your presentation is kept, all teams must present from the machine at the front of the class so that the session can be recorded.