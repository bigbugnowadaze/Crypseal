#!/usr/bin/env bash
set -euo pipefail

mkdir -p ~/.termux
if ! grep -q '^allow-external-apps=true' ~/.termux/termux.properties 2>/dev/null; then
  echo 'allow-external-apps=true' >> ~/.termux/termux.properties
fi

pkg update -y
pkg upgrade -y
pkg install -y python nodejs-lts git ripgrep fd jq tree diffutils patch openssh clang make cmake

python -m ensurepip --upgrade || true
python -m pip install --upgrade pip wheel setuptools || true

mkdir -p ~/Crypseal/projects ~/Crypseal/bin ~/Crypseal/logs

echo 'Crypseal Termux bootstrap complete.'
echo 'Restart Termux after changing allow-external-apps if RUN_COMMAND still fails.'
