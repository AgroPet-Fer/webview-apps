#!/bin/sh
APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
MAX_FD="maximum"
warn () { echo "$*"; }
die () { echo; echo "$*"; echo; exit 1; }
OS="`uname`"
case "$OS" in Cygwin*|Msys*) MSYS=msys;; Darwin*) Darwin=1;; MINGW*) MSYS=msys;; NONSTOP*) NONSTOP=1;; esac
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
JAVACMD="java"
if [ -n "$JAVA_HOME" ] ; then
  if [ -x "$JAVA_HOME/jre/sh/java" ] ; then JAVACMD="$JAVA_HOME/jre/sh/java"
  else JAVACMD="$JAVA_HOME/bin/java"; fi
fi
exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS "-Dorg.gradle.appname=$APP_BASE_NAME" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
