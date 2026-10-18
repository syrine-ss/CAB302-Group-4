# Accessibility checklist

Checked against WCAG 2.1 level AA, adapted for a JavaFX desktop app. The **Baseline**
column is the state before the CSS and accessibility pass; the **After** column is
filled in once the fixes are merged.

Contrast ratios were calculated from the colours in
`src/main/resources/com/cab302/vic/styles/app.css` using the WCAG relative luminance
formula. Everything else is checked by hand in the running app.

Result key: ✅ pass · ❌ fail · ⚠️ borderline

## Text and colour contrast (WCAG 1.4.3: 4.5:1 for normal text, 3:1 for large text)

| # | Check | Baseline | After |
|---|---|---|---|
| A1 | Headings and brand `#1e3a5f` on white / page background | ✅ 11.5:1 / 10.6:1 | ✅ unchanged |
| A2 | Secondary text, field labels, event details `#556680` on white / page background | ✅ 5.8:1 / 5.4:1 | ✅ unchanged |
| A3 | Primary button text: white on `#2f6fd1` | ✅ 4.9:1 | ✅ unchanged |
| A4 | Secondary button text `#2f6fd1` on white / page background | ⚠️ 4.9:1 / 4.50:1, exactly at the minimum on the page background | ⚠️ unchanged, kept (see heuristic review, "Not changed") |
| A5 | Error and Log out text `#c0392b` on white / page background | ✅ 5.4:1 / 5.0:1 | ✅ unchanged |
| A6 | Success message `#1e8449` on white / page background | ❌ 4.7:1 on white, **4.36:1 on the page background (below 4.5:1)** | ✅ changed to `#196f3d`: 6.2:1 on white, 5.7:1 on the page background |
| A7 | Placeholder text in login and register fields (JavaFX default, light grey) | ❌ roughly 2 to 3:1 (measure exactly from a screenshot) | ⚠️ still light grey, but placeholders now only show examples; the field name is in a visible label |

## Labels and instructions (WCAG 1.3.1, 2.4.6, 3.3.2)

| # | Check | Baseline | After |
|---|---|---|---|
| B1 | Every input on the login screen has a visible label | ❌ username and password use placeholder text only, which disappears once you type | ✅ "Username" and "Password" labels above the fields |
| B2 | Every input on the register screen has a visible label | ❌ five fields use placeholder text only; only Role has a label | ✅ all six fields labelled |
| B3 | Every input on the event form has a visible label | ✅ all fields labelled | ✅ unchanged |
| B4 | Password rules are visible before the user types, not only in a placeholder | ❌ the rules are in the placeholder, so they vanish when typing starts | ✅ rules shown under the field as a live checklist |
| B5 | Labels are linked to their fields for screen readers (`labelFor`) | ❌ no `labelFor` set on any label | ⚠️ linked on login and register; event form not yet |

## Text size (WCAG 1.4.4)

| # | Check | Baseline | After |
|---|---|---|---|
| C1 | No text smaller than 12px | ❌ field labels are 11px (`.field-label`) | ✅ `.field-label` and new `.hint` / error text are 12px |

## Keyboard and focus (WCAG 2.1.1, 2.4.3, 2.4.7)

| # | Check | Baseline | After |
|---|---|---|---|
| D1 | Every screen can be completed with the keyboard only (Tab, Shift+Tab, Enter, Space) | Check by hand | |
| D2 | Tab order follows the visual order | Check by hand | |
| D3 | The focused control is clearly visible | Check by hand (JavaFX shows a default focus ring; confirm it is visible on the blue buttons) | |
| D4 | Enter submits the login, register and event forms | Check by hand (`defaultButton="true"` is set on all three) | |
| D5 | Sign up and Withdraw on the volunteer dashboard can be reached by keyboard | Check by hand | |

## Errors and feedback (WCAG 3.3.1, 1.4.1)

| # | Check | Baseline | After |
|---|---|---|---|
| E1 | Error messages say what went wrong and how to fix it | Check by hand for login, register and the event form | |
| E2 | Errors are not shown by colour alone | Check by hand | |
| E3 | Destructive actions ask for confirmation | ✅ Log out and Withdraw ask first | ✅ unchanged |
| E4 | "Edit selected" with nothing selected explains what to do | Check by hand | |

## Summary

Counts for the 14 checks that can be verified from the code and colours (A1 to A7,
B1 to B5, C1, E3). Baseline is `main` at `0970d4e`; After is `main` at
`cd28951`, with PR #36 and PR #37 merged.

| | Baseline | After |
|---|---|---|
| Passed | 6 / 14 | 11 / 14 |
| Failed | 7 | 0 |
| Borderline | 1 | 3 |

The 8 hand checks (D1 to D5, E1, E2, E4) are filled in from the running app and
added to these totals out of 22.
