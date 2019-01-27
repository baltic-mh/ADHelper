Wenn ein neues Release erzeugt werden soll, ist folgender Prozess notwendig:

0. In der Datei 
    => gradle.properties
   die Versionsnummer anpassen.
   Wenn dies nicht gemacht wird, wird einfach die Build-Nummer um eins erhöht.

1. Eingabe der Änderungen/Erweiterungen/Bugfixes seit dem letzten Release
    => misc/build-res/ReleaseNotes-actual.txt
    UNBEDINGT die Release-Nummer anpassen!

2. Eventuell Anpassung der Datei
    => misc/Dokumentation/Dokumentation.txt

3. Alle geänderten Dateien müssen in GIT committed und gepusht sein.

4. Aufruf der Gradle-Task
    gradlew release -Prelease.useAutomaticVersion=true
    (Wenn seit dem letzten Aufruf eine neue Java-Version installiert worden ist,
     muss vorher die Umgebungsvariable JAVA_HOME im System auf den neuen Wert 
     gesetzt werden! 
     In der Datei ~User/.gradle/gradle.properties taucht die Java-Version auch 
     noch mal auf - weiß momentan aber nicht, wann die referenziert wird.)

5. Überprüfen, ob alles chicko ist!

6. Release zum Download freigeben durch Aufruf der Gradle-Task
    gradlew ssh_release
