package eu.kalodiodev.customersnote;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.kalodiodev.customersnote.addeditcustomer.AddEditCustomerActivity;
import eu.kalodiodev.customersnote.data.Customer;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * AddEdit Activity Test
 *
 * @author Raptodimos Athanasios
 */
@RunWith(AndroidJUnit4.class)
public class AddEditActivityTest {

    private Customer customer = new Customer("First Name", "Last Name",
            "Profession", "Company Name", "123456", "Dummy Notes");

    @Rule
    public ActivityTestRule<AddEditCustomerActivity> mActivityRule =
            new ActivityTestRule<>(AddEditCustomerActivity.class, true, false);

    @Test
    public void shouldFinishActivityAfterCustomerAdd() {
        startAddNewCustomer();

        // Fill customer fields
        onView(withId(R.id.addedit_first_name))
                .perform(typeText(customer.getFirstName()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.addedit_last_name))
                .perform(ViewActions.scrollTo(),
                        typeText(customer.getLastName()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.addedit_profession))
                .perform(ViewActions.scrollTo(),
                        typeText(customer.getProfession()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.addedit_company_name))
                .perform(ViewActions.scrollTo(),
                        typeText(customer.getCompanyName()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.addedit_phone_number))
                .perform(ViewActions.scrollTo(),
                        typeText(customer.getPhoneNumber()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.addedit_notes))
                .perform(ViewActions.scrollTo(),
                        typeText(customer.getNotes()), ViewActions.closeSoftKeyboard());

        // Press Save
        onView(withId(R.id.addedit_mnu_save)).perform(click());

        // Activity must finish
        assertTrue("After record added, activity must finish", mActivityRule.getActivity().isFinishing());
    }

    @Test
    public void shouldShowEmptyFieldsWarning() {
        startAddNewCustomer();

        // Press save (without filling fields)
        onView(withId(R.id.addedit_mnu_save)).perform(click());

        // Warning should be shown
        onView(withText(R.string.fill_required_fields_warning)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowAbandonChangesConfirmation() {
        startAddNewCustomer();

        backPressWithChanges();
    }

    @Test
    public void shouldContinueEditingAfterAbandonChangesConfirmation() {
        startAddNewCustomer();

        backPressWithChanges();

        // Press Continue Editing
        onView(withText(R.string.cancelEditDiag_positive_caption))
                .check(matches(isDisplayed()))
                .perform(click());

        // Activity must not finish
        assertFalse("Activity must not finish", mActivityRule.getActivity().isFinishing());
    }

    @Test
    public void shouldFinishActivityAfterAbandonChangesConfirmation() {
        startAddNewCustomer();

        backPressWithChanges();

        // Press Abandon Changes
        onView(withText(R.string.cancelEditDiag_negative_caption))
                .check(matches(isDisplayed()))
                .perform(click());

        // Activity must finish
        assertTrue("Activity must finish", mActivityRule.getActivity().isFinishing());
    }

    @Test
    public void shouldFillFieldsWithCustomerEdited() {
        startEditCustomer();

        // Fields must be filled with customer data
        onView(withId(R.id.addedit_first_name)).check(matches(withText(customer.getFirstName())));
        onView(withId(R.id.addedit_last_name)).check(matches(withText(customer.getLastName())));
        onView(withId(R.id.addedit_profession)).check(matches(withText(customer.getProfession())));
        onView(withId(R.id.addedit_company_name)).check(matches(withText(customer.getCompanyName())));
        onView(withId(R.id.addedit_phone_number)).check(matches(withText(customer.getPhoneNumber())));
        onView(withId(R.id.addedit_notes)).check(matches(withText(customer.getNotes())));
    }

    @Test
    public void showShowDeleteConfirmation() {
        startEditCustomer();

        // Open Menu
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Click delete
        onView(withText(R.string.menu_delete))
                .check(matches(isDisplayed()))
                .perform(click());

        // Delete Confirmation should be shown
        onView(withText(R.string.dialog_delete_confirmation))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldCloseActivityAfterPositiveDeleteConfirmation() {
        startEditCustomer();

        deleteCustomer();

        // Press OK to confirm
        onView(withText(R.string.ok))
                .check(matches(isDisplayed()))
                .perform(click());

        // Activity must finish
        assertTrue("Activity must finish", mActivityRule.getActivity().isFinishing());
    }

    @Test
    public void shouldNotCloseActivityAfterNegativeDeleteConfirmation() {
        startEditCustomer();

        deleteCustomer();

        // Press Cancel to confirmation dialog
        onView(withText(R.string.cancel))
                .check(matches(isDisplayed()))
                .perform(click());

        // Activity must not finish
        assertFalse("Activity must not finish", mActivityRule.getActivity().isFinishing());
    }

    @Test
    public void shouldRetainFieldsValuesOnOrientationChange() {
        startAddNewCustomer();

        // Fill customer fields
        onView(withId(R.id.addedit_first_name))
                .perform(typeText(customer.getFirstName()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.addedit_last_name))
                .perform(ViewActions.scrollTo(),
                        typeText(customer.getLastName()), ViewActions.closeSoftKeyboard());

        // Change Orientation
        TestHelper.changeOrientation(mActivityRule.getActivity());

        // Fields must be filled with customer data
        onView(withId(R.id.addedit_first_name)).check(matches(withText(customer.getFirstName())));
        onView(withId(R.id.addedit_last_name)).check(matches(withText(customer.getLastName())));
    }

    private void deleteCustomer() {
        // Open Menu
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Click delete
        onView(withText(R.string.menu_delete))
                .check(matches(isDisplayed()))
                .perform(click());
    }

    private void backPressWithChanges() {
        // Change field
        onView(withId(R.id.addedit_first_name))
                .perform(ViewActions.scrollTo(), typeText("Test"), ViewActions.closeSoftKeyboard());

        // Perform back press
        onView(withId(R.id.addedit_first_name)).perform(ViewActions.pressBack());

        // Abandon changes confirmation should be shown
        onView(withText(R.string.cancelEditDiag_message)).check(matches(isDisplayed()));
    }

    private void startAddNewCustomer() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, AddEditCustomerActivity.class);
        mActivityRule.launchActivity(intent);
    }

    private void startEditCustomer() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, AddEditCustomerActivity.class);
        intent.putExtra(Customer.class.getSimpleName(), customer);
        mActivityRule.launchActivity(intent);
    }
}
