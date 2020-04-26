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
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.EPropType;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.model.IKey;
import teambaltic.adhelper.model.IPeriod;

// ############################################################################
public abstract class ASettings<KeyType extends IKey> implements ISettings<KeyType>
{
    private static final Logger sm_Log = Logger.getLogger(ASettings.class);

    private final Map<KeyType, Integer> m_IntegerValues;
    // Alle Stundenwerte werden in 100stel Stunden angegeben!
    private final Map<KeyType, Integer> m_HourValues;
    private final Map<KeyType, Map<Halfyear, Integer>> m_HourValuesPeriodSpecific;
    private Properties m_Props;

    // ------------------------------------------------------------------------
    private File m_PropertyFile;
    private File getPropertyFile(){ return m_PropertyFile; }
    private void setPropertyFile( final File fNewVal ){ m_PropertyFile = fNewVal;}
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final boolean m_Local;
    public boolean isLocal() {return m_Local; }
    // ------------------------------------------------------------------------

    public ASettings()
    {
        this( false );
    }
    public ASettings(final boolean fLocal)
    {
        m_Local         = fLocal;
        m_IntegerValues = new HashMap<>();
        m_HourValues    = new HashMap<>();
        m_HourValuesPeriodSpecific = new HashMap<>();
    }

    protected abstract KeyType[] getKeyValues();

