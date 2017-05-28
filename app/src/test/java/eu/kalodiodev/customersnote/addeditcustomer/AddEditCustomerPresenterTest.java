package eu.kalodiodev.customersnote.addeditcustomer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import eu.kalodiodev.customersnote.data.Customer;
import eu.kalodiodev.customersnote.data.ICustomersRepository;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Add Edit Customer Presenter Test
 *
 * @author Athanasios Raptodimos
 */
public class AddEditCustomerPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private AddEditCustomerContract.View view;

    @Mock
    private ICustomersRepository mCustomersRepository;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    private AddEditCustomerPresenter mPresenter;

    private Customer customer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        customer = new Customer("First Name", "Last Name",
                "Profession", "Company Name", "PhoneNumber", "Notes");
    }

    private void withEmptyFields() {
        when(view.getFirstName()).thenReturn("");
        when(view.getLastName()).thenReturn("");
        when(view.getProfession()).thenReturn("");
        when(view.getCompanyName()).thenReturn("");
        when(view.getPhoneNumber()).thenReturn("");
        when(view.getNotes()).thenReturn("");
    }

    private void withNonEmptyFields() {
        when(view.getFirstName()).thenReturn(customer.getFirstName());
        when(view.getLastName()).thenReturn(customer.getLastName());
        when(view.getProfession()).thenReturn(customer.getProfession());
        when(view.getCompanyName()).thenReturn(customer.getCompanyName());
        when(view.getPhoneNumber()).thenReturn(customer.getPhoneNumber());
        when(view.getNotes()).thenReturn(customer.getNotes());
    }

    private void saveNewCustomer() {
        mPresenter = new AddEditCustomerPresenter(null, view, mCustomersRepository);

        // When the presenter is asked to save customer
        mPresenter.save();
    }

    private void updateCustomer() {
        // Updated customer, must have an id
        customer.setId(5);

        mPresenter = new AddEditCustomerPresenter(customer, view, mCustomersRepository);

        // Presenter is asked to save customer
        mPresenter.save();
    }

    private void deleteCustomer(boolean withConfirmation) {
        // Customer to be deleted, must have an id
        customer.setId(5);
        mPresenter = new AddEditCustomerPresenter(customer, view, mCustomersRepository);

        // Presenter is asked to delete a customer
        mPresenter.delete(withConfirmation);
    }

    @Test
    public void shouldInitializeFieldsWithLoadedCustomer() {
        mPresenter = new AddEditCustomerPresenter(customer, view, mCustomersRepository);

        // When the presenter is asked to load customer
        mPresenter.initializeFields();

        // Then presenter calls view to set initialize fields with customer properties
        verify(view).setFirstName(customer.getFirstName());
        verify(view).setLastName(customer.getLastName());
        verify(view).setProfession(customer.getProfession());
        verify(view).setCompanyName(customer.getCompanyName());
        verify(view).setPhoneNumber(customer.getPhoneNumber());
        verify(view).setNotes(customer.getNotes());
    }

    @Test
    public void shouldLeaveEmptyFieldsAddingNewCustomer() {
        mPresenter = new AddEditCustomerPresenter(null, view, mCustomersRepository);

        // When the presenter is asked to load customer
        mPresenter.initializeFields();

        // Then presenter will do nothing with the view
        verifyZeroInteractions(view);
    }

    @Test
    public void shouldShowEmptyNameError() {
        mPresenter = new AddEditCustomerPresenter(null, view, mCustomersRepository);

        // Empty First Name
        when(view.getFirstName()).thenReturn("");

        // When the presenter is asked to save a customer with empty name
        mPresenter.save();

        // Then an empty name error is shown
        verify(view).showEmptyFirstName();
    }

    @Test
    public void shouldSaveNewCustomer() {
        // With filled fields
        withNonEmptyFields();

        saveNewCustomer();

        // Then new customer will be saved
        verify(mCustomersRepository).save(customerCaptor.capture());

        // Customer saved
        Customer savedCustomer = customerCaptor.getValue();
        Assert.assertThat(savedCustomer.getFirstName(), is(customer.getFirstName()));
        Assert.assertThat(savedCustomer.getLastName(), is(customer.getLastName()));
        Assert.assertThat(savedCustomer.getProfession(), is(customer.getProfession()));
        Assert.assertThat(savedCustomer.getCompanyName(), is(customer.getCompanyName()));
        Assert.assertThat(savedCustomer.getPhoneNumber(), is(customer.getPhoneNumber()));
        Assert.assertThat(savedCustomer.getNotes(), is(customer.getNotes()));
    }

    @Test
    public void shouldShowMessageAfterNewCustomerSave() {
        // with filled fields
        withNonEmptyFields();

        saveNewCustomer();

        // Then save completed message shown
        verify(view).showSaveCompletedMessage();
    }

    @Test
    public void shouldCloseViewAfterNewCustomerSave() {
        // with filled fields
        withNonEmptyFields();

        saveNewCustomer();

        // Then close view
        verify(view).close();
    }

    @Test
    public void shouldUpdateCustomer() {
        // with filled fields
        withNonEmptyFields();

        updateCustomer();

        // Presenter will update customer
        verify(mCustomersRepository).update(customerCaptor.capture());

        // Update with customer
        Customer updatedCustomer = customerCaptor.getValue();
        Assert.assertThat(updatedCustomer.getId(), is(customer.getId()));
        Assert.assertThat(updatedCustomer.getFirstName(), is(customer.getFirstName()));
        Assert.assertThat(updatedCustomer.getLastName(), is(customer.getLastName()));
        Assert.assertThat(updatedCustomer.getProfession(), is(customer.getProfession()));
        Assert.assertThat(updatedCustomer.getCompanyName(), is(customer.getCompanyName()));
        Assert.assertThat(updatedCustomer.getPhoneNumber(), is(customer.getPhoneNumber()));
        Assert.assertThat(updatedCustomer.getNotes(), is(customer.getNotes()));
    }

    @Test
    public void shouldShowMessageAfterCustomerUpdate() {
        // With filled fields
        withNonEmptyFields();

        updateCustomer();

        // Then save completed message shown
        verify(view).showSaveCompletedMessage();
    }

    @Test
    public void shouldCloseViewAfterCustomerUpdate() {
        // with filled fields
        withNonEmptyFields();

        updateCustomer();

        // Then close view
        verify(view).close();
    }


    @Test
    public void shouldAskForDeleteConfirmation() {
        // Delete Customer with confirmation
        deleteCustomer(true);

        // Then a delete confirmation should be shown
        verify(view).showDeleteConfirmation();
    }

    @Test
    public void shouldDeleteCustomer() {
        // Delete a customer without confirmation
        deleteCustomer(false);

        // Then customer must be deleted
        verify(mCustomersRepository).delete(customer);
    }

    @Test
    public void shouldShowDeletedMessageAfterCustomerDeletion() {
        // Delete a customer without confirmation
        deleteCustomer(false);

        // Then deleted message shown
        verify(view).showDeleteCompletedMessage();
    }

    @Test
    public void shouldCloseViewAfterCustomerDeletion() {
        // Delete a customer without confirmation
        deleteCustomer(false);

        // Then close view
        verify(view).close();
    }

    @Test
    public void shouldShowCancelEditConfirmationWhenCancelingEditingCustomer() {
        mPresenter = new AddEditCustomerPresenter(customer, view, mCustomersRepository);

        // With filled fields
        withNonEmptyFields();

        // Field changed
        when(view.getLastName()).thenReturn("Changed Last Name");

        // When presenter asked to close view
        mPresenter.closeView();

        // Then cancel edit confirmation is shown
        verify(view).showCancelEditConfirmation();
    }

    @Test
    public void shouldCloseViewWhenCancelingEditingCustomer() {
        mPresenter = new AddEditCustomerPresenter(customer, view, mCustomersRepository);

        // With unchanged filled fields
        withNonEmptyFields();

        // When presenter asked to close view, with fields remain unchanged
        mPresenter.closeView();

        // Then view should close
        verify(view).close();
    }

    @Test
    public void shouldShowCancelEditConfirmationWhenCancelingAddingNewCustomer() {
        mPresenter = new AddEditCustomerPresenter(null, view, mCustomersRepository);

        // fields have been filled
        withNonEmptyFields();

        // When presenter asked to close view
        mPresenter.closeView();

        // Then cancel edit confirmation is shown
        verify(view).showCancelEditConfirmation();
    }

    @Test
    public void shouldCloseViewWhenCancelingAddingNewCustomer() {
        mPresenter = new AddEditCustomerPresenter(null, view, mCustomersRepository);

        // Fields are empty
        withEmptyFields();

        // When presenter asked to close view
        mPresenter.closeView();

        // Then view should close
        verify(view).close();
    }
}