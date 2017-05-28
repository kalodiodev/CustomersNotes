package eu.kalodiodev.customersnote.customers;

import android.support.v4.app.LoaderManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import eu.kalodiodev.customersnote.data.Customer;
import eu.kalodiodev.customersnote.data.CustomersRepository;
import eu.kalodiodev.customersnote.data.source.LoaderProvider;

import static org.mockito.Mockito.verify;

/**
 * Customers Presenter Test
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomersPresenterTest {

    @Mock
    private CustomersContract.View mCustomersView;

    @Mock
    private static LoaderManager mLoaderManager;

    @Mock
    private LoaderProvider mLoaderProvider;

    @Mock
    private CustomersRepository mCustomersRepository;

    private CustomersPresenter mCustomersPresenter;

    @Before
    public void setUp() throws Exception {
        // MockitoAnnotations.initMocks(this);

        mCustomersPresenter = new CustomersPresenter(mLoaderProvider, mLoaderManager,
                mCustomersView, mCustomersRepository);
    }

    @Test
    public void shouldLoadCustomers() {
        String searchTerm = "";
        mCustomersPresenter.loadCustomers(searchTerm);

        // Then repository gets customers
        verify(mCustomersRepository).getCustomers(mCustomersPresenter, searchTerm);
    }

    @Test
    public void shouldSetNewSearchTermAndReloadCustomers() {
        String searchTerm = "Test";
        mCustomersPresenter.setQueryText(searchTerm);

        // Then Reloads Customers using search term provided
        verify(mCustomersRepository).getCustomers(mCustomersPresenter, searchTerm);
    }

    @Test
    public void shouldShowAddCustomer() {
        mCustomersPresenter.addCustomer();

        // Then should call view to show add customer
        verify(mCustomersView).showAddCustomer();
    }

    @Test
    public void shouldShowEditCustomer() {
        Customer customer = new Customer("Test", "Test", "Test");
        mCustomersPresenter.editCustomer(customer);

        // Then should call view to show edit customer
        verify(mCustomersView).showEditCustomer(customer);
    }
}