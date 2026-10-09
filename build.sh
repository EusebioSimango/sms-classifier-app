#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
export ANDROID_HOME="${ANDROID_HOME:-/home/esimango/Android/Sdk}"
export ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$ANDROID_HOME}"

cd "$ROOT"
./gradlew assembleDebug
echo "APK: $ROOT/app/build/outputs/apk/debug/app-debug.apk"
