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
     muss vorher die Umgebungsvariable JAVA_HOME im System auf den neuen Wert 
     gesetzt werden! 
     In der Datei ~User/.gradle/gradle.properties taucht die Java-Version auch 
     noch mal auf - weiß momentan aber nicht, wann die referenziert wird.
     Jetzt weiß ich es: die wird referenziert, wenn eine Gradle-Task aufgerufen wird.
     Wenn also nicht die korrekte Java-Version benutzt wird, muss sie hier 
     definiert werden:
        org.gradle.java.home=/path_to_jdk_directory
     )

4. Überprüfen, ob alles chicko ist!

5. Release zum Download freigeben durch Aufruf der Gradle-Task
    gradlew ssh_release
