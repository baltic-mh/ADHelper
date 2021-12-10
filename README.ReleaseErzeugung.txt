Wenn ein neues Release erzeugt werden soll, ist folgender Prozess notwendig:

0. In der Datei 
    => gradle.properties
   die Versionsnummer anpassen.
   Wenn dies nicht gemacht wird, wird einfach die Build-Nummer um eins erhöht.

1. Eingabe der Änderungen/Erweiterungen/Bugfixes seit dem letzten Release
    => misc/build-res/ReleaseNotes-actual.txt
    UNBEDINGT die Release-Nummer anpassen!

2. Alle geänderten Dateien müssen in GIT committed und gepusht sein.

3. Aufruf der Gradle-Task
    gradlew release -Prelease.useAutomaticVersion=true
    (Wenn seit dem letzten Aufruf eine neue Java-Version installiert worden ist,
     muss vorher in der Datei ~User/.gradle/gradle.properties das Java-Home 
     angegeben werden. Z.B.:
        org.gradle.java.home=c:/Program Files/Java/jdk1.8.0_191 
     taucht die Java-Version auch 

4. Überprüfen, ob alles chicko ist!

5. Release zum Download freigeben durch Aufruf der Gradle-Task
    gradlew ssh_release
