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

Checked by reading the FXML, controllers and CSS for each screen. D3 was then
confirmed in the running app.

| # | Check | Baseline | After |
|---|---|---|---|
| D1 | Every screen can be completed with the keyboard only (Tab, Shift+Tab, Enter, Space) | ⚠️ everything except the dashboard's Sign up / Withdraw buttons, which sat inside list cells (see D5) | ✅ every field, button, tick box and dropdown is a standard focusable control |
| D2 | Tab order follows the visual order | ✅ controls are declared in screen order; labels are not in the Tab order | ✅ unchanged |
| D3 | The focused control is clearly visible | ❌ the custom `.primary`, `.secondary` and `.danger` button styles replace JavaFX's built-in focus ring, so a focused button looks the same as an unfocused one (confirmed in the app) | ✅ 2px `#1e3a5f` outline on focused buttons (branch `ux/focus-visible`) |
| D4 | Enter submits the login, register and event forms | ✅ `defaultButton="true"` on all three | ✅ unchanged |
| D5 | Sign up and Withdraw on the volunteer dashboard can be reached by keyboard | ❌ buttons inside `ListView` cells (heuristic review V7) | ✅ cards are ordinary nodes, so Tab reaches each button (PR #37) |

## Errors and feedback (WCAG 3.3.1, 1.4.1)

| # | Check | Baseline | After |
|---|---|---|---|
| E1 | Error messages say what went wrong and how to fix it | ✅ messages name the problem (e.g. "Event date must be in YYYY-MM-DD format"), but register showed only one at a time | ✅ register errors now also say how to fix, e.g. "Enter a valid email address, like name@example.com" |
| E2 | Errors are not shown by colour alone | ✅ every error has text | ✅ text, plus a red border on the register field at fault |
| E3 | Destructive actions ask for confirmation | ✅ Log out and Withdraw ask first | ✅ unchanged |
| E4 | "Edit selected" with nothing selected explains what to do | ✅ "Select an event to edit first." | ✅ unchanged |

## Summary

Baseline is `main` at `0970d4e`. After is `main` with PR #36, PR #37 and the
focus outline (`ux/focus-visible`) merged.

| | Baseline | After |
|---|---|---|
| Passed | 11 / 22 | 19 / 22 |
| Failed | 9 | 0 |
| Borderline | 2 | 3 |

The three borderline items left (A4, A7, B5) are explained in the heuristic review
under "Not changed".
