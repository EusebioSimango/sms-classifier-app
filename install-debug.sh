#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
APK="$ROOT/app/build/outputs/apk/debug/app-debug.apk"
ADB="$HOME/Library/Android/sdk/platform-tools/adb"

if [[ ! -f "$APK" ]]; then
  echo "APK em falta. A compilar…"
  (cd "$ROOT" && ./gradlew assembleDebug)
fi

if ! "$ADB" devices | awk 'NR>1 && $2=="device" {found=1} END{exit !found}'; then
  echo "Nenhum telemóvel autorizado no ADB."
  echo "Liga o cabo, activa a depuração USB e aceita o pedido no ecrã."
  "$ADB" devices -l
  exit 1
fi

"$ADB" install -r "$APK"
echo "Instalado. Abre Categorizador SMS e concede as permissões."
