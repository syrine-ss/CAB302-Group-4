#!/usr/bin/env bash
#
# Builds the runnable JAR for Volunteer Impact Coordinator on macOS and Linux.
#
# Checks that Java 21+ and Maven are installed, runs the tests, and packages
# the app into target/volunteer-impact-coordinator-<version>-all.jar.
#
# Usage:
#   ./build.sh               run the tests, then build
#   ./build.sh --skip-tests  build without running the tests
#   ./build.sh --run         build, then start the app from the JAR
#
set -euo pipefail
cd "$(dirname "$0")"

skip_tests=false
run_after=false
for arg in "$@"; do
    case "$arg" in
        --skip-tests) skip_tests=true ;;
        --run)        run_after=true ;;
        -h|--help)    sed -n '2,12p' "$0"; exit 0 ;;
        *)            echo "Unknown option: $arg (try --help)" >&2; exit 2 ;;
    esac
done

fail() {
    echo "ERROR: $1" >&2
    exit 1
}

# --- Check the tools are installed ---------------------------------------

command -v java >/dev/null 2>&1 \
    || fail "Java was not found. Install JDK 21 or later and make sure 'java' is on your PATH."

major=$(java -version 2>&1 | head -n 1 | sed -E 's/.*version "([0-9]+).*/\1/')
if [[ "$major" =~ ^[0-9]+$ ]] && (( major < 21 )); then
    fail "Java 21 or later is required, but found Java $major."
fi

command -v mvn >/dev/null 2>&1 \
    || fail "Maven was not found. Install Maven 3.8 or later (see docs/build-and-run.md)."

# --- Build ---------------------------------------------------------------

maven_args=(-B clean package)
if $skip_tests; then
    maven_args+=(-DskipTests)
    echo "Building without tests..."
else
    echo "Running tests and building..."
fi

mvn "${maven_args[@]}" || fail "Maven build failed. See the output above."

jar=$(ls target/*-all.jar 2>/dev/null | head -n 1 || true)
[[ -n "$jar" ]] || fail "The build finished but no runnable JAR was found in target/."

echo
echo "Built: $jar"
echo "Run it with: java -jar $jar"

if $run_after; then
    java -jar "$jar"
fi
