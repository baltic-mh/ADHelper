package teambaltic.adhelper.model.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.utils.Log4J;

public class ClubSettingsTest {

	private ClubSettings OBJECTUNDERTEST;

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart() {
        Log4J.initLog4J("log4j-test.properties");
    }

    @Before
    public void initBeforeEachTest() {
        try{
            OBJECTUNDERTEST = new ClubSettings( Paths.get( "misc/TestResources/VereinsDatenPflichtStundenProHalbjahr.properties") );
        }catch( final Exception fEx ){
            fail("Exception: "+ fEx.getMessage() );
        }
    }

    @After
    public void cleanupAfterEachTest() {
    }

    // ########################################################################
    // TESTS
    // ########################################################################

	@Test
	public void test() {
		assertEquals( 300, OBJECTUNDERTEST.getDutyHoursPerPeriod(null));
		final IPeriod aHalfYear2020_1 = new Halfyear( 2020, EPart.FIRST);
		assertEquals(   0, OBJECTUNDERTEST.getDutyHoursPerPeriod(aHalfYear2020_1));
		final IPeriod aHalfYear2020_2 = new Halfyear( 2020, EPart.SECOND);
		assertEquals( 600, OBJECTUNDERTEST.getDutyHoursPerPeriod(aHalfYear2020_2));
        assertEquals( 9.0, OBJECTUNDERTEST.getHourlyRate(), 0.0001 );
	}

}
