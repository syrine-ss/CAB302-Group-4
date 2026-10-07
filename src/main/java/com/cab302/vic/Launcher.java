package com.cab302.vic;

/**
 * Starts the application from the runnable JAR.
 *
 * <p>The JAR's main class must not extend {@code Application}. If it does,
 * Java looks for JavaFX as separate modules and fails with "JavaFX runtime
 * components are missing". This class does not extend it, so the JavaFX
 * classes bundled inside the JAR load normally.
 */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Hands straight over to the JavaFX application.
     *
     * @param args command-line arguments, passed through unchanged
     */
    public static void main(String[] args) {
        VolunteerImpactApp.main(args);
    }
}
