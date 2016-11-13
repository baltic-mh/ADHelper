Wenn ein neues Release erzeugt werden soll, ist folgender Prozess notwendig:

1. Eingabe der �nderungen/Erweiterungen/Bugfixes seit dem letzten Release
    => misc/build-res/ReleaseNotes-actual.txt

2. Eventuell Anpassung der Datei
    => misc/Dokumentation/Dokumentation.txt

3. Alle ge�nderten Dateien m�ssen in GIT committed und gepusht sein.

4. Aufruf der Gradle-Task
    gradlew release -Prelease.useAutomaticVersion=true

5. �berpr�fen, ob alles chicko ist!

6. Release zum Download freigeben durch Aufruf der Gradle-Task
    gradlew ssh_release
