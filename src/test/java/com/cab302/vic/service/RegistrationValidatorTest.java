package com.cab302.vic.service;

import com.cab302.vic.service.RegistrationValidator.Field;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link RegistrationValidator}, added after the heuristic review
 * found that the register screen showed one error at a time at the bottom
 * of the form (finding R4) and did not check the email format (finding R5).
 */
class RegistrationValidatorTest {

    @Test
    void validFormHasNoErrors() {
        Map<Field, String> errors = RegistrationValidator.validate(
                "syrine", "Syrine Shraim", "syrine@example.com", "Strong!Pass1", "Strong!Pass1");
        assertTrue(errors.isEmpty());
    }

    @Test
    void emptyFormReportsEveryMissingFieldAtOnceInFormOrder() {
        Map<Field, String> errors = RegistrationValidator.validate("", " ", "", "", "");
        assertEquals(List.of(Field.USERNAME, Field.FULL_NAME, Field.EMAIL, Field.PASSWORD),
                List.copyOf(errors.keySet()));
    }

    @Test
    void nullInputsAreTreatedAsBlank() {
        Map<Field, String> errors = RegistrationValidator.validate(null, null, null, null, null);
        assertTrue(errors.containsKey(Field.USERNAME));
        assertTrue(errors.containsKey(Field.PASSWORD));
        assertFalse(errors.containsKey(Field.CONFIRM_PASSWORD),
                "No mismatch message until a password has been entered");
    }

    @Test
    void badEmailFormatIsReportedAgainstTheEmailField() {
        for (String bad : new String[] {"syrine", "syrine@", "syrine@example", "sy rine@example.com", "@example.com"}) {
            Map<Field, String> errors = RegistrationValidator.validate(
                    "syrine", "Syrine Shraim", bad, "Strong!Pass1", "Strong!Pass1");
            assertEquals(List.of(Field.EMAIL), List.copyOf(errors.keySet()), "Expected email error for: " + bad);
        }
    }

    @Test
    void weakPasswordIsReportedAgainstThePasswordField() {
        Map<Field, String> errors = RegistrationValidator.validate(
                "syrine", "Syrine Shraim", "syrine@example.com", "weakpass", "weakpass");
        assertEquals(List.of(Field.PASSWORD), List.copyOf(errors.keySet()));
    }

    @Test
    void mismatchedPasswordsAreReportedAgainstTheConfirmField() {
        Map<Field, String> errors = RegistrationValidator.validate(
                "syrine", "Syrine Shraim", "syrine@example.com", "Strong!Pass1", "Strong!Pass2");
        assertEquals(List.of(Field.CONFIRM_PASSWORD), List.copyOf(errors.keySet()));
    }

    @Test
    void checklistMarksEachPasswordRuleSeparately() {
        String none = RegistrationValidator.passwordChecklist("");
        assertEquals(3L, none.chars().filter(c -> c == '\u2717').count());

        String lengthOnly = RegistrationValidator.passwordChecklist("abcdefgh");
        assertTrue(lengthOnly.contains("\u2713 8+ characters"));
        assertTrue(lengthOnly.contains("\u2717 1 uppercase letter"));
        assertTrue(lengthOnly.contains("\u2717 1 special character"));

        String all = RegistrationValidator.passwordChecklist("Strong!Pass1");
        assertEquals(3L, all.chars().filter(c -> c == '\u2713').count());
    }

    @Test
    void checklistAgreesWithAuthServicePasswordRule() {
        for (String pw : new String[] {"Strong!Pass1", "short!A", "lowercase1!", "NoSpecial1", "\u00c9cole!!!"}) {
            boolean checklistAllMet = !RegistrationValidator.passwordChecklist(pw).contains("\u2717");
            assertEquals(AuthService.isPasswordStrong(pw), checklistAllMet, "Mismatch for: " + pw);
        }
    }
}
