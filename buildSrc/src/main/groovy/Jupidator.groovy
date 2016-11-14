import java.io.File;
import java.util.List;
import java.util.Map;
import groovy.text.Template
import groovy.text.XmlTemplateEngine
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

public class Jupidator
{

    static String   XML_TEMPLATE_JUPIDATOR_ACTUAL = '''\
        <version release="${releaseNum}" version="${version}">
        <description>${description}</description>
        <arch>$fileInfo</arch>
      </version>
'''

    static Template TEMPLATE_JUPIDATOR_ACTUAL = new XmlTemplateEngine().createTemplate(XML_TEMPLATE_JUPIDATOR_ACTUAL)

    // Attribute
    String version
    String releaseNum
    String distributionFolder
    String distributionFolderRoot
    String basename
    String filename
    File   releaseNotesActual
    Map    replacements
    String urlroot
    
    boolean initialized

    Jupidator()
    {
    }

    private void initialize()
    {
        if( initialized ){
            return
        }
        filename     = basename+'.xml'
        replacements = ['APPHOME'       : '\${APPHOME}',
                        'version'       : "$version",
                        'releaseNum'    : "$releaseNum" ]
        initialized  = true
    }
    
    private List getChanges(File fFile)
    {
        List   changes  = []
        def aThisChange = [:]
        fFile.eachLine{
            line ->
                if( line.startsWith("#")  ) return
                if( line.startsWith("//") ) return
                String[] aParts = line.split( "\\]\\s*" )
                if( aParts.length == 2 ) {
                    if( aThisChange.text != null ) {
                        changes << aThisChange
                    }
                    aThisChange = [:]
                    aThisChange.type = aParts[0].replace( '[', '' )
                    aThisChange.text = aParts[1]
                } else {
                    aThisChange.text += "\n"+aParts[0]
                }
        }
        changes << aThisChange
        return changes
    }

    private String makeDescription( List fChanges )
    {
        def writer = new StringWriter()
        def mB = new MarkupBuilder(writer)
        mB.dl{
            fChanges.each{ change ->
                            if( change.type == null ) {
                                return
                            }
                            dt{
                                b(change.type)
                                dd change.text
                            }
            }
        }
        return XmlUtil.escapeXml(writer.toString())
    }

    private String createFileInfo( String fSourceDirName, List fFiles )
    {
        String aResult = "";
        fFiles.each { File aFile ->
            String aFileName = aFile.getName()
            String aSize = aFile.length()
            if( aFileName.endsWith( ".exe" ) ){
                aResult += "<file destdir=\"\${APPHOME}\" name=\"$aFileName\" size=\"$aSize\" sourcedir=\"$fSourceDirName\"/>\n"
            } else {
                aResult += "<file destdir=\"\${APPHOME}\\misc\" name=\"$aFileName\" size=\"$aSize\" sourcedir=\"$fSourceDirName\"/>\n"
            }
        }

        return aResult;
    }

    private List<File> getFiles(String fFolderName)
    {
        File aFolder = new File( fFolderName )
        if( !aFolder.exists() ){
            throw new Exception("Verzeichnis nicht gefunden: "+fFolderName)
        }
        return aFolder.listFiles()
    }

    private Node parse( File fXMLFile )
    {
        String stringXML = fXMLFile.text
        Node aNode       = new XmlParser( false, true ).parseText(stringXML)
        return aNode
    }

    int process()
    {
        initialize()
        File aJupidatorFile_Previous = new File( distributionFolderRoot, basename+'-previous.xml')
        File aJupidatorFile_Actual   = new File( distributionFolderRoot, filename)

        Node nodeJupidator = parse( aJupidatorFile_Actual )
        Node aLatestRelease = nodeJupidator.version[0]
        if( aLatestRelease.@release == releaseNum ) {
            if( aJupidatorFile_Previous.exists() ) {
                aJupidatorFile_Actual.delete()
                aJupidatorFile_Previous.renameTo(aJupidatorFile_Actual);
                nodeJupidator = parse( aJupidatorFile_Actual )
            }
        }

        List changes = getChanges( releaseNotesActual )
        replacements.description = makeDescription( changes )
        List<File> aFiles = getFiles( distributionFolder )
        replacements.fileInfo = createFileInfo( releaseNum, aFiles )

        def jupidatorActual_expanded = TEMPLATE_JUPIDATOR_ACTUAL.make(replacements)
        def nodeJupidator_actual  = new XmlParser( false, true ).parseText(jupidatorActual_expanded.toString())

        nodeJupidator.children().add( 1, nodeJupidator_actual )

        String outxml = XmlUtil.serialize( nodeJupidator )
        File aJupidatorFile_New = new File(distributionFolderRoot, basename+'-new.xml')
        aJupidatorFile_New.write(outxml,'UTF-8')

        // Alte Datei aus dem Weg r�umen:
        File previous = new File (distributionFolderRoot+"/"+basename+'-previous.xml')
        previous.delete()
        // Aktuelle Datei wir die alte:
        aJupidatorFile_Actual.renameTo( previous )
        // Neue Datei ersetzt die alte:
        aJupidatorFile_New.renameTo( distributionFolderRoot+"/"+filename )
        
        return 0
    }

}
