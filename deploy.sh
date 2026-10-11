#!/bin/bash
# Compile le framework (-> dist/framework.jar, réutilisable dans d'autres projets),
# compile l'application de test, construit framework.war et le copie dans Tomcat.
#
#   TOMCAT_WEBAPPS=/chemin/vers/tomcat/webapps ./deploy.sh
#   (sinon $CATALINA_HOME/webapps, sinon /opt/tomcat/webapps)
set -euo pipefail
cd "$(dirname "$0")"

PROJECT="framework"
SRC_JAVA="src/java"
SRC_WEBAPP="src/webapp"
LIB="lib"
BUILD="build"
DIST="dist"

TOMCAT_WEBAPPS="${TOMCAT_WEBAPPS:-${CATALINA_HOME:-/opt/tomcat}/webapps}"
if [ ! -d "$TOMCAT_WEBAPPS" ]; then
  echo "Erreur : dossier Tomcat introuvable : $TOMCAT_WEBAPPS" >&2
  echo "Utilise : TOMCAT_WEBAPPS=/ton/chemin/webapps ./deploy.sh" >&2
  exit 1
fi

rm -rf "$BUILD"
mkdir -p "$BUILD/fw" "$BUILD/war/WEB-INF/classes" "$BUILD/war/WEB-INF/lib" "$DIST"

echo "=== 1/3 Framework -> $DIST/framework.jar ==="
find "$SRC_JAVA/mg/itu/annotation" "$SRC_JAVA/mg/itu/framework" -name "*.java" > "$BUILD/fw-sources.txt"
javac -parameters -encoding UTF-8 -cp "$LIB/*" -d "$BUILD/fw" @"$BUILD/fw-sources.txt"
jar cf "$DIST/framework.jar" -C "$BUILD/fw" .

echo "=== 2/3 Application de test ==="
find "$SRC_JAVA" -name "*.java" \
  ! -path "$SRC_JAVA/mg/itu/annotation/*" ! -path "$SRC_JAVA/mg/itu/framework/*" > "$BUILD/app-sources.txt"
if [ -s "$BUILD/app-sources.txt" ]; then
  # -parameters : indispensable pour lire les noms des arguments des méthodes (sprint 7)
  javac -parameters -encoding UTF-8 -cp "$DIST/framework.jar:$LIB/*" \
        -d "$BUILD/war/WEB-INF/classes" @"$BUILD/app-sources.txt"
fi

echo "=== 3/3 WAR et déploiement ==="
cp -r "$SRC_WEBAPP/." "$BUILD/war/"
cp "$DIST/framework.jar" "$BUILD/war/WEB-INF/lib/"
cp "$LIB"/gson-*.jar "$BUILD/war/WEB-INF/lib/"
jar cf "$BUILD/$PROJECT.war" -C "$BUILD/war" .
cp "$BUILD/$PROJECT.war" "$TOMCAT_WEBAPPS/"

echo "=== Terminé ! http://localhost:8080/$PROJECT/deptnew ==="
