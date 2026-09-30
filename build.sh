#!/usr/bin/env bash
# Volunteer Impact Coordinator - build script (macOS / Linux)
#
# Compiles, runs the tests, and packages a single runnable JAR.
#
#   ./build.sh              compile + test + package
#   ./build.sh --run        the above, then start the app
#   ./build.sh --skip-tests
#
# Uses the Maven Wrapper (./mvnw) when present, so nobody needs Maven
# installed; otherwise falls back to a Maven on PATH.

set -euo pipefail
cd "$(dirname "$0")"

RUN_APP=false
SKIP_TESTS=false
for arg in "$@"; do
  case "$arg" in
    --run)        RUN_APP=true ;;
    --skip-tests) SKIP_TESTS=true ;;
    -h|--help)
      sed -n '2,12p' "$0" | sed 's/^# \{0,1\}//'
      exit 0 ;;
    *)
      echo "Unknown option: $arg" >&2
      exit 1 ;;
  esac
done

if [ -x "./mvnw" ]; then
  MVN="./mvnw"
elif command -v mvn >/dev/null 2>&1; then
  MVN="mvn"
else
  echo "Could not find Maven."
  echo "Easiest fix: open the project in IntelliJ and use the Maven panel"
  echo "on the right (Plugins > javafx > javafx:run)."
  exit 1
fi
echo "Using Maven: $MVN"

GOALS=(clean package)
$SKIP_TESTS && GOALS+=(-DskipTests)

echo "Building..."
"$MVN" -B "${GOALS[@]}"

JAR="$(ls target/*-all.jar 2>/dev/null | head -1 || true)"
if [ -z "$JAR" ]; then
  echo "Build finished but no runnable JAR was produced." >&2
  exit 1
fi

echo
echo "Build succeeded."
echo "Runnable JAR: $JAR"
echo "Run it with:  java -jar $JAR"

if $RUN_APP; then
  echo
  echo "Starting the application..."
  java -jar "$JAR"
fi
