#!/usr/bin/env bash
set -euo pipefail

# Bootstrap script to ensure Gradle 9.3.1 is available and run it.
# This avoids depending on system-installed `gradle` or a checked-in wrapper jar.

GRADLE_VERSION=9.3.1
DIST="gradle-${GRADLE_VERSION}-bin.zip"
CACHE_DIR="$HOME/.gradle/wrapper/dists"
DIST_UNZIP_DIR="$CACHE_DIR/gradle-${GRADLE_VERSION}"
ZIP_PATH="/tmp/${DIST}"

if [ ! -x "${DIST_UNZIP_DIR}/bin/gradle" ]; then
  mkdir -p "${CACHE_DIR}"
  echo "Downloading Gradle ${GRADLE_VERSION}..."
  if command -v curl >/dev/null 2>&1; then
    curl -sS -L "https://services.gradle.org/distributions/${DIST}" -o "${ZIP_PATH}"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "${ZIP_PATH}" "https://services.gradle.org/distributions/${DIST}"
  else
    echo "Neither curl nor wget is available to download Gradle." >&2
    exit 1
  fi
  echo "Unpacking Gradle..."
  unzip -q -o "${ZIP_PATH}" -d "${CACHE_DIR}"
fi

exec "${DIST_UNZIP_DIR}/bin/gradle" "$@"
