/**
 * ASettings.java
 *
 * Created on 02.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import teambaltic.adhelper.model.EPropType;
import teambaltic.adhelper.model.IKey;

// ############################################################################
public abstract class ASettings<KeyType extends IKey> implements ISettings<KeyType>
{
    private final Map<KeyType, Integer> m_IntegerValues;
    // Alle Stundenwerte werden in 100stel Stunden angegeben!
    private final Map<KeyType, Integer> m_HourValues;
    private final Properties m_Props;

    // ------------------------------------------------------------------------
    private File m_PropertyFile;
    private File getPropertyFile(){ return m_PropertyFile; }
    private void setPropertyFile( final File fNewVal ){ m_PropertyFile = fNewVal;}
    // ------------------------------------------------------------------------

    public ASettings()
    {
        m_IntegerValues = new HashMap<>();
        m_HourValues    = new HashMap<>();
        m_Props         = new Properties();
    }

    protected abstract KeyType[] getKeyValues();

    public void init(final Path fPropertyFile) throws FileNotFoundException, IOException
    {
        InputStream aInputStream;
        final File aPropFile = fPropertyFile.toFile();
        if( Files.exists( fPropertyFile ) ){
            setPropertyFile( aPropFile );
            aInputStream = new FileInputStream( aPropFile );
        } else {
            aInputStream = getResourceAsStream( String.format("%s", aPropFile.getName() ) );
        }
        if( aInputStream == null ){
            throw new FileNotFoundException( fPropertyFile.toString() );
        }
        init( aInputStream );
    }
    public void init(final InputStream fPropertyStream) throws FileNotFoundException, IOException
    {
        m_Props.load( fPropertyStream );

        for( final KeyType aKey : getKeyValues() ){
            final EPropType aPropType = aKey.getPropType();
            switch( aPropType ){
                case INTVALUE:
                    transferToIntegerMap( aKey, m_Props );
                    break;

                case HOURVALUE:
                    transferToHourValueMap( aKey, m_Props );
                    break;

                case STRINGVALUE:
                    break;

                default:
                    throw new UnsupportedOperationException("Unbekannter PropertyTyp "+aPropType);
            }
        }
    }

    @Override
    public int getIntValue(final KeyType fKey)
    {
        if( !EPropType.INTVALUE.equals( fKey.getPropType() )){
            throw new UnsupportedOperationException("Schlüssel ist nicht vom Typ INTEGER: "+fKey);
        }
        return m_IntegerValues.get( fKey );
    }

    @Override
    public void setIntValue(final KeyType fKey, final int fNewVal)
    {
        if( !EPropType.INTVALUE.equals( fKey.getPropType() )){
            throw new UnsupportedOperationException("Schlüssel ist nicht vom Typ INTEGER: "+fKey);
        }
        m_IntegerValues.put( fKey, Integer.valueOf( fNewVal ) );
    }

    @Override
    public int getHourValue( final KeyType fKey )
    {
        if( !EPropType.HOURVALUE.equals( fKey.getPropType() )){
            throw new UnsupportedOperationException("Schlüssel ist nicht vom Typ HOURVALUE: "+fKey);
        }
        return m_HourValues.get( fKey );
    }

    @Override
    public void setHourValue( final KeyType fKey, final int fNewVal )
    {
        if( !EPropType.HOURVALUE.equals( fKey.getPropType() )){
            throw new UnsupportedOperationException("Schlüssel ist nicht vom Typ HOURVALUE: "+fKey);
        }
        m_HourValues.put( fKey, Integer.valueOf( fNewVal ) );
    }

    @Override
    public String getStringValue(final KeyType fKey)
    {
        if( !EPropType.STRINGVALUE.equals( fKey.getPropType() )){
            throw new UnsupportedOperationException("Schlüssel ist nicht vom Typ STRING: "+fKey);
        }
        return m_Props.getProperty( fKey.toString() );
    }

    @Override
    public void setStringValue(final KeyType fKey, final String fNewVal)
    {
        if( !EPropType.STRINGVALUE.equals( fKey.getPropType() )){
            throw new UnsupportedOperationException("Schlüssel ist nicht vom Typ STRING: "+fKey);
        }
        m_Props.setProperty( fKey.toString(), fNewVal );
    }

    private void transferToIntegerMap( final KeyType fKey, final Properties fProps )
    {
        m_IntegerValues.put( fKey, Integer.valueOf( fProps.getProperty( fKey.toString() ) ) );
    }

    private void transferToHourValueMap( final KeyType fKey, final Properties fProps )
    {
        final int aHoursInt = Integer.parseInt( fProps.getProperty( fKey.toString() ) );
        m_HourValues.put( fKey, Integer.valueOf( aHoursInt*100 ) );
    }

    @Override
    public void writeToFile() throws IOException
    {
        final File aPropertyFile = getPropertyFile();
        if( aPropertyFile == null ){
            return;
        }
        m_Props.putAll( m_IntegerValues );
        m_Props.putAll( m_HourValues );
        final OutputStream out = new FileOutputStream( aPropertyFile );
        m_Props.store(out, "This is an optional header comment string");
    }

    private static InputStream getResourceAsStream( final String aResourceName )
    {
        InputStream aIS = ASettings.class.getResourceAsStream(aResourceName);
        if( aIS == null ){
            aIS = ASettings.class.getResourceAsStream("/"+aResourceName);
        }
        return aIS;
    }
}

// ############################################################################
