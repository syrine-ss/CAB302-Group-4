<#
.SYNOPSIS
    Builds the runnable JAR for Volunteer Impact Coordinator on Windows.

.DESCRIPTION
    Checks that Java 21+ and Maven are installed, runs the tests, and packages
    the app into target\volunteer-impact-coordinator-<version>-all.jar.

.PARAMETER SkipTests
    Package without running the test suite.

.PARAMETER Run
    Start the app from the JAR once it has been built.

.EXAMPLE
    .\build.ps1
.EXAMPLE
    .\build.ps1 -SkipTests -Run
#>
param(
    [switch]$SkipTests,
    [switch]$Run
)

$ErrorActionPreference = 'Stop'
Set-Location -Path $PSScriptRoot

function Stop-Build([string]$Message) {
    Write-Host "ERROR: $Message" -ForegroundColor Red
    exit 1
}

# --- Check the tools are installed ---------------------------------------

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Stop-Build "Java was not found. Install JDK 21 or later and make sure 'java' is on your PATH."
}

# 'java -version' writes to stderr, so run it through cmd to read it safely.
$versionLine = (cmd /c "java -version 2>&1" | Select-Object -First 1) -as [string]
if ($versionLine -match 'version "(\d+)') {
    if ([int]$Matches[1] -lt 21) {
        Stop-Build "Java 21 or later is required, but found: $versionLine"
    }
}

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Stop-Build ("Maven was not found. Install Maven 3.8 or later and add its 'bin' folder to PATH " +
                "(see docs\build-and-run.md), or build from IntelliJ: Maven panel > Lifecycle > package.")
}

# --- Build ---------------------------------------------------------------

$mavenArgs = @('-B', 'clean', 'package')
if ($SkipTests) {
    $mavenArgs += '-DskipTests'
    Write-Host 'Building without tests...'
} else {
    Write-Host 'Running tests and building...'
}

& mvn @mavenArgs
if ($LASTEXITCODE -ne 0) {
    Stop-Build "Maven build failed (exit code $LASTEXITCODE). See the output above."
}

$jar = Get-ChildItem -Path 'target' -Filter '*-all.jar' | Select-Object -First 1
if (-not $jar) {
    Stop-Build 'The build finished but no runnable JAR was found in target\.'
}

Write-Host ''
Write-Host "Built: $($jar.FullName)" -ForegroundColor Green
Write-Host "Run it with: java -jar `"$($jar.FullName)`""

if ($Run) {
    & java -jar $jar.FullName
}
