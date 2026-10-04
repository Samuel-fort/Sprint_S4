#!/bin/bash
# Se placer dans le dossier du script (marche quel que soit l'endroit d'où on le lance)
cd "$(dirname "$0")" || exit 1

# Nom du projet
PROJECT="framework"
# Répertoires
SRC_JAVA="src/java"
SRC_WEBAPP="src/webapp"
LIB="lib"
BUILD="build"


# Dossier webapps de Tomcat :
#   1) variable TOMCAT_WEBAPPS si fournie, 2) sinon $CATALINA_HOME/webapps, 3) sinon /opt/tomcat/webapps
TOMCAT_WEBAPPS="${TOMCAT_WEBAPPS:-${CATALINA_HOME:-/opt/tomcat}/webapps}"
if [ ! -d "$TOMCAT_WEBAPPS" ]; then
  echo "Erreur : dossier Tomcat introuvable : $TOMCAT_WEBAPPS" >&2
  echo "Utilise : TOMCAT_WEBAPPS=/ton/chemin/webapps ./deploy.sh" >&2
  exit 1
fi

TOMCAT_WEBAPPS="/home/itu/Documents/apps/tomcat/tomcat/webapps"
 

# Nettoyage de l’ancien build
rm -rf $BUILD/*
mkdir -p $BUILD/WEB-INF/classes
mkdir -p $BUILD/WEB-INF/lib

echo "=== Compilation des sources (mg.itu.framework + mg.itu.controllers) ==="
find $SRC_JAVA -name "*.java" > sources.txt
javac -cp "$LIB/*:." -d $BUILD/WEB-INF/classes @sources.txt || { echo "Erreur de compilation, déploiement annulé" >&2; rm -f sources.txt; exit 1; }
rm sources.txt
cp $LIB/gson-*.jar $BUILD/WEB-INF/lib/ || { echo "Erreur : gson-*.jar introuvable dans $LIB/" >&2; exit 1; }

echo "=== Copie du web.xml et app.xml ==="
cp $SRC_WEBAPP/WEB-INF/web.xml $BUILD/WEB-INF/
cp $SRC_WEBAPP/WEB-INF/app.xml $BUILD/WEB-INF/

echo "=== Copie des fichiers HTML (si existants) ==="
cp -r $SRC_WEBAPP/*.html $BUILD/ 2>/dev/null

echo "=== Création du fichier WAR ==="
cd $BUILD
jar -cvf $PROJECT.war *
cd ..

echo "=== Déploiement dans Tomcat ($TOMCAT_WEBAPPS) ==="
cp $BUILD/$PROJECT.war "$TOMCAT_WEBAPPS/" || { echo "Erreur : copie du WAR impossible vers $TOMCAT_WEBAPPS" >&2; exit 1; }

echo "=== Terminé ! Ouvre ton navigateur sur : http://localhost:8080/$PROJECT/hello ==="