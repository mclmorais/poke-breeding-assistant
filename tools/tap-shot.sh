#!/usr/bin/env bash
# tap-shot.sh X Y OUT.png [WAIT_SECONDS]: tap device pixel X,Y, wait, screenshot, print app crashes
ADB=${ADB:-$HOME/Android/Sdk/platform-tools/adb}
$ADB shell input tap "$1" "$2"
sleep "${4:-3}"
$ADB exec-out screencap -p > "$3"
$ADB logcat -d -b crash | grep -A20 'marcelo.breguenait' || true
