#!/usr/bin/env bash
# Resolves the Android SDK on macOS, Linux and Windows (Git Bash).

resolve_android_sdk() {
  if [[ -n "${ANDROID_HOME:-}" && -d "$ANDROID_HOME" ]]; then
    printf '%s\n' "$ANDROID_HOME"
    return 0
  fi

  local candidate=""
  case "$(uname -s)" in
    Darwin)
      candidate="$HOME/Library/Android/sdk"
      ;;
    Linux)
      candidate="$HOME/Android/Sdk"
      ;;
    MINGW*|MSYS*|CYGWIN*|Windows*)
      if [[ -n "${LOCALAPPDATA:-}" ]]; then
        candidate="$LOCALAPPDATA/Android/Sdk"
      elif [[ -n "${USERPROFILE:-}" ]]; then
        candidate="$USERPROFILE/AppData/Local/Android/Sdk"
      fi
      ;;
  esac

  if [[ -n "$candidate" && -d "$candidate" ]]; then
    printf '%s\n' "$candidate"
    return 0
  fi
  return 1
}

resolve_adb() {
  local sdk="${1:-}"
  if [[ -n "$sdk" && -x "$sdk/platform-tools/adb" ]]; then
    printf '%s\n' "$sdk/platform-tools/adb"
    return 0
  fi
  if [[ -n "$sdk" && -x "$sdk/platform-tools/adb.exe" ]]; then
    printf '%s\n' "$sdk/platform-tools/adb.exe"
    return 0
  fi
  if command -v adb >/dev/null 2>&1; then
    command -v adb
    return 0
  fi
  return 1
}
