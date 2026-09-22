#!/bin/sh
dir=$(cd "$(dirname "$0")" && pwd -P)
java=java
[ -n "$JAVA_HOME" ] && java="$JAVA_HOME/bin/java"
command -v "$java" >/dev/null 2>&1 || { echo "u need java 17 or newer, install it or set JAVA_HOME" >&2; exit 1; }
exec "$java" -Xmx64m -Xms64m -Dorg.gradle.appname=gradlew -jar "$dir/gradle/wrapper/gradle-wrapper.jar" "$@"
