#!/usr/bin/env sh

BASEDIR=$(cd "$(dirname "$0")"; pwd)
WRAPPER_DIR="$BASEDIR/.mvn/wrapper"
MAVEN_VERSION="3.9.9"
MAVEN_TGZ="$WRAPPER_DIR/apache-maven-$MAVEN_VERSION-bin.tar.gz"
MAVEN_HOME="$WRAPPER_DIR/apache-maven-$MAVEN_VERSION"
MAVEN_BIN="$MAVEN_HOME/bin/mvn"

mkdir -p "$WRAPPER_DIR"

if [ ! -f "$MAVEN_TGZ" ]; then
  echo "Downloading Apache Maven $MAVEN_VERSION..."
  curl -fsSL "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" -o "$MAVEN_TGZ"
fi

if [ ! -x "$MAVEN_BIN" ]; then
  echo "Extracting Apache Maven $MAVEN_VERSION..."
  tar -xzf "$MAVEN_TGZ" -C "$WRAPPER_DIR"
fi

exec "$MAVEN_BIN" "$@"
