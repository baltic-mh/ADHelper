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

import teambaltic.adhelper.model.EPropType;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.model.IKey;
import teambaltic.adhelper.model.IPeriod;

// ############################################################################
public abstract class ASettings<KeyType extends IKey> implements ISettings<KeyType>
{
    private final Map<KeyType, Integer> m_IntegerValues;
    // Alle Stundenwerte werden in 100stel Stunden angegeben!
    private final Map<KeyType, Integer> m_HourValues;
    private final Map<KeyType, Map<IPeriod, Integer>> m_HourValuesPeriodSpecific;
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
        m_HourValuesPeriodSpecific = new HashMap<>();
        m_Props         = new Properties();
    }

    protected abstract KeyType[] getKeyValues();

    public void init(final Path fPropertyFile) throws FileNotFoundException, IOException
    {
        InputStream aInputStream;
        final File aPropFile = fPropertyFile.toFile();
        setPropertyFile( aPropFile );
        if( Files.exists( fPropertyFile ) ){
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
    public int getHourValue( final KeyType fKey, final IPeriod fPeriod) {
        if( !EPropType.HOURVALUE.equals( fKey.getPropType() )){
            throw new UnsupportedOperationException("Schlüssel ist nicht vom Typ HOURVALUE: "+fKey);
        }
		final Map<IPeriod, Integer> aHourValuesForThisKey = m_HourValuesPeriodSpecific.get(fKey);
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
        m_IntegerValues.put( fKey, Integer.valueOf( fProps.getProperty( fKey.toString() ) ) );
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
				Map<IPeriod, Integer> aSpecificHourValuesForThisKey = m_HourValuesPeriodSpecific.get(fKey);
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
    public void writeToFile() throws IOException
    {
        final File aPropertyFile = getPropertyFile();
        if( aPropertyFile == null ){
            return;
        }
        for( final Entry<KeyType, Integer> aEntry : m_IntegerValues.entrySet() ){
            m_Props.put( aEntry.getKey().toString(), aEntry.getValue().toString() );
        }
        for( final Entry<KeyType, Integer> aEntry : m_HourValues.entrySet() ){
            m_Props.put( aEntry.getKey().toString(), aEntry.getValue().toString() );
        }
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
