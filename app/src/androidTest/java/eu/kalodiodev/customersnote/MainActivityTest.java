package eu.kalodiodev.customersnote;

import android.os.Build;
import android.os.Environment;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.kalodiodev.customersnote.data.Customer;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * MainActivity Instrumented Test
 *
 * <p>Test using Espresso</p>
 *
 * @author Raptodimos Athanasios
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final String BACKUP_FILE = "/CustomerNotesBackup/customerNotesBackup.db";

    private Customer customer = new Customer("First Name", "Last Name",
            "Profession", "Company Name", "123456", "Dummy Notes");

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {

        grantPermissions();

        clearBackupFile();
    }

    @Test
    public void testToolbar() {
        onView(withId(R.id.toolbar))
                .check(matches(isDisplayed()));

        onView(withText(R.string.app_name))
                .check(matches(withParent(withId(R.id.toolbar))));
    }

    @Test
    public void shouldShowCustomersList() {
        onView(withId(R.id.customers_list))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowAddCustomer() {
        onView(withId(R.id.fab))
                .perform(click());

        onView(withId(R.id.addedit_first_name)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowAbout_AboutMenuItem() {
        // Open the overflow menu OR open the options menu,
        // depending on if the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText(R.string.action_about))
                .perform(click());

        onView(withId(R.id.version_textView)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowNoBackupFile_RestoreMenuItem() {

        perform_restore_database();

        // No Database Backup File Found Message shown
        onView(withText(R.string.dialog_database_backup_not_found))
                .check(matches(isDisplayed()));

        // Click Ok
        onView(withText(R.string.ok))
                .check(matches(isDisplayed()))
                .perform(click());
    }

    @Test
    public void shouldShowDatabaseOverwriteConfirmation_RestoreMenuItem() {
        // Perform Backup to create backup file
        perform_backup_database();

        // Click Restore Database
        perform_restore_database();

        // Overwrite database confirmation shown
        onView(withText(R.string.dialog_restore_overwrite_confirmation))
                .check(matches(isDisplayed()));

        // Confirmation has cancel button
        onView(withText(R.string.cancel))
                .check(matches(isDisplayed()));

        // Perform Overwrite database
        onView(withText(R.string.overwrite))
                .check(matches(isDisplayed()))
                .perform(click());
    }

    @Test
    public void shouldShowBackupDone_BackupMenuItem() {

        perform_backup_database();

        // Backup done toast message shown
        onView(withText(R.string.backup_done))
                .inRoot(withDecorView(not(is(mActivityRule
                        .getActivity()
                        .getWindow()
                        .getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowOverwriteBackupFileConfirmation_BackupMenuItem() {
        // Perform backup to create backup file
        perform_backup_database();
        // Repeat backup, to show overwrite confirmation
        perform_backup_database();

        // Overwrite confirmation should be shown
        // Click Overwrite
        onView(withText(R.string.overwrite))
                .check(matches(isDisplayed()))
                .perform(click());
    }

    @Test
    public void shouldShowSearchView() {

        onView(withId(R.id.menu_search))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withHint(R.string.menu_search))
                .perform(typeText("Test"));
    }

    @Test
    public void shouldKeepShowingListOnRotate() {

        onView(withId(R.id.customers_list))
                .check(matches(isDisplayed()));

        TestHelper.changeOrientation(mActivityRule.getActivity());

        onView(withId(R.id.customers_list))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldBeTwoPane() {
        // If not a tablet, the test will halt and be ignored.
        Assume.assumeTrue(TestHelper.isLargeScreen(mActivityRule.getActivity()));

        // Customer details container
        onView(withId(R.id.customer_details_container))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowCustomerAddEdit() {
        // If not a tablet, the test will halt and be ignored.
        Assume.assumeTrue(TestHelper.isLargeScreen(mActivityRule.getActivity()));

        onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.customer_details_container))
                .check(matches(hasDescendant(withId(R.id.addedit_first_name))));
    }

    @Test
    public void shouldAddNewCustomerOnTablet() {
        // If not a tablet, the test will halt and be ignored.
        Assume.assumeTrue(TestHelper.isLargeScreen(mActivityRule.getActivity()));

        // Click add
        onView(withId(R.id.fab)).perform(click());

        fillAddCustomerFields();

        // Press save
        onView(withId(R.id.addedit_mnu_save))
                .check(matches(isDisplayed()))
                .perform(click());

        // Add Edit Fragment should close
        onView(withId(R.id.addedit_first_name)).check(doesNotExist());
    }

    @Test
    public void shouldKeepFieldsTextAfterOrientationChangeOnTablet() {
        // If not a tablet, the test will halt and be ignored.
        Assume.assumeTrue(TestHelper.isLargeScreen(mActivityRule.getActivity()));

        // Click add
        onView(withId(R.id.fab)).perform(click());

        fillAddCustomerFields();

        TestHelper.changeOrientation(mActivityRule.getActivity());

        onView(withId(R.id.addedit_first_name)).check(matches(withText(customer.getFirstName())));
        onView(withId(R.id.addedit_last_name)).check(matches(withText(customer.getLastName())));
    }

    private void fillAddCustomerFields() {
        // Fill fields
        onView(withId(R.id.addedit_first_name))
                .check(matches(isDisplayed()))
                .perform(typeText(customer.getFirstName()));

        onView(withId(R.id.addedit_last_name))
                .check(matches(isDisplayed()))
                .perform(typeText(customer.getLastName()));
    }

    private void perform_backup_database() {
        // Open Menu
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Click backup database
        onView(withText(R.string.action_backup))
                .check(matches(isDisplayed()))
                .perform(click());
    }

    private void perform_restore_database() {
        // Open Menu
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Click Restore Database
        onView(withText(R.string.action_restore))
                .check(matches(isDisplayed()))
                .perform(click());
    }

    private void grantPermissions() {
        // Permission to write external storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName() +
                            " android.permission.WRITE_EXTERNAL_STORAGE");
        }
    }

    private void clearBackupFile() {
        // Delete Backup file from external storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String backupFile = Environment.getExternalStorageDirectory() + BACKUP_FILE;
            getInstrumentation().getUiAutomation().executeShellCommand(
                    String.format("rm -rf %s", backupFile));
        }
    }
}
