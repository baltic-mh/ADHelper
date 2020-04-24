package teambaltic.adhelper.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NetworkUtilsTest {

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################

    @BeforeClass
    public static void initOnceBeforeStart()
    {
        TestUtils.initLog4J();
    }
    @AfterClass
    public static void shutdownWhenFinished()
    {
    }

    @Before
    public void initBeforeEachTest()
    {
    }

    @After
    public void cleanupAfterEachTest()
    {
    }

    // ########################################################################
    // TESTS
    // ########################################################################

	@Test
	public void test() {
		String aURLAsString = "https://raw.githubusercontent.com/baltic-mh/ADHelper/master/Update.url";
		try {
			String aContentFromURL = NetworkUtils.getContentFromURL(aURLAsString);
			assertFalse( aContentFromURL.isEmpty() );
			assertTrue(aContentFromURL.contains("https://"));
		} catch (Exception fEx) {
			fail( fEx.getMessage() );
		}
	}

}
