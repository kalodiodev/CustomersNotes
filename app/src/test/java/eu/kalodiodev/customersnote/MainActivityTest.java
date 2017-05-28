package eu.kalodiodev.customersnote;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;

/**
 * Main Activity Unit Test Using Robolectric
 *
 * @author Athanasios Raptodimos
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    private MainActivity mMainActivity;

    @Before
    public void setUp() throws Exception {
        mMainActivity = Robolectric.setupActivity( MainActivity.class );
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertNotNull(mMainActivity);
    }

    @Test
    public void shouldHaveCustomersFragment() {
        assertNotNull(mMainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment));
    }
}