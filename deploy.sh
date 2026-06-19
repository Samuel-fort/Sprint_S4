#!/bin/bash

# Nom du projet
PROJECT="HelloWorld"

# Répertoires
SRC_JAVA="src/java"
SRC_WEBAPP="src/webapp"
LIB="lib"
BUILD="build"
TOMCAT_WEBAPPS="/home/ubuntu/Documents/tomcat/webapps"

# Nettoyage de l’ancien build
rm -rf $BUILD/*
mkdir -p $BUILD/WEB-INF/classes
mkdir -p $BUILD/WEB-INF/lib

echo "=== Compilation de la servlet ==="
javac -cp "lib/servlet-api.jar:." -d build/WEB-INF/classes src/java/*.java

echo "=== Copie du web.xml ==="
cp src/webapp/WEB-INF/web.xml build/WEB-INF/

echo "=== Copie des fichiers HTML (si existants) ==="
cp -r $SRC_WEBAPP/*.html $BUILD/ 2>/dev/null

echo "=== Création du fichier WAR ==="
cd $BUILD
jar -cvf $PROJECT.war *
cd ..

echo "=== Déploiement dans Tomcat ==="
cp $BUILD/$PROJECT.war $TOMCAT_WEBAPPS/

echo "=== Terminé ! Ouvre ton navigateur sur : http://localhost:8080/$PROJECT/hello ==="

