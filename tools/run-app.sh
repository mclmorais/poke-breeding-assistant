#!/usr/bin/env bash
# Build, install and cold-start the debug app on the connected device; prints app crashes.
set -euo pipefail
cd "$(dirname "$0")/.."
export JAVA_HOME=${JAVA_HOME:-/snap/android-studio/current/jbr}
ADB=${ADB:-$HOME/Android/Sdk/platform-tools/adb}
./gradlew -q :app:installDebug
$ADB logcat -c
$ADB shell am force-stop marcelo.breguenait.breedingassistant
$ADB shell am start -W -n marcelo.breguenait.breedingassistant/.screens.goals.GoalsActivity >/dev/null
