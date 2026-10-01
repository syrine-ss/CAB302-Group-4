package com.cab302.vic;

/**
 * Entry point used by the packaged JAR.
 *
 * <p>This class exists for a specific JavaFX reason. When the main class
 * extends {@link javafx.application.Application}, the JVM checks for the
 * JavaFX runtime as a module <em>before</em> running any of our code, and
 * fails with:
 *
 * <pre>Error: JavaFX runtime components are missing, and are required to run this application</pre>
 *
 * <p>That is the error every member of the team hit when they tried to run
 * {@link VolunteerImpactApp} directly from the IDE. Launching through a
 * class that does <em>not</em> extend Application skips that check, so the
 * shaded JAR starts by double-click with a plain {@code java -jar}, with no
 * module path arguments and no Maven installed.
 *
 * <p>{@code mvn javafx:run} still works during development and remains the
 * documented way to run from source.
 */
public final class Launcher {

    private Launcher() {
        // Not instantiable: this is only an entry point.
    }

    public static void main(String[] args) {
        VolunteerImpactApp.main(args);
    }
}