    public void init(final Path fPropertyFile) throws FileNotFoundException, IOException
    {
        final File aPropFile = fPropertyFile.toFile();
        setPropertyFile( aPropFile );
        final InputStream aResourceStream = getResourceAsStream( aPropFile.getName() );
        InputStream aFileStream = null;
        if( Files.exists( fPropertyFile ) ){
            aFileStream = new FileInputStream( aPropFile );
        }
        if( aFileStream == null && aResourceStream == null ){
            throw new FileNotFoundException( fPropertyFile.toString() );
        }
        init(aResourceStream, aFileStream, aPropFile );
    }
    private void init(
            final InputStream fResourceStream,
            final InputStream fFileStream,
            final File fPropFile) throws FileNotFoundException, IOException
    {
        final Properties aDefaultProps = new Properties();
        if( fResourceStream != null ) {
            aDefaultProps.load(fResourceStream);
        }
        m_Props = new Properties(aDefaultProps);
        if( fFileStream != null ) {
            m_Props.load( fFileStream );
            if( !isLocal() ) {
                for ( final Entry<Object, Object> aEntry : m_Props.entrySet() ) {
                    sm_Log.warn( String.format("Lokal überschriebener Wert in Datei %s: %s => %s",
                            fPropFile, aEntry.getKey(), aEntry.getValue() ));
                }
            }
        }

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
    public int getHourValue( final KeyType fKey, final IPeriod fPeriod) {
        if( !EPropType.HOURVALUE.equals( fKey.getPropType() )){
            throw new UnsupportedOperationException("Schlüssel ist nicht vom Typ HOURVALUE: "+fKey);
        }
        final Map<Halfyear, Integer> aHourValuesForThisKey = m_HourValuesPeriodSpecific.get(fKey);
        if( fPeriod == null || aHourValuesForThisKey == null ) {
            return getHourValue(fKey);
        }
        final Integer aHourValuesForThisPeriod = aHourValuesForThisKey.get(fPeriod);
        return aHourValuesForThisPeriod == null ? getHourValue(fKey) : aHourValuesForThisPeriod;
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
        final String aProperty = fProps.getProperty( fKey.toString() );
        if( aProperty == null ) {
            throw new UnsupportedOperationException("Kein Wert angegeben für Schlüssel: "+fKey);
        }
        m_IntegerValues.put( fKey, Integer.valueOf( aProperty ) );
    }

    private void transferToHourValueMap( final KeyType fKey, final Properties fProps )
    {
        // Direct value:
        final String aRootKeyAsString = fKey.toString();
        final int aHoursInt = Integer.parseInt( fProps.getProperty( aRootKeyAsString ) );
        m_HourValues.put( fKey, Integer.valueOf( aHoursInt*100 ) );
        // Find period specific values
        for(final String aThisPropKey : fProps.stringPropertyNames() ) {
            if( aThisPropKey.startsWith(aRootKeyAsString+".") ) {
                final Halfyear aHY = halfyearFromPropKey( aThisPropKey );
                Map<Halfyear, Integer> aSpecificHourValuesForThisKey = m_HourValuesPeriodSpecific.get(fKey);
                if( aSpecificHourValuesForThisKey == null ){
                    aSpecificHourValuesForThisKey = new HashMap<>();
                    m_HourValuesPeriodSpecific.put( fKey, aSpecificHourValuesForThisKey);
                }
                final int aSpecificHoursInt = Integer.parseInt( fProps.getProperty( aThisPropKey ) );
                aSpecificHourValuesForThisKey.put(aHY, Integer.valueOf( aSpecificHoursInt*100 ) );
            }
        }
    }

    @Override
    public void writeToFile() throws IOException {
        writeToFile( getPropertyFile(), "This is an optional header comment string" );
    }

    @Override
    public void writeToFile(final File fOutputFile, final String fComment ) throws IOException
    {
        if( fOutputFile == null ){
            return;
        }
        for( final Entry<KeyType, Integer> aEntry : m_IntegerValues.entrySet() ){
            m_Props.put( aEntry.getKey().toString(), aEntry.getValue().toString() );
        }
        for( final Entry<KeyType, Integer> aEntry : m_HourValues.entrySet() ){
            final Integer aValue = aEntry.getValue()/100;
            m_Props.put( aEntry.getKey().toString(), aValue.toString() );
        }
        for ( final Entry<KeyType, Map<Halfyear, Integer>> aEntryForKey : m_HourValuesPeriodSpecific.entrySet() ) {
            final KeyType aKey = aEntryForKey.getKey();
            final Map<Halfyear, Integer> aMapForKey = aEntryForKey.getValue();
            for ( final Entry<Halfyear, Integer> aEntryForPeriod : aMapForKey.entrySet() ) {
                final Halfyear aPeriod = aEntryForPeriod.getKey();
                final Integer aValue  = aEntryForPeriod.getValue()/100;
                m_Props.put( String.format("%s.%d_%d", aKey.toString(), aPeriod.getYear(), aPeriod.getPart().equals(EPart.FIRST) ? 1 : 2 ), aValue.toString());
            }
        }
        final OutputStream out = new FileOutputStream( fOutputFile );
        m_Props.store(out, fComment);
        out.close();
    }

    private static InputStream getResourceAsStream( final String aResourceName )
    {
        InputStream aIS = ASettings.class.getResourceAsStream(aResourceName);
        if( aIS == null ){
            aIS = ASettings.class.getResourceAsStream("/"+aResourceName);
        }
        return aIS;
    }

    private static Halfyear halfyearFromPropKey(final String fPropKey) {
        final String[] aParts = fPropKey.split("\\.", 2);
        if(aParts.length != 2) {
            throw  new UnsupportedOperationException("Kein korrektes Format für Halbjahres-spezifischen Schlüssel (<Schluessel>.<YYYY>_[1|2]): "+fPropKey);
        }
        final String aHalfyearString = aParts[1];
        final String[] aYearAndPart = aHalfyearString.split("_", 2);
        if(aYearAndPart.length != 2) {
            throw  new UnsupportedOperationException("Kein korrektes Format für Halbjahres-spezifischen Schlüssel (<Schluessel>.<YYYY>_[1|2]): "+fPropKey);
        }
        return new Halfyear(Integer.parseInt( aYearAndPart[0] ), aYearAndPart[1].equals("1") ? EPart.FIRST : EPart.SECOND);
    }

}

// ############################################################################
