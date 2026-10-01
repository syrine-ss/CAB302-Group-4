# Volunteer Impact Coordinator - build script (Windows)
#
# Compiles, runs the tests, and packages a single runnable JAR.
#
#   .\build.ps1          compile + test + package
#   .\build.ps1 -Run     the above, then start the app
#   .\build.ps1 -SkipTests
#
# Uses the Maven Wrapper (.\mvnw.cmd) when present, so nobody needs Maven
# installed. Falls back to a Maven on PATH, then to IntelliJ's bundled copy.

param(
    [switch]$Run,
    [switch]$SkipTests
)

$ErrorActionPreference = "Stop"
Set-Location -Path $PSScriptRoot

function Resolve-Maven {
    if (Test-Path ".\mvnw.cmd") { return ".\mvnw.cmd" }
    if (Get-Command mvn -ErrorAction SilentlyContinue) { return "mvn" }

    # IntelliJ bundles Maven; use it rather than making people install one.
    $candidates = @(
        "$env:LOCALAPPDATA\Programs\IntelliJ IDEA Community Edition\plugins\maven\lib\maven3\bin\mvn.cmd",
        "$env:LOCALAPPDATA\Programs\IntelliJ IDEA Ultimate\plugins\maven\lib\maven3\bin\mvn.cmd",
        "C:\Program Files\JetBrains\*\plugins\maven\lib\maven3\bin\mvn.cmd"
    )
    foreach ($c in $candidates) {
        $hit = Get-Item $c -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($hit) { return $hit.FullName }
    }

    Write-Host ""
    Write-Host "Could not find Maven." -ForegroundColor Red
    Write-Host "Easiest fix: open the project in IntelliJ, then use the Maven panel"
    Write-Host "on the right (Plugins > javafx > javafx:run)."
    exit 1
}

$mvn = Resolve-Maven
Write-Host "Using Maven: $mvn" -ForegroundColor Cyan

$goals = @("clean", "package")
if ($SkipTests) { $goals += "-DskipTests" }

Write-Host "Building..." -ForegroundColor Cyan
& $mvn -B @goals
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed." -ForegroundColor Red
    exit $LASTEXITCODE
}

$jar = Get-ChildItem "target\*-all.jar" -ErrorAction SilentlyContinue | Select-Object -First 1
if (-not $jar) {
    Write-Host "Build finished but no runnable JAR was produced." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Build succeeded." -ForegroundColor Green
Write-Host "Runnable JAR: $($jar.FullName)"
Write-Host "Run it with:  java -jar `"$($jar.Name)`""

if ($Run) {
    Write-Host ""
    Write-Host "Starting the application..." -ForegroundColor Cyan
    & java -jar $jar.FullName
}
