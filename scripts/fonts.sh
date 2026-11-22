#!/usr/bin/env bash
# دانلود فونت وزیرمتن (مجوز OFL) و قرار دادن آن در اپ و سایت. نیازمند npm.
set -euo pipefail
cd "$(dirname "$0")/.."
tmp=$(mktemp -d)
(cd "$tmp" && npm pack vazirmatn --silent >/dev/null && tar xzf vazirmatn-*.tgz)
mkdir -p app/src/main/res/font docs/fonts
for w in Regular Medium SemiBold Bold; do
  l=$(echo "$w" | tr '[:upper:]' '[:lower:]')
  ttf=$(find "$tmp/package" -name "Vazirmatn-$w.ttf" | head -1)
  woff=$(find "$tmp/package" -name "Vazirmatn-$w.woff2" | head -1)
  [ -n "$ttf" ] && [ -n "$woff" ] || { echo "Vazirmatn-$w not found in package"; exit 1; }
  cp "$ttf" "app/src/main/res/font/vazirmatn_$l.ttf"
  cp "$woff" "docs/fonts/vazirmatn-$l.woff2"
done
echo "Vazirmatn OK"
