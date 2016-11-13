/**
 * @file
 *
 * Created on 07.11.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerw�rmung!
 *
 * Copyright (C) 2016 TeamBaltic
 */
//#############################################################################


//########################################################################
/**
 * Liest den Inhalt der Datei 'ReleaseNotes-actual.txt' und fügt ihn an den
 * Anfang der Datei 'ReleaseNotes.txt' ein. Als erste Zeile wird die
 * ReleaseNummer, die Version und das Datum vorangestellt.
 * Kommentare aus der eingelesenen Datei werden ignoriert.
 */
public class ReleaseNotesUpdater
{
    static String SEP_LINE = '='*80
    static String NL       = "\r\n"

    String  version
    String  releaseNum
    File    releaseNotesActual
    File    releaseNotes
    
    public ReleaseNotes( Map fProps )
    {
        version     = fProps.version
        releaseNum  = fProps.releaseNum
        releaseNotes        = fProps.releaseNotes
        releaseNotesActual  = fProps.releaseNotesActual
    }

    boolean isReleaseNotesActualFileUptodate()
    {
        boolean upToDate = false
        releaseNotesActual.withReader { reader ->
            String line
            while( line = reader.readLine() ) {
                if( !line.startsWith("//RELEASE:")  ) {
                    continue
                }
                def group = ( line =~ /^\/\/RELEASE:\s*(\d+)/ )
                if( !group.hasGroup() || group.size() == 0 ){
                    break;
                }
                String releaseNumInFile = group[0][1]
                if( releaseNumInFile == releaseNum ){
                    upToDate = true
                }
                break;
            }
        }
        return upToDate
    }
    boolean isReleaseNotesFileUptodate()
    {
        boolean upToDate = false
        releaseNotes.withReader { reader ->
            String line
            while( line = reader.readLine() ) {
                if( !line.startsWith("Release")  ) {
                    continue
                }
                def group = ( line =~ /Release:\s*(\d+)\s+/ )
                if( !group.hasGroup() || group.size() == 0 ){
                    break;
                }
                String latestReleaseNumInFile = group[0][1]
                if( latestReleaseNumInFile == releaseNum ){
                    upToDate = true
                }
                break;
            }
        }
        return upToDate
    }
    
    void writeNewReleaseNotesFile( String newText )
    {
        def parent = releaseNotes.getParent()
        String[] parts = releaseNotes.getName().split("\\.")
        String basename = parts[0]
        String ext      = parts[1]
        File releaseNotes_New = new File(parent, basename+'-new.'+ext)

        releaseNotes_New.write(newText,'UTF-8')
        
        // Alte Datei aus dem Weg räumen:
        File releaseNotes_Prev = new File(parent+"/"+basename+'-previous.'+ext)
        releaseNotes_Prev.delete()
        releaseNotes.renameTo( releaseNotes_Prev )
        
        // Neue Datei ersetzt die alte:
        releaseNotes.delete()
        releaseNotes_New.renameTo(releaseNotes )
    }
    
    int process()
    {
        boolean upToDate = isReleaseNotesFileUptodate() 
        if( upToDate ){
            println "Datei '$releaseNotes' enthält bereits Eintrag für Release '$releaseNum'"
            return -1
        }
        upToDate = isReleaseNotesActualFileUptodate() 
        if( !upToDate ){
            println "Datei '$releaseNotesActual' enthält keinen Eintrag für Release '$releaseNum'"
            return -1
        }

        String text_ReleaseNotes = releaseNotes.text
        StringBuffer newText = new StringBuffer()
        newText.append(SEP_LINE).append(NL)
        def today = new Date().format( "YYYY-MM-dd" )
         newText.append( "Release: $releaseNum\tVersion: $version\tDatum: $today").append(NL)
        releaseNotesActual.eachLine{
            line ->
                if( line.startsWith("#")  ) return
                if( line.startsWith("//") ) return
                newText.append(line).append(NL)
        }
        newText.append(text_ReleaseNotes)
        writeNewReleaseNotesFile( newText.toString() )
        
        return 0
    }

    static int main(args){
        
        def testObject = new ReleaseNotesUpdater(
            version             : "1.2.3",
            releaseNum          : "1002003",
            releaseNotes        : new File('d:\\Mathias\\Entwicklung\\Projekte\\Test\\Jupidator-Test\\RELEASENOTES.txt'),
            releaseNotesActual  : new File('d:\\Mathias\\Entwicklung\\Projekte\\Test\\Jupidator-Test\\misc\\build-res\\ReleaseNotes-actual.txt'),
        )
        return testObject.process();
    }
}

//#############################################################################