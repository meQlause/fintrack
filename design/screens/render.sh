#!/bin/sh
# DOC-08: renders mockup screens of design/project/Main.dc.html to PNG with headless Chrome (390x844, light/dark).
# Usage: design/screens/render.sh <name> "<steps>" [theme]
#   steps: '|'-separated aria-labels or exact texts to tap, or scroll:<px>; e.g. "Riwayat|Statistik"
# Example: design/screens/render.sh stats "Riwayat|Statistik" dark
name="$1"; steps="$2"; theme="${3:-light}"
chrome="${CHROME:-/c/Program Files/Google/Chrome/Application/chrome.exe}"
dir=$(cd "$(dirname "$0")" && pwd)
page="$dir/../project/Main.dc.html"
out="$dir/$name.png"
win() { if command -v cygpath >/dev/null 2>&1; then cygpath -m "$1"; else echo "$1"; fi; }
"$chrome" --headless=new --disable-gpu --hide-scrollbars --window-size=390,844 --force-device-scale-factor=1 \
  --virtual-time-budget=4000 --screenshot="$(win "$out")" "file:///$(win "$page")#theme=$theme&steps=$steps" >/dev/null 2>&1
echo "$out"
