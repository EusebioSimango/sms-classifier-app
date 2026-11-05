#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
# shellcheck source=android-sdk.sh
source "$ROOT/android-sdk.sh"

if ! SDK="$(resolve_android_sdk)"; then
  echo "Android SDK não encontrado. Define ANDROID_HOME."
  echo "Mac: \$HOME/Library/Android/sdk"
  echo "Linux: \$HOME/Android/Sdk"
  echo "Windows: %LOCALAPPDATA%\\Android\\Sdk"
  exit 1
fi

export ANDROID_HOME="$SDK"
export ANDROID_SDK_ROOT="$SDK"

cd "$ROOT"
./gradlew assembleDebug
echo "APK: $ROOT/app/build/outputs/apk/debug/app-debug.apk"
