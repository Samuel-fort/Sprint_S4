#!/bin/bash
# Nom du projet
PROJECT="framework"
# Répertoires
SRC_JAVA="src/java"
SRC_WEBAPP="src/webapp"
LIB="lib"
BUILD="build"
TOMCAT_WEBAPPS="/home/itu/Documents/apps/tomcat/tomcat/webapps"

# Nettoyage de l’ancien build
rm -rf $BUILD/*
mkdir -p $BUILD/WEB-INF/classes
mkdir -p $BUILD/WEB-INF/lib

echo "=== Compilation des sources (mg.itu.framework + mg.itu.controllers) ==="
find $SRC_JAVA -name "*.java" > sources.txt
javac -cp "$LIB/servlet-api.jar:." -d $BUILD/WEB-INF/classes @sources.txt
rm sources.txt

echo "=== Copie du web.xml et app.xml ==="
cp $SRC_WEBAPP/WEB-INF/web.xml $BUILD/WEB-INF/
cp $SRC_WEBAPP/WEB-INF/app.xml $BUILD/WEB-INF/

echo "=== Copie des fichiers HTML (si existants) ==="
cp -r $SRC_WEBAPP/*.html $BUILD/ 2>/dev/null

echo "=== Création du fichier WAR ==="
cd $BUILD
jar -cvf $PROJECT.war *
cd ..

echo "=== Déploiement dans Tomcat ==="
cp $BUILD/$PROJECT.war $TOMCAT_WEBAPPS/

echo "=== Terminé ! Ouvre ton navigateur sur : http://localhost:8080/$PROJECT/hello ==="