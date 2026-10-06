# Building and running

How to build Volunteer Impact Coordinator into a single runnable JAR, and how
to run it.

## What you need

- **JDK 21 or later.** Check with `java -version`.
- **Maven 3.8 or later.** Check with `mvn -v`. Only needed to build; the finished
  JAR runs with Java alone.

### Installing Maven on Windows

1. Download the binary zip from <https://maven.apache.org/download.cgi>.
2. Unzip it somewhere permanent, for example `C:\Tools\apache-maven-3.9.x`.
3. Add its `bin` folder to your PATH: Start, then "Edit the system environment
   variables", then Environment Variables, select `Path` and add the folder.
4. Open a **new** PowerShell window and check with `mvn -v`.

If you'd rather not install it, IntelliJ has Maven built in: open the **Maven**
panel on the right, expand **Lifecycle** and double-click **package**.

## Build with the script

The scripts check that Java and Maven are installed, run the tests, and package
the JAR.

**Windows (PowerShell)**

```powershell
.\build.ps1              # run the tests, then build
.\build.ps1 -SkipTests   # build without the tests
.\build.ps1 -Run         # build, then start the app
```

If PowerShell refuses to run scripts, allow them for the current window only:
`Set-ExecutionPolicy -Scope Process Bypass`.

**macOS and Linux**

```bash
./build.sh               # run the tests, then build
./build.sh --skip-tests  # build without the tests
./build.sh --run         # build, then start the app
```

Both produce `target/volunteer-impact-coordinator-0.1.0-SNAPSHOT-all.jar`.

## Run the JAR

```bash
java -jar target/volunteer-impact-coordinator-0.1.0-SNAPSHOT-all.jar
```

The login screen opens. The database, `vic.db`, is created in the folder you run
the command from.

The JAR contains the JavaFX libraries for the operating system it was **built**
on, so a JAR built on Windows runs on Windows. Build it on each platform you
want to run it on.

## How it works

- **`maven-shade-plugin`** in `pom.xml` bundles the app's classes and every
  library (JavaFX, SQLite) into one JAR during `mvn package`.
- **`Launcher`** is the JAR's entry point instead of `VolunteerImpactApp`. When
  a JAR's main class extends JavaFX's `Application`, Java expects JavaFX as
  separate modules and stops with "JavaFX runtime components are missing".
  `Launcher` doesn't extend it, so it just calls `VolunteerImpactApp.main()` and
  the bundled JavaFX classes load normally.
- The build drops library signature files (`*.SF`, `*.DSA`, `*.RSA`) and
  `module-info.class`, which are only valid inside each library's own JAR and
  stop a merged JAR from starting.

## Running during development

`mvn clean javafx:run` still works as before and is quicker than building the
JAR each time.
