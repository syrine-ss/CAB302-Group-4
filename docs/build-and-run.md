# Building and Running

CAB302 Group 4 | Volunteer Impact Coordinator

## Quick start for a marker

Download `volunteer-impact-coordinator-0.1.0-SNAPSHOT-all.jar` from the
latest CI run (repository > Actions > most recent run > Artifacts), then:

```
java -jar volunteer-impact-coordinator-0.1.0-SNAPSHOT-all.jar
```

Nothing else is needed. No Maven, no JavaFX SDK, no configuration. The
JAR contains every dependency including the JavaFX natives for Windows,
macOS (Intel and Apple Silicon) and Linux. The database file `vic.db` is
created next to the JAR on first run.

Requires Java 21 or newer.

---

## Building from source

### With the build script

```powershell
.\build.ps1              # compile, test, package
.\build.ps1 -Run         # ...then start the app
.\build.ps1 -SkipTests
```

```bash
./build.sh               # compile, test, package
./build.sh --run         # ...then start the app
./build.sh --skip-tests
```

Both scripts look for Maven in order: the Maven Wrapper, then Maven on
PATH, then IntelliJ's bundled copy. The last fallback exists because
several of us had IntelliJ but no standalone Maven, and `mvn` alone
produced "not recognized as the name of a cmdlet".

The build produces `target/volunteer-impact-coordinator-0.1.0-SNAPSHOT-all.jar`.

### With Maven directly

```
mvn clean package        # build the runnable JAR
mvn test                 # tests only
mvn javafx:run           # run from source without packaging
```

### From IntelliJ

Open the project, then the Maven panel on the right:
**Plugins > javafx > javafx:run**.

Do not run `VolunteerImpactApp` directly with the green arrow. It fails
with:

```
Error: JavaFX runtime components are missing, and are required to run this application
```

That is not a broken setup. The JVM checks for the JavaFX runtime as a
module before running any application code when the main class extends
`Application`. Either use `javafx:run`, or run the `Launcher` class, which
exists precisely to sidestep that check.

---

## How the packaging works

Three pieces make the single-file JAR possible.

**`Launcher`** is the JAR's main class and does not extend `Application`.
It just calls `VolunteerImpactApp.main`. Because the class being launched
is an ordinary class, the JVM's JavaFX module check never fires and the
app starts from a plain `java -jar`.

**JavaFX native classifiers.** JavaFX ships compiled native libraries per
operating system. By default Maven resolves only the natives for the
machine doing the build, so a JAR built on Windows would not start on a
marker's Mac. `pom.xml` lists `win`, `mac`, `mac-aarch64` and `linux`
explicitly so one artefact runs anywhere.

**`ServicesResourceTransformer`.** The SQLite driver registers itself
through a `META-INF/services` entry. Shading merges many JARs into one and
would otherwise drop those entries, producing a JAR that starts and then
fails at login with "No suitable driver found". The transformer merges
them correctly.

---

## Continuous integration

`.github/workflows/ci.yml` runs on every push and pull request to `main`.

| Step | What it proves |
|---|---|
| Compile | The code builds on a clean machine, not just someone's laptop |
| Run tests | All 80 tests pass |
| Package | The build script itself works |
| Smoke test | The packaged JAR actually starts, under a virtual display |
| Upload artefacts | The runnable JAR and test reports are downloadable |

The smoke test is the step worth pointing at. A JAR existing is not the
same as a JAR running: both of the packaging pitfalls above produce a file
that builds fine and fails on launch. Starting it under Xvfb catches that
in CI rather than in front of a marker.

---

## Troubleshooting

**"JavaFX runtime components are missing"** — you ran
`VolunteerImpactApp` directly. Use the Maven panel (`javafx:run`), or run
`Launcher`, or use the packaged JAR.

**"mvn is not recognized"** — use `.\build.ps1`, which finds IntelliJ's
bundled Maven, or open the project in IntelliJ and use the Maven panel.

**"No suitable driver found for jdbc:sqlite"** — the JAR was built without
the services transformer. Rebuild with `mvn clean package`.

**SLF4J and "restricted method" warnings on startup** — harmless. SLF4J is
reporting that no logging backend is configured, and the others are newer
JDKs commenting on what SQLite and JavaFX do internally. The application
runs normally.

**Old database errors after pulling** — `DatabaseManager` migrates older
files automatically on startup. If something is still wrong, delete
`vic.db` and it will be recreated.
